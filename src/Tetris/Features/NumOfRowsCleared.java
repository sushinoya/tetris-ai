package Tetris.Features;

import Tetris.PotentialNextState;

public class NumOfRowsCleared extends Feature {

    @Override
    public double getValue(PotentialNextState state) {
        return (double)state.getCleared();
    }

}
