package hawaii.edu.hiraide;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Otsu {
	private Map<Integer, Integer> histogram = new HashMap<>();
	private Map<Integer, Integer> background = new HashMap<>();
	private Map<Integer, Integer> foreground = new HashMap<>();
	private int amountOfPixels = 0;
	private int t = 0;
	
	public Otsu(Histogram histogram, int t) {
		this.histogram = histogram.getPixels();
		this.amountOfPixels = histogram.getTileSize();
		this.t = t;
		int counter = 0;
		for (Map.Entry<Integer, Integer> entry : this.histogram.entrySet()) {
		    Integer key = entry.getKey();
		    Integer value = entry.getValue(); 
		    if (counter >= t) {
		    	this.foreground.put(key, value);
		    }
		    else {
		    	this.background.put(key, value);
		    }
		    counter++;
		}
	}
	
	public double getWeightBackground() {
		int numerator = 0;
		int counter = 0;

		for (Map.Entry<Integer, Integer> entry : this.background.entrySet()) {
			if (counter == this.background.size()) {
				break;
			}
			numerator += entry.getValue();
			counter++;
		}
		return ((double)numerator) / this.amountOfPixels;	
	}
	
	public double getMeanBackground() {
		int numerator = 0;
		int denominator = 0;
		int counter = 0;
		for (Map.Entry<Integer, Integer> entry : this.background.entrySet()) {
			if (counter == this.background.size()) {
				break;
			}
			numerator += (entry.getKey() * entry.getValue());
			denominator += entry.getValue();
			counter++;
		}
		return ((double)numerator) / denominator;
	}
	
	public double getVarianceBackground() {
		double numerator = 0;
		double denominator = 0;
		int counter = 0;
		for (Map.Entry<Integer, Integer> entry : this.background.entrySet()) {
			if (counter == this.background.size()) {
				break;
			}
			numerator += Math.pow(entry.getKey() - getMeanBackground(), 2) * entry.getValue();
			denominator += entry.getValue();
			counter++;
		}
		return (numerator / denominator);
	}
	
	public double getWeightForeground() {
		int numerator = 0;
		int counter = 0;

		for (Map.Entry<Integer, Integer> entry : this.foreground.entrySet()) {
			if (counter == this.foreground.size()) {
				break;
			}
			numerator += entry.getValue();
			counter++;
		}
		return ((double)numerator) / this.amountOfPixels;	
	}
	
	public double getMeanForeground() {
		int numerator = 0;
		int denominator = 0;
		int counter = 0;
		for (Map.Entry<Integer, Integer> entry : this.foreground.entrySet()) {
			if (counter == this.foreground.size()) {
				break;
			}
			numerator += (entry.getKey() * entry.getValue());
			denominator += entry.getValue();
			counter++;
		}
		return ((double)numerator) / denominator;
	}
	
	public double getVarianceForeground() {
		double numerator = 0;
		double denominator = 0;
		int counter = 0;
		for (Map.Entry<Integer, Integer> entry : this.foreground.entrySet()) {
			if (counter == this.foreground.size()) {
				break;
			}
			numerator += Math.pow(entry.getKey() - getMeanForeground(), 2) * entry.getValue();
			denominator += entry.getValue();
			counter++;
		}
		return (numerator / denominator);
	}
	
	public double getWithinClassVariance() {
		return this.getWeightBackground() 
				* this.getVarianceBackground() 
				+ this.getWeightForeground() 
				* this.getVarianceForeground();
	}
}
