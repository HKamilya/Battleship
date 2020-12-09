package ru.kpfu.itis;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Board extends Parent implements Serializable {
    private VBox rows = new VBox();
    private boolean enemy = false;
    public int ships = 10;

    public Board(boolean enemy) {
        this.enemy = enemy;
        for (int y = 0; y < 10; y++) {
            HBox row = new HBox();
            for (int x = 0; x < 10; x++) {
                Cell c = new Cell(x, y, this);
                row.getChildren().add(c);
            }
            rows.getChildren().add(row);
        }
        getChildren().add(rows);
    }

    public Board(boolean enemy, EventHandler<? super MouseEvent> handler) {
        this.enemy = enemy;
        for (int y = 0; y < 10; y++) {
            HBox row = new HBox();
            for (int x = 0; x < 10; x++) {
                Cell c = new Cell(x, y, this);
                c.setOnMouseClicked(handler);
                row.getChildren().add(c);
            }
            rows.getChildren().add(row);
        }
        getChildren().add(rows);
    }

    public void deleteShip(Ship ship, int[][] field, int x, int y) {
        int length = ship.type;
        if (ship.vertical) {
            for (int i = y; i < y + length; i++) {
                Cell cell = getCell(x, i);
                field[x][i] = 0;
                ship.cells.remove(cell);
                cell.ship = null;
                if (!enemy) {
                    cell.setFill(Color.LIGHTBLUE);
                    cell.setStroke(Color.DARKBLUE);
                }
            }
        } else {
            for (int i = x; i < x + length; i++) {
                Cell cell = getCell(i, y);
                field[i][y] = 0;
                cell.ship = null;
                ship.cells.remove(cell);
                if (!enemy) {
                    cell.setFill(Color.LIGHTBLUE);
                    cell.setStroke(Color.DARKBLUE);
                }
            }
        }
    }

    public boolean placeShip(Ship ship, int[][] field, int x, int y) {
        if (canPlaceShip(ship, x, y)) {
            int length = ship.type;

            if (ship.vertical) {
                for (int i = y; i < y + length; i++) {
                    Cell cell = getCell(x, i);
                    field[x][i] = 1;
                    ship.cells.add(cell);
                    cell.ship = ship;
                    if (!enemy) {
                        cell.setFill(Color.YELLOW);
                        cell.setStroke(Color.YELLOW);
                    }
                }
            } else {
                for (int i = x; i < x + length; i++) {
                    Cell cell = getCell(i, y);
                    field[i][y] = 1;
                    cell.ship = ship;
                    ship.cells.add(cell);
                    if (!enemy) {
                        cell.setFill(Color.YELLOW);
                        cell.setStroke(Color.YELLOW);
                    }
                }
            }

            return true;
        }

        return false;
    }

    public Cell getCell(int x, int y) {
        return (Cell) ((HBox) rows.getChildren().get(y)).getChildren().get(x);
    }

    private Cell[] getNeighbors(int x, int y) {
        Point2D[] points = new Point2D[]{
                new Point2D(x - 1, y),
                new Point2D(x + 1, y),
                new Point2D(x, y - 1),
                new Point2D(x, y + 1),
                new Point2D(x + 1, y + 1),
                new Point2D(x - 1, y - 1),
                new Point2D(x + 1, y - 1),
                new Point2D(x - 1, y + 1)
        };

        List<Cell> neighbors = new ArrayList<Cell>();

        for (Point2D p : points) {
            if (isValidPoint(p)) {
                neighbors.add(getCell((int) p.getX(), (int) p.getY()));
            }
        }

        return neighbors.toArray(new Cell[0]);
    }

    private boolean canPlaceShip(Ship ship, int x, int y) {
        int length = ship.type;

        if (ship.vertical) {
            for (int i = y; i < y + length; i++) {
                if (!isValidPoint(x, i))
                    return false;

                Cell cell = getCell(x, i);
                if (cell.ship != null)
                    return false;

                for (Cell neighbor : getNeighbors(x, i)) {
                    if (!isValidPoint(x, i))
                        return false;

                    if (neighbor.ship != null)
                        return false;
                }
            }
        } else {
            for (int i = x; i < x + length; i++) {
                if (!isValidPoint(i, y))
                    return false;

                Cell cell = getCell(i, y);
                if (cell.ship != null)
                    return false;

                for (Cell neighbor : getNeighbors(i, y)) {
                    if (!isValidPoint(i, y))
                        return false;

                    if (neighbor.ship != null)
                        return false;
                }
            }
        }

        return true;
    }

    private boolean isValidPoint(Point2D point) {
        return isValidPoint(point.getX(), point.getY());
    }

    private boolean isValidPoint(double x, double y) {
        return x >= 0 && x < 10 && y >= 0 && y < 10;
    }
}

