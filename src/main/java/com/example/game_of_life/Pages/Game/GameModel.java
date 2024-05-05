package com.example.game_of_life.Pages.Game;

import eu.hansolo.tilesfx.tools.Point;
import javafx.animation.AnimationTimer;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.*;


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

    private byte[] grid;

    private List<Point> changedCells;

    private NeighborCountFunction function;

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
        function = gridX <= 819 ? this::countAliveNeighborsCycle : this::countAliveNeighborsNotCycle;
        grid = new byte[gridX * gridY];
        if (generateStartCivilization) { //случайное заполнение поля
            Random random = new Random();
            int blockSize = gridX < 900 ? 3 : 10;
            for (int i = 0; i < gridX; i += blockSize) {
                for (int j = 0; j < gridY; j += blockSize) {

                    byte value = (byte) random.nextInt(2);
                    for (int x = i; x < Math.min(gridX, i + blockSize); x++) {
                        for (int y = j; y < Math.min(gridY, j + blockSize); y++) {
                            grid[x * gridY + y] = value;
                            if (value == 1)
                                cellsAlive++;
                        }
                    }
                }
            }
        }
    }

    public void newGeneration() {
        generationsCount++;
        byte[] nextGeneration = new byte[gridX * gridY];
        changedCells = new ArrayList<>(); //cписок для отслеживания измененных клеток
        cellsAlive = 0;

        byte[] temp = function.countAliveNeighbors();
        for (int i = 0; i < gridX; i++) {
            for (int j = 0; j < gridY; j++) {
                int pos = i * gridY + j;
                int neighbors = temp[pos];
                byte newState = 0;
                if (grid[pos] == 1) {
                    if (Arrays.binarySearch(aliveRules, neighbors) >= 0) {
                        newState = 1;
                    }
                } else {
                    if (Arrays.binarySearch(deadRules, neighbors) >= 0) {
                        newState = 1;
                    }
                }

                //проверяем, изменилось ли состояние клетки
                if (grid[pos] != newState) {
                    changedCells.add(new Point(i, j)); //добавляем измененную клетку в список
                }

                nextGeneration[pos] = newState;
                if (newState == 1) {
                    cellsAlive++;
                }
            }
        }

        //обновляем сетку после создания нового поколения
        grid = nextGeneration;

        notifyGameObserver();
    }

    private byte[] countAliveNeighborsCycle() {
        byte[] temp = new byte[gridX * gridY];

        //обход одномерного массива
        for (int i = 0; i < gridX; i++) {
            for (int j = 0; j < gridY; j++) {
                //используем long для сложения восьми соседей сразу
                long sum = 0;

                //список из значений смещений для обхода соседей
                int[] dX = {-1, -1, -1, 0, 0, 1, 1, 1};
                int[] dY = {-1, 0, 1, -1, 1, -1, 0, 1};

                for (int k = 0; k < 8; k++) {
                    //вычисляем циклический индекс по x и y
                    int neighborX = (i + dX[k] + gridX) % gridX;
                    int neighborY = (j + dY[k] + gridY) % gridY;

                    //индекс в одномерном массиве
                    int neighborIndex = neighborX * gridY + neighborY;

                    //суммируем значения соседей
                    sum += grid[neighborIndex] & 0xFFL;
                }

                //сохраняем число соседей для конкретной клетки
                temp[i * gridY + j] = (byte) sum;
            }
        }

        return temp;
    }

    private byte[] countAliveNeighborsNotCycle() {
        byte[] temp = new byte[gridX * gridY];
        //обход одномерного массива
        for (int i = gridY + 1; i < gridY * gridX - gridY - 1; i++) {
            //используем long для сложения восьми соседей сразу
            long sum = 0;

            /* обход соседей клетки
               побитовая операция AND позволяет из grid[] извлечь только младшие 8 бит значения, в которых
               как раз и хранится необходимая нам информация (0xFFL - 16-ричное число = 255, Long тип) */
            sum += grid[i - gridY - 1] & 0xFFL;
            sum += grid[i - gridY] & 0xFFL;
            sum += grid[i - gridY + 1] & 0xFFL;
            sum += grid[i - 1] & 0xFFL;
            sum += grid[i + 1] & 0xFFL;
            sum += grid[i + gridY - 1] & 0xFFL;
            sum += grid[i + gridY] & 0xFFL;
            sum += grid[i + gridY + 1] & 0xFFL;

            //сохраняем число соседей для конкретной клетки
            temp[i] = (byte) sum;
        }
        return temp;
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
                    grid[x * gridY + y] = temp;
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

    public void setPointsInGrid(byte[] grid, int x, int y, int action) {
        if (grid[x * gridY + y] == 0 && action == 1) { //если клетка мертва и нажата ЛКМ
            cellsAlive++;
            grid[x * gridY + y] = 1;
        } else if (grid[x * gridY + y] == 1 && action == 0) { //если клетка мертва и нажата ПКМ
            cellsAlive--;
            grid[x * gridY + y] = 0;
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
            int i = 0;
            for (byte row : grid) {
                writer.print(row);
                i++;
                if (i == gridY) {
                    i = 0;
                    writer.println();
                }
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

    public byte[] getGrid() {
        return grid;
    }

    public void setGrid(byte[] grid) {
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

    public List<Point> getChangedCells() {
        return changedCells;
    }
}