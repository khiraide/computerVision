package hawaii.edu.hiraide;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.math.plot.Plot3DPanel;

public class PhotometricStereo {
	private RealMatrix I1, I2, I3, I4, S, P, Nx, Ny, Nz, D;
	
	/**
	 * Takes in four images and retrieves the intensities for each image.
	 * 
	 * @param image1 The 1st image.
	 * @param image2 The 2nd image.
	 * @param image3 The 3rd image.
	 * @param image4 The 4th image.
	 */
	public PhotometricStereo(String image1, String image2, String image3, String image4) {
		this.I1 = retrieveImageIntensities(image1);
		this.I2 = retrieveImageIntensities(image2);
		this.I3 = retrieveImageIntensities(image3);
		this.I4 = retrieveImageIntensities(image4);
	}
	
	/**
	 * @param S The light source direction.
	 */
	public void setSource(double[][] S) {
		this.S = MatrixUtils.createRealMatrix(S);
	}
	
	/**
	 * Takes in four images and retrieves their intensities for each pixel
	 * and divides them by 255 in order to normalize these values
	 * in the range from 0-1. 
	 */
	private RealMatrix retrieveImageIntensities(String filename) {
		BufferedImage img = null;
		double matrixData[][] = null; 
		try {
			
			// Read in the image.
			img = ImageIO.read(new File(filename));
			matrixData = new double[img.getWidth()][img.getHeight()];

			// Transform the image into a grayscale image.
			Raster raster = img.getData();
			for (int i=0; i < img.getWidth(); i++) {
				for (int j=0; j < img.getHeight(); j++) {
					int gray = raster.getPixel(i, j, new int[raster.getNumBands()])[0];
					double convertedGray = (double) gray / 255;
					if (convertedGray > 255) {
						System.out.println("wrong2");
					}
					matrixData[i][j] = convertedGray;
				}
			}
		} catch (IOException e) {
			System.out.println("Error reading " + filename);
		}
		return MatrixUtils.createRealMatrix(matrixData);
	}
	
	public void execute() {
		calculateAlbedoAndSurfaceNormals();
		createDepthMap();
	}
	
	/**
	 * Computes the albedo and the surface normal
	 */
	public void calculateAlbedoAndSurfaceNormals() {
		RealMatrix STranspose = this.S.transpose();
		double[][] p = new double[I1.getColumnDimension()][I1.getRowDimension()];
		double[][] nx = new double[I1.getColumnDimension()][I1.getRowDimension()];
		double[][] ny = new double[I1.getColumnDimension()][I1.getRowDimension()];
		double[][] nz = new double[I1.getColumnDimension()][I1.getRowDimension()];
		
		RealMatrix gMatrix = null;
		for(int i=0; i < I1.getColumnDimension(); i++) {
			for (int j=0; j < I1.getRowDimension(); j++) {
				
				// Stacking our 'I's.
				double[][] intensityMatrixData = { {I1.getEntry(i, j)},
						          			   		{I2.getEntry(i, j)},
						          			   		{I3.getEntry(i, j)},
						          			   		{I4.getEntry(i, j)}
						          			 	  };
				RealMatrix intensityMatrix = MatrixUtils.createRealMatrix(intensityMatrixData);
				
				
				// Pseudo-Inverse step multiplied by 'I'.
				gMatrix = new LUDecomposition(STranspose.multiply(scalarMultiplication(intensityMatrix, this.S)))
					.getSolver()
					.getInverse()
					.multiply(STranspose)
					.multiply(scalarMultiplication(intensityMatrix, intensityMatrix));
				
				// Calculating the albedo.
				p[i][j] = (Math.sqrt(Math.pow(gMatrix.getEntry(0, 0), 2)+
						           Math.pow(gMatrix.getEntry(1, 0), 2)+
						           Math.pow(gMatrix.getEntry(2, 0), 2)));
				
				
				if (p[i][j] > 1) {
					System.out.println("wrong!");
				}
				
				// Calculating the surface normals.
				nx[i][j] = gMatrix.getEntry(0, 0) / p[i][j];
				ny[i][j] = gMatrix.getEntry(1, 0) / p[i][j];
				nz[i][j] = gMatrix.getEntry(2, 0) / p[i][j];
			}

		}
		this.P = MatrixUtils.createRealMatrix(p);
		this.Nx = MatrixUtils.createRealMatrix(nx).transpose();
		this.Ny = MatrixUtils.createRealMatrix(ny).transpose();
		this.Nz = MatrixUtils.createRealMatrix(nz).transpose();	
	}
	/**
	 * Creates the albedo map.
	 * 
	 * @param filename Filename.
	 */
	public void createAlbedoMap(String filename) {
		try {
			// Read in the image.
			BufferedImage img = new BufferedImage(I1.getRowDimension(), 
					I1.getColumnDimension(),
				    BufferedImage.TYPE_INT_RGB);
			
			double p[][] = this.P.getData();
			
			// Transform the image into a grayscale image.
			for (int i=0; i < this.P.getRowDimension(); i++) {
				for (int j=0; j < this.P.getColumnDimension(); j++) {
					int albedo = (int)(p[i][j] * 255);
					img.setRGB(i, j, new Color(albedo, albedo, albedo).getRGB());
				}
			}
			
			ImageIO.write(img, "png", new File(filename));
		} catch (IOException e) {
			System.out.println("Error reading " + "yeah");
		}
	}
	
	
	/**
	 * Used for printing the images.
	 * @param image
	 * @param value
	 */
	public void printResult(String image, String value) {
		try {
			// Read in the image.
			BufferedImage img = new BufferedImage(I1.getRowDimension(), 
					I1.getColumnDimension(),
				    BufferedImage.TYPE_INT_RGB);
			double n[][] = new double[I1.getRowDimension()][I1.getRowDimension()];
			if (value.equals("X"))
				n = normalize255(this.Nx);
			else if (value.equals("Y"))
				n = normalize255(this.Ny);
			else if (value.equals("Z"))
				n = normalize255(this.Nz);
			else if (value.equals("D"))
				n = normalize255(this.D);
			// Transform the image into a grayscale image.
			for (int i=0; i < I1.getRowDimension(); i++) {
				for (int j=0; j < I1.getColumnDimension(); j++) {
					int albedo = (int) n[i][j];
					img.setRGB(i, j, new Color(albedo, albedo, albedo).getRGB());
				}
			}
			
			ImageIO.write(img, "png", new File(image));
		} catch (IOException e) {
			System.out.println("Error reading " + "yeah");
		}
	}
	
