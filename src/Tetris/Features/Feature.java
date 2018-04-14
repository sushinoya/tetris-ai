package Tetris.Features;

import Tetris.PotentialNextState;

public abstract class Feature {

    public abstract double getValue(PotentialNextState state);

}
