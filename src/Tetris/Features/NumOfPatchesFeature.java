package Tetris.Features;

import Tetris.PotentialNextState;
import Tetris.State;
import Tetris.Helper.UFDS;

public class NumOfPatchesFeature extends Feature {

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
