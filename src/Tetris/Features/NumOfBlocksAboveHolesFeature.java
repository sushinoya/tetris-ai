package Tetris.Features;

import Tetris.PotentialNextState;
import Tetris.State;

public class NumOfBlocksAboveHolesFeature extends Feature {

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
