package Tetris;

import Tetris.Features.*;
import Tetris.Helper.*;

import java.util.Random;

//this class represents the features and their corresponding weights
public class Heuristic {

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
        new NumOfWells()
    };

    public Heuristic() {
        weights = new double[9];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = new Random().nextDouble() * 2 - 1;
        }
    }

    public Heuristic(double... weights) {
        this.weights = weights;
    }

    public double getValue(PotentialNextState state) {
        double normalisingFactor = 0;
        double sum = 0;
        for (int i = 0; i < Constants.NUMBER_OF_FEATURES; i++) {
            double featureValue = features[i].getValue(state);
            sum += featureValue * weights[i];
            normalisingFactor += Math.pow(featureValue, 2);
        }
        normalisingFactor = Math.sqrt(normalisingFactor);
        return sum / normalisingFactor;
    }


    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder("(");
        for (double weight:  this.weights) {
            sb.append(Helper.round(weight, 2) + ", ");
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
