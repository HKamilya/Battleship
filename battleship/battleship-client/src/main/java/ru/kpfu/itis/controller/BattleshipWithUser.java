package ru.kpfu.itis.controller;


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
import ru.kpfu.itis.*;
import ru.kpfu.itis.Connection;
import ru.kpfu.itis.model.Player;


import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class BattleshipWithUser implements ConnectionListener {
    private static String room;
    private static String ipAddr = "127.0.0.1";
    int id = 0;
    private Connection connection;
    private Player player1;
    private Player player2;

    private Board enemyBoard, playerBoard;
    private int[][] enemyBattleField = new int[10][10];
    private int[][] playerBattleField = new int[10][10];
    private Button deleteButton;
    private Button startButton;
    private Label moodLabel;
    private boolean isGaming = false;
    private HashMap<int[], HashMap<Integer, Boolean>> myShips = new HashMap<>();
    private HashMap<int[], Integer> enemyShips = null;
    private Cell enemyMove;
    int[] ships = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};


    private Deque<Integer> shipsToPlace = Arrays.stream(ships).boxed().collect(Collectors.toCollection(ArrayDeque::new));
    private boolean enemyTurn = false;

    public BattleshipWithUser() {
    }


    public Parent createContent(String room) {
        moodLabel = new Label("");
        player1 = new Player(0);
        player2 = new Player(0);
        this.room = room;
        try {
            connection = new Connection(this, ipAddr, 6745);
        } catch (IOException e) {

        }
        BorderPane root = new BorderPane();
        root.setPrefSize(950, 600);


        Label label1 = new Label("Нажмите правую кнопку мыши, для измения положения корабля");
        label1.setFont(Font.font("Arial", 15));
        deleteButton = new Button("Отмена");
        startButton = new Button("Начать игру");
        VBox vBox = new VBox(10, label1, startButton, deleteButton, moodLabel);
        vBox.setPadding(new Insets(15, 15, 15, 15));
        startButton.setVisible(false);
        startButton.setDisable(true);

        if (enemyShips != null) {
            moodLabel.setText("Корабли противника установлены");
        }
        enemyBoard = new Board(true, event -> {
            if (!isGaming | enemyTurn) {
                moodLabel.setText("Ход противника");
                connection.sendObject(room + ";" + player1.getId() + "turn", enemyTurn);
            } else {
                if (!enemyTurn) {
                    moodLabel.setText("Ваш ход");
                    Cell cell = (Cell) event.getSource();
                    int[] move = {cell.x, cell.y};
                    if (cell.wasShot) {
                        return;
                    }
                    connection.sendObject(room + ";" + player1.getId() + "move", move);
                    enemyTurn = !cell.shoot();
                }
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
                    Main.primaryStage.setResizable(false);
                    Main main = new Main();
                    try {
                        main.start(Main.primaryStage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (result.get() == buttonTypeCancel) {
                    System.exit(0);
                }
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
                    Main.primaryStage.setResizable(false);
                    Main main = new Main();
                    try {
                        main.start(Main.primaryStage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (result.get() == buttonTypeCancel) {
                    System.exit(0);
                }
            }

            if (enemyTurn) {
                connection.sendObject(room + ";" + player1.getId() + "turn", enemyTurn);
            }
        });
        playerBoard = new Board(false, event -> {
            if (shipsToPlace.size() > 0) {
                Cell cell = (Cell) event.getSource();
                int type = shipsToPlace.getFirst();
                Ship ship = new Ship(type, event.getButton() == MouseButton.PRIMARY, new ArrayList<>());
                if (playerBoard.placeShip(ship, playerBattleField, cell.x, cell.y)) {
                    int rem = shipsToPlace.removeFirst();
                    int[] temp = {cell.x, cell.y};
                    HashMap<Integer, Boolean> temp2 = new HashMap<>();
                    temp2.put(type, ship.vertical);
                    myShips.put(temp, temp2);
                    deleteButton.setOnAction(a -> {
                                playerBoard.deleteShip(ship, playerBattleField, cell.x, cell.y);
                                shipsToPlace.addFirst(rem);
                                myShips.remove(temp, temp2);
                            }
                    );

                    if (shipsToPlace.size() == 0) {
                        startButton.setVisible(true);
                        startButton.setDisable(false);
                        startButton.setOnAction(a -> {
                            deleteButton.setDisable(true);
                            deleteButton.setVisible(false);
                            sendShips();
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

    private void startGame() {
        if (player2.getId() != 0 & enemyShips != null) {
            isGaming = player2.getId() < player1.getId();
            isGaming = true;
            enemyTurn = false;
        }
    }

    private void makeTurn() {
        enemyTurn = false;
        isGaming = true;
    }


    private void enemyMove(Cell enemyMove) {
        enemyTurn = enemyMove.shoot();
    }


    private void sendShips() {
        startButton.setVisible(false);
        startButton.setDisable(true);
        connection.sendObject(room + ";" + player1.getId() + "ships", myShips);
    }


    @Override
    public void onConnectionReady(Connection connection) {
    }

    @Override
    public void onReceiveObject(Connection connection, String string, Object object) {
        if (player1.getId() == 0) {
            String substr = string.substring(45);
            id = Integer.parseInt(substr);
            player1.setId(id);
        }
        String sub = string.substring(0, room.length());
        if (sub.equals(room)) {
            String id = string.substring(room.length() + 1, room.length() + 6);
            if (player1.getId() != Integer.parseInt(id)) {
                if (player2.getId() == 0) {
                    player2.setId(Integer.parseInt(id));
                    connection.sendObject(room + ";" + player1.getId() + "ships", myShips);
                }
                if (string.equals(room + ";" + player2.getId() + "ships")) {
                    System.out.println(string);
                    enemyShips = (HashMap<int[], Integer>) object;
                    for (Map.Entry shipEq : enemyShips.entrySet()) {
                        int[] coord = (int[]) shipEq.getKey();
                        HashMap<Integer, Boolean> temp = (HashMap<Integer, Boolean>) shipEq.getValue();
                        for (Map.Entry shipT : temp.entrySet()) {
                            int type = (int) shipT.getKey();
                            boolean ver = (boolean) shipT.getValue();
                            Ship ship = new Ship(type, ver, new ArrayList<>());
                            enemyBoard.placeShip(ship, enemyBattleField, coord[0], coord[1]);
                        }
                    }
                    startGame();
                }
                if (string.equals(room + ";" + player2.getId() + "move")) {
                    System.out.println(string);
                    int[] temp = (int[]) object;
                    enemyMove = playerBoard.getCell(temp[0], temp[1]);
                    enemyMove(enemyMove);
                }
                if (string.equals(room + ";" + player2.getId() + "turn")) {
                    System.out.println(string);
                    makeTurn();
                }
            }
        }
    }

    @Override
    public void onDisconnect(Connection connection) {

    }

    @Override
    public void onException(Connection connection, Exception e) {

    }
}
