package ru.kpfu.itis.controller;


import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import ru.kpfu.itis.controller.BattleshipWithBot;
import ru.kpfu.itis.controller.BattleshipWithUser;
import ru.kpfu.itis.util.RoomDao;

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
        TextInputDialog dialog = new TextInputDialog("KodKod");
        dialog.setHeaderText("");
        dialog.setTitle("");
        dialog.setContentText("Придумайте пароль, который никто не узнает(кроме вашего друга) " +
                "\nили введите код своего друга:");


        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            RoomDao roomDao = new RoomDao();
            boolean flag = roomDao.findRoom(result.get());
            if (flag) {
                Scene scene = new Scene(controller.createContent(result.get()));
                primaryStage.setScene(scene);
                primaryStage.setResizable(true);
                primaryStage.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Такой пораль уже занят, придумайте другой");
                alert.showAndWait();
            }
        }
    }
}
