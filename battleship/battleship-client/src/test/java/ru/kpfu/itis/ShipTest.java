package ru.kpfu.itis;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.kpfu.itis.controller.Cell;
import ru.kpfu.itis.controller.Ship;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ShipTest {

    private static Ship ship;

    @BeforeAll
    static void createShip() {
        ArrayList<Cell> cells = new ArrayList<>();
        cells.add(new Cell(1, 2));
        cells.add(new Cell(1, 3));
        ship = new Ship(2, true, cells);
    }

    @Test
    void hit() {
        int health = ship.health - 1;
        ship.hit();
        assertEquals(ship.health, health);
    }

    @Test
    void isAlive() {
        boolean isAlive = ship.isAlive();
        assertTrue(isAlive);
    }
}