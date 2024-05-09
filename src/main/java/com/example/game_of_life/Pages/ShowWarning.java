package com.example.game_of_life.Pages;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class ShowWarning {
    private final Pane warningPane;

    private final Text warningText;
    private final Text errorText;

    private TranslateTransition translateTransitionShow;
    private TranslateTransition translateTransitionHide;

    private PauseTransition delay;

    private boolean isShowing;

    public ShowWarning(Pane warningPane, Text warningText, Text errorText) {
        this.warningPane = warningPane;
        this.warningText = warningText;
        this.errorText = errorText;
    }

    public void showWarning(String message, String error) {
        if (isShowing) { //если уже показывается предупреждение, останавливаем текущую анимацию и таймеры
            if (translateTransitionShow != null) { //останавливаем текущую анимацию показа и скрытия
                translateTransitionShow.stop();
            }
            if (translateTransitionHide != null) {
                translateTransitionHide.stop();
            }

            if (delay != null) { //останавливаем задержку
                delay.stop();
            }

            warningPane.setVisible(false); //скрываем панель предупреждения
        }

        isShowing = true;
        warningText.setText(message);
        errorText.setText(error);
        warningPane.setVisible(true);

        double startY = warningPane.getLayoutY() - warningPane.getBoundsInParent().getHeight();

        //создание анимации показа
        translateTransitionShow = new TranslateTransition(Duration.seconds(0.5), warningPane);
        warningPane.setLayoutY(startY);
        translateTransitionShow.setToY(-10 - startY);

        //после завершения анимации показа
        translateTransitionShow.setOnFinished(event -> {
            //задержка перед анимацией скрытия
            delay = new PauseTransition(Duration.seconds(5));
            delay.setOnFinished(event1 -> {
                //создание анимации скрытия
                translateTransitionHide = new TranslateTransition(Duration.seconds(0.5), warningPane);
                translateTransitionHide.setToY(startY);

                //после завершения анимации скрытия
                translateTransitionHide.setOnFinished(event2 -> {
                    warningPane.setVisible(false);
                    isShowing = false;
                });

                translateTransitionHide.play();
            });

            delay.play();
        });

        translateTransitionShow.play();
    }
}