package Tetris.Features;

import Tetris.StateAfterMove;

public abstract class Feature {
    public abstract double getValue(StateAfterMove s);
}
