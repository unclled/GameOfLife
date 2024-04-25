package com.example.game_of_life.Pages.Game;

import javafx.animation.AnimationTimer;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;


public class GameModel {
    private static final int MAX_GAME_SPEED = 1000;
    private static final int MIN_GAME_SPEED = 50;
    private static final int TIME_STEP = 50;

    private GameObserver gameObserver;

    private final AnimationTimer animationTimer;

    private int[] aliveRules;
    private int[] deadRules;

    StringBuilder selectedPattern = new StringBuilder();

    private int gridX;
    private int gridY;
    private int gameSpeed;
    private int cellsAlive = 0;
    private int generationsCount = 0;

    private boolean isGameStopped = true;
    private boolean generateStartCivilization;

    private byte[][] grid;

    public GameModel() {
        animationTimer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if ((now - lastUpdate) >= gameSpeed * 1_000_000L) {
                    newGeneration();
                    lastUpdate = now;
                }
            }
        };
    }

    public void initialize(boolean needToInitialize) {
        if (!needToInitialize) return;

        grid = new byte[gridX][gridY];
        if (generateStartCivilization) { //случайное заполнение поля
            Random random = new Random();
            int blockSize = gridX < 900 ? 3 : 10;
            for (int i = 0; i < gridX; i += blockSize) {
                for (int j = 0; j < gridY; j += blockSize) {

                    byte value = (byte) random.nextInt(2);
                    for (int x = i; x < Math.min(gridX, i + blockSize); x++) {
                        for (int y = j; y < Math.min(gridY, j + blockSize); y++) {
                            grid[x][y] = value;
                            if (value == 1)
                                cellsAlive++;
                        }
                    }
                }
            }
        }
    }

    public void newGeneration() { //создаем новую генерацию
        generationsCount++;
        byte[][] nextGeneration = new byte[gridX][gridY];
        cellsAlive = 0;

        for (int i = 0; i < gridX; i++) {
            for (int j = 0; j < gridY; j++) {
                int neighbors = countAliveNeighbors(i, j);

                if (grid[i][j] == 1) {
                    if (Arrays.binarySearch(aliveRules, neighbors) >= 0) { //если количество соседей удовлетворяет
                        nextGeneration[i][j] = 1; //правилам для выживания клетки
                        cellsAlive++;
                    }
                } else {
                    if (Arrays.binarySearch(deadRules, neighbors) >= 0) { //если количество соседей удовлетворяет
                        nextGeneration[i][j] = 1; //правилам для воскрешения клетки
                        cellsAlive++;
                    }
                }
            }
        }
        grid = nextGeneration;

        notifyGameObserver();
    }

    private int countAliveNeighbors(int i, int j) { //подсчет соседей
        int sum = 0;
        for (int a = -1; a <= 1; a++) {
            for (int b = -1; b <= 1; b++) {
                int x = (i + a + gridX) % gridX; //обработка цикличности по X
                int y = (j + b + gridY) % gridY; //обработка цикличности по Y
                sum += grid[x][y];
            }
        }
        sum -= grid[i][j];
        return sum;
    }

    public void readPattern(int x, int y, String pattern) throws IOException {
        int saveX = x;
        String patternFilePath = "/Patterns/" + pattern + ".txt";

        try (InputStream is = this.getClass().getResourceAsStream(patternFilePath);
             Scanner scanner = new Scanner(Objects.requireNonNull(is))) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                x = saveX;
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    if (x < 0 || y < 0 || x >= gridX || y >= gridY) { //обработка выхода за границы
                        if (x >= gridX) {
                            x %= gridX; //обработка цикличности по X
                        } else if (x < 0) {
                            x = gridX + (x % gridX); //обработка цикличности по X
                        }

                        if (y >= gridY) {
                            y %= gridY; //обработка цикличности по Y
                        } else if (y < 0) {
                            y %= gridY; //обработка цикличности по Y
                        }
                    }
                    byte temp = (byte) (c - '0');
                    if (temp == 1)
                        cellsAlive++;
                    grid[x][y] = temp;
                    x++;
                }
                y++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean handleImageClick(MouseEvent event) { //обработка нажатия на картинку паттерна
        setSelectedPattern();

        ImageView imageView = (ImageView) event.getSource();
        String imageName = imageView.getImage().getUrl();
        int length = imageName.length() - 5; //отсекаем .png из названия
        char symbol = imageName.charAt(length);

        while (symbol != '/') { //получаем имя картинки
            selectedPattern.append(symbol);
            length--;
            symbol = imageName.charAt(length);
        }
        selectedPattern.reverse();
        return isPatternBiggerField();
    }

    private boolean isPatternBiggerField() { //проверяем, превышает ли паттерн размер поля
        String patternFilePath = "/Patterns/" + selectedPattern.toString() + ".txt";

        try (InputStream is = this.getClass().getResourceAsStream(patternFilePath)) {
            if (is == null) return false;

            Scanner scanner = new Scanner(is);
            String line = scanner.nextLine();

            if (line.length() > gridX || line.length() > gridY) return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setPointsInGrid(byte[][] grid, int x, int y, int action) {
        if (grid[x][y] == 0 && action == 1) { //если клетка мертва и нажата ЛКМ
            cellsAlive++;
            grid[x][y] = 1;
        } else if (grid[x][y] == 1 && action == 0) { //если клетка мертва и нажата ПКМ
            cellsAlive--;
            grid[x][y] = 0;
        }
    }

    public boolean saveGame(String filename) {
        stopGame();

        if (filename.isEmpty()) return false;

        String userHome = System.getProperty("user.home"); //определяем путь к директории пользователя
        String savesDir = Paths.get(userHome, "GameOfLife", "Saves").toString(); //путь к папке Saves в директории пользователя

        //создаем директорию Saves если ее не существует
        File savesDirectory = new File(savesDir);
        if (!savesDirectory.exists())
            if (!savesDirectory.mkdirs()) return false;

        //путь к файлу сохранения
        String savePath = Paths.get(savesDir, filename + ".txt").toString();

        try (PrintWriter writer = new PrintWriter(savePath)) {
            writer.println(gridX);
            writer.println(gridY);
            for (byte[] row : grid) {
                for (byte cell : row) {
                    writer.print(cell);
                }
                writer.println();
            }
            writer.println(gameSpeed);
            writer.println(generationsCount);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void increaseSpeed() { //повышение скорости обновления
        if (gameSpeed < MAX_GAME_SPEED) {
            gameSpeed += TIME_STEP;
        }
    }

    public void decreaseSpeed() { //понижение скорости обновления
        if (gameSpeed > MIN_GAME_SPEED) {
            gameSpeed -= TIME_STEP;
        }
    }

    public void startGame() {
        isGameStopped = false;
        animationTimer.start();
    }

    public void stopGame() {
        isGameStopped = true;
        animationTimer.stop();
    }

    public void setGameObserver(GameObserver gameObserver) {
        this.gameObserver = gameObserver;
    }

    private void notifyGameObserver() {
        gameObserver.onTick();
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

    public void setGrid(byte[][] grid) {
        this.grid = grid;
    }

    public int getCellsAlive() {
        return cellsAlive;
    }

    public void setCellsAlive(int cellsAlive) {
        this.cellsAlive = cellsAlive;
    }

    public int getGenerationsCount() {
        return generationsCount;
    }

    public void setAliveRules(int[] aliveRules) {
        this.aliveRules = Arrays.stream(aliveRules).toArray();
    }

    public void setDeadRules(int[] deadRules) {
        this.deadRules = Arrays.stream(deadRules).toArray();
    }

    public void setGenerationsCount(int generationsCount) {
        this.generationsCount = generationsCount;
    }
}