	/**
	 * Create the depth map.
	 */
	private void createDepthMap() {
		double depthX = 0.0;
		double depthY = 0.0;
		double[][] depthMap = new double[I1.getRowDimension()][I1.getColumnDimension()];
		for (int i=0; i < I1.getRowDimension(); i++) {
			for (int j=0; j < I1.getColumnDimension(); j++) {
				if (j == 0) {
					depthX += this.Ny.getEntry(i, 0) / -this.Nz.getEntry(i, j) ;
					depthY = 0.0;
				}
				depthY += this.Nx.getEntry(i, j) / -this.Nz.getEntry(i, j);
				depthMap[i][j] = depthX  + depthY ;
			}
		}	
		this.D = MatrixUtils.createRealMatrix(depthMap);
	}
	
	/**
	 * Performs scalar multiplication on two matrices.
	 * @param matrix1
	 * @param matrix2
	 * @return
	 */
	private RealMatrix scalarMultiplication(RealMatrix matrix1, RealMatrix matrix2) {
		double a = matrix1.getEntry(0, 0);
		double b = matrix1.getEntry(1, 0);
		double c = matrix1.getEntry(2, 0);
		double d = matrix1.getEntry(3, 0);
		
		double[][] result = new double[matrix2.getRowDimension()][matrix2.getColumnDimension()];
		
		for (int i=0; i < matrix2.getRowDimension(); i++) {
			for (int j=0; j < matrix2.getColumnDimension(); j++) {
				if (i == 0) result[i][j] = a * matrix2.getEntry(i, j);
				if (i == 1) result[i][j] = b * matrix2.getEntry(i, j);
				if (i == 2) result[i][j] = c * matrix2.getEntry(i, j);
				if (i == 3) result[i][j] = d * matrix2.getEntry(i, j);
			}
		}
		
		return MatrixUtils.createRealMatrix(result);
	}
	
	/**
	 * Normalize the values in a matrix to range from 0-255.
	 * @param realMatrix
	 * @return
	 */
	private double[][] normalize255(RealMatrix realMatrix) {
		double[][] input = realMatrix.getData();
		double[][] normalized = new double[realMatrix.getRowDimension()][realMatrix.getColumnDimension()];
		double minimum = 99999.99;
		double maximum = -9999.99;
		for (int i=0; i < realMatrix.getRowDimension(); i++) {
			for (int j=0; j < realMatrix.getColumnDimension(); j++) {
				minimum = Math.min(minimum, input[i][j]);
				maximum = Math.max(maximum, input[i][j]);
			}
		}
		for (int i=0; i < realMatrix.getRowDimension(); i++) {
			for (int j=0; j < realMatrix.getColumnDimension(); j++) {
				normalized[i][j] = ((input[i][j] - minimum)*(255/(maximum-minimum)));
			}
		}
		return normalized;
	}
	
	public void create3DSurface(String panelName) {
		Plot3DPanel plot = new Plot3DPanel();
		int size = Nx.getColumnDimension() * Nx.getColumnDimension();
		List<Double> x = new ArrayList<Double>();
		List<Double> y = new ArrayList<Double>();
		List<Double> z = new ArrayList<Double>();
		for (int i=0; i < Nx.getRowDimension(); i++) {
			for (int j=0; j < Ny.getColumnDimension(); j++) {
				x.add(Nx.getEntry(i, j));
				y.add(Ny.getEntry(i, j));
				z.add(Nz.getEntry(i, j));
			}
		}
		double[] x2 = new double[x.size()];
		double[] y2 = new double[y.size()];
		double[] z2 = new double[z.size()];
		
		for (int i=0; i < x.size(); i++) {
			x2[i] = x.get(i);
			y2[i] = y.get(i);
			z2[i] = z.get(i);
		}
		
		plot.addScatterPlot("Plot", new Color(0), x2, y2, z2);
		// put the PlotPanel in a JFrame, as a JPanel
		JFrame frame = new JFrame(panelName);
		frame.setContentPane(plot);
		frame.setSize(800, 600);
		frame.setVisible(true);

	}
}


