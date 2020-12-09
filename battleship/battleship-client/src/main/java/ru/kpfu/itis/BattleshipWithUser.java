package ru.kpfu.itis;


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


import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class BattleshipWithUser implements TCPConnectionListener {
    private boolean connected = false;
    private static String room;
    private static String ipAddr = "127.0.0.1";
    int id = 0;
    private TCPConnection connection;
    private Player player1;
    private Player player2;

    private boolean isPlacingShips = false;
    private Board enemyBoard, playerBoard;
    private Label label3;
    private int[][] enemyBattleField = new int[10][10];
    private int[][] playerBattleField = new int[10][10];
    private int[][] playerMoves = new int[10][10];
    private Button deleteButton;
    private Button startButton;
    private Label moveLabel;
    private boolean isGaming = false;
    private boolean isTimeToMove = false;
    private HashMap<int[], HashMap<Integer, Boolean>> myShips = new HashMap<>();
    private HashMap<int[], Integer> enemyShips = null;
    private Cell enemyMove;
    int[] ships = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};


    private Deque<Integer> shipsToPlace = Arrays.stream(ships).boxed().collect(Collectors.toCollection(ArrayDeque::new));
    private boolean enemyTurn = false;

    public BattleshipWithUser() {


    }


    protected Parent createContent(String room) {
        player1 = new Player();
        player1.setId(0);
        player2 = new Player();
        player2.setId(0);
        this.room = room;
        try {
            connection = new TCPConnection(this, ipAddr, 61626);
        } catch (IOException e) {

        }
        BorderPane root = new BorderPane();
        root.setPrefSize(950, 600);


        Label label1 = new Label("Нажмите правую кнопку мыши, для измения положения корабля");
        label1.setFont(Font.font("Arial", 15));
        label3 = new Label(10 + " : " + 10);
        label3.setFont(Font.font("Arial", 32));
        deleteButton = new Button("Отмена");
        startButton = new Button("Начать игру");
        moveLabel = new Label("");
        VBox vBox = new VBox(10, label1, label3, startButton, deleteButton, moveLabel);
        moveLabel.setVisible(false);
        moveLabel.setDisable(true);
        vBox.setPadding(new Insets(15, 15, 15, 15));
        startButton.setVisible(false);
        startButton.setDisable(true);


        enemyBoard = new Board(true, event -> {
            if (!isGaming) {
                return;
            }
            Cell cell = (Cell) event.getSource();
            int[] move = {cell.x, cell.y};
            if (cell.wasShot) {
                return;
            }
            connection.sendObject(room + ";" + player1.getId() + "move", move);
            enemyTurn = !cell.shoot();
            if (!enemyTurn) {
                label3.setText(enemyBoard.ships + " : " + playerBoard.ships);
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
                    WindowManager.renderBattleshipWithBotWindow(Main.primaryStage);
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
        moveLabel.setVisible(true);
        moveLabel.setDisable(false);
        isGaming = isTimeToMove;
        enemyTurn = !isTimeToMove;
    }


    private void enemyMove(Cell enemyMove) {
        enemyTurn = enemyMove.shoot();
        if (!enemyTurn) {
            connection.sendObject(room + ";" + player1.getId() + "turn", !enemyTurn);
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
                WindowManager.renderBattleshipWithBotWindow(Main.primaryStage);
            } else if (result.get() == buttonTypeCancel) {
                System.exit(0);
            }
        }
    }


    private void sendShips() {
        connection.sendObject(room + ";" + player1.getId() + "ships", myShips);
    }


    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {

    }

    @Override
    public void onReceiveObject(TCPConnection tcpConnection, String string, Object object) {
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
                }
                System.out.println("oh, it works? ¯\\_(ツ)_/¯ " + string + " " + object);
                if (string.equals(room + ";" + player2.getId() + "ships")) {
                    if (player2.getId() < player1.getId()) {
                        isTimeToMove = true;
                        startGame();
                    } else {
                        enemyTurn = true;
                        startGame();
                    }
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
                }
                if (string.equals(room + ";" + player2.getId() + "move")) {
                    int[] temp = (int[]) object;
                    enemyMove = playerBoard.getCell(temp[0], temp[1]);
                    enemyMove(enemyMove);
                }
                if (string.equals(room + ";" + player2.getId() + "turn")) {
                    boolean o = (boolean) object;
                    if (o) {
                        isTimeToMove = true;
                        moveLabel.setText("Ваш ход");
                        startGame();
                    }
                }
            }
        }
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {

    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {

    }
}
