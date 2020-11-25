package ru.kpfu.itis;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;

import java.io.IOException;

public class HelloController {
    public Button serverGameButton;
    public Button botGameButton;

    public void login(ActionEvent actionEvent) throws IOException {
        WindowManager.renderBattleshipWindow(Main.primaryStage);
    }
}
