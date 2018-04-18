package Tetris;

import java.io.*;
import java.util.*;
import java.lang.*;

import Tetris.Helper.Tuple;
import Tetris.Helper.Helper;


public class PlayerSkeleton {

    public static double bestScore;
	public static double bestAvgScoreOfHeuristic;
    public static Heuristic bestHeuristic;
	private static BufferedWriter bw;
	public static TFrame frame;


	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves, Heuristic heuristic) {

        double bestHeuristicValue = Integer.MAX_VALUE; // Minimize value
        int bestMove = 0; // Default to first move

        // Pick best move out of possible moves
        for (int i = 0; i < legalMoves.length; i++) {
            PotentialNextState nextState = new PotentialNextState(s);
            nextState.makeMove(legalMoves[i]);
            if (nextState.hasLost()) {
            	continue;
			}
            double currentHeuristicValue = heuristic.getValue(nextState);
            if (currentHeuristicValue < bestHeuristicValue) {
                bestMove = i;
            }
            bestHeuristicValue = Math.min(bestHeuristicValue, currentHeuristicValue);
        }

        return bestMove;
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException{
		if (Constants.DRAW_ENABLED) {
			frame = new TFrame(new State());
		}

        openBuffer();

		if (Constants.IS_GENETIC_RUNNING == true) {
			geneticFunction();
		} else {
			SimulatedAnnealing sa = new SimulatedAnnealing();
			sa.run();
		}

	}

	public static String pickLogFile () {
		if (Constants.NUMBER_OF_FEATURES == 4) {
			return Constants.AVERAGE_LOG_FOR_4HEURISTICS;
		} else {
			return Constants.AVERAGE_LOG_FOR_5HEURISTICS;
		}
	}

	public static ArrayList<Heuristic> getHeuristicsForGeneticFunction()  throws IOException, ClassNotFoundException {

		if (Constants.READ_POPULATION_FROM_FILE) {
			FileInputStream fileInputStream = new FileInputStream("data.txt");
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

			ArrayList<Heuristic> population = (ArrayList<Heuristic>) objectInputStream.readObject();
			if(population.size() < Constants.NUMBER_OF_HEURISTICS) { //if not enough from file, make some random heuristics and add on to it
				ArrayList<Heuristic> additionalPopulation =
						Helper.getRandomHeuristics(Constants.NUMBER_OF_HEURISTICS - population.size());
				population.addAll(additionalPopulation);
			} else if (population.size() > Constants.NUMBER_OF_HEURISTICS) { //if too much from file, take the number of heuristics we need from the file
				return (ArrayList<Heuristic>) population.subList(0, Constants.NUMBER_OF_HEURISTICS);
			}

			return population;
		} else {
			return generateRandomHeuristics();
		}
	}

	public static ObjectOutputStream openObjOutputStream() throws IOException {
		FileOutputStream fileOutputStream
				= new FileOutputStream("data.txt");
		ObjectOutputStream objectOutputStream
				= new ObjectOutputStream(fileOutputStream);
		return objectOutputStream;
	}

	public static ArrayList<Heuristic> generateRandomHeuristics() {
		ArrayList<Heuristic> population = Helper.getRandomHeuristics(Constants.NUMBER_OF_HEURISTICS);
		return population;
	}

	// Simulates the replacement of the population by its member's descendants
	public static void geneticFunction()  throws IOException, ClassNotFoundException {
	    int gen = 0;

	    //TODO: I PUT THIS AS TODO COS IT WILL CHANGE THE COLOUR. SO READ THE FOLLOWING OKAY!!!!!!!!
		// Constants.READ_POPULATION_FROM_FILE decides if you want to read your population from the file data.txt
		// or you want to start from scratch.

		ArrayList<Heuristic> population = getHeuristicsForGeneticFunction();

		for (int i = 0; i < Constants.NUMBER_OF_GENERATIONS; i++) {
            System.out.println("\nCollecting score for generation " + gen + "..." );

            HashMap<Heuristic, Integer> populationWithAverageScores = getPopulationScores(population);
			ObjectOutputStream objectOutputStream = openObjOutputStream();
            System.out.println("Done collecting for generation " + gen + ".\n" );

			double populationAverage = Helper.sum(populationWithAverageScores.values()) / Constants.NUMBER_OF_HEURISTICS;

            System.out.println("Generation " + gen + " Average Score: " + populationAverage);
            gen++;

            population = generateNextGeneration(populationWithAverageScores);
			objectOutputStream.writeObject(population);
			objectOutputStream.flush();
		}
	}


	// Creates NUMBER_OF_HEURISTICS new children from the current population and returns this new population.
	// The probability of two heuristics procreating is proportional to the average score they generated.
	public static ArrayList<Heuristic> generateNextGeneration(HashMap<Heuristic, Integer> populationWithScores) {
		ArrayList<Heuristic> newPopulation = new ArrayList<Heuristic>();
		Tuple<ArrayList<Heuristic>, ArrayList<Integer>> heuristicsAndIntervals = generateProbabilityIntervalList(populationWithScores);

		long numberOfChildrenToGenerate = populationWithScores.size();

		if (Constants.RETAIN_PARENTS) {
			numberOfChildrenToGenerate = Math.round(numberOfChildrenToGenerate * (1 - Constants.FRACTION_OF_RETAINED_PARENTS));

			ArrayList<Heuristic> sortedPopulation = getSortedPopulation(populationWithScores);

			// Retain percentage of fittest in population
			for (int i = 0; i < Constants.FRACTION_OF_RETAINED_PARENTS * Constants.NUMBER_OF_HEURISTICS - 1; i++) {
				newPopulation.add(sortedPopulation.get(i));
			}
		}


        if(Constants.TOURNAMENT_SELECTION) {
            double sampleSize = Constants.TOURNAMENT_SELECT_SAMPLE_PERCENTAGE * Constants.NUMBER_OF_HEURISTICS;
            double numberOfElitistChildrenToGenerate = Math.round(Constants.NUMBER_OF_HEURISTICS
                    * Constants.TOURNAMENT_SELECT_CHILD_PERCENTAGE_TO_GENERATE);

            numberOfChildrenToGenerate -= numberOfElitistChildrenToGenerate;

            for(int i = 0; i < numberOfElitistChildrenToGenerate; i++) {
                ArrayList<Heuristic> sortedSamplePopulation
                        = getSortedPopulation(getRandomSamplePopulation(populationWithScores, sampleSize));

                Heuristic best = sortedSamplePopulation.get(0);
                Heuristic better = sortedSamplePopulation.get(1);

                Tuple<Heuristic, Integer> mother =
                        new Tuple<Heuristic, Integer>(best, populationWithScores.get(best));
                Tuple<Heuristic, Integer> father =
                        new Tuple<Heuristic, Integer>(better, populationWithScores.get(better));

                Heuristic child;
                if (Constants.USE_WEIGHTED_REPRODUCE) {
                    child = weightedReproduce(mother, father);

                } else {
                    child = reproduce(mother, father);
                }

                // Add the elitist child
                newPopulation.add(child);
            }
        }

		for (int i = 0; i < numberOfChildrenToGenerate; i++) {
			Tuple<Heuristic, Integer> mother = randomSelect(populationWithScores, heuristicsAndIntervals);
			Tuple<Heuristic, Integer> father = randomSelect(populationWithScores, heuristicsAndIntervals);

			if (!Constants.ALLOW_MASTURBATION_REPRODUCTION) {
				while (mother.getFirst().equals(father.getFirst())) {
					father = randomSelect(populationWithScores, heuristicsAndIntervals);
				}
			}


			Heuristic child;
			if (Constants.USE_WEIGHTED_REPRODUCE) {
				child = weightedReproduce(mother, father);

			} else {
				child = reproduce(mother, father);
			}

			// Mutate child
			child = mutate(child);

			// Add lines to mimic random mutation
			newPopulation.add(child);
		}

		return newPopulation;
	}


	public static Heuristic mutate(Heuristic heuristic) {

		for (int i = 0; i < Constants.NUMBER_OF_FEATURES; i++) {
			boolean shouldMutate = new Random().nextDouble() < Constants.PROBABILITY_OF_MUTATION;
			boolean shouldAddNotSubtract = new Random().nextDouble() < 0.5;
			double changeBy = new Random().nextDouble() * Constants.MAX_MUTATION_CHANGE;

			if (shouldMutate) {
				if (shouldAddNotSubtract) {
					heuristic.weights[i] += heuristic.weights[i] * changeBy;
				} else {
					heuristic.weights[i] -= heuristic.weights[i] * changeBy;
				}
			}
		}
		return new Heuristic(heuristic.weights);
	}


	public static HashMap<Heuristic, Integer> getPopulationScores(ArrayList<Heuristic> population) {
		HashMap<Heuristic, Integer> averageScores = new HashMap<>();

		// Run every heuristic NUMBER_OF_GAMES times and store the average score
		for (Heuristic heuristic : population) {
			Integer averageScore = 0;
            int scoreForOneRound = 0;
			double[] scores = new double[Constants.NUMBER_OF_GAMES];

			for (int i = 0; i < Constants.NUMBER_OF_GAMES; i++) {
                scoreForOneRound = runGameWithHeuristic(heuristic);
			    averageScore += scoreForOneRound;
				scores[i] = scoreForOneRound;
			}

			averageScore /= Constants.NUMBER_OF_GAMES;

			if (averageScore > bestAvgScoreOfHeuristic) {
				bestAvgScoreOfHeuristic = averageScore;
				System.out.println("New Best Average score: " + bestAvgScoreOfHeuristic + " Weights: "
                       + heuristic + " with S.D. of " + Helper.round(Helper.calculateSD(scores), 2));
			}

			if(averageScore > 1000) {
				writeBuffer(heuristic, averageScore, Helper.calculateSD(scores));
			}
			averageScores.put(heuristic, averageScore);

			flushBuffer();
		}

		return averageScores;
	}


    // Gets a random sample of the population given the sample size
	public static ArrayList<Map.Entry<Heuristic, Integer>> getRandomSamplePopulation(HashMap<Heuristic, Integer> population, double sampleSize) {

	    Random rand = new Random();
	    ArrayList<Map.Entry<Heuristic, Integer>> populationList = new ArrayList<Map.Entry<Heuristic, Integer>>(population.entrySet());

	    ArrayList<Map.Entry<Heuristic, Integer>> randomSamplePopulationList =
                new ArrayList<Map.Entry<Heuristic, Integer>>();

        for(int i = 0; i < sampleSize; i++) {
            randomSamplePopulationList.add(populationList.get(rand.nextInt(populationList.size() - 1)));
        }

        return randomSamplePopulationList;
    }


	// Sort population based on score of individual
	public static ArrayList<Heuristic> getSortedPopulation(HashMap<Heuristic, Integer> population) {

		ArrayList<Map.Entry<Heuristic, Integer>> populationList = new ArrayList<Map.Entry<Heuristic, Integer>>(population.entrySet());

		return getSortedPopulation(populationList);

	}

    //Overload
	public static ArrayList<Heuristic> getSortedPopulation(ArrayList<Map.Entry<Heuristic, Integer>> populationList){

        Collections.sort(populationList, new Comparator<Map.Entry<Heuristic, Integer>>() {
            @Override
            public int compare(Map.Entry<Heuristic, Integer> o1, Map.Entry<Heuristic, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        ArrayList<Heuristic> sortedPopulation = new ArrayList<Heuristic>();
        for (Map.Entry<Heuristic, Integer> individual : populationList) {
            sortedPopulation.add(individual.getKey());
        }

        return sortedPopulation;

    }

	public static void openBuffer() {
        try {
            bw = new BufferedWriter(new FileWriter(pickLogFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public static void writeBuffer(Heuristic heuristic, Integer averageScore, double sd) {
        try {
            bw.write(heuristic.toString() + ", with score of " + averageScore + ", S.D. of " + Helper.round(sd, 2));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void flushBuffer() {
        try {
            bw.flush();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

	public static int runGameWithHeuristic(Heuristic heuristic) {
		State s = new State();
		PlayerSkeleton p = new PlayerSkeleton();

		if (Constants.DRAW_ENABLED) {
			frame.bindState(s);
		}

		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s, s.legalMoves(), heuristic));

			if (Constants.DRAW_ENABLED) {
				s.draw();
				s.drawNext(0,0);
				try {
					Thread.sleep(Constants.WAITING_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

        if (s.getRowsCleared() > bestScore) {
            bestScore = s.getRowsCleared();
			bestHeuristic = heuristic;
            System.out.println("New Best score: " + bestScore + " Weights: " + bestHeuristic);
		}

		return s.getRowsCleared();
	}


	public static Tuple<ArrayList<Heuristic>, ArrayList<Integer>> generateProbabilityIntervalList(HashMap<Heuristic, Integer> populationWithScores) {

		ArrayList<Heuristic> heuristicsList = new ArrayList<Heuristic>(populationWithScores.keySet());
        ArrayList<Integer> intervalList = new ArrayList<Integer>(heuristicsList.size());


        for (int i = 0; i < Constants.NUMBER_OF_HEURISTICS; i++) {
            intervalList.add(null);
        }

		intervalList.set(0, populationWithScores.get(heuristicsList.get(0)));

		for (int k = 1; k < heuristicsList.size(); k ++) {
			intervalList.set(k, intervalList.get(k - 1) + populationWithScores.get(heuristicsList.get(k)));
		}
		return new Tuple<>(heuristicsList, intervalList);
	}


	public static Tuple<Heuristic, Integer> randomSelect(HashMap<Heuristic, Integer> populationWithScores, Tuple<ArrayList<Heuristic>, ArrayList<Integer>> heuristicsAndIntervals) {
		double sumOfScores = Helper.sum(populationWithScores.values());

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
		double[] motherWeights = mother.getFirst().weights;
		double[] fatherWeights = father.getFirst().weights;

		if (Constants.REPRODUCE_PROPORTIONATELY) {
			numOfWeightsFromMother = (int) Math.round(scoreRatio * Constants.NUMBER_OF_FEATURES);
		} else {
			numOfWeightsFromMother = Constants.NUMBER_OF_FEATURES / 2;
		}

		int[] weightIndexesFromMother = Helper.generateRandomIndices(numOfWeightsFromMother, Constants.NUMBER_OF_FEATURES);

		double[] childWeights = new double[Constants.NUMBER_OF_FEATURES];

		for (int i = 0; i < Constants.NUMBER_OF_FEATURES; i++) {
			if (Helper.contains(weightIndexesFromMother, i)) {
				childWeights[i] = motherWeights[i];
			} else {
				childWeights[i] = fatherWeights[i];
			}
		}

		return new Heuristic(childWeights);
	}


	public static Heuristic weightedReproduce(Tuple<Heuristic, Integer> mother, Tuple<Heuristic, Integer> father) {
		double[] motherWeights = mother.getFirst().weights;
		double[] fatherWeights = father.getFirst().weights;
		double motherScore = mother.getSecond();
		double fatherScore = father.getSecond();


		double[] childWeights = new double[Constants.NUMBER_OF_FEATURES];

		for (int i = 0; i < Constants.NUMBER_OF_FEATURES; i++) {
			childWeights[i] = (motherWeights[i] * motherScore + fatherWeights[i] * fatherScore);
		}

		// Normalisation happens when a Heuristic is constructed.
		return new Heuristic(childWeights);
	}
}