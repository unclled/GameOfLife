package com.example.game_of_life.Pages.MainMenu;

import com.example.game_of_life.Pages.Game.GameController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public class MainMenuModel {
    private List<Integer> aliveRuleSet = new ArrayList<>();
    private List<Integer> deadRuleSet = new ArrayList<>();

    public void getSaves(ListView<String> saves) { //получаем все существующие сохранения
        File directory = new File("src/main/java/com/example/game_of_life/Saves");
        saves.getItems().clear();

        if (directory.exists() && directory.isDirectory()) {
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                if (file.isFile()) {
                    saves.getItems().add(file.getName());
                }
            }
        }
    }

    public boolean deleteSave(ListView<String> saves) { //удаляем выбранное сохранение
        var selectedItem = saves.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            File selectedFile = new File("src/main/java/com/example/game_of_life/Saves/" + selectedItem);
            selectedFile.delete();
            getSaves(saves);
            return true;
        }
        return false;
    }

    public boolean loadGamePressed(ListView<String> saves) { //загрузка выбранной игры
        var isSelected = saves
                .getSelectionModel()
                .getSelectedItem();
        if (isSelected == null) return false;

        File selectedFile = new File("src/main/java/com/example/game_of_life/Saves/" + isSelected);

        try (Scanner scanner = new Scanner(selectedFile)) {
            int gridX = Integer.parseInt(scanner.nextLine());
            int gridY = Integer.parseInt(scanner.nextLine());

            byte[][] grid = new byte[gridX][gridY];
            for (int i = 0; i < gridX; i++) {
                String line = scanner.nextLine();
                for (int j = 0; j < gridY; j++) {
                    char c = line.charAt(j);
                    grid[i][j] = (byte) (c - '0');
                }
            }

            int gameSpeed = Integer.parseInt(scanner.nextLine());
            int generationsCount = Integer.parseInt(scanner.nextLine());

            loadGame(gridX, gridY, grid, gameSpeed, generationsCount);
        } catch (IOException e) {
            System.out.println("Ошибка чтения");
            e.printStackTrace();
        }
        return true;
    }

    private void loadGame(int gridX, int gridY, byte[][] grid, int gameSpeed, int generationsCount) throws IOException {
        //инициализация сохраненной игры для игрового окна
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/game_of_life/game_window.fxml"));
        Parent root = fxmlLoader.load();
        Stage primaryStage = new Stage();
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Игра \"Жизнь\"");
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/background/patterns.png"))));
        GameController gameController = fxmlLoader.getController();

        getDefaultRules();
        gameController.uploadData(
                gridX,
                gridY,
                grid,
                gameSpeed,
                generationsCount,
                aliveRuleSet,
                deadRuleSet);
        gameController.showAmountOfAliveCells(generationsCount);
        gameController.initialize(false);
        primaryStage.setOnCloseRequest(event -> gameController.onClose());
        primaryStage.show();
    }

    public boolean startGamePressed(int gridX, int gridY, int gameSpeed, boolean generateStart) { //создание новой игры
        Preferences prefs = Preferences.userRoot();
        int maxFieldSize = getMaxFieldSize(prefs.getInt("CELLSIZE", 20));
        if (gridX > maxFieldSize || gridY > maxFieldSize || gridX < 3 || gridY < 3) return false;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/game_of_life/game_window.fxml"));
        Parent root;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage primaryStage = new Stage();
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Игра \"Жизнь\"");
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/background/patterns.png"))));
        GameController gameController = fxmlLoader.getController();

        getDefaultRules();
        gameController.setData(
                gridX,
                gridY,
                gameSpeed,
                generateStart,
                aliveRuleSet,
                deadRuleSet);
        gameController.initialize(true);
        primaryStage.setOnCloseRequest(event -> gameController.onClose());
        primaryStage.show();
        return true;
    }

    public void getDefaultRules() { //получить правила по умолчанию
        Preferences prefs = Preferences.userRoot();
        aliveRuleSet = Arrays.stream(prefs.get("ALIVERULESET", "2 3").split(" "))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        deadRuleSet = Arrays.stream(prefs.get("DEADRULESET", "3").split(" "))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    public void saveRules(Group aliveRulesSet, Group deadRulesSet) { //сохранить изменения в правилах
        Preferences prefs = Preferences.userRoot();
        aliveRuleSet.clear();
        deadRuleSet.clear();
        final int[] i = {0};

        //перебираем все чекбоксы для каждой из групп
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

        //обновляем значения в сохраненных данных
        String aliveRuleSetStr = aliveRuleSet.stream()
                .map(Object::toString)
                .collect(Collectors.joining(" "));
        prefs.put("ALIVERULESET", aliveRuleSetStr);

        String deadRuleSetStr = deadRuleSet.stream()
                .map(Object::toString)
                .collect(Collectors.joining(" "));
        prefs.put("DEADRULESET", deadRuleSetStr);
    }

    public int tryParseGridX(TextField gridX) {
        if (gridX.getText().matches("\\d+")) { //введены корректные данные
            return Integer.parseInt(gridX.getText());
        }
        return -1;
    }

    public int tryParseGridY(TextField gridY) {
        if (gridY.getText().matches("\\d+")) { //введены корректные данные
            return Integer.parseInt(gridY.getText());
        } return -1;
    }

    public List<Integer> getAliveRuleSet() {
        return aliveRuleSet;
    }

    public List<Integer> getDeadRuleSet() {
        return deadRuleSet;
    }

    public int getMaxFieldSize(double value) {
        return (int) (20.0 / value * 819);
    }
}
