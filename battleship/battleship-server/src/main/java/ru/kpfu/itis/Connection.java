package ru.kpfu.itis;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection {

    private final Socket socket;
    private final Thread rxThread;
    private final ConnectionListener eventListener;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    public Connection(ConnectionListener eventListener, String ipAddr, int port) throws IOException {
        this(eventListener, new Socket(ipAddr, port));
    }

    public Connection(ConnectionListener eventListener, Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        this.in = new ObjectInputStream(socket.getInputStream());
        this.out = new ObjectOutputStream(socket.getOutputStream());
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.onConnectionReady(Connection.this);
                    while (!rxThread.isInterrupted()) {
                        eventListener.onReceiveObject(Connection.this, in.readUTF(), in.readObject());
                    }
                } catch (IOException | ClassNotFoundException e) {
                    eventListener.onException(Connection.this, e);
                } finally {
                    eventListener.onDisconnect(Connection.this);
                }
            }
        });
        rxThread.start();
    }

    public synchronized String sendObject(String string, Object object) {
        try {
            out.writeUTF(string);
            out.writeObject(object);
            out.flush();
        } catch (IOException e) {
            eventListener.onException(Connection.this, e);
            disconnect();
        }
        return string;
    }

    public synchronized void disconnect() {
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(Connection.this, e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }

}
