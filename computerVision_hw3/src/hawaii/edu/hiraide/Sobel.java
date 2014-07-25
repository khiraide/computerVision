package hawaii.edu.hiraide;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;

public class Sobel {
	
	private int[][] sobelKernelX = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
	private int[][] sobelKernelY = {{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}};
	private double[][] sobelX;
	private double[][] sobelY;
	private double[][] sobelD;
	private double[][] sobelM;
	private double max;
	
	public BufferedImage sobelX(BufferedImage input) {
		return input;	
	}
	
	public BufferedImage sobelY(BufferedImage input) {
		return input;	
	}
	
	public BufferedImage apply(String filename) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(filename));
			
			// Now apply a Gaussian Filter to it.
			BufferedImage magnitude = convolution2D(img);
			
			maximumSuppression(magnitude);
			
			hysteresis(magnitude, 0.1, 0.3);
			
		} catch (IOException e) {
			System.out.println("error opening "+ filename);
		}
		
		return img;
	}
	
	private double singlePixelConvolution(BufferedImage input, int x, int y, int kernel[][]) {
		double output = 0.0;
		int sizeCenter = 3 / 2;
		x = x - sizeCenter;
		y = y - sizeCenter;
		for(int i=0; i < 3; i++){
			for(int j=0; j < 3; j++) {	
				if (x+i >= 0 && y+j >= 0 && x+i < input.getWidth() && y+j < input.getHeight()) {
					int gray = this.getGrayValue(input, x+i, y+j);
					output = output + (gray * kernel[i][j]);
				}
			}
		}
		return output;
	}
	
	private BufferedImage convolution2D(BufferedImage input) {
		this.sobelX = new double[input.getWidth()][input.getHeight()];
		this.sobelY = new double[input.getWidth()][input.getHeight()];
		this.sobelD = new double[input.getWidth()][input.getHeight()];
		this.sobelM = new double[input.getWidth()][input.getHeight()];
		
		BufferedImage output = new BufferedImage(input.getWidth(), input.getHeight(), input.getType());
		for(int i=0;i < input.getWidth(); ++i){
			for(int j=0;j < input.getHeight();++j) {
				
				// Getting sobelX
				double value = singlePixelConvolution(input, i, j, this.sobelKernelX);
				this.sobelX[i][j] = value;
				
				// Getting sobelY
				value = singlePixelConvolution(input, i, j, this.sobelKernelY);
				this.sobelY[i][j] = value;
				
				// Combining sobelX and sobelY
				//value = (Math.abs(this.sobelX[i][j]) + Math.abs(this.sobelY[i][j]));
				value = Math.sqrt(Math.pow(this.sobelX[i][j], 2) + Math.pow(this.sobelY[i][j], 2));
				int tempValue = (int)value;
				if (value > 255) {
					tempValue = 255;
				}
				
				// Getting the direction.
				// So in the code there has to be a restriction set whenever 
				// this takes place. Whenever the gradient in the x direction is
				// equal to zero, the edge direction has to be equal to 90 degrees
				// or 0 degrees, depending on what the value of the gradient in the
				// y-direction is equal to. If GY has a value of zero, the edge 
				// direction will equal 0 degrees. Otherwise the edge direction 
				// will equal 90 degrees. 
				if (this.sobelY[i][j] == 0.0 && this.sobelX[i][j] == 0.0) {
					this.sobelD[i][j] = 0.0;
				}
				else if (this.sobelY[i][j] != 0.0 && this.sobelX[i][j] == 0.0) {
					this.sobelD[i][j] = 90.0;
				}
				else {
					double theta = Math.atan2(this.sobelY[i][j], this.sobelX[i][j]);
					if (theta < 0) { // So that values fall between 0 - 2pi
						theta += (2 * Math.PI);
					}
					// Turning radians to degrees.
					double thetaDegrees = theta * (180/Math.PI); 
					
					// Ensuring that theta falls in the I and II quadrants.
					if (thetaDegrees > 180) {
						thetaDegrees -= 180;
					}
					
					// Rounding degrees into either 0, 135, 90, or 45.
					if (thetaDegrees >= 0.0 && thetaDegrees < 22.5) {
						thetaDegrees = 0.0;
					}
					else if (thetaDegrees >= 22.5 && thetaDegrees < 67.5) {
						thetaDegrees = 45.0;
					}
					else if (thetaDegrees >= 67.5 && thetaDegrees < 112.5) {
						thetaDegrees = 90.0;
					}
					else if (thetaDegrees >= 112.5 && thetaDegrees < 157.5) {
						thetaDegrees = 135.0;
					}
					else if (thetaDegrees >= 157.5 && thetaDegrees <= 180.0) {
						thetaDegrees = 0.0;
					}
					
					else {
						System.out.println("weird Angle!!!");
					}
					this.sobelD[i][j] = thetaDegrees; 
				}
				//System.out.println(this.sobelD[i][j]);
				
				this.sobelM[i][j] = value;
				// Seeing how the combined image looks like.
				output.setRGB(i, j, new Color(tempValue, tempValue, tempValue).getRGB());
			}
		}
		
		try {
		    File outputfile = new File("gradientMagnitude.jpg");
			ImageIO.write(output, "jpg", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		double max = this.sobelM[0][0];
		double min = this.sobelM[0][0];

		// Getting the min and max for normalizing.
		for (int i=0; i < output.getWidth(); i++) {
			for (int j=0; j < output.getHeight(); j++) {
				max = Math.max(max, this.sobelM[i][j]);
				min = Math.min(min, this.sobelM[i][j]);
			}
		}
		
		// Noramlizing so that the magnitudes are in the range of 0 - 1
		for (int i=0; i < output.getWidth(); i++) {
			for (int j=0; j < output.getHeight(); j++) {
				//this.sobelM[i][j] =  (this.sobelM[i][j] - min) / (max - min);
				//this.sobelM[i][j] =  (this.sobelM[i][j] / 4);
			}
		}
		
		System.out.println(max);
		this.max = max;
//		System.out.println(sobelM[0][1]);
		return output;
	}
	
	public BufferedImage maximumSuppression(BufferedImage img) {
		BufferedImage output = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		for (int i=0; i < img.getWidth(); i++) {
			for (int j=0; j < img.getHeight(); j++) {
				double currentPixelValue = this.sobelM[i][j];
				//System.out.println(currentPixelValue);
				// Angle is 90 or horizontal.
				// Check north and south.
				
				if (this.sobelD[i][j] == 90.0 && northIsGood(i) && southIsGood(i, img.getWidth())) {
					if (currentPixelValue > this.sobelM[i-1][j] && currentPixelValue >  this.sobelM[i+1][j] ){
							//&& this.sobelD[i-1][j] == 90.0 && this.sobelD[i+1][j] == 90.0) {
						//this.sobelM[i][j] = 255;
						//output.setRGB(i, j, new Color(255, 255, 255).getRGB());
					}
					else {
						this.sobelM[i][j] = 0;
					}
				}
				
				else if (this.sobelD[i][j] == 0.0 && westIsGood(j) && eastIsGood(j, img.getHeight())) {
					if (currentPixelValue > this.sobelM[i][j-1] && currentPixelValue > this.sobelM[i][j+1]){
							//&& this.sobelD[i][j-1] == 0.0 && this.sobelD[i][j+1] == 0.0) {
						//this.sobelM[i][j] = 255;
						//output.setRGB(i, j, new Color(255, 255, 255).getRGB());
					}
					else {
						this.sobelM[i][j] = 0;
					}
				}
				else if (this.sobelD[i][j] == 135.0 && northIsGood(i) && southIsGood(i, img.getWidth()) && westIsGood(j) && eastIsGood(j, img.getHeight())) {
					if (currentPixelValue > this.sobelM[i-1][j-1] && currentPixelValue > this.sobelM[i+1][j+1] ){
							//&& this.sobelD[i-1][j-1] == 135.0 && this.sobelD[i+1][j+1] == 135.0) {
						//this.sobelM[i][j] = 255;
						//output.setRGB(i, j, new Color(255, 255, 255).getRGB());
					}
					else {
						this.sobelM[i][j] = 0;
					}
				}
				else if (this.sobelD[i][j] == 45.0 && northIsGood(i) && southIsGood(i, img.getWidth()) && westIsGood(j) && eastIsGood(j, img.getHeight())) {
					if (currentPixelValue > this.sobelM[i-1][j+1] && currentPixelValue > this.sobelM[i+1][j-1] ){
							//&& this.sobelD[i-1][j+1] == 45.0 && this.sobelD[i+1][j-1] == 45.0) {
						//output.setRGB(i, j, new Color(255, 255, 255).getRGB());
						//this.sobelM[i][j] = 255;
					}
					else {
						this.sobelM[i][j] = 0;
					}
				}
			}
		}
		
		for (int i=0; i < output.getWidth(); i++) {
			for (int j=0; j < output.getHeight(); j++) {
				int value = (int)this.sobelM[i][j];
				//System.out.println(value);
				if (value > 255) {
					value = 255;
				}
				//value *= 255;
				output.setRGB(i, j, new Color(value, value, value).getRGB());
			}
		}
		
		try {
		    File outputfile = new File("nonMaximumSupression.jpg");
			ImageIO.write(output, "jpg", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;	
	}
	
	private BufferedImage hysteresis(BufferedImage img, double low, double high) {
		BufferedImage output = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		List<int[]> pixels = new ArrayList<int[]>();
		
		for (int i=0; i < output.getWidth(); i++) {
			for (int j=0; j < output.getHeight(); j++) {
				output.setRGB(i, j, new Color(0, 0, 0).getRGB());
			}
		}
		low *= max;
		high *= max;
		
		for (int i=0; i < img.getWidth(); i++) {
			for (int j=0; j < img.getHeight(); j++) {
				if (this.sobelM[i][j] < low) continue;
				
				if (this.sobelM[i][j] >= high) {
					output.setRGB(i, j, new Color(255, 255, 255).getRGB());
					continue;
				}
				output.setRGB(i, j, new Color(130, 130, 130).getRGB());
				pixels.add(new int[]{i,j});
			}
		}
		
		boolean change=true;
		while (change) {
			change = false;
			Iterator<int[]> iter = pixels.iterator();
			
			while (iter.hasNext()) {
				int[] pixel = iter.next();
				int x = pixel[0];
				int y = pixel[1];
				try {
					if(getGrayValue(output, x-1,y+1) == 255 ||getGrayValue(output, x-1, y) == 255 || getGrayValue(output, x-1, y-1) == 255 ||getGrayValue(output, x, y+1) == 255|| getGrayValue(output, x, y-1)== 255||getGrayValue(output, x+1, y+1)== 255||getGrayValue(output, x+1, y)== 255||getGrayValue(output, x+1, y-1)== 255) {
						output.setRGB(x, y, new Color(255, 255, 255).getRGB());
					}
				} catch (Exception e) {
					
				}
             
             iter.remove();
             change=true;
			}
		}
		
		try {
		    File outputfile = new File("postHysteresis.jpg");
			ImageIO.write(output, "jpg", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return output;
	}
	
	private int getGrayValue(BufferedImage img, int x, int y) {
		int rgb = img.getRGB(x, y);
		int r = (rgb >> 16) & 0xFF;
		int g = (rgb >> 8) & 0xFF;
		int b = (rgb & 0xFF);
		int gray = (r + g + b) / 3;

		return gray;
	}
	
	private boolean northIsGood(int i) {
		boolean isGood = false;
		if ((i-1) > -1) {
			isGood = true;
		}
		return isGood;
	}
	
	private boolean southIsGood(int i, int size) {
		boolean isGood = false;
		if ((i+1) < size) {
			isGood = true;
		}
		
		return isGood;
	}
	
	private boolean westIsGood(int j) {
		boolean isGood = false;
		if ((j-1) > -1) {
			isGood = true;
		}
		return isGood;
	}
	
	private boolean eastIsGood(int j, int size) {
		boolean isGood = false;
		if ((j+1) < size) {
			isGood = true;
		}
		return isGood;
	}
}
