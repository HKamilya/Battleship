package ru.kpfu.itis;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;

import java.io.IOException;

public class HelloController {
    public Button serverGameButton;
    public Button botGameButton;

    public void gameWithBot(ActionEvent actionEvent) throws IOException {
        WindowManager.renderBattleshipWithBotWindow(Main.primaryStage);
    }

    public void gameWithUser(ActionEvent actionEvent) throws IOException {
        WindowManager.renderBattleshipWithUserWindow(Main.primaryStage);
    }
}
