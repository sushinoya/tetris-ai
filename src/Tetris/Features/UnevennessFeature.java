package Tetris.Features;

import Tetris.PotentialNextState;

public class UnevennessFeature extends Feature {

    @Override
    public double getValue(PotentialNextState state) {
        double result = 0;

        int[] tops = state.getTop();

        for (int i = 0; i < tops.length - 1; i++) {
            result += Math.abs(tops[i] - tops[i + 1]);
        }

        return result;
    }

}
