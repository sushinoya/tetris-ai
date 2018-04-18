package Tetris.Features;

import Tetris.PotentialNextState;

import java.io.Serializable;

public abstract class Feature implements Serializable{

    public abstract double getValue(PotentialNextState state);

}
