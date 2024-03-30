package com.example.game_of_life.Pages.Game;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;

import java.util.prefs.Preferences;

public class GameView {
    @FXML public Canvas gameField;
    private GraphicsContext graphics;
    @FXML public Text showSpeed;
    @FXML public Text fieldSize;
    @FXML public Text cellsAlive;
    @FXML public ScrollPane scrollPane;
    @FXML public Text generationsCounter;
    protected Color liveCellColor;
    protected Color deadCellColor;
    protected byte cellSize;
    private byte outline;

    public void initializeView(int gridX, int gridY) {
        graphics = gameField.getGraphicsContext2D();
        Preferences prefs = Preferences.userRoot();
        liveCellColor = Color.valueOf(prefs.get("LIVECELLCOLOR", "white"));
        deadCellColor = Color.valueOf(prefs.get("DEADCELLCOLOR", "black"));
        cellSize = Byte.parseByte(prefs.get("CELLSIZE", "20"));
        outline = (byte) (cellSize < 10 ? 1 : 2);
        gameField.setHeight(gridY * cellSize);
        gameField.setWidth(gridX * cellSize);
        fieldSize.setText("Размер поля: " + gridX + "x" + gridY);

        for (int i = 0; i < gridX; i++) {
            for (int j = 0; j < gridY; j++) {
                graphics.setFill(deadCellColor);
                graphics.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
            }
        }
    }

    public void draw(byte[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                int x = i;
                int gridX = grid.length;
                int gridY = grid[i].length;
                if (i >= gridX) {
                    x = i % gridX; // Обработка цикличности по X
                } else if (i < 0) {
                    x = gridX + (i % gridX); // Обработка цикличности по X
                }

                int y = j;
                if (j >= gridY) {
                    y = j % gridY; // Обработка цикличности по Y
                } else if (j < 0) {
                    y = gridY + (j % gridY); // Обработка цикличности по Y
                }

                if (grid[x][y] == 1) {
                    graphics.setFill(liveCellColor);
                    graphics.fillRect((x * cellSize) + 1, (y * cellSize) + 1, cellSize - outline, cellSize - outline);
                } else {
                    graphics.setFill(deadCellColor);
                    graphics.fillRect((x * cellSize) + 1, (y * cellSize) + 1, cellSize - outline, cellSize - outline);
                }
            }
        }
    }

    public void drawPoints(int x, int y, Color color) {
        graphics.setFill(color);
        graphics.fillRect((x * cellSize) + 1, (y * cellSize) + 1, cellSize - outline, cellSize - outline);
    }

    public void showAmountOfAliveCells(int currentCellsAlive) {
        cellsAlive.setText("Количество живых клеток: " + currentCellsAlive);
    }

    public void showGenerationsCounter(int generationsCount) {
        generationsCounter.setText("Количество генераций: " + generationsCount);
    }

    public void doZoomOut() {
        Group contentGroup = new Group(gameField);
        scrollPane.setContent(contentGroup);

        Scale scale = new Scale(0.8, 0.8);
        gameField.getTransforms().add(scale);
    }

    public void doZoomIn() {
        Scale scale = new Scale(1.2, 1.2);
        gameField.getTransforms().add(scale);
    }

    public void showCurrentGameSpeed(int gameSpeed) {
        showSpeed.setText(String.valueOf(gameSpeed));
    }

    public void setButtonDisabled(Button button, boolean action) {
        button.setDisable(action);
    }
}
