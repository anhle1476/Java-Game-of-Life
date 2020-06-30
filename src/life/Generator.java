package life;

public class Generator {
    boolean[][] universe;
    int size;

    Generator(int size) {
        this.size = size;
    }

    public Universe nextGen(Universe prevGen) {
        this.universe = prevGen.getUniverse();
        boolean[][] nextGen = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int aliveCells = getNeighborAliveCells(i, j);
                if (universe[i][j]) {
                    nextGen[i][j] = (aliveCells == 2 || aliveCells == 3);
                } else {
                    nextGen[i][j] = aliveCells == 3;
                }
            }
        }
        return new Universe(nextGen);
    }

    private int getNeighborAliveCells(int i, int j) {
        int count = 0;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0) continue;
                int neighborX = (i + x == -1) ? size - 1 : (i + x) % size;
                int neighborY = (j + y == -1) ? size - 1 : (j + y) % size;
                if (universe[neighborX][neighborY]) {
                    count++;
                }
            }
        }
        return count;
    }
}
