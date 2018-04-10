package Tetris;

import Tetris.Features.Feature;
import Tetris.Features.Heuristic;

import java.util.*;

public class PlayerSkeleton {

	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {
		
		return 0;
	}
	
	public static void main(String[] args) {

	}


	public static int runGameWithHeuristic(Heuristic heuristic) {
		State s = new State();
		new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();
		while(!s.hasLost()) {
			//compare here


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

	//genetic function takes in an arraylist of Heuristics and outputs the best Heuristic
	public static Heuristic geneticFunction(ArrayList<Heuristic> population, State state) {
		for (int j = 0; j < 100; j++) {
			ArrayList<Heuristic> newPopulation = new ArrayList<Heuristic>();
			for (int i = 0; i < population.size(); i++) {
				Heuristic x = randomSelect(population);
				Heuristic y = randomSelect(population);
				Heuristic child = reproduce(x, y);


				// if some shit, then mutate child

				newPopulation.add(child);
			}

			population = newPopulation;

		}
	}


	//ideas from https://softwareengineering.stackexchange.com/questions/150616/return-random-list-item-by-its-weight
	public static ArrayList<Double> generateProbability(ArrayList<Heuristic> population) {
		ArrayList<Double> intervalList = new ArrayList<>(population.size());
		intervalList.set(0, population.get(0).getValue(state));
		for (int k = 1; k < population.size(); k ++) {
			intervalList.set(k, intervalList.get(k - 1) + population.get(k).getValue(state));
		}
		return intervalList;
	}

	public static Heuristic randomSelect(ArrayList<Heuristic> population) {
		double sumWeights = 0;
		for (int i = 0; i < population.size(); i ++) {
			sumWeights += population.get(i).getValue(state);
		}

		Random rand = new Random();
		Double randomDouble = rand.nextDouble(); //this method generates a random number between 0.0 to 1.0
		randomDouble = randomDouble * sumWeights;

		ArrayList<Double> probabilityInterval = generateProbability(population);
		int k;
		for (k = 0; k < probabilityInterval.size(); k++) {
			if (randomDouble - 1 < probabilityInterval.get(k)) {
				break;
			}
		}

		return population.get(k);
	}


	//takes in 2 Heuristics, and return an offspring. keep in mind that there are 4 features at this point.
	public static Heuristic reproduce(Heuristic x, Heuristic y) {
		double totalWeight = x.getValue() + y.getValue();
		double indexToCut_Double = (x.getValue() / totalWeight) * 4;
		int indexToCut_int = (int) Math.round(indexToCut_Double);


		for (int i = 0; i < indexToCut_int; i++) {

		}
	}


}
