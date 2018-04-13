package Tetris.Features;

import Tetris.State;
import Tetris.PotentialNextState;

public class NumOfHolesFeature extends Feature {

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
