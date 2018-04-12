package Tetris.Features;

import Tetris.State;

public class MaxHeightFeature extends Feature {

    @Override
    public double getValue(State state) {
        double max = 0;

        int[] tops = state.getTop();

        for (int i = 0; i < tops.length; i++) {
            max = Math.max(max, tops[i]);
        }

        return max;
    }

}
