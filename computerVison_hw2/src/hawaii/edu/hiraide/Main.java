package hawaii.edu.hiraide;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double num = 9999;
		int thresholdValue = 0;
		int t = 0;
		Otsu otsu = null;
		String filename = "balls.gif";
		Histogram histogram = new Histogram(filename);
		
		// Inputting all the t values until we find the best one.
		for (int i=1; i < histogram.size(); i++) {
			otsu = new Otsu(histogram, i);
			if (otsu.getWithinClassVariance() < num) {
				num = otsu.getWithinClassVariance();
				t = i;
			}
		}
		System.out.println("Input Image: " + filename);
		System.out.println("t = " + t);
		thresholdValue = histogram.getThresholdValue(t);
		System.out.println("The threshold from our Histogram is: " + thresholdValue);
		
		// Create the binary image with the threshold we found.
		histogram.outputBinaryImage(thresholdValue);
		System.out.println("Outputting " + filename + " binary image file: output.gif");
		int diameter = 4; // CHANGE THE DIAMETER OF THE STRUCTURING ELEMENT FOR DIFFERENT RESULTS!
		histogram.executeMorphology(diameter, 0);
		System.out.println("Execution erosion on a disk structuring element with diameter: " + diameter);
		System.out.println("Outputting Eroded image file: " + "morphology.gif");
		ConnectedComponent connectComponent = new ConnectedComponent("morphology.gif");
		connectComponent.amountOfConnectedComponents(640, 640);

	}

}
