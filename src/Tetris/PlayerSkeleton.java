package Tetris;

import java.util.*;

import Tetris.Util.Tuple;
import Tetris.Util.Util;

public class PlayerSkeleton {

	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {
		return 0;
	}

	public static void main(String[] args) {
		geneticFunction();
	}

	// Simulates the replacement of the population by its member's descendants
	public static void geneticFunction() {
		ArrayList<Heuristic> population = Util.getRandomHeuristics(Constants.NUMBER_OF_HEURISTICS);

		for (int i = 0; i < Constants.NUMBER_OF_GENERATIONS; i++) {
			HashMap<Heuristic, Integer> averageScores = getPopulationScores(population);
			population = generateNextGeneration(averageScores);
		}
	}


	// Creates NUMBER_OF_HEURISTICS new children from the current population and returns this new population.
	// The probability of two heuristics procreating is proportional to the average score they generated.
	public static ArrayList<Heuristic> generateNextGeneration(HashMap<Heuristic, Integer> population) {
		ArrayList<Heuristic> newPopulation = new ArrayList<Heuristic>();

		for (int i = 0; i < population.size(); i++) {
			Tuple<Heuristic, Integer> mother = randomSelect(population);
			Tuple<Heuristic, Integer> father = randomSelect(population);
			Heuristic child = reproduce(mother, father);

			// Add lines to mimic random mutation
			newPopulation.add(child);
		}

		return newPopulation;
	}


	public static HashMap<Heuristic, Integer> getPopulationScores(ArrayList<Heuristic> population) {
		HashMap<Heuristic, Integer> averageScores = new HashMap<>();

		// Run every heuristic NUMBER_OF_GAMES times and store the average score
		for (Heuristic heuristic : population) {
			Integer averageScore = 0;
			for (int i = 0; i < Constants.NUMBER_OF_GAMES; i++) {
				averageScore += runGameWithHeuristic(heuristic);
			}
			averageScore /= Constants.NUMBER_OF_GAMES;

			averageScores.put(heuristic, averageScore);
		}

		return averageScores;
	}


	public static int runGameWithHeuristic(Heuristic heuristic) {
		State s = new State();
		new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
		return s.getRowsCleared();
	}



	//ideas from https://softwareengineering.stackexchange.com/questions/150616/return-random-list-item-by-its-weight
	public static Tuple<ArrayList<Heuristic>, ArrayList<Integer>> generateProbabilityIntervalList(HashMap<Heuristic, Integer> populationWithScores) {

		ArrayList<Heuristic> heuristicsList = new ArrayList<Heuristic>(populationWithScores.keySet());
		ArrayList<Integer> intervalList = new ArrayList<Integer>(heuristicsList.size());

		intervalList.set(0, populationWithScores.get(heuristicsList.get(0)));
		for (int k = 1; k < heuristicsList.size(); k ++) {
			intervalList.set(k, intervalList.get(k - 1) + populationWithScores.get(heuristicsList.get(k)));
		}
		return new Tuple<>(heuristicsList, intervalList);
	}

	public static Tuple<Heuristic, Integer> randomSelect(HashMap<Heuristic, Integer> populationWithScores) {
		double sumOfScores = Util.sum(populationWithScores.values());

		Tuple<ArrayList<Heuristic>, ArrayList<Integer>> heuristicsAndIntervals = generateProbabilityIntervalList(populationWithScores);

		ArrayList<Heuristic> heuristicsList= heuristicsAndIntervals.getFirst();
		ArrayList<Integer> intervalsList= heuristicsAndIntervals.getSecond();


		Random rand = new Random();
		Double randomDouble = rand.nextDouble();
		randomDouble = randomDouble * sumOfScores;

		int chosenIndex;

		for (chosenIndex = 0; chosenIndex < intervalsList.size(); chosenIndex++) {
			if (randomDouble - 1 < intervalsList.get(chosenIndex)) {
				break;
			}
		}


		Heuristic chosenHeuristic = heuristicsList.get(chosenIndex);
		Integer chosenHeuristicScore = populationWithScores.get(chosenHeuristic);

		return new Tuple<>(chosenHeuristic, chosenHeuristicScore);
	}


	public static Heuristic reproduce(Tuple<Heuristic, Integer> mother, Tuple<Heuristic, Integer> father) {
		double scoreRatio = mother.getSecond() / father.getSecond();

		int numOfWeightsFromMother = 0;
		ArrayList<Double> motherWeights = mother.getFirst().weights;
		ArrayList<Double> fatherWeights = father.getFirst().weights;

		if (scoreRatio < 1/4) {
			numOfWeightsFromMother = (Constants.NUMBER_OF_FEATURES / 4) + 1;
		} else if (scoreRatio < 3/4) {
			numOfWeightsFromMother = Constants.NUMBER_OF_FEATURES / 2;
		} else {
			numOfWeightsFromMother = (3 * Constants.NUMBER_OF_FEATURES / 4);
		}


		int[] weightIndexesFromMother = Util.generateRandomIndices(numOfWeightsFromMother, Constants.NUMBER_OF_FEATURES);


		double[] childWeights = new double[Constants.NUMBER_OF_FEATURES];

		for (int i = 0; i < Constants.NUMBER_OF_FEATURES; i++) {
			if (Arrays.asList(weightIndexesFromMother).contains((i))) {
				childWeights[i] = motherWeights.get(i);
			} else {
				childWeights[i] = fatherWeights.get(i);
			}
		}

		childWeights = Util.scaleWeights(childWeights);

		return new Heuristic(childWeights);
	}
}
