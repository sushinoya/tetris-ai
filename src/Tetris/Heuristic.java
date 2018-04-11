package Tetris;

import Tetris.State;
import Tetris.Features.*;
import com.sun.javafx.collections.ArrayListenerHelper;

import java.util.ArrayList;
import java.util.Random;

//this class represents the features and their corresponding weights
public class Heuristic {

    ArrayList<Double> weights;

    //these double values represent the weight on each feature
    //the feature represents the features themselves
    double averageHeightWeight;
    AverageHeightFeature averageHeightFeature;

    double maxHeightWeight;
    MaxHeightFeature maxHeightFeature;

    double numOfHolesWeight;
    NumOfHolesFeature numOfHolesFeature;

    double unevennessWeight;
    UnevennessFeature unevennessFeature;



    public Heuristic(double averageHeightWeight, double maxHeightWeight, double numOfHolesWeight, double unevennessWeight) {

        this.weights = new ArrayList<Double>();

        this.averageHeightWeight = averageHeightWeight;
        weights.add(averageHeightWeight);
        this.averageHeightFeature = new AverageHeightFeature();

        this.maxHeightWeight = maxHeightWeight;
        weights.add(maxHeightWeight);
        this.maxHeightFeature = new MaxHeightFeature();

        this.numOfHolesWeight = numOfHolesWeight;
        weights.add(numOfHolesWeight);
        this.numOfHolesFeature = new NumOfHolesFeature();

        this.unevennessWeight = unevennessWeight;
        weights.add(unevennessWeight);
        this.unevennessFeature = new UnevennessFeature();

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
