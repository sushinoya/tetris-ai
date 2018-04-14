package Tetris.Features;

import Tetris.State;
import Tetris.PotentialNextState;

public class AverageHeightFeature extends Feature{

    @Override
    public double getValue(PotentialNextState state) {
        double sumOfHeight = 0;

        int[] tops = state.getTop();
        for (int i = 0; i < tops.length; i++) {
            sumOfHeight += tops[i];
        }

        double average = sumOfHeight / State.COLS;

        return average;
    }

}
