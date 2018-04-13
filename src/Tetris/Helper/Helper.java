package Tetris.Helper;

import Tetris.Constants;
import Tetris.Heuristic;

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
        int sum = Helper.sum(unscaledWeights);
        unscaledWeights = Arrays.stream(unscaledWeights).map(s -> s * Constants.SUM_OF_PROBABILITIES / sum).toArray();
        return unscaledWeights;
    }


    // This method generates a list of random weights. This sum does not add up to Constants.SUM_OF_PROBABILITIES
    // However, when a Heuristic Object is constructed using these weights, the weights will be automatically adjusted
    // in the Heuristic constructed to scaled the weights so that they add up to Constants.SUM_OF_PROBABILITIES.
    public static double[] getRandomWeights(int numberOfWeights) {
        double[] weights = new double[numberOfWeights];

        Random rand = new Random();

        for (int i = 0; i < weights.length; i++) {
            weights[i] = rand.nextInt(1000);
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

    // Contains method cause stupid Java couldn't have provided one
    public static boolean contains (int[] arr, int toFind) {
        for (int elem: arr) {
            if (elem == toFind) {
                return true;
            }
        }

        return false;
    }

}
