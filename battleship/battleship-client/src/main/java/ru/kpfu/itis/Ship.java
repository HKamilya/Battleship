package ru.kpfu.itis;

import java.util.ArrayList;
import java.util.List;

import ru.kpfu.itis.Board.Cell;

public class Ship {
    public int type;
    public boolean vertical = true;
    public List<Cell> cells;

    private int health;

    public Ship(int type, boolean vertical, ArrayList<Cell> cells) {
        this.type = type;
        this.vertical = vertical;
        health = type;
        this.cells = cells;

    }

    public void hit() {
        health--;
    }

    public boolean isAlive() {
        return health > 0;
    }
}
