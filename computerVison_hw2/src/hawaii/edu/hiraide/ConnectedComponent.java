package hawaii.edu.hiraide;


import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;


public class ConnectedComponent {
    int components = 1;
    private Integer[] image;
    
    public ConnectedComponent(String filename) {
    	try {
			BufferedImage img = ImageIO.read(new File(filename));
			ArrayList<Integer> list = new ArrayList<>();
			int tileHeight = img.getHeight();
			int tileWidth = img.getWidth();
			Raster raster = img.getData();
			for (int i=0; i < tileHeight; i++) {
				for (int j=0; j < tileWidth; j++) {
					if (raster.getSample(i, j, 0) == 255) {
						list.add(0);
					}
					else {
						list.add(1);
					}
				}
			}
			
			image = list.toArray(new Integer[list.size()]);
		} catch (IOException e) {
			System.out.println("error opening "+ filename);
		}
    	
    }
    
    private int find( int x, int[] representative, int[] labels)
    {
   	for (int i=0; i < representative.length; i++) {
   		if (representative[x] > 0) {
   			x = representative[x];
   		}
   		else {
   			break;
   		}
   	}
       if ( labels[x] == 0 ) {
           labels[x] = components++;
       }
       return labels[x];
   }
    
    private void merge( int x, int y, int[] representative)
    {
    	for (int i=0; i < representative.length; i++) {
    		if (representative[x] > 0) {
    			x = representative[x];
    		}
    		else {
    			break;
    		}
    	}
    	for (int i=0; i < representative.length; i++) {
    		if (representative[y] > 0) {
    			y = representative[y];
    		}
    		else {
    			break;
    		}
    	}
    	
        if ( x != y ) {
            if (x < y) {
                representative[x] = y;
            }
            else {
            	representative[y] = x;
            }
        }
    }

    public int[] amountOfConnectedComponents(int w, int h)    
    {
        int width= w, height= h;
        int[] copy= new int[width*height], representative= new int[99999], labels= new int[99999];

        int next_region = 1;
        for (int i = 0; i < height; ++i ){
            for (int j = 0; j < width; ++j ){
                int k = 0;
                boolean heighbors = false;

                if (j > 0) {
                	if (image[i*width+j-1] == image[i*width+j]) {
                		k = copy[i*width+j-1];
                		heighbors = true;
                	}
                }

                if (i > 0) { 
                	if (image[(i-1)*width+j] == image[i*width+j]) { 
                		if ((heighbors = false || image[(i-1)*width+j] < k )) {
                			k = copy[(i-1)*width+j];
                			heighbors = true;
                		}
                	}
                }
                 
                if ( !heighbors ) {
                    k = next_region;
                    next_region++;
                }
                copy[i*width+j]= k;
                
                if ( j > 0) {
                	if (image[i*width+j-1]== image[i*width+j]) {
                		if (copy[i*width+j-1]!= k ) {
                			 merge( k, copy[i*width+j-1], representative );
                		}
                	}
                }
                       
                if (i > 0) { 
                	if (image[(i-1)*width+j]== image[i*width+j]) {
                		if (copy[(i-1)*width+j]!= k ) {
                			merge( k, copy[(i-1)*width+j], representative );
                		}
                	}
                }
                        
            }
        }
        components = 1;
        for (int i = 0; i < width*height; i++ ) {
            if (image[i] != 0) {           
                copy[i] = find( copy[i], representative, labels );                
            }
        }
        components -= 2;

        System.out.println(components + " connected-components");
        return copy;
    }
}