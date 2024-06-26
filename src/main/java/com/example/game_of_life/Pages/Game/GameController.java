package com.example.game_of_life.Pages.Game;

import com.example.game_of_life.Pages.ShowWarning;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class GameController extends GameView implements GameObserver {
    private final GameModel gameModel = new GameModel();
    private ShowWarning showWarning;

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
        showWarning = new ShowWarning(warningPane, warningText, errorText);
        setOnAction();

        showAmountOfAliveCells(gameModel.getCellsAlive());
        showCurrentGameSpeed(gameModel.getGameSpeed());

        if (needToInitialize) {
            drawChanged(gameModel.getGrid(), gameModel.getChangedCells(), gameModel.getGridY());
        } else {
            fullDraw(gameModel.getGrid(), gameModel.getGridX(), gameModel.getGridY());
        }
    }

    public void setData(int gridX, int gridY, int gameSpeed, boolean generateStartCivilization, List<Integer> aliveRuleSet, List<Integer> deadRuleSet) {
        gameModel.setGridX(gridX);
        gameModel.setGridY(gridY);
        gameModel.setGameSpeed(gameSpeed);
        gameModel.setGenerateStartCivilization(generateStartCivilization);
        gameModel.setAliveRules(aliveRuleSet.stream().mapToInt(i -> i).toArray());
        gameModel.setDeadRules(deadRuleSet.stream().mapToInt(i -> i).toArray());
    }

    public void uploadData(int gridX, int gridY, byte[] grid, int gameSpeed, int generationsCount, List<Integer> aliveRuleSet, List<Integer> deadRuleSet) {
        gameModel.setGridX(gridX);
        gameModel.setGridY(gridY);
        gameModel.setGrid(grid);
        gameModel.setGameSpeed(gameSpeed);
        gameModel.setGenerationsCount(generationsCount);
        gameModel.setAliveRules(aliveRuleSet.stream().mapToInt(i -> i).toArray());
        gameModel.setDeadRules(deadRuleSet.stream().mapToInt(i -> i).toArray());
    }

    private void setOnAction() { //устанавливаем слушатели на взаимодействия
        play.setOnAction(event -> play());
        pause.setOnAction(event -> pause());
        generateNext.setOnAction(event -> generateNext());
        gameField.setOnMouseClicked(event -> setPointsInGrid(event.getX(), event.getY(), event));
        gameField.setOnMouseDragged(event -> {
            if (gameModel.getSelectedPattern().equals(""))
                setPointsInGrid(event.getX(), event.getY(), event);
        });
        patternSelector.setOnMouseClicked(event -> showPatternWindow());
        increaseSpeed.setOnAction(event -> increaseSpeed());
        decreaseSpeed.setOnAction(event -> decreaseSpeed());
        zoomOut.setOnAction(event -> doZoomOut());
        zoomIn.setOnAction(event -> doZoomIn());
        gameField.setOnScroll(this::detectScroll);
        drawingMode.setOnMouseClicked(event -> gameModel.setSelectedPattern());
        saveGame.setOnAction(event -> {
            pause();
            saveWindow.setVisible(true);
        });
        clearField.setOnAction(event -> clearField());
        saveGameToFile.setOnAction(event -> saveGameToFile());
        closeSaveWindow.setOnAction(event -> {
            saveWindow.setVisible(false);
            filename.setText("");
        });

        accordion.getPanes().forEach(titledPane -> {
            VBox vBox = (VBox) ((ScrollPane) titledPane.getContent()).getContent();
            vBox.getChildren().forEach(node -> {
                if (node instanceof ImageView imageView) {
                    imageView.setOnMouseClicked(event -> {
                        if (gameModel.handleImageClick(event)) {
                            showWarning.showWarning("Выбранный паттерн превышает размеры поля!", "Внимание!");
                        }
                    });
                }
            });
        });
    }

    private void saveGameToFile() {
        if (!gameModel.saveGame(filename.getText())) {
            showWarning.showWarning("Укажите имя для сохранения!", "Ошибка!");
        } else {
            saveWindow.setVisible(false);
        }
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
            gameModel.newGeneration();
        }
    }

    private void clearField() {
        gameModel.setGenerationsCount(0);
        gameModel.setCellsAlive(0);
        showAmountOfAliveCells(0);
        showGenerationsCounter(0);
        gameModel.setGrid(new byte[gameModel.getGridX() * gameModel.getGridY()]);
        fullDraw(gameModel.getGrid(), gameModel.getGridY(), gameModel.getGridX());
        pause();
    }

    private void increaseSpeed() {
        gameModel.increaseSpeed();
        showCurrentGameSpeed(gameModel.getGameSpeed());
    }

    private void decreaseSpeed() {
        gameModel.decreaseSpeed();
        showCurrentGameSpeed(gameModel.getGameSpeed());
    }

    private void setPointsInGrid(double eventX, double eventY, MouseEvent event) {
        int x = (int) (eventX / cellSize);
        int y = (int) (eventY / cellSize);
        if (gameModel.getSelectedPattern().equals("")) { //если паттерн не выбран
            if (x >= 0 && y >= 0 && x < gameModel.getGridX() && y < gameModel.getGridY()) { //точка ставится в границах поля
                if (event.getButton() == MouseButton.PRIMARY) { //нажата ЛКМ
                    gameModel.setPointsInGrid(gameModel.getGrid(), x, y, 1);
                    drawPoints(x, y, liveCellColor);
                } else if (event.getButton() == MouseButton.SECONDARY) { //нажата ПКМ
                    gameModel.setPointsInGrid(gameModel.getGrid(), x, y, 0);
                    drawPoints(x, y, deadCellColor);
                }
                showAmountOfAliveCells(gameModel.getCellsAlive());
            }
        } else { //паттерн выбран, размещаем его на поле
            try {
                gameModel.readPattern(x, y, gameModel.getSelectedPattern());
                showAmountOfAliveCells(gameModel.getCellsAlive());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            fullDraw(gameModel.getGrid(), gameModel.getGridY(), gameModel.getGridX());
        }
    }

    private void detectScroll(ScrollEvent event) {
        if (event.getDeltaY() < 0) { //скролл вниз
            if (event.isControlDown()) //комбинация ctrl + scrolldown
                scrollPane.setVvalue(scrollPane.getVvalue() + 0.02);
            else if (event.isAltDown()) //комбинация alt + scrolldown
                scrollPane.setHvalue(scrollPane.getHvalue() + 0.1);
            else
                doZoomOut();
        } else { //скролл вверх
            if (event.isControlDown()) //комбинация ctrl + scrollup
                scrollPane.setVvalue(scrollPane.getVvalue() - 0.02);
            else if (event.isAltDown()) //комбинация alt + scrollup
                scrollPane.setHvalue(scrollPane.getHvalue() - 0.1);
            else
                doZoomIn();
        }
    }

    @Override
    public void onTick() {
        showAmountOfAliveCells(gameModel.getCellsAlive());
        showGenerationsCounter(gameModel.getGenerationsCount());
        drawChanged(gameModel.getGrid(), gameModel.getChangedCells(), gameModel.getGridY());
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