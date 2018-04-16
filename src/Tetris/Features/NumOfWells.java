package Tetris.Features;

import Tetris.PotentialNextState;
import Tetris.State;

public class NumOfWells extends Feature {

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
