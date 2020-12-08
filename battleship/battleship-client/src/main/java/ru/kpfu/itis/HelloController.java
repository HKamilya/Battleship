package ru.kpfu.itis;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;

import java.io.IOException;

public class HelloController {
    public Button serverGameButton;
    public Button botGameButton;
    public Button createGameButton;

    public void gameWithBot(ActionEvent actionEvent) {
        WindowManager.renderBattleshipWithBotWindow(Main.primaryStage);
    }

    public void gameWithUser(ActionEvent actionEvent) {
        WindowManager.renderBattleshipWithUserWindow(Main.primaryStage);
    }

    public void createGame(ActionEvent actionEvent) {
        WindowManager.renderBattleShipWithUserCreateGame(Main.primaryStage);
    }
}
