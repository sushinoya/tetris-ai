package Tetris;

import Tetris.Features.*;
import Tetris.Helper.*;

//this class represents the features and their corresponding weights
public class Heuristic {

    double[] weights;

    //these double values represent the weight on each feature
    //the feature represents the features themselves
    double averageHeightWeight;
    public static AverageHeightFeature averageHeightFeature = new AverageHeightFeature();

    double maxHeightWeight;
    public static MaxHeightFeature maxHeightFeature = new MaxHeightFeature();

    double numOfHolesWeight;
    public static NumOfHolesFeature numOfHolesFeature = new NumOfHolesFeature();

    double unevennessWeight;
    public static UnevennessFeature unevennessFeature = new UnevennessFeature();

    double altitudeDeltaWeight;
    public static AltitudeDeltaFeature altitudeDeltaFeature = new AltitudeDeltaFeature();


    // Automatically scales the weights such that their sum is Constants.SUM_OF_PROBABILITIES
    public Heuristic(double averageHeightWeight, double maxHeightWeight, double numOfHolesWeight, double unevennessWeight,
                        double altitudeDeltaWeight) {

        double[] weights = new double[Constants.NUMBER_OF_FEATURES];

        weights[0] = averageHeightWeight;
        weights[1] = maxHeightWeight;
        weights[2] = numOfHolesWeight;
        weights[3] = unevennessWeight;
        weights[4] = altitudeDeltaWeight;

        this.weights = Helper.scaleWeights(weights);

        this.averageHeightWeight = this.weights[0];
        this.maxHeightWeight = this.weights[1];
        this.numOfHolesWeight = this.weights[2];
        this.unevennessWeight = this.weights[3];
        this.altitudeDeltaWeight = this.weights[4];

    }


    public Heuristic(double[] weights) {
        this(weights[0], weights[1], weights[2], weights[3], weights[4]);
    }


    public double getValue(PotentialNextState state) {
        double sum = 0;
        sum = averageHeightWeight * averageHeightFeature.getValue(state)
            + maxHeightWeight * maxHeightFeature.getValue(state)
            + numOfHolesWeight * numOfHolesFeature.getValue(state)
            + unevennessWeight * unevennessFeature.getValue(state)
            + altitudeDeltaWeight * altitudeDeltaFeature.getValue(state);

        return sum;
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
}
