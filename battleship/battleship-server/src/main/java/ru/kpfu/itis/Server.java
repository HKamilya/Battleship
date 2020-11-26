package ru.kpfu.itis;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.util.function.Consumer;

public class Server extends NetworkConnection {

    private int port;


    public Server(int port, Consumer<Serializable> onReceiveCallback) {
        super(onReceiveCallback);
        this.port = port;
    }

    @Override
    protected boolean isServer() {
        return false;
    }

    @Override
    protected String getIP() {
        return null;
    }

    @Override
    protected int getPort() {
        return 0;
    }
}
