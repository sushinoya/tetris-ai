package Tetris.Helper;

import Tetris.Constants;
import Tetris.Heuristic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Helper {

    /* Generates x distinct random integers between 0(inclusive) and y(non-inclusive) */
    public static int[] generateRandomIndices(int x, int y) {
        if (x > y) {
            return new int[0];
        } else {
            return ThreadLocalRandom.current().ints(0, y).distinct().limit(x).toArray();
        }
    }


    public static double[] scaleWeights(double[] unscaledWeights) {

        double sumOfWeights =  Helper.sum(Arrays.stream(unscaledWeights).map(x -> Math.pow(x, 2)).toArray());
        double normalisingFactor = Math.sqrt(sumOfWeights);

        return Arrays.stream(unscaledWeights).map(x -> x / normalisingFactor).toArray();

    }

    // Old scaled weight function
//    public static double[] scaleWeights(double[] unscaledWeights) {
//        int sum = Helper.sum(unscaledWeights);
//        unscaledWeights = Arrays.stream(unscaledWeights).map(s -> s * Constants.SUM_OF_PROBABILITIES / sum).toArray();
//        return unscaledWeights;
//    }



    // This method generates a list of random weights. This sum does not add up to Constants.SUM_OF_PROBABILITIES
    // However, when a Heuristic Object is constructed using these weights, the weights will be automatically adjusted
    // in the Heuristic constructed to scaled the weights so that they add up to Constants.SUM_OF_PROBABILITIES.
    public static double[] getRandomWeights(int numberOfWeights) {
        double[] weights = new double[numberOfWeights];

        for (int i = 0; i < weights.length; i++) {
            weights[i] = new Random().nextDouble();
        }

        return weights;
    }

    // Returns an ArrayList of "count" random heuristics.
    public static ArrayList<Heuristic> getRandomHeuristics(int count) {
        ArrayList<Heuristic> randomHeuristics = new ArrayList<Heuristic>();

        for (int i = 0; i < count; i++) {
            double[] randomWeights = getRandomWeights(Constants.NUMBER_OF_FEATURES);
            randomHeuristics.add(new Heuristic(randomWeights));
        }

        return randomHeuristics;
    }

    // Sums the values in an ArrayList<T>
    public static int sum(Collection<Integer> list) {
        int sum = 0;
        for (int i: list) {
            sum += i;
        }
        return sum;
    }


    // Sums the values in an ArrayList<T>
    public static int sum(double[] arr) {
        int sum = 0;
        for (double i: arr) {
            sum += i;
        }
        return sum;
    }

    // Sums the values in an ArrayList<T>
    public static int sum(ArrayList<Double> arr) {
        int sum = 0;
        for (double i: arr) {
            sum += i;
        }
        return sum;
    }

    // Contains method cause stupid Java couldn't have provided one
    public static boolean contains (int[] arr, int toFind) {
        for (int elem: arr) {
            if (elem == toFind) {
                return true;
            }
        }

        return false;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double calculateSD(double numArray[])
    {
        double sum = 0.0, standardDeviation = 0.0;

        for(double num : numArray) {
            sum += num;
        }

        double mean = sum/10;

        for(double num: numArray) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return Math.sqrt(standardDeviation/10);
    }
}
