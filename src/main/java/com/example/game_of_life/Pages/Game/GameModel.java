package com.example.game_of_life.Pages.Game;

import com.example.game_of_life.Pages.MersenneTwister;
import javafx.animation.AnimationTimer;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
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

    private final AnimationTimer animationTimer;
    private GameObserver gameObserver;

    private int[] aliveRules;
    private int[] deadRules;

    StringBuilder selectedPattern = new StringBuilder();

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

    public void handleImageClick(MouseEvent event) {
        setSelectedPattern();
        ImageView imageView = (ImageView) event.getSource();
        String imageName = imageView.getImage().getUrl();
        int length = imageName.length() - 5;
        char symbol = imageName.charAt(length);
        while (symbol != '/') {
            selectedPattern.append(symbol);
            length--;
            symbol = imageName.charAt(length);
        }
        selectedPattern.reverse();
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
        for (int a = -1; a <= 1; a++) {
            for (int b = -1; b <= 1; b++) {
                int x = (i + a + gridX) % gridX; // Обработка цикличности по X
                int y = (j + b + gridY) % gridY; // Обработка цикличности по Y
                sum += grid[x][y];
            }
        }
        sum -= grid[i][j];
        return sum;
    }

    public void readPattern(int x, int y, String pattern) throws IOException {
        int saveX = x;
        String filePath = "C:\\Projects\\GameOfLife\\src\\main\\java\\com\\example\\game_of_life\\Patterns\\" + pattern + ".txt";
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            Scanner scanner = new Scanner(fis);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                x = saveX;
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    if (x < 0 || y < 0 || x >= gridX || y >= gridY) {
                        if (x >= gridX) {
                            x %= gridX; // Обработка цикличности по X
                        } else if (x < 0) {
                            x = gridX + (x % gridX); // Обработка цикличности по X
                        }

                        if (y >= gridY) {
                            y %= gridY; // Обработка цикличности по Y
                        } else if (y < 0) {
                            y %= gridY; // Обработка цикличности по Y
                        }
                    }
                    grid[x][y] = (byte) (c - '0');
                    x++;
                }
                y++;
            }

            scanner.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getSelectedPattern() {
        return selectedPattern.toString();
    }

    public void setSelectedPattern() {
        this.selectedPattern.delete(0, selectedPattern.length());
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

    public void saveGame(String filename, byte[][] grid, int gameSpeed, int generationsCount) {
        stopGame();
        try (PrintWriter writer = new PrintWriter(filename + ".txt")) {
            for (byte[] bytes : grid) {
                for (byte aByte : bytes) {
                    writer.print(aByte);
                }
                writer.println();
            }
            writer.println(gameSpeed);
            writer.println(generationsCount);
        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл");
            e.printStackTrace();
        }
    }

    public void setGenerationsCount(int generationsCount) {
        this.generationsCount = generationsCount;
    }
}
