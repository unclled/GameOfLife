package com.example.game_of_life.Pages.Game;

import javafx.scene.control.Button;

public class GameController extends GameView implements GameObserver {
    private GameModel gameModel = new GameModel();

    //--------------------------//
    public Button play;
    public Button pause;
    public Button generateNext;
    public Button increaseSpeed;
    public Button decreaseSpeed;
    //--------------------------//

    public GameController() {}

    private void setButtonsOnAction() {
        play.setOnAction(l -> play());
        pause.setOnAction(l -> pause());
        generateNext.setOnAction(l -> generateNext());
        gameField.setOnMouseClicked(event -> setPointsInGrid(event.getX(), event.getY()));
        increaseSpeed.setOnAction(event -> increaseSpeed());
        decreaseSpeed.setOnAction(event -> decreaseSpeed());
    }

    public void setData(int gridX, int gridY, int gameSpeed, boolean generateStartCivilization) {
        gameModel.setGridX(gridX);
        gameModel.setGridY(gridY);
        gameModel.setGameSpeed(gameSpeed);
        gameModel.setGenerateStartCivilization(generateStartCivilization);
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

    public void setPointsInGrid(double eventX, double eventY) {
        double cellSizeX = gameField.getWidth() / gameModel.getGridX();
        double cellSizeY = gameField.getHeight() / gameModel.getGridY();

        int x = (int) (eventX / cellSizeX);
        int y = (int) (eventY / cellSizeY);

        gameModel.setPointsInGrid(gameModel.getGrid(), x, y);
        showAmountOfAliveCells(gameModel.getCellsAlive());
        draw(gameModel.getGrid());
    }

    public void increaseSpeed() {
        gameModel.increaseSpeed();
        showCurrentGameSpeed(gameModel.getGameSpeed());
    }

    public void decreaseSpeed() {
        gameModel.decreaseSpeed();
        showCurrentGameSpeed(gameModel.getGameSpeed());
    }

    @Override
    public void onTick() {
        showAmountOfAliveCells(gameModel.getCellsAlive());
        draw(gameModel.getGrid());
    }
}
