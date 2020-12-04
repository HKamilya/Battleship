package ru.kpfu.itis;


import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;

public class WindowManager {
    public static void renderBattleshipWithBotWindow(Stage primaryStage) throws IOException {
        BattleshipWithBot controller = new BattleshipWithBot();
        Scene scene = new Scene(controller.createContent());
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static void renderBattleshipWithUserWindow(Stage primaryStage) throws IOException {
        BattleshipWithUser controller = new BattleshipWithUser();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                new BattleshipWithUser();
            }
        });
        Scene scene = new Scene(controller.createContent());
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }
}
