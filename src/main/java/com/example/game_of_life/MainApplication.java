package com.example.game_of_life;

import com.example.game_of_life.Pages.MainMenu.MainMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/game_of_life/main_menu_window.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 674, 550);
        stage.setScene(scene);
        stage.setTitle("Игра \"Жизнь\"");
        stage.setResizable(false);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/background/patterns.png")));

        MainMenuController mainMenuController = new MainMenuController();
        stage.show();
    }
}