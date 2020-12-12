package ru.kpfu.itis.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomDao {
    public boolean findRoom(String roomPass) {
        Connection con1 = null;
        PreparedStatement ps1 = null;
        ResultSet rs1 = null;
        int playerCounter = 0;
        try {
            con1 = DBConnection.createConnection();
            String query = "select \"playerCounter\" from room where \"roomPass\" =?";
            ps1 = con1.prepareStatement(query);
            ps1.setString(1, roomPass);
            rs1 = ps1.executeQuery();
            while (rs1.next()) {
                playerCounter = rs1.getInt("playerCounter");
            }
            if (playerCounter == 1) {
                addPlayer(roomPass);
                return true;
            }
            if (playerCounter == 0) {
                createRoom(roomPass);
                return true;
            }
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
        return false;
    }

    public boolean addPlayer(String roomPass) {
        Connection con1 = null;
        PreparedStatement ps1 = null;
        try {
            con1 = DBConnection.createConnection();
            String query = "update room set \"playerCounter\"=? where \"roomPass\"=?";
            ps1 = con1.prepareStatement(query);
            ps1.setInt(1, 2);
            ps1.setString(2, roomPass);
            ps1.executeUpdate();
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
        return true;
    }

    public boolean createRoom(String roomPass) {
        Connection con1 = null;
        PreparedStatement ps1 = null;
        try {
            con1 = DBConnection.createConnection();
            String query = "insert into room(\"roomPass\", \"playerCounter\") values (?,?)";
            ps1 = con1.prepareStatement(query);
            ps1.setString(1, roomPass);
            ps1.setInt(2, 1);
            ps1.executeUpdate();
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
        return true;
    }
}
