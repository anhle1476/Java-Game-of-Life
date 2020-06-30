package life;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class GameOfLife extends JFrame {
    Random random = new Random();
    int size = 30;
    int gen = 1;
    boolean isPlaying = true;
    int timeToNextGen = 300;

    Universe universe = new Universe(size, random);
    Generator generator = new Generator(size);

    public GameOfLife() {
        super("Game Of Life");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


        controllerDisplay();

        while (true) {
            try {
                if (!isPlaying) {
                    // while pause -> update status every 0.1s
                    TimerThread timerThread = new TimerThread(100);
                    timerThread.start();
                    timerThread.join();
                    continue;
                }
                // process display current gen, get next gen and set timer concurrently
                NextGenThread nextGenThread = new NextGenThread(universe, generator);
                TimerThread timerThread = new TimerThread(timeToNextGen);

                nextGenThread.start();
                timerThread.start();

                generationDisplay();
                setVisible(true);

                nextGenThread.join();
                universe = nextGenThread.getUniverse();
                gen++;

                timerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void generationDisplay() {
        JPanel statusBoard = new JPanel();
        statusBoard.setLayout(new GridLayout(2, 1));

        JLabel generationLabel = new JLabel("GenerationLabel");
        generationLabel.setText("Generation: #" + gen);
        statusBoard.add(generationLabel);

        int alive = 0;

        JPanel gameBoard = new JPanel();
        gameBoard.setLayout(new GridLayout(size, size, 1, 1));
        gameBoard.setBackground(Color.BLACK);

        boolean[][] game = universe.getUniverse();
        for (boolean[] row : game) {
            for (boolean cell : row) {
                JPanel cellDisplay = new JPanel();
                if (cell) {
                    alive++;
                    cellDisplay.setBackground(Color.DARK_GRAY);
                } else {
                    cellDisplay.setBackground(Color.WHITE);
                }
                gameBoard.add(cellDisplay);
            }
        }

        JLabel aliveLabel = new JLabel("AliveLabel");
        aliveLabel.setText("Alive: " + alive);
        statusBoard.add(aliveLabel);

        add(statusBoard, BorderLayout.NORTH);
        add(gameBoard, BorderLayout.CENTER);
    }


    private void controllerDisplay() {
        JPanel controller = new JPanel();
        controller.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel gameSizeLabel = new JLabel("Size: ");
        controller.add(gameSizeLabel);

        JTextField gameSize = new JTextField();
        gameSize.setText("20");
        gameSize.setEditable(true);
        gameSize.addActionListener(e -> {
            try {
                int value = Integer.parseInt(gameSize.getText().trim());
                if (value > 50) {
                    gameSize.setText("50");
                    value = 50;
                }
                if (value < 10) {
                    gameSize.setText("10");
                    value = 10;
                }
                size = value;
                universe = new Universe(size, random);
                generator = new Generator(size);
                gen = 1;
                isPlaying = true;
            } catch (NumberFormatException ignored) {}
        });
        controller.add(gameSize);

        JToggleButton playToggleButton = new JToggleButton("PlayToggleButton");
        playToggleButton.setText("\u23F5");
        playToggleButton.addActionListener(e -> {
            isPlaying = !isPlaying;
            playToggleButton.setText(isPlaying ? "\u23F5" : "\u23F8");
        });
        controller.add(playToggleButton);

        JButton resetButton = new JButton("ResetButton");
        resetButton.setText("\u21BA");
        resetButton.addActionListener(e -> {
            universe = new Universe(size, random);
            gen = 1;
            isPlaying = true;
        });
        controller.add(resetButton);

        JLabel speedSliderLabel = new JLabel("Speed: ");
        controller.add(speedSliderLabel);

        JSlider speedSlider = new JSlider();
        speedSlider.addChangeListener(e -> timeToNextGen = 100 + 4 * (100 - speedSlider.getValue()));
        controller.add(speedSlider);

        add(controller, BorderLayout.SOUTH);
    }
}

class TimerThread extends Thread {
    int time;

    public TimerThread(int time) {
        this.time = time;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class NextGenThread extends Thread {
    private Universe universe;
    private final Generator generator;

    public NextGenThread(Universe universe, Generator generator) {
        this.universe = universe;
        this.generator = generator;
    }

    public Universe getUniverse() {
        return universe;
    }

    @Override
    public void run() {
        universe = generator.nextGen(universe);
    }
}