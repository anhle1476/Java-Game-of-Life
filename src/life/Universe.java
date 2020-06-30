package life;

import java.util.Random;

public class Universe {
    boolean[][] universe;

    Universe(int size, Random random) {
        this.universe = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                universe[i][j] = random.nextBoolean();
            }
        }
    }

    Universe(boolean[][] universe) {
        this.universe = universe;
    }

    public boolean[][] getUniverse() {
        return universe;
    }
}
