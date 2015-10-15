package com.tobyrich.dev.hangarapp.connection;

import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds information concerning the current plane connection status.
 * Created by Jonas on 27.05.2015.
 */
@Singleton
public class ConnectionStatus {

    private Connection connection = new BlueToothSmartPlaneConnection();

    private boolean connected;

    private String connectionId;

    private List<ConnectionListener> connectionListeners = new ArrayList<>();

    /**
     * @return {@code true} if the app is connected to the plane, {@code false} otherwise.
     */
    public boolean isConnected() {
        return this.connected;
    }

    /**
     * Sets the connection status to {@code true}.
     *
     * @param connectionId - the connectionId.
     */
    private void setConnected(String connectionId) {
        this.connectionId = connectionId;
        this.connected = true;
        notifyListeners();
    }

    public String getConnectionId() {
        return this.connectionId;
    }

    /**
     * Sets the connection status to {@code fale} and removes any existing connectionId.
     */
    private void setDisconnected() {
        this.connectionId = null;
        this.connected = false;
        notifyListeners();
    }

    public void addConnectionListener(ConnectionListener listener) {
        this.connectionListeners.add(listener);
    }

    private void notifyListeners() {
        for (ConnectionListener listener : connectionListeners) {
            listener.onConnectionChanged(connected, connectionId);
        }
    }


    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    public Connection getConnection() {
        return this.connection;
    }
}
