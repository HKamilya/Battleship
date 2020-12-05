package ru.kpfu.itis;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;

import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ru.kpfu.itis.Board.Cell;
import javafx.scene.text.Font;


import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class BattleshipWithBot {
    private boolean isPlacingShips = false;
    private Board enemyBoard, playerBoard;
    private Label label3;
    private int[][] enemyBattleField = new int[10][10];
    private int[][] playerBattleField = new int[10][10];
    private Cell lastSuccessMove = null;
    private Button deleteButton;
    private Button startButton;
    private Boolean vertical = null;
    private ArrayList<Cell> successMoves = new ArrayList<>();
    private int[] ships = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};

    private Deque<Integer> shipsToPlace = Arrays.stream(ships).boxed().collect(Collectors.toCollection(ArrayDeque::new));
    private Deque<Integer> shipsToPlace2 = Arrays.stream(ships).boxed().collect(Collectors.toCollection(ArrayDeque::new));

    private boolean enemyTurn = false;

    private Random random = new Random();

    protected Parent createContent() {
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
        startButton.setVisible(false);
        startButton.setDisable(true);

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
                    WindowManager.renderBattleshipWithBotWindow(new Stage());
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
            if (shipsToPlace.size() > 0) {
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

    private void enemyMove() {
        while (enemyTurn) {
            Cell cell;
            int x, y;
            x = random.nextInt(10);
            y = random.nextInt(10);
            cell = playerBoard.getCell(x, y);
            if (lastSuccessMove != null) {
                x = lastSuccessMove.x;
                y = lastSuccessMove.y;
                if (vertical == null) {
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
                } else {
                    if (vertical) {
                        if (y > 0 & playerBoard.getCell(lastSuccessMove.x, lastSuccessMove.y - 1) != null &
                                !playerBoard.getCell(lastSuccessMove.x, lastSuccessMove.y - 1).wasShot) {
                            cell = playerBoard.getCell(lastSuccessMove.x, lastSuccessMove.y - 1);
                        } else if (y < 9 & playerBoard.getCell(lastSuccessMove.x, lastSuccessMove.y + 1) != null
                                & !playerBoard.getCell(lastSuccessMove.x, lastSuccessMove.y + 1).wasShot) {
                            cell = playerBoard.getCell(lastSuccessMove.x, lastSuccessMove.y + 1);
                        } else if (successMoves.size() != 0) {
                            lastSuccessMove = successMoves.get(0);
                            continue;
                        }
                    }
                    if (!vertical) {
                        if (x > 0 & playerBoard.getCell(lastSuccessMove.x - 1, lastSuccessMove.y) != null &
                                !playerBoard.getCell(lastSuccessMove.x - 1, lastSuccessMove.y).wasShot) {
                            cell = playerBoard.getCell(lastSuccessMove.x - 1, lastSuccessMove.y);
                        } else if (x < 9 & playerBoard.getCell(lastSuccessMove.x + 1, lastSuccessMove.y) != null
                                & !playerBoard.getCell(lastSuccessMove.x + 1, lastSuccessMove.y).wasShot) {
                            cell = playerBoard.getCell(lastSuccessMove.x + 1, lastSuccessMove.y);
                        } else if (successMoves.size() != 0) {
                            lastSuccessMove = successMoves.get(0);
                            continue;
                        }
                    }
                }
            }
            if (cell.wasShot) {
                continue;
            }

            int shipsCounter = playerBoard.ships;
            enemyTurn = cell.shoot();
            if (enemyTurn) {
                if (lastSuccessMove != null & vertical == null) {
                    int y1 = lastSuccessMove.y;
                    int y2 = cell.y;
                    vertical = y1 != y2;
                }
                successMoves.add(cell);
                lastSuccessMove = cell;
                if (shipsCounter > playerBoard.ships) {
                    for (Cell successMove : successMoves) {
                        if (successMove.x > 0) {
                            playerBoard.getCell(successMove.x - 1, successMove.y).wasShot = true;
                        }
                        if (successMove.x < 9) {
                            playerBoard.getCell(successMove.x + 1, successMove.y).wasShot = true;
                        }
                        if (successMove.y > 0) {
                            playerBoard.getCell(successMove.x, successMove.y - 1).wasShot = true;
                        }
                        if (successMove.y < 9) {
                            playerBoard.getCell(successMove.x, successMove.y + 1).wasShot = true;
                        }
                        if (successMove.y > 0 & successMove.x > 0) {
                            playerBoard.getCell(successMove.x - 1, successMove.y - 1).wasShot = true;
                        }
                        if (successMove.y < 9 & successMove.x < 9) {
                            playerBoard.getCell(successMove.x + 1, successMove.y + 1).wasShot = true;
                        }
                        if (successMove.y < 9 & successMove.x > 0) {
                            playerBoard.getCell(successMove.x - 1, successMove.y + 1).wasShot = true;
                        }
                        if (successMove.y > 0 & successMove.x < 9) {
                            playerBoard.getCell(successMove.x + 1, successMove.y - 1).wasShot = true;
                        }
                    }
                    for (Cell successMove : successMoves) {
                        playerBoard.getCell(successMove.x, successMove.y).setFill(Color.RED);
                    }
                    lastSuccessMove = null;
                    successMoves.clear();
                    vertical = null;
                }
                label3.setText(enemyBoard.ships + " : " + playerBoard.ships);
            }

            if (playerBoard.ships == 0) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Вы проиграли");
                alert.setHeaderText(null);
                alert.setContentText("Удача скоро будет на вашей стороне! \nА пока готовьтесь к новым сражениям.");
                ButtonType buttonTypeCancel = new ButtonType("Выйти из игры", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(buttonTypeCancel);

                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == buttonTypeCancel) {
                    System.exit(0);
                }
            }
        }

    }


    private void startGame() {
        while (shipsToPlace2.size() > 0) {
            int x = random.nextInt(10);
            int y = random.nextInt(10);
            int ship = shipsToPlace2.getFirst();
            if (enemyBoard.placeShip(new Ship(ship, Math.random() < 0.5, new ArrayList<>()), enemyBattleField, x, y)) {
                shipsToPlace2.removeFirst();
            }
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText("Можно приступать к атаке противника");
        alert.showAndWait();
        startButton.setDisable(true);
        startButton.setVisible(false);
        isPlacingShips = true;
    }
}

