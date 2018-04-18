package Tetris.Features;

import Tetris.PotentialNextState;

public class AggregateHeightFeature extends Feature{

    @Override
    public double getValue(PotentialNextState state) {
        double sumOfHeight = 0;

        int[] tops = state.getTop();
        for (int i = 0; i < tops.length; i++) {
            sumOfHeight += tops[i];
        }

        return sumOfHeight;
    }

}
