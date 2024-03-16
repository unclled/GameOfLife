package com.example.game_of_life.Pages.Game;

import javafx.scene.control.Button;

public class GameController extends GameView implements GameObserver {
    private GameModel gameModel = new GameModel();

    //--------------------------//
    public Button play;
    public Button pause;
    public Button generateNext;
    //--------------------------//

    public GameController() {}

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
        draw(gameModel.getGrid());
    }

    private void setButtonsOnAction() {
        play.setOnAction(l -> play());
        pause.setOnAction(l -> pause());
        generateNext.setOnAction(l -> generateNext());
        gameField.setOnMouseClicked(event -> setPointsInGrid(event.getX(), event.getY()));
    }

    @Override
    public void onTick() {
        draw(gameModel.getGrid());
    }
}
