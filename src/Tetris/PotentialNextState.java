package Tetris;

public class PotentialNextState {

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

}
