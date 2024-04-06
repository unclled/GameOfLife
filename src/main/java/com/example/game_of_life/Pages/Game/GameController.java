package com.example.game_of_life.Pages.Game;

import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class GameController extends GameView implements GameObserver {
    private final GameModel gameModel = new GameModel();

    //--------------------------//
    public Button play;
    public Button pause;
    public Button generateNext;
    public Button increaseSpeed;
    public Button decreaseSpeed;
    public Button zoomOut;
    public Button zoomIn;
    public Button patternSelector;
    public Button drawingMode;
    public Button clearField;
    public Button saveGame;
    public Button saveGameToFile;
    public Button closeSaveWindow;
    //--------------------------//

    public GameController() {}

    public void initialize(boolean needToInitialize) {
        gameModel.setGameObserver(this);
        initializeView(gameModel.getGridX(), gameModel.getGridY());

        gameModel.initialize(needToInitialize);
        setOnAction();

        showAmountOfAliveCells(gameModel.getCellsAlive());
        showCurrentGameSpeed(gameModel.getGameSpeed());

        draw(gameModel.getGrid());
    }

    public void setData(int gridX, int gridY, int gameSpeed, boolean generateStartCivilization, List<Integer> aliveRuleSet, List<Integer> deadRuleSet) {
        gameModel.setGridX(gridX);
        gameModel.setGridY(gridY);
        gameModel.setGameSpeed(gameSpeed);
        gameModel.setGenerateStartCivilization(generateStartCivilization);
        gameModel.setAliveRules(aliveRuleSet.stream().mapToInt(i -> i).toArray());
        gameModel.setDeadRules(deadRuleSet.stream().mapToInt(i -> i).toArray());
    }

    public void uploadData(int gridX, int gridY, byte[][] grid, int gameSpeed, int generationsCount) {
        gameModel.setGridX(gridX);
        gameModel.setGridY(gridY);
        gameModel.setGrid(grid);
        gameModel.setGameSpeed(gameSpeed);
        gameModel.setGenerationsCount(generationsCount);
    }

    private void setOnAction() {
        play.setOnAction(event -> play());
        pause.setOnAction(event -> pause());
        generateNext.setOnAction(event -> generateNext());
        gameField.setOnMouseClicked(event -> setPointsInGrid(event.getX(), event.getY(), event));
        gameField.setOnMouseDragged(event -> setPointsInGrid(event.getX(), event.getY(), event));
        patternSelector.setOnMouseClicked(event -> showPatternWindow());
        increaseSpeed.setOnAction(event -> increaseSpeed());
        decreaseSpeed.setOnAction(event -> decreaseSpeed());
        zoomOut.setOnAction(event -> doZoomOut());
        zoomIn.setOnAction(event -> doZoomIn());
        gameField.setOnScroll(event -> detectScroll(event));
        drawingMode.setOnMouseClicked(event -> gameModel.setSelectedPattern());
        saveGame.setOnAction(event -> saveWindow.setVisible(true));
        clearField.setOnAction(event -> clearField());
        saveGameToFile.setOnAction(event -> {
            gameModel.saveGame(filename.getText());
            saveWindow.setVisible(false);
        });
        closeSaveWindow.setOnAction(event -> {
            saveWindow.setVisible(false);
            filename.setText("");
        });

        accordion.getPanes().forEach(titledPane -> {
            VBox vBox = (VBox) ((ScrollPane) titledPane.getContent()).getContent();
            vBox.getChildren().forEach(node -> {
                if (node instanceof ImageView imageView) {
                    imageView.setOnMouseClicked(gameModel::handleImageClick);
                }
            });
        });
    }

    private void play() {
        gameModel.startGame();
        setButtonDisabled(play, true);
        setButtonDisabled(pause, false);
        setButtonDisabled(generateNext, true);
    }

    private void pause() {
        gameModel.stopGame();
        setButtonDisabled(play, false);
        setButtonDisabled(pause, true);
        setButtonDisabled(generateNext, false);
    }

    private void generateNext() {
        if (gameModel.isGameStopped()) {
            gameModel.tick();
        }
    }

    private void clearField() {
        gameModel.setGenerationsCount(0);
        gameModel.setCellsAlive(0);
        showAmountOfAliveCells(0);
        showGenerationsCounter(0);
        gameModel.setGrid(new byte[gameModel.getGridX()][gameModel.getGridY()]);
        draw(gameModel.getGrid());
        pause();
    }

    public void increaseSpeed() {
        gameModel.increaseSpeed();
        showCurrentGameSpeed(gameModel.getGameSpeed());
    }

    public void decreaseSpeed() {
        gameModel.decreaseSpeed();
        showCurrentGameSpeed(gameModel.getGameSpeed());
    }

    private void setPointsInGrid(double eventX, double eventY, MouseEvent event) {
        int x = (int) (eventX / cellSize);
        int y = (int) (eventY / cellSize);
        if (gameModel.getSelectedPattern().equals("")) {
            if (x >= 0 && y >= 0 && x < gameModel.getGridX() && y < gameModel.getGridY()) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    gameModel.setPointsInGrid(gameModel.getGrid(), x, y, 1);
                    drawPoints(x, y, liveCellColor);
                } else if (event.getButton() == MouseButton.SECONDARY) {
                    gameModel.setPointsInGrid(gameModel.getGrid(), x, y, 0);
                    drawPoints(x, y, deadCellColor);
                }
                showAmountOfAliveCells(gameModel.getCellsAlive());
            }
        } else {
            try {
                gameModel.readPattern(x, y, gameModel.getSelectedPattern());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            draw(gameModel.getGrid());
        }
    }

    private void detectScroll(ScrollEvent event) {
        if (event.getDeltaY() < 0) {
            if (event.isControlDown())
                scrollPane.setVvalue(scrollPane.getVvalue() + 0.02);
            else if (event.isAltDown())
                scrollPane.setHvalue(scrollPane.getHvalue() + 0.1);
            else
                doZoomOut();
        } else {
            if (event.isControlDown())
                scrollPane.setVvalue(scrollPane.getVvalue() - 0.02);
            else if (event.isAltDown())
                scrollPane.setHvalue(scrollPane.getHvalue() - 0.1);
            else
                doZoomIn();
        }
    }

    @Override
    public void onTick() {
        showAmountOfAliveCells(gameModel.getCellsAlive());
        showGenerationsCounter(gameModel.getGenerationsCount());
        draw(gameModel.getGrid());
    }

    public void onClose() {
        gameModel.stopGame();
        gameModel.setGrid(null);

        if (graphics != null) {
            graphics.clearRect(0, 0, gameField.getWidth(), gameField.getHeight());
            graphics = null;
        }
    }
}
