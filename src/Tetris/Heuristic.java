package Tetris;

import Tetris.Features.*;
import Tetris.Helper.*;

import java.io.Serializable;

//this class represents the features and their corresponding weights
public class Heuristic implements Serializable{

    double[] weights;
    public static Feature[] features = {
        new AverageHeightFeature(),
        new MaxHeightFeature(),
        new NumOfHolesFeature(),
        new UnevennessFeature(),
        new NumOfPatchesFeature(),
        new NumOfRowsCleared(),
        new NumOfBlocksAboveHolesFeature(),
        new SumOfDepthOfHoles(),
        new NumberOfWellBlocks(),
        new AggregateHeightFeature()
    };


    // Automatically scales the weights such that their the weights are normalised
    public Heuristic(double... weights) {
        this.weights = Helper.scaleWeights(weights);
    }


    public double getValue(PotentialNextState state) {
        double sum = 0;
        for (int i = 0; i < Constants.NUMBER_OF_FEATURES; i++) {
            sum += features[i].getValue(state) * weights[i];
        }
        return sum;
    }


    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder("(");
        for (double weight:  this.weights) {
            sb.append(Helper.round(weight, 8) + ", ");
        }

        String separatedByCommas = sb.toString();
        return separatedByCommas.substring(0, separatedByCommas.length() - 2) + ")";
    }


    @Override
    public boolean equals(Object obj) {
        Heuristic other = (Heuristic) obj;
        for (int i = 0; i < this.weights.length; i++) {
            if (this.weights[i] != other.weights[i]) {
                return false;
            }
        }
        return true;
    }
}
