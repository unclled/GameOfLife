package com.example.game_of_life.Pages.Game;

import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

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
    //--------------------------//

    public GameController() {}

    private void setButtonsOnAction() {
        play.setOnAction(l -> play());
        pause.setOnAction(l -> pause());
        generateNext.setOnAction(l -> generateNext());
        gameField.setOnMouseClicked(event -> setPointsInGrid(event.getX(), event.getY(), event));
        gameField.setOnMouseDragged(event -> setPointsInGrid(event.getX(), event.getY(), event));
        increaseSpeed.setOnAction(event -> increaseSpeed());
        decreaseSpeed.setOnAction(event -> decreaseSpeed());
        zoomOut.setOnAction(event -> zoomOut());
        zoomIn.setOnAction(event -> zoomIn());
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

    public void play() {
        gameModel.startGame();
        setButtonDisabled(play, true);
        setButtonDisabled(pause, false);
        setButtonDisabled(generateNext, true);
    }

    public void pause() {
        gameModel.stopGame();
        setButtonDisabled(play, false);
        setButtonDisabled(pause, true);
        setButtonDisabled(generateNext, false);
    }

    public void generateNext() {
        if (gameModel.isGameStopped()) {
            gameModel.tick();
        }
    }

    public void setPointsInGrid(double eventX, double eventY, MouseEvent event) {
        int x = (int) (eventX / cellSize);
        int y = (int) (eventY / cellSize);
        if (x >= 0 && y >= 0 && x < gameModel.getGridX() && y < gameModel.getGridY()) {
            if (event.getButton() == MouseButton.PRIMARY) {
                gameModel.setPointsInGrid(gameModel.getGrid(), x, y, 1);
/*                try {
                    gameModel.readPattern(x, y);
                } catch (IOException e) {
                    throw new RuntimeException(e); РИСОВАТЬ ПАТТЕРН
                }
                draw(gameModel.getGrid());*/
                drawPoints(x, y, liveCellColor);
            }
            else if (event.getButton() == MouseButton.SECONDARY) {
                gameModel.setPointsInGrid(gameModel.getGrid(), x, y, 0);
                drawPoints(x, y, deadCellColor);
            }
            showAmountOfAliveCells(gameModel.getCellsAlive());
        }
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

    public void zoomOut() {
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
