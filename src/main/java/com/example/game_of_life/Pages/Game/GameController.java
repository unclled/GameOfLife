package com.example.game_of_life.Pages.Game;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

import java.util.Random;

public class GameController {
    //--------------------------//
    public Canvas gameField;
    public Button play;
    public Button pause;
    public Button generateNext;

    //--------------------------//
    private int gridX;
    private int gridY;
    private int gameSpeed;

    private boolean isGameStopped = true;

    private boolean generateStartCivilization;
    private byte[][] grid;
    private Random random = new Random();
    private GraphicsContext graphics;

    //--------------------------//

    public void initialize() {
        grid = new byte[gridX][gridY];
        graphics = gameField.getGraphicsContext2D();

        init();

        setButtonsOnAction();
    }

    private void setButtonsOnAction() {
        AnimationTimer runAnimation = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if ((now - lastUpdate) >= gameSpeed * 1_000_000L) {
                    tick();
                    lastUpdate = now;
                }
            }
        };

        play.setOnAction(l -> {
            runAnimation.start();
            isGameStopped = false;
            generateNext.setDisable(true);
        });
        pause.setOnAction(l -> {
            runAnimation.stop();
            isGameStopped = true;
            generateNext.setDisable(false);
        });
        generateNext.setOnAction(l -> {
            if (isGameStopped) {
                tick();
            }
        });

        gameField.setOnMouseClicked(event -> {
            double cellSizeX = gameField.getWidth() / gridX;
            double cellSizeY = gameField.getHeight() / gridY;

            int x = (int) (event.getX() / cellSizeX);
            int y = (int) (event.getY() / cellSizeY);

            grid[x][y] = 1;
            draw();
        });
    }

    public GameController() {}

    public void init() {
        gameField.setHeight(gridY*20);
        gameField.setWidth(gridX*20);
        if (generateStartCivilization) {
            for (int i = 0; i < gridX; i++) {
                for (int j = 0; j < gridY; j++) {
                    grid[i][j] = (byte) random.nextInt(2);
                }
            }
        }
        draw();
    }

    private void draw() {
        graphics.setFill(Color.WHITE);
        graphics.fillRect(0, 0, 1200, 720);

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 1) {
                    graphics.setFill(Color.BLACK);
                    graphics.fillRect(i * 20, j * 20, 20, 20);
                    graphics.setFill(Color.WHITE);
                    graphics.fillRect((i * 20) + 1, (j * 20) + 1, 20 - 2, 20 - 2);
                }else {
                    graphics.setFill(Color.BLACK);
                    graphics.fillRect(i * 20, j * 20, 20, 20);
                    graphics.setFill(Color.BLACK);
                    graphics.fillRect((i * 20) + 1, (j * 20) + 1, 40 - 2, 40 - 2);
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
        draw();
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

    public void setGridX(int gridX) {
        this.gridX = gridX;
    }

    public void setGridY(int gridY) {
        this.gridY = gridY;
    }

    public void setGameSpeed(int gameSpeed) {
        this.gameSpeed = gameSpeed;
    }

    public void setGenerateStartCivilization(boolean generateStartCivilization) {
        this.generateStartCivilization = generateStartCivilization;
    }
}
