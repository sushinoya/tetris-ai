package Tetris;

import Tetris.Helper.Helper;
import java.util.Random;

public class SimulatedAnnealing {

    private double score;
    private int iteration;
    private Random random;

    public SimulatedAnnealing() {
        score = 0;
        iteration = 0;
        random = new Random();
    }

    public void run() {
        score = PlayerSkeleton.runGameWithHeuristic(getHeuristic());
        System.out.println(score);
    }

    public Heuristic getHeuristic() {
        double initialTemperature = calculateInitialTemperature();
        double temperature = initialTemperature;
        Heuristic heuristic  = new Heuristic(8.15, 2.31, 50.41, 11.44, 30.79);
        while (true) {
            if (temperature < 1) {
                System.out.println("Cooled down! The result is obtained.");
                return heuristic;
            }

            Heuristic newHeuristic = getNeighbourHeuristic(heuristic);
            double averageScoreWithOldHeuristic = getAverageScore(heuristic, 5);
            double averageScoreWithNewHeuristic = getAverageScore(newHeuristic, 5);
            double improvementFromOlderHeuristic = averageScoreWithNewHeuristic - averageScoreWithOldHeuristic;
            if (isAccepted(temperature, improvementFromOlderHeuristic)) {
                heuristic = newHeuristic;
            }

            temperature = scheduleNewTemperature(initialTemperature, iteration);
            System.out.println(temperature);
            System.out.println(heuristic);
            iteration++;
        }
    }

    public double calculateInitialTemperature() {
        return 500;
    }

    public Heuristic getNeighbourHeuristic(Heuristic heuristic) {
        Heuristic newHeuristic = new Heuristic(heuristic.weights);
        double valueChange = random.nextDouble() * 5;
        double sum = Helper.sum(heuristic.weights) + valueChange;
        // The index indicates which weight is changed
        int index = random.nextInt(5);

        newHeuristic.weights[index] += valueChange;

        for (int i = 0; i < Constants.NUMBER_OF_FEATURES; i++) {
            newHeuristic.weights[i] = newHeuristic.weights[i] * 100 / sum;
        }

        return newHeuristic;
    }

    public boolean isAccepted(double temperature, double improvementFromOlderHeuristic) {
        double acceptanceProbability = getAcceptanceProbability(temperature, improvementFromOlderHeuristic);
        if (acceptanceProbability >= random.nextDouble()) {
            System.out.println("This is called");
            return true;
        }
        return false;
    }

    public double getAcceptanceProbability(double temperature, double improvementFromOlderHeuristic) {
        if (improvementFromOlderHeuristic > 0) {
            return 1.0;
        } else {
            return Math.exp((improvementFromOlderHeuristic) / temperature);
        }
    }

    public double scheduleNewTemperature(double initialTemperature, int iteration) {
        double newTemperature = initialTemperature / (1 + Math.log(1 + iteration));
        return newTemperature;
    }

    public double getAverageScore(Heuristic heuristic, int rounds) {
        double sum = 0;
        for (int i = 0; i < rounds; i++) {
            sum += PlayerSkeleton.runGameWithHeuristic(heuristic);
        }
        System.out.println("Score: " + sum / rounds);
        return sum / rounds;
    }
}
