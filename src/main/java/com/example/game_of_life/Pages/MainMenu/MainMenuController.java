    package com.example.game_of_life.Pages.MainMenu;

    import com.example.game_of_life.Pages.Game.GameController;
    import javafx.application.Application;
    import javafx.event.ActionEvent;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Group;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.scene.control.*;
    import javafx.scene.layout.VBox;
    import javafx.scene.paint.Color;
    import javafx.scene.shape.Rectangle;
    import javafx.scene.text.Text;
    import javafx.stage.FileChooser;
    import javafx.stage.Stage;

    import java.io.File;
    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.List;
    import java.util.Scanner;
    import java.util.prefs.Preferences;
    import java.util.stream.Collectors;

    public class MainMenuController extends Application {
        public TextField gameScreenX;
        public TextField gameScreenY;
        public Text gsXshow;
        public Text gsYshow;

        public Slider gameSpeed;
        public Slider cellSizeSlider;
        public VBox startWindow;
        public VBox newGameWindow;
        public VBox loadGameWindow;
        public VBox helpWindow;

        public Button newGameButton;
        public Button loadButton;
        public Button helpButton;
        public Button exitButton;
        public Button startGame;
        public Button goBack;

        public CheckBox generateStart;
        public Button settingsButton;
        public VBox settingsWindow;
        public ColorPicker deadCellColor;
        public ColorPicker liveCellColor;
        public Group liveGroup;
        public Group deadGroup;

        public Button changeRulesButton;
        public VBox rulesWindow;
        public Group aliveRulesSet;
        public Group deadRulesSet;
        public Button confirmRules;

        List<Integer> aliveRuleSet = new ArrayList<>();
        List<Integer> deadRuleSet = new ArrayList<>();

        @Override
        public void start(Stage stage) throws Exception {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/game_of_life/main_menu_window.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 674, 550);
            stage.setTitle("Игра \"Жизнь\"");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();

            init(scene);
            setToDefault();
        }

        public void init(Scene scene) {
            startWindow = (VBox) scene.lookup("#startWindow");
            newGameWindow = (VBox) scene.lookup("#newGameWindow");
            loadGameWindow = (VBox) scene.lookup("#loadGameWindow");
            helpWindow = (VBox) scene.lookup("#helpWindow");
            settingsWindow = (VBox) scene.lookup("#settingsWindow");
            rulesWindow = (VBox) scene.lookup("#rulesWindow");

            startGame = (Button) scene.lookup("#startGame");
            goBack = (Button) scene.lookup("#goBack");

            liveCellColor = (ColorPicker) scene.lookup("#liveCellColor");
            deadCellColor = (ColorPicker) scene.lookup("#deadCellColor");

            liveGroup = (Group) scene.lookup("#liveGroup");
            deadGroup = (Group) scene.lookup("#deadGroup");
            aliveRulesSet = (Group) scene.lookup("#aliveRulesSet");
            deadRulesSet = (Group) scene.lookup("#deadRulesSet");
        }

        public void showNewGameWindow() {
            goBack.setVisible(true);
            startWindow.setVisible(false);
            newGameWindow.setVisible(true);
            startGame.setVisible(true);
            gameScreenX.textProperty().addListener((observableValue, s, t1) -> gsXshow.setText(t1));
            gameScreenY.textProperty().addListener((observableValue, s, t1) -> gsYshow.setText(t1));
            resetData();
        }

        public void showLoadGameWindow() {
            goBack.setVisible(true);
            startWindow.setVisible(false);
            loadGameWindow.setVisible(true);
            startGame.setVisible(true);
            loadGame();
        }

        public void showHelpWindow() {
            goBack.setVisible(true);
            startWindow.setVisible(false);
            helpWindow.setVisible(true);
        }

        public void setToDefault() {
            startWindow.setVisible(true);
            newGameWindow.setVisible(false);
            loadGameWindow.setVisible(false);
            helpWindow.setVisible(false);
            startGame.setVisible(false);
            goBack.setVisible(false);
            settingsWindow.setVisible(false);
            rulesWindow.setVisible(false);
        }

        private void resetData() {
            gameScreenX.clear();
            gameScreenY.clear();
            gsXshow.setText("");
            gsYshow.setText("");
            gameSpeed.setValue(50);
            generateStart.setSelected(false);
        }

        public void showSettingsWindow() {
            goBack.setVisible(true);
            startWindow.setVisible(false);
            settingsWindow.setVisible(true);

            Preferences prefs = Preferences.userRoot();
            liveCellColor.setValue(Color.valueOf(prefs.get("LIVECELLCOLOR", "white")));
            deadCellColor.setValue(Color.valueOf(prefs.get("DEADCELLCOLOR", "black")));
            cellSizeSlider.setValue(Integer.parseInt(prefs.get("CELLSIZE", "20")));
            liveExampleColor(liveCellColor.getValue());
            deadExampleColor(deadCellColor.getValue());

            liveCellColor.setOnAction((ActionEvent e) -> {
                Color color = liveCellColor.getValue();
                liveExampleColor(color);
                prefs.put("LIVECELLCOLOR", color.toString());
            });

            deadCellColor.setOnAction((ActionEvent e) -> {
                Color color = deadCellColor.getValue();
                deadExampleColor(color);
                prefs.put("DEADCELLCOLOR", color.toString());
            });

            cellSizeSlider.valueProperty().addListener((observableValue, number, t1) ->
                    prefs.put("CELLSIZE", String.valueOf((int) cellSizeSlider.getValue())));
        }

        private void liveExampleColor(Color live) {
            liveGroup.getChildren().forEach(node -> {
                if (node instanceof Rectangle) {
                    ((Rectangle) node).setFill(live);
                }
            });
        }

        private void deadExampleColor(Color dead) {
            deadGroup.getChildren().forEach(node -> {
                if (node instanceof Rectangle) {
                    ((Rectangle) node).setFill(dead);
                }
            });
        }

        public void loadGame() { /* TODO ДОДЕЛАТЬ */
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Game File");

            Stage stage = new Stage();

            File selectedFile = fileChooser.showOpenDialog(stage);

            if (selectedFile != null) {
                try (Scanner scanner = new Scanner(selectedFile)) {
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    System.out.println("Ошибка чтения");
                    e.printStackTrace();
                }
            }
        }

        public void startGamePressed() throws Exception {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/game_of_life/game_window.fxml"));
            Parent root = fxmlLoader.load();
            Stage primaryStage = new Stage();
            Scene scene = new Scene(root, 1200, 800);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Игра \"Жизнь\"");
            primaryStage.setResizable(false);
            GameController gameController = fxmlLoader.getController();

            getDefaultRules();
            gameController.setData(
                    Integer.parseInt(gameScreenX.getText()),
                    Integer.parseInt(gameScreenY.getText()),
                    (int) gameSpeed.getValue(),
                    generateStart.isSelected(),
                    aliveRuleSet,
                    deadRuleSet);
            gameController.initialize();
            primaryStage.setOnCloseRequest(event -> gameController.onClose());
            primaryStage.show();
        }

        public void exitPressed() {
            System.exit(0);
        }

        private void getDefaultRules() {
            Preferences prefs = Preferences.userRoot();
            aliveRuleSet = Arrays.stream(prefs.get("ALIVERULESET", "2 3").split(" "))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            deadRuleSet = Arrays.stream(prefs.get("DEADRULESET", "3").split(" "))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }

        public void showRulesWindow() {
            getDefaultRules();

            final int[] i = {0};

            aliveRulesSet.getChildren().forEach(node -> {
                if (node instanceof CheckBox&& i[0] < aliveRuleSet.size()) {
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
            Preferences prefs = Preferences.userRoot();
            aliveRuleSet.clear();
            deadRuleSet.clear();
            final int[] i = {0};

            aliveRulesSet.getChildren().forEach(node -> {
                if (node instanceof CheckBox) {
                    if (((CheckBox) node).isSelected()) {
                        aliveRuleSet.add(i[0]);
                    }
                    i[0]++;
                }
            });

            i[0] = 0;
            deadRulesSet.getChildren().forEach(node -> {
                if (node instanceof CheckBox) {
                    if (((CheckBox) node).isSelected()) {
                        deadRuleSet.add(i[0]);
                    }
                    i[0]++;
                }
            });
            String aliveRuleSetStr = aliveRuleSet.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(" "));
            prefs.put("ALIVERULESET", aliveRuleSetStr);

            String deadRuleSetStr = deadRuleSet.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(" "));
            prefs.put("DEADRULESET", deadRuleSetStr);



            rulesWindow.setVisible(false);
            settingsWindow.setDisable(false);
            goBack.setDisable(false);
        }
    }