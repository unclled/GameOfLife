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

public class GameView {
    @FXML public Canvas gameField;
    private GraphicsContext graphics;
    @FXML public Text showSpeed;
    @FXML public Text fieldSize;
    @FXML public Text cellsAlive;
    @FXML public ScrollPane scrollPane;
    @FXML public Text generationsCounter;

    public void initializeView(int gridX, int gridY) {
        graphics = gameField.getGraphicsContext2D();

        gameField.setHeight(gridY * 20);
        gameField.setWidth(gridX * 20);
        fieldSize.setText("Размер поля: " + gridX + "x" + gridY);
    }

    public void draw(byte[][] grid) {
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
