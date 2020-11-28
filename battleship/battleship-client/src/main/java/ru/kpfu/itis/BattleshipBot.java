package ru.kpfu.itis;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;

import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import ru.kpfu.itis.Board.Cell;
import javafx.scene.text.Font;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


public class BattleshipBot {
    private boolean isPlacingShips = false;
    private Board enemyBoard, playerBoard;
    Label label3;
    private int[][] enemyBattleField = new int[10][10];
    private int[][] playerBattleField = new int[10][10];
    private int[][] enemyMoves = new int[10][10];
    private int[][] playerMoves = new int[10][10];
    private Cell lastSuccessMove = null;
    Button deleteButton;
    private ArrayList<Cell> successMoves = new ArrayList<>();
    int[] ships = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};

    Deque<Integer> shipsToPlace = Arrays.stream(ships).boxed().collect(Collectors.toCollection(ArrayDeque::new));
    Deque<Integer> shipsToPlace2 = Arrays.stream(ships).boxed().collect(Collectors.toCollection(ArrayDeque::new));

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
        deleteButton = new Button("Отмена");
        VBox vBox = new VBox(10, label1, label2, label3, deleteButton);
        vBox.setPadding(new Insets(15, 15, 15, 15));


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
        playerBoard = new Board(false, event -> {
            if (isPlacingShips) {
                return;
            }

            Cell cell = (Cell) event.getSource();
            int type = shipsToPlace.getFirst();
            Ship ship = new Ship(type, event.getButton() == MouseButton.PRIMARY, new ArrayList<>());
            if (playerBoard.placeShip(ship, playerBattleField, cell.x, cell.y)) {
                int rem = shipsToPlace.removeFirst();
                deleteButton.setOnAction(a -> {
                            playerBoard.deleteShip(ship, playerBattleField, cell.x, cell.y);
                            shipsToPlace.addFirst(rem);
                        }
                );

                if (shipsToPlace.size() == 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setContentText("Можно приступать к атаке противника");
                    alert.showAndWait();
                    deleteButton.setDisable(true);
                    startGame();
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
        while (enemyTurn) {
            Cell cell;
            int x, y;
            x = random.nextInt(10);
            y = random.nextInt(10);
            cell = playerBoard.getCell(x, y);
            if (lastSuccessMove != null) {
                if ((playerBoard.getCell(lastSuccessMove.x, lastSuccessMove.y + 1) == null | playerBoard.getCell(lastSuccessMove.x, lastSuccessMove.y + 1).wasShot) &
                        (playerBoard.getCell(lastSuccessMove.x + 1, lastSuccessMove.y) == null | playerBoard.getCell(lastSuccessMove.x + 1, lastSuccessMove.y).wasShot) &
                        (playerBoard.getCell(lastSuccessMove.x - 1, lastSuccessMove.y) == null | playerBoard.getCell(lastSuccessMove.x - 1, lastSuccessMove.y).wasShot) &
                        (playerBoard.getCell(lastSuccessMove.x, lastSuccessMove.y - 1) == null | playerBoard.getCell(lastSuccessMove.x, lastSuccessMove.y - 1).wasShot)) {
                    if (successMoves.size() != 0) {
                        lastSuccessMove = successMoves.get(0);
                        continue;
                    }
                }
                if (lastSuccessMove.x < 9 & lastSuccessMove.y < 9 & lastSuccessMove.y > 0 & lastSuccessMove.x > 0) {
                    if (!playerBoard.getCell(lastSuccessMove.x + 1, lastSuccessMove.y).wasShot) {
                        cell = playerBoard.getCell(lastSuccessMove.x + 1, lastSuccessMove.y);
                    } else if (!playerBoard.getCell(lastSuccessMove.x, lastSuccessMove.y + 1).wasShot) {
                        cell = playerBoard.getCell(lastSuccessMove.x, lastSuccessMove.y + 1);
                    } else if (!playerBoard.getCell(lastSuccessMove.x - 1, lastSuccessMove.y).wasShot) {
                        cell = playerBoard.getCell(lastSuccessMove.x - 1, lastSuccessMove.y);
                    } else if (!playerBoard.getCell(lastSuccessMove.x, lastSuccessMove.y - 1).wasShot) {
                        cell = playerBoard.getCell(lastSuccessMove.x, lastSuccessMove.y - 1);
                    }
                } else if (lastSuccessMove.y < 9 & lastSuccessMove.x < 9 & lastSuccessMove.x > 0) {
                    if (!playerBoard.getCell(lastSuccessMove.x, lastSuccessMove.y + 1).wasShot) {
                        cell = playerBoard.getCell(lastSuccessMove.x, lastSuccessMove.y + 1);
                    } else if (!playerBoard.getCell(lastSuccessMove.x - 1, lastSuccessMove.y).wasShot) {
                        cell = playerBoard.getCell(lastSuccessMove.x - 1, lastSuccessMove.y);
                    } else if (!playerBoard.getCell(lastSuccessMove.x + 1, lastSuccessMove.y).wasShot) {
                        cell = playerBoard.getCell(lastSuccessMove.x + 1, lastSuccessMove.y);
                    }
                } else if (lastSuccessMove.y < 9 & lastSuccessMove.x < 9 & lastSuccessMove.y > 0) {
                    if (!playerBoard.getCell(lastSuccessMove.x, lastSuccessMove.y + 1).wasShot) {
                        cell = playerBoard.getCell(lastSuccessMove.x, lastSuccessMove.y + 1);
                    } else if (!playerBoard.getCell(lastSuccessMove.x, lastSuccessMove.y - 1).wasShot) {
                        cell = playerBoard.getCell(lastSuccessMove.x, lastSuccessMove.y - 1);
                    } else if (!playerBoard.getCell(lastSuccessMove.x + 1, lastSuccessMove.y).wasShot) {
                        cell = playerBoard.getCell(lastSuccessMove.x + 1, lastSuccessMove.y);
                    }
                } else if (lastSuccessMove.y < 9 & lastSuccessMove.x > 0 & lastSuccessMove.y > 0) {
                    if (!playerBoard.getCell(lastSuccessMove.x, lastSuccessMove.y + 1).wasShot) {
                        cell = playerBoard.getCell(lastSuccessMove.x, lastSuccessMove.y + 1);
                    } else if (!playerBoard.getCell(lastSuccessMove.x, lastSuccessMove.y - 1).wasShot) {
                        cell = playerBoard.getCell(lastSuccessMove.x, lastSuccessMove.y - 1);
                    } else if (!playerBoard.getCell(lastSuccessMove.x - 1, lastSuccessMove.y).wasShot) {
                        cell = playerBoard.getCell(lastSuccessMove.x - 1, lastSuccessMove.y);
                    }
                } else if (lastSuccessMove.x < 9 & lastSuccessMove.x > 0 & lastSuccessMove.y > 0) {
                    if (!playerBoard.getCell(lastSuccessMove.x + 1, lastSuccessMove.y).wasShot) {
                        cell = playerBoard.getCell(lastSuccessMove.x + 1, lastSuccessMove.y);
                    } else if (!playerBoard.getCell(lastSuccessMove.x - 1, lastSuccessMove.y).wasShot) {
                        cell = playerBoard.getCell(lastSuccessMove.x - 1, lastSuccessMove.y);
                    } else if (!playerBoard.getCell(lastSuccessMove.x - 1, lastSuccessMove.y).wasShot) {
                        cell = playerBoard.getCell(lastSuccessMove.x - 1, lastSuccessMove.y);
                    }
                } else if (lastSuccessMove.x < 9 & lastSuccessMove.y > 0) {
                    if (!playerBoard.getCell(lastSuccessMove.x + 1, lastSuccessMove.y).wasShot) {
                        cell = playerBoard.getCell(lastSuccessMove.x + 1, lastSuccessMove.y);
                    } else if (!playerBoard.getCell(lastSuccessMove.x, lastSuccessMove.y - 1).wasShot) {
                        cell = playerBoard.getCell(lastSuccessMove.x, lastSuccessMove.y - 1);
                    }
                } else if (lastSuccessMove.y < 9 & lastSuccessMove.x > 0) {
                    if (!playerBoard.getCell(lastSuccessMove.x, lastSuccessMove.y + 1).wasShot) {
                        cell = playerBoard.getCell(lastSuccessMove.x, lastSuccessMove.y + 1);
                    } else if (!playerBoard.getCell(lastSuccessMove.x - 1, lastSuccessMove.y).wasShot) {
                        cell = playerBoard.getCell(lastSuccessMove.x - 1, lastSuccessMove.y);
                    }
                }
                x = cell.x;
                y = cell.y;
            }

            if (cell.wasShot) {
                continue;
            }
            int shipsCounter = playerBoard.ships;
            enemyTurn = cell.shoot();
            if (enemyTurn) {
                successMoves.add(cell);
                lastSuccessMove = cell;
                if (shipsCounter > playerBoard.ships) {
                    if (x < 9) {
                        playerBoard.getCell(x + 1, y).wasShot = true;
                    }
                    if (y < 9) {
                        playerBoard.getCell(x, y + 1).wasShot = true;
                    }
                    if (x > 0) {
                        playerBoard.getCell(x - 1, y).wasShot = true;
                    }
                    if (y > 0) {
                        playerBoard.getCell(x, y - 1).wasShot = true;
                    }
                    if (y > 0 & x > 0) {
                        playerBoard.getCell(x - 1, y - 1).wasShot = true;
                    }
                    if (y < 9 & x < 9) {
                        playerBoard.getCell(x + 1, y + 1).wasShot = true;
                    }
                    if (y < 9 & x > 0) {
                        playerBoard.getCell(x - 1, y + 1).wasShot = true;
                    }
                    if (y > 0 & x < 9) {
                        playerBoard.getCell(x + 1, y - 1).wasShot = true;
                    }
                    lastSuccessMove = null;
                    successMoves.clear();
                }
                enemyMoves[x][y] = 1;
                label3.setText(enemyBoard.ships + " : " + playerBoard.ships);
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
            int ship = shipsToPlace2.getFirst();
            if (enemyBoard.placeShip(new Ship(ship, Math.random() < 0.5, new ArrayList<>()), enemyBattleField, x, y)) {
                shipsToPlace2.removeFirst();
            }
        }

        isPlacingShips = true;
    }
}

