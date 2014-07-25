package hawaii.edu;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Detects doors using edge detection, angle detection, and other methods
 * related to the field of computer vision.
 * 
 * @author Kendyll Doi
 * 			Edward Meyer
 * 			Keone Hiraide
 */
public class DoorDetector {
	/**
	 * Image of a door.
	 */
	private BufferedImage img = null;
	
	/**
	 * Reads in the name of an image file and converts it to a grayscale
	 * image to be used for processing.
	 * 
	 * @param filename The name of the image file.
	 */
	public DoorDetector(String filename) {
		try {
			// Read in the image.
			this.img = ImageIO.read(new File(filename));
			
			// Transform the image into a grayscale image.
			for (int i=0; i < this.img.getWidth(); i++) {
				for (int j=0; j < this.img.getHeight(); j++) {
					int rgb = this.img.getRGB(i, j);
					int r = (rgb >> 16) & 0xFF;
					int g = (rgb >> 8) & 0xFF;
					int b = (rgb & 0xFF);
					int gray = (r + g + b) / 3;
					this.img.setRGB(i, j, new Color(gray, gray, gray).getRGB());
				}
			}
		} catch (IOException e) {
			System.out.println("Error reading " + filename);
		}
		
		
	}
	
	public BufferedImage getImage() {
		return img;
	}
}
