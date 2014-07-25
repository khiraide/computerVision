package hawaii.edu.hiraide;

public class Runner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double sigma = 3.5;
		int size = (int) (sigma * 2);
		GaussianKernel kernel = new GaussianKernel(size, sigma);
		kernel.apply("board.tif");
		Sobel sobel = new Sobel();
		sobel.apply("smoothedImage.jpg");
	}

}
