package ru.kpfu.itis;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {


    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(8000);
        serverSocket.accept();

        serverSocket.close();

    }
}
