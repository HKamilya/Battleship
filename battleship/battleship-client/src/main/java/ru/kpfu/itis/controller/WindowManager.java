package ru.kpfu.itis.controller;


import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import ru.kpfu.itis.controller.BattleshipWithBot;
import ru.kpfu.itis.controller.BattleshipWithUser;

import java.util.Optional;

public class WindowManager {
    public static void renderBattleshipWithBotWindow(Stage primaryStage) {
        BattleshipWithBot controller = new BattleshipWithBot();
        Scene scene = new Scene(controller.createContent());
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static void renderBattleshipWithUserWindow(Stage primaryStage) {
        BattleshipWithUser controller = new BattleshipWithUser();
        TextInputDialog dialog = new TextInputDialog("8080");
        dialog.setHeaderText("");
        dialog.setTitle("");
        dialog.setContentText("Please enter your port:");


        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            Scene scene = new Scene(controller.createContent(result.get()));
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.show();
        }
    }
}