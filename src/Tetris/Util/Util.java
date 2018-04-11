package Tetris.Util;
import java.util.Random;

public class Util {

    /* Generates x times of a number between 0(inclusive) and y(non-inclusive) */
    public static int[] generateRandomArray(int x, int y) {
        int[] randomNums = new int[x];
        Random random = new Random();
        for (int i = 0; i < x; i++) {
            randomNums[i] = random.nextInt(y);
        }
        return randomNums;
    }

}
