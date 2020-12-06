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
import javafx.stage.Stage;


import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class BattleshipWithUser implements TCPConnectionListener {
    private static int port;
    private static String ipAddr = "localhost";

    private TCPConnection connection;

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
    private int[][] myships = new int[10][10];
    private List<Ship> enemyShips = new ArrayList<>();

    private Deque<Integer> shipsToPlace = Arrays.stream(ships).boxed().collect(Collectors.toCollection(ArrayDeque::new));
    private boolean enemyTurn = false;

    public BattleshipWithUser() {


    }


    protected Parent createContent(int port) {
        this.port = port;
        try {
            connection = new TCPConnection(this, ipAddr, port);
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
        VBox vBox = new VBox(10, label1, label3, startButton, deleteButton);
        vBox.setPadding(new Insets(15, 15, 15, 15));


        enemyBoard = new Board(true, event -> {
            if (!isGaming) {
                return;
            }
            Cell cell = (Cell) event.getSource();
            int[] move = {cell.x, cell.y};
            if (cell.wasShot) {
                return;
            }
            // TODO: оправить ход
            connection.sendObject(move);
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
                    WindowManager.renderBattleshipWithBotWindow(new Stage());
                } else if (result.get() == buttonTypeCancel) {
                    System.exit(0);
                }
            }

            if (enemyTurn) {
                // TODO: получить ход противника
            }
//
        });
//TODO: корабли противника
        for (Ship enemyShip : enemyShips) {
            enemyBoard.placeShip(enemyShip, enemyBattleField, enemyShip.cells.get(0).x, enemyShip.cells.get(0).y);
        }
        playerBoard = new Board(false, event -> {
            if (shipsToPlace.size() > 0) {
                Cell cell = (Cell) event.getSource();
                int type = shipsToPlace.getFirst();
                Ship ship = new Ship(type, event.getButton() == MouseButton.PRIMARY, new ArrayList<>());
                if (playerBoard.placeShip(ship, playerBattleField, cell.x, cell.y)) {
                    int rem = shipsToPlace.removeFirst();
                    myShips.add(ship);
                    myships[cell.y][cell.x] = 1;
                    deleteButton.setOnAction(a -> {
                                playerBoard.deleteShip(ship, playerBattleField, cell.x, cell.y);
                                shipsToPlace.addFirst(rem);
                                myShips.remove(ship);
                                myships[cell.y][cell.x] = 0;
                            }
                    );

                    if (shipsToPlace.size() == 0) {
                        startButton.setVisible(true);
                        startButton.setDisable(false);
                        startButton.setOnAction(a -> {
                            deleteButton.setDisable(true);
                            deleteButton.setVisible(false);
                            startGame();
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
        // TODO: получить корабли соперника
        isGaming = true;
    }


    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        System.out.println("hi");
    }

    @Override
    public void onReceiveObject(TCPConnection tcpConnection, Object object) {
        System.out.println(object);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {

    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {

    }
}
