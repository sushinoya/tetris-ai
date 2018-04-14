package Tetris.Features;

import Tetris.PotentialNextState;

public class AltitudeDeltaFeature extends Feature{

    @Override
    public double getValue(PotentialNextState state) {
        double max = 0;
        double min = Double.MAX_VALUE;

        int[] tops = state.getTop();

        for (int i = 0; i < tops.length; i++) {
            max = Math.max(max, tops[i]);
            min = Math.min(min, tops[i]);
        }

        return max - min;
    }

}
