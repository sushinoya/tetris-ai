package Tetris;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.lang.*;
import java.util.concurrent.ThreadLocalRandom;



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

		while (true) {
			System.out.println(runGameWithHeuristic(new Heuristic(0.1626939, 0.75036857, 0.05860699, 0.0, 0.0, 0.63509098, 3.0921E-4, 0.06080744, 0.00217679)));
		}

		/*

		*/

		/*
        openBuffer();

		if (Constants.IS_GENETIC_RUNNING == true) {
			geneticFunction();
		} else {
			SimulatedAnnealing sa = new SimulatedAnnealing();
			sa.run();
		}
		*/

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
			for (int i = 0; i < Constants.FRACTION_OF_RETAINED_PARENTS * Constants.NUMBER_OF_HEURISTICS; i++) {
				newPopulation.add(sortedPopulation.get(i));
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

	// Sort population based on score of individual
	public static ArrayList<Heuristic> getSortedPopulation(HashMap<Heuristic, Integer> population) {

		ArrayList<Map.Entry<Heuristic, Integer>> populationList = new ArrayList<Map.Entry<Heuristic, Integer>>(population.entrySet());

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

abstract class Feature implements Serializable{
	public abstract double getValue(PotentialNextState state);
}

class AggregateHeightFeature extends Feature {
	@Override
	public double getValue(PotentialNextState state) {
		double sumOfHeight = 0;

		int[] tops = state.getTop();
		for (int i = 0; i < tops.length; i++) {
			sumOfHeight += tops[i];
		}

		return sumOfHeight;
	}
}

class MaxHeightFeature extends Feature {

	@Override
	public double getValue(PotentialNextState state) {
		double max = 0;

		int[] tops = state.getTop();

		for (int i = 0; i < tops.length; i++) {
			max = Math.max(max, tops[i]);
		}

		return max;
	}
}

class NumOfBlocksAboveHolesFeature extends Feature {

	@Override
	public double getValue(PotentialNextState state) {
		int count = 0;
		boolean isHoleExist = false;
		int[][] field = state.getField();
		int[] tops = state.getTop();

		for (int col = 0; col < State.COLS; col++) {
			for (int row = 0; row < tops[col]; row++) {
				if (field[row][col] == 0) {
					isHoleExist = true;
				}
				if (isHoleExist && field[row][col] == 1) {
					count++;
				}
			}
			isHoleExist = false;
		}

		return count;
	}
}

class NumOfHolesFeature extends Feature {

	@Override
	public double getValue(PotentialNextState state) {
		int count = 0;
		int[][] field = state.getField();
		int[] heights = state.getTop();
		for (int col = 0; col < State.COLS; col++) {
			for (int row = heights[col] - 1; row >= 0; row--) {
				if (field[row][col] == 0) {
					count++;
				}
			}
		}

		return count;
	}
}

class NumOfPatchesFeature extends Feature {

	@Override
	public double getValue(PotentialNextState state) {

		int[][] field = state.getField();
		UFDS patches = new UFDS(State.COLS * State.ROWS);

		//Set initial number of patches to the number of holes
		patches.numSets = new NumOfHolesFeature().getValue(state);

		// Construct UFDS
		for (int row = 0; row < State.ROWS; row++) {
			for (int col = 0; col < State.COLS; col++) {

				int cellNumber = cellNumber(row, col);

				if (field[row][col] == 0 && isRightNeighbourEmpty(field, row, col)) {
					int neighbourCellNumber = cellNumber(row, col + 1);
					patches.union(cellNumber, neighbourCellNumber);
				}

				if (field[row][col] == 0 && isBottomNeighbourEmpty(field, row, col)) {
					int neighbourCellNumber = cellNumber(row + 1, col);
					patches.union(cellNumber, neighbourCellNumber);
				}
			}
		}

		return patches.numSets;
	}

	public boolean isRightNeighbourEmpty(int[][] field, int row, int col) {
		return col + 1 < State.COLS && field[row][col + 1] == 0;
	}

	public boolean isBottomNeighbourEmpty(int[][] field, int row, int col) {
		return row + 1 < State.ROWS && field[row + 1][col] == 0;
	}

	public int cellNumber(int row, int col) {
		return row * State.COLS + col;
	}

	public void print() {
		for (int row = 0; row < State.ROWS; row++) {
			for (int col = 0; col < State.COLS; col++) {
				System.out.print(cellNumber(row, col) + ",");
			}
			System.out.print("\n");
		}
	}
}

class NumOfRowsCleared extends Feature {

	@Override
	public double getValue(PotentialNextState state) {
		return -(double)state.getCleared();
	}

}

class NumOfWells extends Feature {

	@Override
	public double getValue(PotentialNextState s) {

		int[][] field = s.getField();
		int[] top = s.getTop();
		int wellSum = 0;
		for (int i = 0;  i < State.COLS;  i++) {
			for (int j = State.ROWS - 1;  j >= 0;  j--) {
				if (field[j][i] == 0) {
					if (i == 0 || field[j][i - 1] != 0) {
						if (i == State.COLS - 1 || field[j][i + 1] != 0) {
							int wellHeight = j - top[i] + 1;
							wellSum += wellHeight * (wellHeight + 1) / 2;
						}
					}
				} else {
					break;
				}
			}
		}
		return wellSum;

	}

}

class SumOfDepthOfHoles extends Feature {

	@Override
	public double getValue(PotentialNextState state) {
		int sumOfDepthOfHoles = 0;
		int[][] field = state.getField();
		int[] tops = state.getTop();

		for (int col = 0; col < State.COLS; col++) {
			for (int row = 0; row < tops[col]; row++) {
				if (field[row][col] == 0) {
					sumOfDepthOfHoles += tops[col] - row;
				}
			}
		}

		return sumOfDepthOfHoles;
	}

}

class UnevennessFeature extends Feature {

	@Override
	public double getValue(PotentialNextState state) {
		double result = 0;

		int[] tops = state.getTop();

		for (int i = 0; i < tops.length - 1; i++) {
			result += Math.abs(tops[i] - tops[i + 1]);
		}

		return result;
	}

}

class Heuristic implements Serializable{

	double[] weights;
	public static Feature[] features = {
			new MaxHeightFeature(),
			new NumOfHolesFeature(),
			new UnevennessFeature(),
			new NumOfPatchesFeature(),
			new NumOfRowsCleared(),
			new NumOfBlocksAboveHolesFeature(),
			new SumOfDepthOfHoles(),
			new NumOfWells(),
			new AggregateHeightFeature()
	};


	// Automatically scales the weights such that their the weights are normalised
	public Heuristic(double... weights) {
		this.weights = Helper.scaleWeights(weights);
	}


	public double getValue(PotentialNextState state) {
		double sum = 0;
		for (int i = 0; i < Constants.NUMBER_OF_FEATURES; i++) {
			sum += features[i].getValue(state) * weights[i];
		}
		return sum;
	}


	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder("(");
		for (double weight:  this.weights) {
			sb.append(Helper.round(weight, 2) + ", ");
		}

		String separatedByCommas = sb.toString();
		return separatedByCommas.substring(0, separatedByCommas.length() - 2) + ")";
	}


	@Override
	public boolean equals(Object obj) {
		Heuristic other = (Heuristic) obj;
		for (int i = 0; i < this.weights.length; i++) {
			if (this.weights[i] != other.weights[i]) {
				return false;
			}
		}
		return true;
	}
}

class Helper {

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
		double[] normalised = Arrays.stream(unscaledWeights).map(x -> x / normalisingFactor).toArray();

		return normalised;
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

			if (Constants.USE_ZERO_INITIAL_POPULATON) {
				weights[i] = 0;
			} else {
				weights[i] = new Random().nextDouble();
			}

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
	public static double sum(double[] arr) {
		double sum = 0;
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

	public static double calculateSD(double numArray[]) {
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

class Tuple<First,Second> {

	private final First first;
	private final Second second;

	public Tuple(First first, Second second) {
		this.first = first;
		this.second = second;
	}

	public First getFirst() { return first; }
	public Second getSecond() { return second; }

	@Override
	public int hashCode() { return first.hashCode() ^ second.hashCode(); }

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Tuple)) return false;
		Tuple pairo = (Tuple) o;
		return this.first.equals(pairo.getFirst()) &&
				this.second.equals(pairo.getSecond());
	}

}

class UFDS {
	int[] rank, parent;
	int n;
	public double numSets;

	public UFDS(int n) {
		rank = new int[n];
		parent = new int[n];
		this.n = n;
		this.numSets = n;
		makeSet();
	}

	public void makeSet() {
		for (int i=0; i<n; i++) {
			parent[i] = i;
		}
	}

	public int find(int x) {
		if (parent[x]!=x) {
			parent[x] = find(parent[x]);
		}
		return parent[x];
	}

	public void union(int x, int y) {
		int xRoot = find(x), yRoot = find(y);
		if (xRoot == yRoot)
			return;

		if (rank[xRoot] < rank[yRoot]) {
			parent[xRoot] = yRoot;
		} else if (rank[yRoot] < rank[xRoot]) {
			parent[yRoot] = xRoot;
		} else {
			parent[yRoot] = xRoot;
			rank[xRoot] = rank[xRoot] + 1;
		}

		this.numSets--;
	}
}

class PotentialNextState {

	// Copy from State
	private int[][][] pBottom;
	private int[][] pHeight;
	private int[][] pWidth;
	private int[][][] pTop;

	public boolean lost = false;

	private int turn;
	private int cleared;

	private int[][] field = new int[State.ROWS][State.COLS];
	private int[] top = new int[State.COLS];

	//number of next piece
	protected int nextPiece;

	public PotentialNextState(State originalState) {
		pBottom = State.getpBottom();
		pHeight = State.getpHeight();
		pWidth = State.getpWidth();
		pTop = State.getpTop();

		setField(originalState.getField());
		setTop(originalState.getTop());

		nextPiece = originalState.getNextPiece();
		turn = originalState.getTurnNumber();
	}

	private void setField(int[][] newField) {
		for (int i = 0; i < State.ROWS; i++) {
			for (int j = 0; j < State.COLS; j++) {
				field[i][j] = newField[i][j];
			}
		}
	}

	private void setTop(int[] newTop) {
		for (int i = 0; i < State.COLS; i++) {
			top[i] = newTop[i];
		}
	}

	public int[][] getField() {
		return field;
	}

	public int[] getTop() {
		return top;
	}

	public boolean hasLost() {
		return lost;
	}

	public int getTurnNumber() {
		return turn;
	}

	//make a move based on an array of orient and slot
	public void makeMove(int[] move) {
		makeMove(move[State.ORIENT],move[State.SLOT]);
	}

	//returns false if you lose - true otherwise
	public boolean makeMove(int orient, int slot) {
		turn++;
		//height if the first column makes contact
		int height = top[slot]-pBottom[nextPiece][orient][0];
		//for each column beyond the first in the piece
		for(int c = 1; c < pWidth[nextPiece][orient];c++) {
			height = Math.max(height,top[slot+c]-pBottom[nextPiece][orient][c]);
		}

		//check if game ended
		if(height+pHeight[nextPiece][orient] >= State.ROWS) {
			lost = true;
			return false;
		}

		//for each column in the piece - fill in the appropriate blocks
		for(int i = 0; i < pWidth[nextPiece][orient]; i++) {

			//from bottom to top of brick
			for(int h = height+pBottom[nextPiece][orient][i]; h < height+pTop[nextPiece][orient][i]; h++) {
				field[h][i+slot] = turn;
			}
		}

		//adjust top
		for(int c = 0; c < pWidth[nextPiece][orient]; c++) {
			top[slot+c]=height+pTop[nextPiece][orient][c];
		}

		int rowsCleared = 0;

		//check for full rows - starting at the top
		for(int r = height+pHeight[nextPiece][orient]-1; r >= height; r--) {
			//check all columns in the row
			boolean full = true;
			for(int c = 0; c < State.COLS; c++) {
				if(field[r][c] == 0) {
					full = false;
					break;
				}
			}
			//if the row was full - remove it and slide above stuff down
			if(full) {
				rowsCleared++;
				cleared++;
				//for each column
				for(int c = 0; c < State.COLS; c++) {

					//slide down all bricks
					for(int i = r; i < top[c]; i++) {
						field[i][c] = field[i+1][c];
					}
					//lower the top
					top[c]--;
					while(top[c]>=1 && field[top[c]-1][c]==0)	top[c]--;
				}
			}
		}
		return true;
	}

	public int getCleared() {
		return cleared;
	}

}

final class Constants {

	public final static int NUMBER_OF_GAMES = 5;
	public final static int NUMBER_OF_HEURISTICS = 500;
	public final static int SUM_OF_PROBABILITIES = 100;
	public final static int NUMBER_OF_GENERATIONS = 100;
	public final static double PROBABILITY_OF_MUTATION = 0.05;
	public final static double MAX_MUTATION_CHANGE = 0.2;
	public final static int NUMBER_OF_FEATURES = Heuristic.features.length;
	public final static boolean DRAW_ENABLED = false;
	public final static boolean ALLOW_MASTURBATION_REPRODUCTION = false;
	public final static boolean RETAIN_PARENTS = true;
	public final static double FRACTION_OF_RETAINED_PARENTS = 0.3;
	public final static boolean REPRODUCE_PROPORTIONATELY = false;
	public final static boolean USE_WEIGHTED_REPRODUCE = false;
	public final static boolean USE_ZERO_INITIAL_POPULATON = false;
	public final static int WAITING_TIME = 1;
	public final static boolean IS_GENETIC_RUNNING = true;
	public final static boolean READ_POPULATION_FROM_FILE = false;
	public final static String AVERAGE_LOG_FOR_4HEURISTICS = "best_4_averages.txt";
	public final static String AVERAGE_LOG_FOR_5HEURISTICS = "best_5_averages.txt";

}

class SimulatedAnnealing {

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
