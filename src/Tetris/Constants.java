package Tetris;

public final class Constants {

    public final static int NUMBER_OF_THREADS = 4; // Make sure number of threads is a factor of the number of heuristics
    public final static int NUMBER_OF_GAMES = 5;
    public final static int NUMBER_OF_HEURISTICS = 100;
    public final static int SUM_OF_PROBABILITIES = 100;
    public final static int NUMBER_OF_GENERATIONS = 100;
    public final static double PROBABILITY_OF_MUTATION = 0.05;
    public final static double MAX_MUTATION_CHANGE = 0.2;
    public final static int NUMBER_OF_FEATURES = Heuristic.features.length;
    public final static boolean DRAW_ENABLED = false;
    public final static boolean ALLOW_MASTURBATION_REPRODUCTION = false;
    public final static boolean RETAIN_PARENTS = true;
    public final static double FRACTION_OF_RETAINED_PARENTS = 0.3;
    public final static boolean REPRODUCE_PROPORTIONATELY = false;
    public final static boolean USE_WEIGHTED_REPRODUCE = false;
    public final static boolean USE_ZERO_INITIAL_POPULATON = false;
    public final static int WAITING_TIME = 1;
    public final static boolean IS_GENETIC_RUNNING = true;
    public final static boolean READ_POPULATION_FROM_FILE = false;
    public final static String AVERAGE_LOG_FOR_4HEURISTICS = "best_4_averages.txt";
    public final static String AVERAGE_LOG_FOR_5HEURISTICS = "best_5_averages.txt";

}
