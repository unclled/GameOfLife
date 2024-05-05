package com.example.game_of_life.Pages.MainMenu;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class MainMenuView {
    //----------Контейнеры----------//
    @FXML public VBox startWindow;
    @FXML public VBox newGameWindow;
    @FXML public VBox loadGameWindow;
    @FXML public ScrollPane infoWindow;
    @FXML public VBox settingsWindow;
    @FXML public VBox rulesWindow;
    @FXML public Pane warningPane;

    //-----Текст и текстовые поля-----//
    @FXML public Text gridXVisualize;
    @FXML public Text gridYVisualize;
    @FXML public Text fieldSizeText;
    @FXML public Text warningText;
    @FXML public Text errorText;
    @FXML public TextField gridX;
    @FXML public TextField gridY;

    //-----------Ползунки-----------//
    @FXML public Slider gameSpeed;
    @FXML public Slider cellSizeSlider;

    //-----------Выбор цвета-----------//
    @FXML public ColorPicker deadCellColor;
    @FXML public ColorPicker liveCellColor;

    //-----------Группы-----------//
    @FXML public Group liveGroup;
    @FXML public Group deadGroup;
    @FXML public Group aliveRulesSet;
    @FXML public Group deadRulesSet;

    @FXML public CheckBox generateStart;

    @FXML ListView<String> saves;

    @FXML public Button goBack;

    public void showNewGameWindow() {
        goBack.setVisible(true);
        startWindow.setVisible(false);
        newGameWindow.setVisible(true);

        /* устанавливаем дополнительные слушатели для визуального отображения
         * и отслеживания корректного ввода в текстовые поля */
        gridX.textProperty().addListener((observableValue, s, t1) -> {
            if (gridX.getLength() <= 4)
                gridXVisualize.setText(t1);
        });
        gridY.textProperty().addListener((observableValue, s, t1) -> {
            if (gridY.getLength() <= 4)
                gridYVisualize.setText(t1);
        });
        gridX.setTextFormatter(new TextFormatter<Integer>(change -> {
            if (!(change.getControlNewText().matches("\\d{0,4}"))) {
                return null;
            } else {
                return change;
            }
        }));
        gridY.setTextFormatter(new TextFormatter<Integer>(change -> {
            if (!(change.getControlNewText().matches("\\d{0,4}"))) {
                return null;
            } else {
                return change;
            }
        }));
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

    public void setToDefault() { //устанавливаем значения по умолчанию
        startWindow.setVisible(true);
        newGameWindow.setVisible(false);
        loadGameWindow.setVisible(false);
        infoWindow.setVisible(false);
        goBack.setVisible(false);
        settingsWindow.setVisible(false);
        rulesWindow.setVisible(false);
    }

    private void resetData() { //сброс ввода для новой игры
        gridX.clear();
        gridY.clear();
        gridXVisualize.setText("");
        gridYVisualize.setText("");
        gameSpeed.setValue(50);
        generateStart.setSelected(false);
    }

    //перекрашиваем квадрат предпросмотра
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