package Tetris;

import Tetris.Helper.Helper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class SimulatedAnnealing {

    private double score;
    private double bestAverage;
    private int iteration;
    private Random random;
    private BufferedWriter bw;

    public SimulatedAnnealing() throws IOException {
        score = 0;
        bestAverage = 0;
        iteration = 0;
        random = new Random();
        bw = new BufferedWriter(new FileWriter("good_Heuristics.txt"));
    }

    public void run() throws IOException {
        score = PlayerSkeleton.runGameWithHeuristic(getHeuristic());
        System.out.println(score);
    }

    public Heuristic getHeuristic() throws IOException {
        double initialTemperature = calculateInitialTemperature();
        double temperature = initialTemperature;
        Heuristic heuristic  = new Heuristic(0.03, 0.75, 0.09, 0.000000001, 0.0000001, 0.65, 3.0848E-4, 0.06, 0.00000001);
        while (true) {
            if (temperature < 1) {
                System.out.println("Cooled down! The result is obtained.");
                return heuristic;
            }

            Heuristic newHeuristic = getNeighbourHeuristic(heuristic);
            double averageScoreWithOldHeuristic = getAverageScore(heuristic, 10);
            double averageScoreWithNewHeuristic = getAverageScore(newHeuristic, 10);
            double improvementFromOlderHeuristic = averageScoreWithNewHeuristic - averageScoreWithOldHeuristic;
            if (isAccepted(temperature, improvementFromOlderHeuristic)) {
                heuristic = newHeuristic;
            }

            temperature = scheduleNewTemperature(initialTemperature, iteration);
            iteration++;
        }
    }

    public double calculateInitialTemperature() {
        return 50000;
    }

    public Heuristic getNeighbourHeuristic(Heuristic heuristic) {
        Heuristic newHeuristic = new Heuristic(heuristic.weights);
        double valueChange = random.nextDouble() * 2 - 1;
        // The index indicates which weight is changed
        int index = random.nextInt(Constants.NUMBER_OF_FEATURES);

        newHeuristic.weights[index] = newHeuristic.weights[index] * (1 + valueChange);
        newHeuristic.weights[index] = Math.max(0, newHeuristic.weights[index]);
        newHeuristic.weights[index] = Math.min(1, newHeuristic.weights[index]);

        return new Heuristic(newHeuristic.weights);
    }

    public boolean isAccepted(double temperature, double improvementFromOlderHeuristic) {
        double acceptanceProbability = getAcceptanceProbability(temperature, improvementFromOlderHeuristic);
        if (acceptanceProbability >= random.nextDouble()) {
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

    public double getAverageScore(Heuristic heuristic, int rounds) throws IOException {
        double sum = 0;
        for (int i = 0; i < rounds; i++) {
            sum += PlayerSkeleton.runGameWithHeuristic(heuristic);
        }
        if (sum / rounds > bestAverage) {
            bestAverage = sum / rounds;
            System.out.println("New best average score: " + bestAverage + " Weights: " + heuristic);
            bw.write("Best average: " + bestAverage);
            bw.newLine();
            bw.write(heuristic.toString());
            bw.newLine();
            bw.flush();
        }
        return sum / rounds;
    }
}

