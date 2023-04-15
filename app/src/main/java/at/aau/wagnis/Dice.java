package at.aau.wagnis;

import java.security.SecureRandom;
import java.util.Random;

public class Dice {
    private Random random;

    public Dice() {
        this.random = new SecureRandom();
    }

    public int roll() {
        return random.nextInt(6) + 1;
    }

    public int[] roll(int count) {
        int[] rolls = new int[count];
        for (int i = 0; i < count; i++) {
            rolls[i] = roll();
        }
        return rolls;
    }
}