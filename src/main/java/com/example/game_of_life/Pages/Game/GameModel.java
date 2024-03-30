package com.example.game_of_life.Pages.Game;

import com.example.game_of_life.Pages.MersenneTwister;
import javafx.animation.AnimationTimer;

import java.io.*;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;


public class GameModel {

    private int gridX;
    private int gridY;
    private int gameSpeed;
    private int cellsAlive = 0;
    private int generationsCount = 0;

    private boolean isGameStopped = true;
    private boolean generateStartCivilization;

    private byte[][] grid;

    private Random random = new Random();

    private AnimationTimer animationTimer;
    private GameObserver gameObserver;

    private int[] aliveRules;
    private int[] deadRules;

    public GameModel() {
        animationTimer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if ((now - lastUpdate) >= gameSpeed * 1_000_000L) {
                    tick();
                    lastUpdate = now;
                }
            }
        };
    }

    public void increaseSpeed() {
        if (gameSpeed < 1000) {
            gameSpeed += 50;
        }
    }

    public void decreaseSpeed() {
        if (gameSpeed > 50) {
            gameSpeed -= 50;
        }
    }

    public void setGameObserver(GameObserver gameObserver) {
        this.gameObserver = gameObserver;
    }

    private void notifyGameObserver() {
        gameObserver.onTick();
    }

    public void startGame() {
        isGameStopped = false;
        animationTimer.start();
    }

    public void stopGame() {
        isGameStopped = true;
        animationTimer.stop();
    }

    public void initialize() {
        grid = new byte[gridX][gridY];

        MersenneTwister mersenneTwister = new MersenneTwister();
        if (generateStartCivilization) {
            for (int i = 0; i < gridX; i++) {
                for (int j = 0; j < gridY; j++) {
                    byte num = grid[i][j] = (byte) (mersenneTwister.nextByte() & 1);
                    if (num == 1) {
                        cellsAlive++;
                    }
                }
            }
        }
    }

    public void tick() {
        generationsCount++;
        byte[][] next = new byte[gridX][gridY];
        int newCellsAlive = 0;

        for (int i = 0; i < gridX; i++) {
            for (int j = 0; j < gridY; j++) {
                int neighbors = countAliveNeighbors(i, j);

                if (grid[i][j] == 1) {
                    if (Arrays.binarySearch(aliveRules, neighbors) >= 0) {
                        next[i][j] = 1;
                        newCellsAlive++;
                    }
                } else {
                    if (Arrays.binarySearch(deadRules, neighbors) >= 0) {
                        next[i][j] = 1;
                        newCellsAlive++;
                    }
                }
            }
        }

        grid = next;
        cellsAlive = newCellsAlive;

        notifyGameObserver();
    }

    private int countAliveNeighbors(int i, int j) {
        int sum = 0;
        for (int k = -1; k <= 1; k++) {
            for (int l = -1; l <= 1; l++) {
                int x = (i + k + gridX) % gridX; // Обработка цикличности по X
                int y = (j + l + gridY) % gridY; // Обработка цикличности по Y
                sum += grid[x][y];
            }
        }
        sum -= grid[i][j];
        return sum;
    }

    public void readPattern(int x, int y) throws IOException {
        int saveX = x;
        int saveY = y;
        String filePath = "C:\\Projects\\GameOfLife\\src\\main\\java\\com\\example\\game_of_life\\Patterns\\68P9.txt";
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            Scanner scanner = new Scanner(fis);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                x = saveX;
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    grid[x][y] = (byte) (c - '0');
                    x++;
                }
                y++;
            }

            scanner.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void stopAnimator() {
        animationTimer.stop();
    }

    public int getGridX() {
        return gridX;
    }

    public void setGridX(int gridX) {
        this.gridX = gridX;
    }

    public int getGridY() {
        return gridY;
    }

    public void setGridY(int gridY) {
        this.gridY = gridY;
    }

    public int getGameSpeed() {
        return gameSpeed;
    }

    public void setGameSpeed(int gameSpeed) {
        this.gameSpeed = gameSpeed;
    }

    public boolean isGameStopped() {
        return isGameStopped;
    }

    public void setGenerateStartCivilization(boolean generateStartCivilization) {
        this.generateStartCivilization = generateStartCivilization;
    }

    public byte[][] getGrid() {
        return grid;
    }

    public void setPointsInGrid(byte[][] grid, int x, int y, int action) {
        if (grid[x][y] == 0 && action == 1) {
            cellsAlive++;
            grid[x][y] = 1;
        }
        else if (grid[x][y] == 1 && action == 0) {
            cellsAlive--;
            grid[x][y] = 0;
        }
    }

    public void setGrid(byte[][] grid) {
        this.grid = grid;
    }

    public int getCellsAlive() {
        return cellsAlive;
    }

    public int getGenerationsCount() {
        return generationsCount;
    }

    public void setAliveRules(int[] aliveRules) {
        this.aliveRules = aliveRules;
    }

    public void setDeadRules(int[] deadRules) {
        this.deadRules = deadRules;
    }
}
