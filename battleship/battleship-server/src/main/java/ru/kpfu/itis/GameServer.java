package ru.kpfu.itis;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;


public class GameServer implements TCPConnectionListener {
    public static void main(String[] args) {
        new GameServer();
    }

    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private GameServer() {
        System.out.println("Server running...");
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            while (true) {
                try {
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println("TCPConnection exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        System.out.println("otkryt");
        connections.add(tcpConnection);
        sendAllConnections("Client connected: " + tcpConnection);
    }

    @Override
    public synchronized void onReceiveObject(TCPConnection tcpConnection, Object object) {
        sendAllConnections(object);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendAllConnections("Client disconnected: " + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception: " + e);
    }

    private void sendAllConnections(Object object) {
        System.out.println(object);
        for (TCPConnection connection : connections) {
            connection.sendObject(object);
        }
    }
}

