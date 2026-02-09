/**
 * Name: Suad Gafarli
 * Project: Random Number Statistics Analysis
 * Class: CSCI-3612: Object Oriented Analysis and Design
 * Date: February 9, 2026
 */

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

// Class definition - defines the structure and behavior of Generator objects
public class Generator {

    // Class attribute - stores the different sample sizes to test
    private int[] testSizes = {10, 1000, 1000000};

    /**
     * Method definition - generates a list of random doubles
     * 
     * @param n number of random values to generate
     * @param randNumGen selector for which generator to use (1, 2, or 3)
     * @return ArrayList containing n random doubles in range [0, 1)
     */
    public ArrayList<Double> populate(int n, int randNumGen) {
        ArrayList<Double> values = new ArrayList<>();
        
        // Object instantiation - creates a Random object for generator type 1
        Random rand = new Random();

        for (int i = 0; i < n; i++) {
            if (randNumGen == 1) {
                values.add(rand.nextDouble());
            } else if (randNumGen == 2) {
                values.add(Math.random());
            } else {
                values.add(ThreadLocalRandom.current().nextDouble());
            }
        }
        return values;
    }

    /**
     * Computes basic descriptive statistics for a dataset
     * 
     * @param randomValues the dataset to analyze
     * @return ArrayList with [count, mean, std dev, min, max]
     */
    public ArrayList<Double> statistics(ArrayList<Double> randomValues) {
        int count = randomValues.size();
        double total = 0.0;
        double smallest = Double.MAX_VALUE;
        double largest = Double.MIN_VALUE;

        // First pass: calculate sum, min, max
        for (double value : randomValues) {
            total += value;
            if (value < smallest) smallest = value;
            if (value > largest) largest = value;
        }

        double average = total / count;
        
        // Second pass: calculate variance for standard deviation
        double varianceSum = 0.0;
        for (double value : randomValues) {
            double diff = value - average;
            varianceSum += diff * diff;
        }
        
        // Sample standard deviation uses n-1 denominator
        double stdDev = Math.sqrt(varianceSum / (count - 1));

        ArrayList<Double> stats = new ArrayList<>();
        stats.add((double) count);
        stats.add(average);
        stats.add(stdDev);
        stats.add(smallest);
        stats.add(largest);
        
        return stats;
    }

    /**
     * Accessibility example - public method can be called from outside the class
     * Prints statistical results in tabular format
     * 
     * @param results the statistics to display
     * @param headerOn whether to print column headers
     */
    public void display(ArrayList<Double> results, boolean headerOn) {
        if (headerOn) {
            System.out.println("-----------------------------------------------------------------------------");
            System.out.printf("%-10s %-12s %-12s %-12s %-12s\n", 
                "n", "Mean", "StdDev", "Min", "Max");
            System.out.println("-----------------------------------------------------------------------------");
        }
        System.out.printf("%-10.0f %-12.4f %-12.4f %-12.4f %-12.4f\n", 
            results.get(0), results.get(1), results.get(2), results.get(3), results.get(4));
    }

    /**
     * Runs the complete analysis for all generators and sample sizes
     */
    public void execute() {
        String[] generators = {"java.util.Random", "Math.random()", "ThreadLocalRandom"};
        
        for (int g = 1; g <= 3; g++) {
            System.out.println("\nGenerator: " + generators[g - 1]);
            boolean showHeader = true;
            
            for (int size : testSizes) {
                ArrayList<Double> dataset = populate(size, g);
                ArrayList<Double> result = statistics(dataset);
                display(result, showHeader);
                showHeader = false;
            }
        }
    }

    public static void main(String[] args) {
        Generator g = new Generator();
        g.execute();
    }
}