package hawaii.edu.hiraide;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;

import java.io.File;
import java.io.IOException;


import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;

public class GaussianKernel {
	private double[][] gaussianKernel;
	private int size;
	
	public GaussianKernel(int size, double sigma) 
	{
	    this.gaussianKernel = new double[size][size]; 
	    this.size = size;
	    double kernelSum = 0.0, distance = 0.0; 
	    double e = 1.0 /  (2.0 * Math.PI * Math.pow(sigma, 2)); 
	    int radius = size / 2; 
	    for (int i = -radius; i <= radius; i++) 
	    {
	        for (int j = -radius; j <= radius; j++) 
	        {
	            distance = ((j * j) +  (i * i)) /  (2 * (sigma * sigma)); 
	            gaussianKernel[i + radius][j + radius] = e * Math.exp(-distance); 
	            kernelSum += gaussianKernel[i + radius][j + radius]; 
	        } 
	    } 
 
	    for (int y = 0; y < size; y++) 
	    { 
	        for (int x = 0; x < size; x++) 
	        { 
	        	gaussianKernel[y][x] = gaussianKernel[y][x] *  (1.0 / kernelSum); 
	        } 
	    } 
	}
	
	public BufferedImage apply(String filename) {
		BufferedImage img = null;
		try {
			// Reading in a TIF file.
			FileSeekableStream stream = new FileSeekableStream(filename);
			TIFFDecodeParam decodeParam = new TIFFDecodeParam();
			decodeParam.setDecodePaletteAsShorts(true);
			ParameterBlock params = new ParameterBlock();
			params.add(stream);
			RenderedOp image1 = JAI.create("tiff", params);
			img = image1.getAsBufferedImage();
			//img = ImageIO.read(new File(filename));
			
			// Now apply a Gaussian Filter to it.
			convolution2D(img);
			
		} catch (IOException e) {
			System.out.println("error opening "+ filename);
		}
		
		return img;
	}

	private double singlePixelConvolution(BufferedImage input, int x, int y) {
		double output = 0.0;
		int sizeCenter = this.size / 2;
		x = x - sizeCenter;
		y = y - sizeCenter;
		for(int i=0; i < this.size; i++){
			for(int j=0; j < this.size; j++) {	
				if (x+i >= 0 && y+j >= 0 && x+i < input.getWidth() && y+j < input.getHeight()) {
					int rgb = input.getRGB(x+i, y+j);
					int r = (rgb >> 16) & 0xFF;
					int g = (rgb >> 8) & 0xFF;
					int b = (rgb & 0xFF);
					int gray = (r + g + b) / 3;
					output = output + (gray * this.gaussianKernel[i][j]);
				}
			}
		}
		return output;
	}
	
	private BufferedImage convolution2D(BufferedImage input) {
		BufferedImage output = new BufferedImage(input.getWidth(), input.getHeight(), input.getType());
		for(int i=0;i < input.getWidth(); ++i){
			for(int j=0;j < input.getHeight();++j){
				int blah = (int)singlePixelConvolution(input, i, j);
				output.setRGB(i, j, new Color(blah, blah, blah).getRGB());
			}
		}
		
		try {
		    File outputfile = new File("smoothedImage.jpg");
			ImageIO.write(output, "jpg", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}
} // End of GaussianKernel Class.


