package ru.kpfu.itis;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;


public class GameServer implements ConnectionListener {
    public static void main(String[] args) {
        new GameServer();
    }

    private final ArrayList<Connection> connections = new ArrayList<>();

    public GameServer() {
        System.out.println("Server running...");
        try (ServerSocket serverSocket = new ServerSocket(6745)) {
            while (true) {
                try {
                    new Connection(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println("TCPConnection exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(Connection connection) {
        connections.add(connection);
        sendAllConnections("Client connected: " + connection, "");
    }

    @Override
    public synchronized void onReceiveObject(Connection connection, String string, Object object) {
        sendAllConnections(string, object);
    }

    @Override
    public synchronized void onDisconnect(Connection connection) {
        connections.remove(connection);
        sendAllConnections("Client disconnected: " + connection, "");
    }

    @Override
    public synchronized void onException(Connection connection, Exception e) {
        System.out.println("Connection exception: " + e);
    }

    private void sendAllConnections(String string, Object object) {
        System.out.println(object);
        for (Connection connection : connections) {
            connection.sendObject(string, object);
        }
    }
}

