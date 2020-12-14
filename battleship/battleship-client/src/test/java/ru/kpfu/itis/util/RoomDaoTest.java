package ru.kpfu.itis.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class RoomDaoTest {
    private static RoomDao roomDao;

    @BeforeAll
    static void createRoomDao() {
        roomDao = new RoomDao();
    }

    @Test
    void findRoom() {
        Connection con1 = null;
        PreparedStatement ps1 = null;
        ResultSet rs1 = null;
        int playerCounter = 0;
        try {
            con1 = DBConnection.createConnection();
            String query = "select \"playerCounter\" from room where \"roomPass\" =?";
            ps1 = con1.prepareStatement(query);
            ps1.setString(1, "123");
            rs1 = ps1.executeQuery();
            while (rs1.next()) {
                playerCounter = rs1.getInt("playerCounter");
            }
            assertEquals(false, roomDao.findRoom("123"));
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        } finally {
            if (ps1 != null) {
                try {
                    ps1.close();
                } catch (SQLException ignore) {
                }
            }
            if (rs1 != null) {
                try {
                    rs1.close();
                } catch (SQLException ignore) {
                }
            }
        }
    }

    @Test
    void addPlayer() {
        Connection con1 = null;
        PreparedStatement ps1 = null;
        try {
            con1 = DBConnection.createConnection();
            String query = "update room set \"playerCounter\"=? where \"roomPass\"=?";
            ps1 = con1.prepareStatement(query);
            ps1.setInt(1, 2);
            ps1.setString(2, "000");
            ps1.executeUpdate();
            boolean temp = roomDao.addPlayer("000");
            assertEquals(temp, roomDao.addPlayer("000"));
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        } finally {
            if (ps1 != null) {
                try {
                    ps1.close();
                } catch (SQLException ignore) {
                }
            }
            if (con1 != null) {
                try {
                    con1.close();
                } catch (SQLException ignore) {
                }
            }
        }
    }

    @Test
    void createRoom() {
        int random = (int) (Math.random() * 89);
        boolean temp = roomDao.createRoom(String.valueOf(random));
        assertEquals(roomDao.findRoom(String.valueOf(random)), temp);

    }
}
