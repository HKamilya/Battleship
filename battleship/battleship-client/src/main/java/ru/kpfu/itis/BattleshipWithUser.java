package ru.kpfu.itis;


import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;


import javax.swing.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

//impl TCPConnectionListener
public class BattleshipWithUser {
    private static int port;

    private boolean isPlacingShips = false;
    private Board enemyBoard, playerBoard;
    private Label label3;
    private int[][] enemyBattleField = new int[10][10];
    private int[][] playerBattleField = new int[10][10];
    private int[][] enemyMoves = new int[10][10];
    private int[][] playerMoves = new int[10][10];
    private Button deleteButton;
    private Button startButton;
    private boolean isGaming = false;
    int[] ships = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};
    private List<Ship> myShips = new ArrayList<>();
    private List<Ship> enemyShips = new ArrayList<>();

    private Deque<Integer> shipsToPlace = Arrays.stream(ships).boxed().collect(Collectors.toCollection(ArrayDeque::new));
    private boolean enemyTurn = false;

    public BattleshipWithUser() {


    }


    protected Parent createContent(int port) {
        this.port = port;
//        try {
//            connection = new TCPConnection(this, IP_ADDR, port);
//        } catch (IOException e) {
//            printMessage("Connection exception: " + e);
//        }
        BorderPane root = new BorderPane();
        root.setPrefSize(950, 600);


        Label label1 = new Label("Нажмите правую кнопку мыши, для измения положения корабля");
        label1.setFont(Font.font("Arial", 15));
        label3 = new Label(10 + " : " + 10);
        label3.setFont(Font.font("Arial", 32));
        deleteButton = new Button("Отмена");
        startButton = new Button("Начать игру");
        VBox vBox = new VBox(10, label1, label3, startButton, deleteButton);
        vBox.setPadding(new Insets(15, 15, 15, 15));


        enemyBoard = new Board(true, event -> {
            if (!isGaming) {
                return;
            }
            Board.Cell cell = (Board.Cell) event.getSource();
            if (cell.wasShot) {
                return;
            }
            //TODO:тут должен быть
            // метод оправляющий мой ход
            enemyTurn = !cell.shoot();
            if (!enemyTurn) {
                playerMoves[cell.x][cell.y] = 1;
                label3.setText(enemyBoard.ships + " : " + playerBoard.ships);
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
        List<Ship> enemyShips = getEnemyShips();
        for (Ship enemyShip : enemyShips) {
            enemyBoard.placeShip(enemyShip, enemyBattleField, enemyShip.cells.get(0).x, enemyShip.cells.get(0).y);
        }
        List<Ship> myShips = new ArrayList<>();
        playerBoard = new Board(false, event -> {
            if (shipsToPlace.size() > 0) {
                Board.Cell cell = (Board.Cell) event.getSource();
                int type = shipsToPlace.getFirst();
                Ship ship = new Ship(type, event.getButton() == MouseButton.PRIMARY, new ArrayList<>());
                if (playerBoard.placeShip(ship, playerBattleField, cell.x, cell.y)) {
                    int rem = shipsToPlace.removeFirst();
                    myShips.add(ship);
                    deleteButton.setOnAction(a -> {
                                playerBoard.deleteShip(ship, playerBattleField, cell.x, cell.y);
                                shipsToPlace.addFirst(rem);
                                myShips.remove(ship);
                            }
                    );

                    if (shipsToPlace.size() == 0) {
                        startButton.setOnAction(a -> {
                            deleteButton.setDisable(true);
                            deleteButton.setVisible(false);
                            startGame();
                            //TODO: тут должен быть
                            // метод, правляющий мои корабли
                        });
                    }
                }
            }
        });
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        HBox.setHgrow(enemyBoard, Priority.ALWAYS);
        HBox.setHgrow(playerBoard, Priority.ALWAYS);
        hbox.getChildren().addAll(enemyBoard, playerBoard);
        hbox.setSpacing(50);


        vBox.setAlignment(Pos.CENTER);
        VBox general = new VBox(vBox, hbox);
        root.setCenter(general);

        return root;
    }

    private void enemyMove() {
        Board.Cell cell = getEnemyMove();
        playerBoard.getCell(cell.x, cell.y).shoot();
    }

    private Board.Cell getEnemyMove() {
        return enemyBoard.getCell(0, 0);
    }

    private void startGame() {
        isGaming = true;
    }


    private List<Ship> getEnemyShips() {
        return new ArrayList<>();
    }


//    @Override
//    public void onConnectionReady(TCPConnection tcpConnection) {
//        printMessage("Connection ready...");
//    }
//
//    @Override
//    public void onReceiveObject(TCPConnection tcpConnection, Object object) {
//        printMessage(object);
//    }
//
//    @Override
//    public void onDisconnect(TCPConnection tcpConnection) {
//        printMessage("Connection closed...");
//    }
//
//    @Override
//    public void onException(TCPConnection tcpConnection, Exception e) {
//        printMessage("Connection exception: " + e);
//    }
//
//    private synchronized void printMessage(Object object) {
//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println(object);
//            }
//        });
//    }
}
