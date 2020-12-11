package ru.kpfu.itis;

public interface ConnectionListener {

    void onConnectionReady(Connection connection);

    void onReceiveObject(Connection connection, String string, Object object);

    void onDisconnect(Connection connection);

    void onException(Connection connection, Exception e);
}
