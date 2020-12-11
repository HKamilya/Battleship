package ru.kpfu.itis;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.kpfu.itis.controller.Cell;
import ru.kpfu.itis.controller.Ship;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CellTest {
    private static Cell cell;
    private static Ship ship;

    @BeforeAll
    static void createCell() {
        cell = new Cell(1, 3);
        ArrayList<Cell> cells = new ArrayList<>();
        cells.add(cell);
        ship = new Ship(1, true, cells);
    }

    @Test
    void shoot() {
        boolean shoot = cell.shoot();
        if (ship != null) {
            ship.hit();
            cell.setFill(Color.GRAY);
            Paint green = cell.getFill();
            assertSame(green, cell.getFill());
            if (!ship.isAlive()) {
                for (int i = 0; i < ship.cells.size(); i++) {
                    ship.cells.get(i).setFill(Color.RED);
                    ship.cells.get(i).setStroke(Color.DARKRED);
                    Paint colorFill = ship.cells.get(i).getFill();
                    Paint colorStroke = ship.cells.get(i).getStroke();
                    assertEquals(ship.health, 0);
                    assertSame(Color.RED, colorFill);
                    assertSame(Color.DARKRED, colorStroke);
                }
            }
        }
        assertFalse(shoot);
    }
}