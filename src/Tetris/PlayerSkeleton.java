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
	}

	public static void geneticFunction(ArrayList<HashMap<Feature, Double>> population) {
		while (true) {
			ArrayList<HashMap<Feature, Double>> newPopulation = new ArrayList<HashMap<Feature, Double>>();
			for (int i = 0; i < population.size(); i++) {

			}
		}
	}


	public static void generateProbability() {

	}
	public static void randomSelection(ArrayList<Heuristic> population) {

	}


}
