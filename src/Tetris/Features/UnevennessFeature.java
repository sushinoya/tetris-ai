package Tetris.Features;

import Tetris.State;

public class UnevennessFeature extends Feature {

    @Override
    public double getValue(State state) {
        double result = 0;

        int[] tops = state.getTop();

        for (int i = 0; i < tops.length - 1; i++) {
            result += Math.abs(tops[i] - tops[i + 1]);
        }

        return result;
    }

}
