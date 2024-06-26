module com.example.game_of_life {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.prefs;

    opens com.example.game_of_life to javafx.fxml;
    exports com.example.game_of_life.Pages.MainMenu;
    exports com.example.game_of_life.Pages.Game;
    exports com.example.game_of_life;
    opens com.example.game_of_life.Pages.MainMenu;
}