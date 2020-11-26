package ru.kpfu.itis;


import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WindowManager {
    public static void renderBattleshipWindow(Stage primaryStage) throws IOException {
        BattleshipBot controller = new BattleshipBot();
        Scene scene = new Scene(controller.createContent());
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }
}
