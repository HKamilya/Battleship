package ru.kpfu.itis.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import ru.kpfu.itis.Main;

public class HelloController {
    public Button serverGameButton;
    public Button botGameButton;

    public void gameWithBot(ActionEvent actionEvent) {
        WindowManager.renderBattleshipWithBotWindow(Main.primaryStage);
    }

    public void gameWithUser(ActionEvent actionEvent) {
        WindowManager.renderBattleshipWithUserWindow(Main.primaryStage);
    }

}
