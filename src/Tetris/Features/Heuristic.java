package Tetris.Features;

import Tetris.State;

//this class represents the features and their corresponding weights
public class Heuristic {
    //these double values represent the weight on each feature
    //the feature represents the features themselves
    double averageHeightValue;
    AverageHeightFeature averageHeightFeature;

    double maxHeightValue;
    MaxHeightFeature maxHeightFeature;

    double numOfHolesValue;
    NumOfHolesFeature numOfHolesFeature;

    double unEvennessValue;
    UnevennessFeature unevennessFeature;



    public Heuristic(double averageHeightWeight, double maxHeightValue, double numOfHolesValue, double unEvennessValue) {

        this.averageHeightValue = averageHeightValue;
        this.averageHeightFeature = new AverageHeightFeature();

        this.maxHeightValue = maxHeightValue;
        this.maxHeightFeature = new MaxHeightFeature();

        this.numOfHolesValue = numOfHolesValue;
        this.numOfHolesFeature = new NumOfHolesFeature();

        this.unEvennessValue = unEvennessValue;
        this.unevennessFeature = new UnevennessFeature();

    }

    public double getValue(State state) {
        double sum = 0;
        sum = averageHeightValue * averageHeightFeature.getValue(state)
            + maxHeightValue * maxHeightFeature.getValue(state)
            + numOfHolesValue * numOfHolesFeature.getValue(state)
            + unEvennessValue * unevennessFeature.getValue(state);

        return sum;
    }
}
