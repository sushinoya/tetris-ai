package Tetris.Features;

import Tetris.State;

public class NumOfHolesFeature extends Feature {

    @Override
    public double getValue(State state) {
        int count = 0;
        int[][] field = state.getField();
        int[] heights = state.getTop();
        for (int col = 0; col < state.COLS; col++) {
            for (int row = heights[col] - 1; row >= 0; row--) {
                if (field[row][col] == 0) {
                    count++;
                }
            }
        }

        return count;
    }

}
