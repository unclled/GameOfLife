package com.example.game_of_life.Pages.Game;

import javafx.animation.AnimationTimer;

import java.util.Random;

public class GameModel {

    private int gridX;
    private int gridY;
    private int gameSpeed;

    private boolean isGameStopped = true;
    private boolean generateStartCivilization;

    private byte[][] grid;

    private Random random = new Random();

    private AnimationTimer animationTimer;
    private GameObserver gameObserver;

    public void setGameObserver(GameObserver gameObserver) {
        this.gameObserver = gameObserver;
    }

    private void notifyGameObserver() {
        gameObserver.onTick();
    }

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

        if (generateStartCivilization) {
            for (int i = 0; i < gridX; i++) {
                for (int j = 0; j < gridY; j++) {
                    grid[i][j] = (byte) random.nextInt(2);
                }
            }
        }
    }

    public void tick() {
        byte[][] next = new byte[gridX][gridY];

        for (int i = 0; i < gridX; i++) {
            for (int j = 0; j < gridY; j++) {
                int neighbors = countAliveNeighbors(i, j);

                if (neighbors == 3) {
                    next[i][j] = 1;
                }else if (neighbors < 2 || neighbors > 3) {
                    next[i][j] = 0;
                }else {
                    next[i][j] = grid[i][j];
                }
            }
        }
        grid = next;
        notifyGameObserver();
    }

    private int countAliveNeighbors(int i, int j) {
        int sum = 0;
        int iStart = i == 0 ? 0 : -1;
        int iEndInclusive = i == grid.length - 1 ? 0 : 1;
        int jStart = j == 0 ? 0 : -1;
        int jEndInclusive = j == grid[0].length - 1 ? 0 : 1;

        for (int k = iStart; k <= iEndInclusive; k++) {
            for (int l = jStart; l <= jEndInclusive; l++) {
                sum += grid[i + k][l + j];
            }
        }

        sum -= grid[i][j];

        return sum;
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

    public void setGameStopped(boolean gameStopped) {
        isGameStopped = gameStopped;
    }

    public boolean isGenerateStartCivilization() {
        return generateStartCivilization;
    }

    public void setGenerateStartCivilization(boolean generateStartCivilization) {
        this.generateStartCivilization = generateStartCivilization;
    }

    public byte[][] getGrid() {
        return grid;
    }

    public void setPointsInGrid(byte[][] grid, int x, int y) {
        grid[x][y] = 1;
    }

    public void setGrid(byte[][] grid) {
        this.grid = grid;
    }

}
