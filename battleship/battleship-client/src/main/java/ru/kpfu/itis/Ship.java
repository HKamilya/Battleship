package ru.kpfu.itis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Ship implements Serializable {
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
