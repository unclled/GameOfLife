package com.example.game_of_life.Pages.Game;

import javafx.animation.AnimationTimer;

import java.util.Random;

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

        if (generateStartCivilization) {
            for (int i = 0; i < gridX; i++) {
                for (int j = 0; j < gridY; j++) {
                    int num = grid[i][j] = (byte) random.nextInt(2);
                    if (num == 1)
                        cellsAlive++;
                }
            }
        }
    }

    public void tick() {
        generationsCount++;
        byte[][] next = new byte[gridX][gridY];
        cellsAlive = 0;
        for (int i = 0; i < gridX; i++) {
            for (int j = 0; j < gridY; j++) {
                int neighbors = countAliveNeighbors(i, j);

                if (neighbors == 3) {
                    cellsAlive++;
                    next[i][j] = 1;
                } else if (neighbors < 2 || neighbors > 3) {
                    next[i][j] = 0;
                } else {
                    int num = next[i][j] = grid[i][j];
                    if (num == 1)
                        cellsAlive++;
                }
            }
        }
        grid = next;
        notifyGameObserver();
    }
    /* С ГРАНИЦАМИ */
/*    private int countAliveNeighbors(int i, int j) {
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
    }*/

    /* ЦИКЛИЧЕСКАЯ */
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
        if (grid[x][y] == 0)
            cellsAlive++;

        grid[x][y] = 1;
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
}
