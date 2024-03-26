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
    import javafx.stage.Stage;

    import java.util.prefs.Preferences;

    public class MainMenuController extends Application {
        public TextField gameScreenX;
        public TextField gameScreenY;
        public Text gsXshow;
        public Text gsYshow;

        public Slider gameSpeed;
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
            startGame = (Button) scene.lookup("#startGame");
            goBack = (Button) scene.lookup("#goBack");
            liveCellColor = (ColorPicker) scene.lookup("#liveCellColor");
            deadCellColor = (ColorPicker) scene.lookup("#deadCellColor");
            liveGroup = (Group) scene.lookup("#liveGroup");
            deadGroup = (Group) scene.lookup("#deadGroup");
            settingsWindow = (VBox) scene.lookup("#settingsWindow");
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

            Preferences colorsAndRules = Preferences.userRoot();
            liveCellColor.setValue(Color.valueOf(colorsAndRules.get("LIVECELLCOLOR", "white")));
            deadCellColor.setValue(Color.valueOf(colorsAndRules.get("DEADCELLCOLOR", "black")));
            liveExampleColor(liveCellColor.getValue());
            deadExampleColor(deadCellColor.getValue());

            liveCellColor.setOnAction((ActionEvent e) -> {
                Color color = liveCellColor.getValue();
                liveExampleColor(color);
                colorsAndRules.put("LIVECELLCOLOR", color.toString());
            });

            deadCellColor.setOnAction((ActionEvent e) -> {
                Color color = deadCellColor.getValue();
                deadExampleColor(color);
                colorsAndRules.put("DEADCELLCOLOR", color.toString());
            });
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

        public void startGamePressed() throws Exception {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/game_of_life/game_window.fxml"));
            Parent root = fxmlLoader.load();
            Stage primaryStage = new Stage();
            Scene scene = new Scene(root, 1200, 800);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Игра \"Жизнь\"");
            primaryStage.setResizable(false);
            GameController gameController = fxmlLoader.getController();

            gameController.setData(
                    Integer.parseInt(gameScreenX.getText()),
                    Integer.parseInt(gameScreenY.getText()),
                    (int) gameSpeed.getValue(),
                    generateStart.isSelected());
            gameController.initialize();
            primaryStage.setOnCloseRequest(event -> gameController.onClose());
            primaryStage.show();
        }

        public void exitPressed() {
            System.exit(0);
        }
    }

