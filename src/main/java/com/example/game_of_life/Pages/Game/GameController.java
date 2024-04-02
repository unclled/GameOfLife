package com.example.game_of_life.Pages.Game;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

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
    //--------------------------//

    public GameController() {}

    private void setButtonsOnAction() {
        play.setOnAction(l -> play());
        pause.setOnAction(l -> pause());
        generateNext.setOnAction(l -> generateNext());
        gameField.setOnMouseClicked(event -> setPointsInGrid(event.getX(), event.getY(), event));
        gameField.setOnMouseDragged(event -> setPointsInGrid(event.getX(), event.getY(), event));
        patternSelector.setOnMouseClicked(event -> showPatternsList(event.getX(), event.getY()));
        increaseSpeed.setOnAction(event -> increaseSpeed());
        decreaseSpeed.setOnAction(event -> decreaseSpeed());
        zoomOut.setOnAction(event -> zoomOut());
        zoomIn.setOnAction(event -> zoomIn());
        drawingMode.setOnMouseClicked(event -> gameModel.setSelectedPattern());
        for (Node node : oscilators.getChildren()) {
            if (node instanceof ImageView) {
                ImageView imageView = (ImageView) node;
                imageView.setOnMouseClicked(event -> gameModel.handleImageClick(event));
            }
        }
    }

    public void setData(int gridX, int gridY, int gameSpeed, boolean generateStartCivilization, List<Integer> aliveRuleSet, List<Integer> deadRuleSet) {
        gameModel.setGridX(gridX);
        gameModel.setGridY(gridY);
        gameModel.setGameSpeed(gameSpeed);
        gameModel.setGenerateStartCivilization(generateStartCivilization);
        gameModel.setAliveRules(aliveRuleSet.stream().mapToInt(i->i).toArray());
        gameModel.setDeadRules(deadRuleSet.stream().mapToInt(i->i).toArray());
    }

    public void initialize() {
        gameModel.setGameObserver(this);
        initializeView(gameModel.getGridX(), gameModel.getGridY());
        gameModel.initialize();
        setButtonsOnAction();

        showAmountOfAliveCells(gameModel.getCellsAlive());
        showCurrentGameSpeed(gameModel.getGameSpeed());

        draw(gameModel.getGrid());
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

    private void showPatternsList(double eventX, double eventY) {
        showPatternWindow();

    }

    public void increaseSpeed() {
        gameModel.increaseSpeed();
        showCurrentGameSpeed(gameModel.getGameSpeed());
    }

    public void decreaseSpeed() {
        gameModel.decreaseSpeed();
        showCurrentGameSpeed(gameModel.getGameSpeed());
    }

    private void zoomIn() {
        doZoomIn();
    }

    private void zoomOut() {
        doZoomOut();
    }

    @Override
    public void onTick() {
        showAmountOfAliveCells(gameModel.getCellsAlive());
        showGenerationsCounter(gameModel.getGenerationsCount());
        draw(gameModel.getGrid());
    }

    public void onClose() {
        gameModel.stopAnimator();
        gameModel.setGrid(null);
    }
}
