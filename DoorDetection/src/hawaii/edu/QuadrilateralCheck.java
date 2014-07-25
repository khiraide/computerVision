package hawaii.edu;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class QuadrilateralCheck {
	
	public BufferedImage img = null;
	public static void QCheck(String filename) {
		try {
			// Read in the image.
			BufferedImage img = ImageIO.read(new File(filename));
			ArrayList<ArrayList<Integer>> cornerlist = new ArrayList<ArrayList<Integer>>();
			ArrayList<Integer> corner = new ArrayList<Integer>();
			ArrayList<ArrayList<Integer>> shapeCorners = new ArrayList<ArrayList<Integer>>();
			for (int i=0; i < img.getWidth(); i++) {
				for (int j=0; j < img.getHeight(); j++) {
					int rgb = img.getRGB(j, i);
					if(rgb > -16645630){
						corner = CornerCheck(img,j,i);
						if(!corner.contains(-1)){
							System.out.println(corner);
							cornerlist.add(corner);
						}
						//System.out.println("x:"+j+" y:"+i+" v:"+rgb);						
					}
				}
			}
			System.out.println("Corners:"+cornerlist.size());
			shapeCorners = FindShape(img,cornerlist);
		} catch (IOException e) {
			System.out.println("Error reading " + filename);
		}	
	}
	
	/**
	 * Temporary FindShape for simplified graphic
	 * @param img
	 * @param cornerlist
	 * @return
	 */
	private static ArrayList<ArrayList<Integer>> FindShape(BufferedImage img,
			ArrayList<ArrayList<Integer>> cornerlist) {
			ArrayList<Integer> corner1 = cornerlist.get(0);
			ArrayList<Integer> corner2 = cornerlist.get(1);
			ArrayList<Integer> corner3 = cornerlist.get(2);
			ArrayList<Integer> corner4 = cornerlist.get(3);
			int x1 = corner1.get(0);
			int y1 = corner1.get(1);
			int x2 = corner2.get(0);
			int y2 = corner2.get(1);
			int x3 = corner3.get(0);
			int y3 = corner3.get(1);
			int x4 = corner4.get(0);
			int y4 = corner4.get(1);
			System.out.println("NotDoneYet");
			
		return cornerlist;
	}


	/*
	private static ArrayList<ArrayList<Integer>> FindShape(BufferedImage img,
			ArrayList<ArrayList<Integer>> cornerlist) {
			ArrayList<ArrayList<Integer>> shape = null;
			for (ArrayList<Integer> activeCorner : cornerlist){
					shape.add(TraceShape(img, activeCorner, cornerlist));
			}
		return shape;
	}


	private static ArrayList<ArrayList<Integer>> TraceShape(BufferedImage img2,
			ArrayList<Integer> activeCorner,
			ArrayList<ArrayList<Integer>> cornerlist) {
		
		return null;
	}
	*/

	private static ArrayList<Integer> CornerCheck(BufferedImage img, int x, int y) {
		ArrayList<Integer> corner = new ArrayList<Integer>();
		int base = 0;
		int up = 0;
		int down = 0;
		int left = 0;
		int right = 0;
		for (int i=-1; i <= 1; i++) {
			for (int j=-1; j <= 1; j++) {
					base = base + img.getRGB(x+j, y+i);
					up = up + img.getRGB(x+j, y+1+i);
					down = down + img.getRGB(x+j, y-1+i);
					left = left + img.getRGB(x-1+j, y+i);
					right = right + img.getRGB(x+1+j, y+i);
			}
		}
		
		if(((base != up)&&(base != down)&&(up != down))&&((base != left)&&(base != right)&&(left != right))){
			//System.out.println("HIT!2");
			//System.out.println("x:"+x+" y:"+y);
			//System.out.println("base:"+base+" up:"+up+" down:"+down);
			//System.out.println("base:"+base+" left:"+left+" right:"+right);
			corner.add(x);
			corner.add(y);
		}
		
		else{
			corner.add(-1);
			corner.add(-1);
		}
		return corner;
	}
	public static void main(String[] args){
		QCheck("DoorStraight.gif");
	}
}
