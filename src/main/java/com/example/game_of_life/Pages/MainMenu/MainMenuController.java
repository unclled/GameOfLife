    package com.example.game_of_life.Pages.MainMenu;

    import com.example.game_of_life.Pages.Game.GameController;
    import javafx.application.Application;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.scene.control.Button;
    import javafx.scene.control.CheckBox;
    import javafx.scene.control.Slider;
    import javafx.scene.control.TextField;
    import javafx.scene.layout.VBox;
    import javafx.scene.text.Text;
    import javafx.stage.Stage;

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

        public CheckBox generateStart;

        private int gridX, gridY;

        @Override
        public void start(Stage stage) throws Exception {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/game_of_life/main_menu_window.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 674, 550);
            stage.setTitle("Игра \"Жизнь\"");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();

            init();
            setToDefault();
        }

        @Override
        public void init() {
            startWindow = new VBox();
            newGameWindow = new VBox();
            loadGameWindow = new VBox();
            helpWindow = new VBox();
            startGame = new Button();
            exitButton = new Button();
        }

        public void showNewGameWindow() {
            startWindow.setVisible(false);
            newGameWindow.setVisible(true);
            startGame.setVisible(true);
            /*ValidationSupport validationSupport = new ValidationSupport();
            validationSupport.setErrorDecorationEnabled(true); // включаем отображение ошибок
            validationSupport.registerValidator(rowInput, Validator.createRegexValidator("Поле должно быть заполнено целочисленным значением!", "\\d*", Severity.ERROR));*/
        }

        public void showLoadGameWindow() {
            startWindow.setVisible(false);
            loadGameWindow.setVisible(true);
            startGame.setVisible(true);
        }

        public void showHelpWindow() {
            startWindow.setVisible(false);
            helpWindow.setVisible(true);
        }

        public void setToDefault() {
            startWindow.setVisible(true);
            newGameWindow.setVisible(false);
            loadGameWindow.setVisible(false);
            helpWindow.setVisible(false);
            startGame.setVisible(false);
        }

        public void startGamePressed() throws Exception {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/game_of_life/game_window.fxml"));
            Parent root = fxmlLoader.load();
            Stage primaryStage = new Stage();
            Scene scene = new Scene(root, 1200, 800);
            primaryStage.setScene(scene);

            GameController gameController = fxmlLoader.getController();
            gameController.setGridX(Integer.parseInt(gameScreenX.getText()));
            gameController.setGridY(Integer.parseInt(gameScreenY.getText()));
            gameController.setGameSpeed((int) gameSpeed.getValue());
            gameController.setGenerateStartCivilization(generateStart.isSelected());
            gameController.initialize();
            primaryStage.show();
        }

        public void showRows() {
            gameScreenX.textProperty().addListener((observableValue, s, t1) -> {
                /* TODO при вводе значений в col row менять число у матрицы */
            });
        }

        public void showCols() {
            gameScreenY.textProperty().addListener((observableValue, s, t1) -> {
                /* TODO при вводе значений в col row менять число у матрицы */
            });
        }

        public void exitPressed() {
            System.exit(0);
        }

        private void tryAgain(String message) {

        }
    }
