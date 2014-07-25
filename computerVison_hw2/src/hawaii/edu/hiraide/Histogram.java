package hawaii.edu.hiraide;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.imageio.ImageIO;

public class Histogram {
	private Map<Integer, Integer> pixels = new TreeMap<Integer, Integer>();
	private Map<Integer, Integer> temp = new TreeMap<Integer, Integer>();
	private BufferedImage img = null;
	
	public Histogram(String filename) {
		try {
			this.img = ImageIO.read(new File(filename));
		} catch (IOException e) {
			System.out.println("error opening "+ filename);
		}
		
		populatePixels();
	}
	
	public void populatePixels() {
		temp = new TreeMap<Integer, Integer>();
		int tileHeight = img.getHeight();
		int tileWidth = img.getWidth();
		Raster raster = img.getData();
		for (int i=0; i < tileHeight; i++) {
			for (int j=0; j < tileWidth; j++) {
				int pixelValue = raster.getSample(i, j, 0);
				if (temp.containsKey(pixelValue)) {
					temp.put(pixelValue, temp.get(pixelValue) + 1);
				}
				else {
					temp.put(pixelValue, 1);
				}
			}
		}
		
		int counter = 0;
		for (Entry<Integer, Integer> entry : temp.entrySet()) {
		    this.pixels.put(counter, entry.getValue());
		    counter++;
		}
	}
	
	public Map<Integer, Integer> getPixels() {
		return pixels;
	}
	
	public int getThresholdValue(int t) {
		int counter = 1;
		int thresholdValue = 0;
		for (Entry<Integer, Integer> entry : temp.entrySet()) {
		    if (counter == t) {
		    	thresholdValue = entry.getKey();
		    	break;
		    }
		    counter++;
		}
		return thresholdValue;
		
	}
	
	public int getTileSize() {
		return img.getTileHeight() * img.getWidth();
		
	}
	
	public int size() {
		return temp.size();
		
	}
	
	public void outputBinaryImage(int threshold) {
		int tileHeight = img.getHeight();
		int tileWidth = img.getWidth();
		for (int i=0; i < tileHeight; i++) {
			for (int j=0; j < tileWidth; j++) {
				if (img.getRaster().getSample(i, j, 0) >= threshold) {
					img.getRaster().setSample(i, j, 0, 255);
				}
				else {
					img.getRaster().setSample(i, j, 0, 0);
				}
			}
		}
		File outputfile = new File("output.gif");
	    try {
			ImageIO.write(img, "gif", outputfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void executeMorphology(int radius, int mode) {
		// If mode is 0, then perform erosion.
		if (mode == 0) {
			mode = 255;
		}
		else { // We'll do dilation if the mode is not zero.
			mode = 0;
		}
		ColorModel colorModel = this.img.getColorModel();
		boolean isAlphaPremultiplied = colorModel.isAlphaPremultiplied();
		WritableRaster raster = img.copyData(null);
		BufferedImage copy = new BufferedImage(colorModel, raster, isAlphaPremultiplied, null);
	    for (int i=0; i < this.img.getWidth(); i++){
	    	for (int j=0; j < this.img.getHeight(); j++){
	    		if (img.getRaster().getSample(i, j, 0) == mode){
	    			copy.getRaster().setSample(i, j, 0, mode);
	    			for (int x=0; x < radius-1; x++) {
		    			// Check West.
		    			if (i > x) copy.getRaster().setSample(i - (x+1), j, 0, mode);
		    			// Check South.
		    			if (j > x) copy.getRaster().setSample(i, j - (x+1), 0, mode);
		    			// Check East.
		    			if (i + x+1 < img.getWidth()) copy.getRaster().setSample(i + (x+1), j, 0, mode);
		    			// Check North
		    			if (j + x+1 < img.getHeight()) copy.getRaster().setSample(i, j + (x+1), 0, mode);
		    			// Check North-East.
		    			if (i + x+1 < img.getWidth() && j + x+1 < img.getHeight()) copy.getRaster().setSample(i + (x+1), j + (x+1), 0, mode);
		    			// Check South-West.
		    			if (i > x && j > x) copy.getRaster().setSample(i - (x+1), j - (x+1), 0, mode);
		    			// Check South-East
		    			if (i + x+1 < img.getWidth() && j > x) copy.getRaster().setSample(i + (x+1), j - (x+1), 0, mode);
		    			// Check North-West
		    			if (j + x+1 < img.getHeight() && i > x) copy.getRaster().setSample(i - (x+1), j + (x+1), 0, mode);
	    			}
	    			
	    			// Final touches to make the structuring element into a disk shape.
	    			// Check West.
	    			if (i > radius-1) copy.getRaster().setSample(i - radius, j, 0, mode);
	    			// Check South.
	    			if (j > radius-1) copy.getRaster().setSample(i, j - radius, 0, mode);
	    			// Check East.
	    			if (i + radius+1 < img.getWidth()) copy.getRaster().setSample(i + radius, j, 0, mode);
	    			// Check North
	    			if (j + radius+1 < img.getHeight()) copy.getRaster().setSample(i, j + radius, 0, mode);
	    		}
	    	}
	    }
		
		File outputfile = new File("morphology.gif");
	    try {
			ImageIO.write(copy, "gif", outputfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
