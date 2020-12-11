package ru.kpfu.itis;

import javafx.geometry.Point2D;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.kpfu.itis.controller.Board;
import ru.kpfu.itis.controller.Cell;
import ru.kpfu.itis.controller.Ship;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    private static Board board;
    private static Ship shipVert;
    private static int[][] arr = new int[10][10];
    private static Ship shipHor;


    @BeforeAll
    static void setBoard() {
        Board board1 = new Board(true, event -> {
        });
        board = new Board();
        shipVert = new Ship(2, true, new ArrayList<>());
        shipHor = new Ship(3, false, new ArrayList<>());
    }

    @Test
    void deleteShip() {
        if (shipVert.vertical) {
            ArrayList<Cell> cell = new ArrayList<>();
            cell.add(new Cell(1, 2));
            cell.add(new Cell(1, 3));
            shipVert = new Ship(2, true, cell);
            arr[1][2] = 1;
            arr[1][3] = 1;
            board.deleteShip(shipVert, arr, 1, 2);
            assertNull(board.getCell(1, 2).ship);
        }
        if (!shipHor.vertical) {
            ArrayList<Cell> cells = new ArrayList<>();
            cells.add(new Cell(2, 1));
            cells.add(new Cell(3, 1));
            cells.add(new Cell(4, 1));
            shipHor = new Ship(3, false, cells);
            arr[1][2] = 1;
            arr[1][3] = 1;
            board.deleteShip(shipHor, arr, 1, 2);
            assertNull(board.getCell(1, 2).ship);
        }
    }

    @Test
    void placeShipVert() {
        if (shipVert.vertical) {
            board.placeShip(shipVert, arr, 1, 2);
            int cellsSize = shipVert.cells.size();
            assertTrue(cellsSize != 0);
        }
        if (!shipHor.vertical) {
            board.placeShip(shipHor, arr, 5, 6);
            int cellsSize = shipHor.cells.size();
            assertTrue(cellsSize != 0);
        }
    }


    @Test
    void getCell() {
        Board newBoard = new Board();
        Cell newCell = newBoard.getCell(1, 2);
        Cell cell = board.getCell(1, 2);
        assertNotSame(cell, newCell);
    }

    @Test
    void isValidPoint() {
        int x = 0;
        int y = 10;
        assertFalse(isValidPoint(x, y));
    }

    private boolean isValidPoint(Point2D point) {
        return isValidPoint(point.getX(), point.getY());
    }

    private boolean isValidPoint(double x, double y) {
        return x >= 0 && x < 10 && y >= 0 && y < 10;
    }

}