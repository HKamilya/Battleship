package ru.kpfu.itis;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.kpfu.itis.model.Player;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private static Player player;

    @BeforeAll
    static void setPlayer() {
        player = new Player(1);
    }

    @Test
    void getId() {
        int id = player.getId();
        assertEquals(1, id);
    }

    @Test
    void setId() {
        player.setId(2);
        assertEquals(2, player.getId());
    }
}