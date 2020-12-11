package ru.kpfu.itis.controller;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;

public class Cell extends Rectangle implements Serializable {
    public int x, y;
    public Ship ship = null;
    public boolean wasShot = false;

    private Board board;


    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Cell(int x, int y, Board board) {
        super(30, 30);
        this.x = x;
        this.y = y;
        this.board = board;
        setFill(Color.LIGHTBLUE);
        setStroke(Color.DARKBLUE);
    }

    public boolean shoot() {
        wasShot = true;
        setFill(Color.GRAY);

        if (ship != null) {
            ship.hit();
            setFill(Color.GREEN);
            if (!ship.isAlive()) {
                for (int i = 0; i < ship.cells.size(); i++) {
                    ship.cells.get(i).setFill(Color.RED);
                    ship.cells.get(i).setStroke(Color.DARKRED);
                }
                board.ships--;
            }
            return true;
        }
        return false;
    }

}
