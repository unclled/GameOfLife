package com.example.game_of_life.Pages.MainMenu;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class MainMenuView {
    @FXML public TextField gridX;
    @FXML public TextField gridY;

    @FXML public Text gridXVisualize;
    @FXML public Text gridYVisualize;
    @FXML public Text fieldSizeText;

    @FXML public Slider gameSpeed;
    @FXML public Slider cellSizeSlider;

    @FXML public VBox startWindow;
    @FXML public VBox newGameWindow;
    @FXML public VBox loadGameWindow;
    @FXML public VBox infoWindow;
    @FXML public VBox settingsWindow;
    @FXML public VBox rulesWindow;

    @FXML public Button goBack;

    @FXML public ColorPicker deadCellColor;
    @FXML public ColorPicker liveCellColor;

    @FXML public Group liveGroup;
    @FXML public Group deadGroup;
    @FXML public Group aliveRulesSet;
    @FXML public Group deadRulesSet;

    @FXML public CheckBox generateStart;

    @FXML ListView saves;

    public void showNewGameWindow() {
        goBack.setVisible(true);
        startWindow.setVisible(false);
        newGameWindow.setVisible(true);
        gridX.textProperty().addListener((observableValue, s, t1) -> gridXVisualize.setText(t1));
        gridY.textProperty().addListener((observableValue, s, t1) -> gridYVisualize.setText(t1));
        resetData();
    }

    public void showLoadGameWindow() {
        goBack.setVisible(true);
        startWindow.setVisible(false);
        loadGameWindow.setVisible(true);
    }

    public void showRulesWindow() {
        goBack.setVisible(true);
        startWindow.setVisible(false);
        infoWindow.setVisible(true);
    }

    public void setToDefault() {
        startWindow.setVisible(true);
        newGameWindow.setVisible(false);
        loadGameWindow.setVisible(false);
        infoWindow.setVisible(false);
        goBack.setVisible(false);
        settingsWindow.setVisible(false);
        rulesWindow.setVisible(false);
    }

    private void resetData() {
        gridX.clear();
        gridY.clear();
        gridXVisualize.setText("");
        gridYVisualize.setText("");
        gameSpeed.setValue(50);
        generateStart.setSelected(false);
    }

    public void liveExampleColor(Color live) {
        liveGroup.getChildren().forEach(node -> {
            if (node instanceof Rectangle) {
                ((Rectangle) node).setFill(live);
            }
        });
    }

    public void deadExampleColor(Color dead) {
        deadGroup.getChildren().forEach(node -> {
            if (node instanceof Rectangle) {
                ((Rectangle) node).setFill(dead);
            }
        });
    }
}
