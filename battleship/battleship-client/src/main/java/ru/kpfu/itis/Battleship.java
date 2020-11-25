package ru.kpfu.itis;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ru.kpfu.itis.Board.Cell;
import javafx.scene.text.Font;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


public class Battleship {
    private boolean isPlacingShips = false;
    private Board enemyBoard, playerBoard;
    Label label3;
    private int[][] enemyBattleField = new int[10][10];
    private int[][] playerBattleField = new int[10][10];
    private int[][] enemyMoves = new int[10][10];
    private int[][] playerMoves = new int[10][10];
    private Cell lastSuccessMove;
    int[] ships = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};

    List<Integer> shipsToPlace = Arrays.stream(ships).boxed().collect(Collectors.toList());
    List<Integer> shipsToPlace2 = Arrays.stream(ships).boxed().collect(Collectors.toList());

    private boolean enemyTurn = false;

    private Random random = new Random();

    protected Parent createContent() {
        BorderPane root = new BorderPane();
        root.setPrefSize(950, 600);


        Label label1 = new Label("Нажмите правую кнопку мыши, для измения положения корабля");
        label1.setFont(Font.font("Arial", 15));
        Label label2 = new Label("Игра начнется сразу же, как вы расположите последний корабль");
        label2.setFont(Font.font("Arial", 15));
        label3 = new Label(10 + " : " + 10);
        label3.setFont(Font.font("Arial", 32));
        VBox vBox = new VBox(10, label1, label2, label3);
//        vBox.setPadding(new Insets(15, 10, 3, 10));

        enemyBoard = new Board(true, event -> {
            if (!isPlacingShips) {
                return;
            }

            Cell cell = (Cell) event.getSource();
            if (cell.wasShot) {
                return;
            }
            enemyTurn = !cell.shoot();
            if (!enemyTurn) {
                playerMoves[cell.x][cell.y] = 1;
                label3.setText(playerBoard.ships + " : " + enemyBoard.ships);
            } else {
                playerMoves[cell.x][cell.y] = 2;
            }

            if (enemyBoard.ships == 0) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Вы выиграли");
                alert.setHeaderText(null);
                alert.setContentText("Поздравляем, вы одержали победу в этом сражении!");
                ButtonType buttonTypeAgain = new ButtonType("Начать сначала");
                ButtonType buttonTypeCancel = new ButtonType("Выйти из игры", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(buttonTypeAgain, buttonTypeCancel);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == buttonTypeAgain) {

                } else if (result.get() == buttonTypeCancel) {
                    System.exit(0);
                }
            }

            if (enemyTurn)
                enemyMove();
        });
        playerBoard = new Board(false, event -> {
            if (isPlacingShips) {
                return;
            }

            Cell cell = (Cell) event.getSource();
            int type = shipsToPlace.get(0);
            if (playerBoard.placeShip(new Ship(type, event.getButton() == MouseButton.PRIMARY, new ArrayList<>()), playerBattleField, cell.x, cell.y)) {
                shipsToPlace.remove(0);
                if (shipsToPlace.size() == 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setContentText("Можно приступать к атаке противника");
                    alert.showAndWait();
                    startGame();
                }
            }
        });

        HBox hbox = new HBox(50, enemyBoard, playerBoard);
        hbox.setAlignment(Pos.CENTER);


        vBox.setAlignment(Pos.CENTER);

        VBox general = new VBox(vBox, hbox);
        root.setCenter(general);

        return root;
    }

    private void enemyMove() {

        while (enemyTurn) {
            int x = random.nextInt(10);
            int y = random.nextInt(10);
            Cell cell = playerBoard.getCell(x, y);
            if (cell.wasShot) {
                continue;
            }
            label3.setText(playerBoard.ships + " : " + enemyBoard.ships);
            enemyTurn = cell.shoot();
            System.out.println(x + " " + y);
            if (enemyTurn) {
                System.out.println("popal");
                enemyMoves[x][y] = 1;
                label3.setText(playerBoard.ships + " : " + enemyBoard.ships);
            } else {
                enemyMoves[x][y] = 2;
            }


            if (playerBoard.ships == 0) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Вы проиграли");
                alert.setHeaderText(null);
                alert.setContentText("Удача скоро будет на вашей стороне! \nА пока готовьтесь к новым сражениям.");
                ButtonType buttonTypeAgain = new ButtonType("Начать сначала");
                ButtonType buttonTypeCancel = new ButtonType("Выйти из игры", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(buttonTypeAgain, buttonTypeCancel);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == buttonTypeAgain) {

                } else if (result.get() == buttonTypeCancel) {
                    System.exit(0);
                }
            }
        }
    }

    private void startGame() {
        // place enemy ships

        while (shipsToPlace2.size() > 0) {
            int x = random.nextInt(10);
            int y = random.nextInt(10);
            int ship = shipsToPlace2.get(0);
            if (enemyBoard.placeShip(new Ship(ship, Math.random() < 0.5, new ArrayList<>()), enemyBattleField, x, y)) {
                shipsToPlace2.remove(0);
            }
        }

        isPlacingShips = true;
    }


    public int[] getRandom(int x, int y) {
        int a = random.nextInt(4);
        if (a == 0) {
            if (y > 0) {
                return new int[]{x, y - 1};
            }
        }
        if (a == 1) {
            if (x > 0) {
                return new int[]{x - 1, y};
            }
        }
        if (a == 2) {
            if (y < 9) {
                return new int[]{x, y + 1};
            }
        }
        if (a == 3) {
            if (x < 9) {
                return new int[]{x + 1, y};
            }
        }
        return new int[]{x, y};
    }

}

