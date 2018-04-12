package Tetris;

import Tetris.Features.*;
import Tetris.Util.Util;

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


    // Automatically scales the weights such that their sum is Constants.SUM_OF_PROBABILITIES
    public Heuristic(double averageHeightWeight, double maxHeightWeight, double numOfHolesWeight, double unevennessWeight) {

        double[] weights = new double[Constants.NUMBER_OF_FEATURES];

        weights[0] = averageHeightWeight;
        weights[1] = maxHeightWeight;
        weights[2] = numOfHolesWeight;
        weights[3] = unevennessWeight;

        this.weights = Util.scaleWeights(weights);

        this.averageHeightWeight = this.weights[0];
        this.maxHeightWeight = this.weights[1];
        this.numOfHolesWeight = this.weights[2];
        this.unevennessWeight = this.weights[3];

    }


    public Heuristic(double[] weights) {
        this(weights[0], weights[1], weights[2], weights[3]);
    }


    public double getValue(State state) {
        double sum = 0;
        sum = averageHeightWeight * averageHeightFeature.getValue(state)
            + maxHeightWeight * maxHeightFeature.getValue(state)
            + numOfHolesWeight * numOfHolesFeature.getValue(state)
            + unevennessWeight * unevennessFeature.getValue(state);

        return sum;
    }
}
