package Tetris.Util;

import Tetris.Constants;
import Tetris.Heuristic;

import java.util.*;

public class Util {

    /* Generates x times of a number between 0(inclusive) and y(non-inclusive) */
    public static int[] generateRandomIndices(int x, int y) {
        int[] randomNums = new int[x];
        Random random = new Random();
        for (int i = 0; i < x; i++) {
            randomNums[i] = random.nextInt(y);
        }
        return randomNums;
    }


    public static double[] scaleWeights(double[] unscaledWeights) {
        int sum = Util.sum(unscaledWeights);
        unscaledWeights = Arrays.stream(unscaledWeights).map(s -> s * Constants.SUM_OF_PROBABILITIES / sum).toArray();
        return unscaledWeights;
    }


    public static double[] getRandomWeights(int numberOfWeights) {
        double sum = Constants.SUM_OF_PROBABILITIES;
        double[] weights = new double[numberOfWeights];

        Random rand = new Random();
        double intermediateSum = 0;

        for (int i = 0; i < weights.length; i++) {
            weights[i] = rand.nextInt(Constants.SUM_OF_PROBABILITIES) + 1;
            intermediateSum += weights[i];
        }

        for (int i = 0; i < weights.length; i++) {
            weights[i] /= intermediateSum;
            weights[i] *= sum;
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

}
