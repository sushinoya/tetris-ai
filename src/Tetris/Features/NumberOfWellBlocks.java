package Tetris.Features;

import Tetris.PotentialNextState;
import Tetris.State;

public class NumberOfWellBlocks extends Feature {

    @Override
    public double getValue(PotentialNextState s) {
        int numOfWellBlocks = 0;
        int[][] field = s.getField();
        for (int i = 0;  i < State.COLS;  i++) {
            for (int j = State.ROWS - 1;  j >= 0;  j--) {
                if (isEmpty(field, i, j) && leftNeighbourNotEmpty(field, i, j) && rightNeighbourNotEmpty(field, i, j)) {
                    numOfWellBlocks += scaleHeight(j - s.getTop()[i] + 1);
                } else {
                    break;
                }
            }
        }
        return numOfWellBlocks;
    }

    public boolean rightNeighbourNotEmpty(int[][] field, int i, int j) {
        return i == State.COLS - 1 || field[j][i + 1] != 0;
    }

    public boolean leftNeighbourNotEmpty(int[][] field, int i, int j) {
        return i == 0 || field[j][i - 1] != 0;
    }

    public boolean isEmpty(int[][] field, int i, int j) {
        return field[j][i] == 0;
    }

    public int scaleHeight(int wellDepth) {
        return wellDepth * (wellDepth + 1) / 2;
    }


}
