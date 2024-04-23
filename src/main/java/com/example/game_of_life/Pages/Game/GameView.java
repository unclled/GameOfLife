package com.example.game_of_life.Pages.Game;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

import java.util.prefs.Preferences;

public class GameView {
    //-----------Контейнеры-----------//
    @FXML public Canvas gameField;
    @FXML public ScrollPane scrollPane;
    @FXML public Accordion accordion;
    @FXML public Pane saveWindow;
    @FXML public Pane warningPane;

    @FXML public VBox patternsWindow;
    @FXML public VBox spaceships;
    @FXML public VBox methuselahs;
    @FXML public VBox oscillators;
    @FXML public VBox puffers;
    @FXML public VBox guns;
    @FXML public VBox gardenOfEden;
    @FXML public VBox conduits;

    //------Текст и текстовые поля------//
    @FXML public Text generationsCounter;
    @FXML public Text showSpeed;
    @FXML public Text fieldSize;
    @FXML public Text cellsAlive;
    @FXML public Text warningText;
    @FXML public Text errorText;
    @FXML public TextField filename;
    //---------------------------------//

    protected GraphicsContext graphics;

    protected Color liveCellColor;
    protected Color deadCellColor;

    protected byte cellSize;
    private byte outline;

    public void initializeView(int gridX, int gridY) {
        Preferences prefs = Preferences.userRoot(); //считываем сохраненные значения для поля
        liveCellColor = Color.valueOf(prefs.get("LIVECELLCOLOR", "white"));
        deadCellColor = Color.valueOf(prefs.get("DEADCELLCOLOR", "black"));
        cellSize = Byte.parseByte(prefs.get("CELLSIZE", "20"));
        outline = (byte) (cellSize < 10 ? 1 : 2);

        gameField.setHeight(gridY * cellSize);
        gameField.setWidth(gridX * cellSize);
        graphics = gameField.getGraphicsContext2D();
        fieldSize.setText("Размер поля: " + gridX + "x" + gridY);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        for (int i = 0; i < gridX; i++) { //красим поле
            for (int j = 0; j < gridY; j++) {
                graphics.setFill(deadCellColor);
                graphics.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
            }
        }
    }

    public void drawField(byte[][] grid) { //отрисовываем поле
        int gridY = grid[0].length;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < gridY; j++) {
                if (grid[i][j] == 1) { //клетка живая
                    graphics.setFill(liveCellColor);
                    graphics.fillRect((i * cellSize) + 1, (j * cellSize) + 1, cellSize - outline, cellSize - outline);
                } else { //клетка мертвая
                    graphics.setFill(deadCellColor);
                    graphics.fillRect((i * cellSize) + 1, (j * cellSize) + 1, cellSize - outline, cellSize - outline);
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
        generationsCounter.setText("Число генераций: " + generationsCount);
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

    public void showPatternWindow() {
        if (!patternsWindow.isVisible()) { //анимация появления/исчезновения окна паттернов
            patternsWindow.setVisible(true);
            patternsWindow.setTranslateX(250);
            TranslateTransition translate = new TranslateTransition();
            translate.setNode(patternsWindow);
            translate.setDuration(Duration.millis(400));
            translate.setByX(-250);
            translate.play();
        } else {
            TranslateTransition translate = new TranslateTransition();
            translate.setNode(patternsWindow);
            translate.setDuration(Duration.millis(400));
            translate.setByX(250);
            translate.setOnFinished(event -> patternsWindow.setVisible(false));
            translate.play();
        }
    }

    public void showCurrentGameSpeed(int gameSpeed) {
        showSpeed.setText(String.valueOf(gameSpeed));
    }

    public void setButtonDisabled(Button button, boolean action) {
        button.setDisable(action);
    }
}
