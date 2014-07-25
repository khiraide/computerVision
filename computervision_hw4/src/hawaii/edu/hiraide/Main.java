package hawaii.edu.hiraide;

public class Main {

	public static void main(String[] args) {

		// Synthetic data.
		PhotometricStereo photometricStereo = 
		new PhotometricStereo("hw4-images/im1.png", 
				              "hw4-images/im2.png",
				              "hw4-images/im3.png",
				              "hw4-images/im4.png");
		
		double[][] s = { {0,	0,		-1}, 
				          {0,	0.2,	-1},
		                  {0,	-0.2,	-1},
				          {0.2,	0,		-1}
		                 };
		
		photometricStereo.setSource(s);
		photometricStereo.execute();
		photometricStereo.createAlbedoMap("imAlbedo.png");
		photometricStereo.printResult("imX.png", "X");
		photometricStereo.printResult("imY.png", "Y");
		photometricStereo.printResult("imZ.png", "Z");
		photometricStereo.printResult("imD.png", "D");
		photometricStereo.create3DSurface("synthetic data");
		
		// Real data.
		photometricStereo = new PhotometricStereo("hw4-images/real1.bmp", 
	              "hw4-images/real2.bmp",
	              "hw4-images/real3.bmp",
	              "hw4-images/real4.bmp");
		
		double[][] s2 = { {-0.38359,		-0.236647,	0.892668}, 
		  {-0.372825,	0.303914,	0.87672},
		  {0.250814,	0.34752,	0.903505},
		  {0.203844,	-0.096308,	0.974255}
		};
		
		photometricStereo.setSource(s2);
		photometricStereo.execute();
		photometricStereo.createAlbedoMap("realAlbedo.png");
		photometricStereo.printResult("realX.png", "X");
		photometricStereo.printResult("realY.png", "Y");
		photometricStereo.printResult("realZ.png", "Z");
		photometricStereo.printResult("realD.png", "D");
		photometricStereo.create3DSurface("real data");
	}
}
