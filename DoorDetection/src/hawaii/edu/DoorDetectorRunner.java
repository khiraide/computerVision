package hawaii.edu;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class DoorDetectorRunner {
	public static void main (String[] args) {
		
		CannyEdgeDetector canny1 = new CannyEdgeDetector();
		DoorDetector newDoor1 = new DoorDetector("./img/blueDoor.jpeg");
		
		canny1.setSourceImage(newDoor1.getImage());
		
		canny1.setLowThreshold(0.5f);
		canny1.setHighThreshold(1f);
		
		canny1.process();
		
		BufferedImage edge1 = canny1.getEdgesImage();
		
		File canny1Test = new File("canny1Test.png");
		try {
			ImageIO.write(edge1, "png", canny1Test.getAbsoluteFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
