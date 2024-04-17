package com.example.game_of_life.Pages.MainMenu;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.prefs.Preferences;

public class MainMenuController extends MainMenuView {
    private final MainMenuModel mainMenuModel = new MainMenuModel();

    //--------------------------//
    public Button newGameButton;
    public Button loadButton;
    public Button infoButton;
    public Button exitButton;
    public Button startGame;
    public Button settingsButton;
    public Button changeRulesButton;
    public Button confirmRules;
    public Button deleteSave;
    public Button loadGame;
    //--------------------------//

    public void initialize() {
        setOnAction();
        setToDefault();
    }

    public void setOnAction() { //устанавливаем слушатели на взаимодействия
        newGameButton.setOnAction(event -> showNewGameWindow());
        loadButton.setOnAction(event -> {
            showLoadGameWindow();
            mainMenuModel.getSaves(saves);
        });
        infoButton.setOnAction(event -> showInfoWindow());
        exitButton.setOnAction(event -> System.exit(0));
        settingsButton.setOnAction(event -> showSettingsWindow());
        startGame.setOnAction(event -> mainMenuModel.startGamePressed(
                mainMenuModel.tryParseGridX(gridX),
                mainMenuModel.tryParseGridY(gridY),
                (int) gameSpeed.getValue(),
                generateStart.isSelected()));
        changeRulesButton.setOnAction(event -> showRulesWindow());
        confirmRules.setOnAction(event -> saveRules());
        deleteSave.setOnAction(event -> mainMenuModel.deleteSave(saves));
        loadGame.setOnAction(event -> mainMenuModel.loadGamePressed(saves));
        goBack.setOnAction(event -> setToDefault());
    }

    public void showSettingsWindow() {
        goBack.setVisible(true);
        startWindow.setVisible(false);
        settingsWindow.setVisible(true);

        Preferences prefs = Preferences.userRoot(); //получаем сохраненные данные
        liveCellColor.setValue(Color.valueOf(prefs.get("LIVECELLCOLOR", "white")));
        deadCellColor.setValue(Color.valueOf(prefs.get("DEADCELLCOLOR", "black")));
        cellSizeSlider.setValue(Integer.parseInt(prefs.get("CELLSIZE", "20")));
        int maxFieldSize = mainMenuModel.getMaxFieldSize(cellSizeSlider.getValue());
        fieldSizeText.setText("Максимальный размер поля: " + maxFieldSize + "x" + maxFieldSize);
        liveExampleColor(liveCellColor.getValue());
        deadExampleColor(deadCellColor.getValue());

        liveCellColor.setOnAction((ActionEvent e) -> { //слушатель на изменения
            Color color = liveCellColor.getValue();
            liveExampleColor(color);
            prefs.put("LIVECELLCOLOR", color.toString());
        });

        deadCellColor.setOnAction((ActionEvent e) -> { //слушатель на изменения
            Color color = deadCellColor.getValue();
            deadExampleColor(color);
            prefs.put("DEADCELLCOLOR", color.toString());
        });

        cellSizeSlider.valueProperty().addListener((observableValue, number, t1) -> { //слушатель на изменения
            double cellSize = cellSizeSlider.getValue();
            int temp = mainMenuModel.getMaxFieldSize(cellSize);
            prefs.put("CELLSIZE", String.valueOf((int) cellSize));
            fieldSizeText.setText("Максимальный размер поля: " + temp + "x" + temp);
        });
    }

    public void showRulesWindow() {
        mainMenuModel.getDefaultRules();

        final int[] i = {0};
        List<Integer> aliveRuleSet = mainMenuModel.getAliveRuleSet();
        List<Integer> deadRuleSet = mainMenuModel.getDeadRuleSet();

        //Устанавливаем чекбоксы для правил, на основе сохраненных данных
        aliveRulesSet.getChildren().forEach(node -> {
            if (node instanceof CheckBox && i[0] < aliveRuleSet.size()) {
                if (node.getId().equals("alive" + aliveRuleSet.get(i[0]).toString())) {
                    ((CheckBox) node).setSelected(true);
                    i[0]++;
                }
            }
        });

        i[0] = 0;
        deadRulesSet.getChildren().forEach(node -> {
            if (node instanceof CheckBox && i[0] < deadRuleSet.size()) {
                if (node.getId().equals("dead" + deadRuleSet.get(i[0]).toString())) {
                    ((CheckBox) node).setSelected(true);
                    i[0]++;
                }
            }
        });

        rulesWindow.setVisible(true);
        settingsWindow.setDisable(true);
        goBack.setDisable(true);
    }

    public void saveRules() {
        mainMenuModel.saveRules(aliveRulesSet, deadRulesSet);
        rulesWindow.setVisible(false);
        settingsWindow.setDisable(false);
        goBack.setDisable(false);
    }

    private void showInfoWindow() {
        goBack.setVisible(true);
        startWindow.setVisible(false);
        infoWindow.setVisible(true);
    }
}