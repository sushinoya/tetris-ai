package Tetris.Features;

import Tetris.PotentialNextState;
import Tetris.State;

public class SumOfDepthOfHoles extends Feature {

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
