package ru.kpfu.itis;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConnectionTest {

    private static Connection connection;

    @Test
    void sendObject() {
        connection = mock(Connection.class);
        when(connection.sendObject("hello", null)).thenReturn("hello");
        String hello = connection.sendObject("hello", null);
        assertEquals("hello", hello);
    }

}