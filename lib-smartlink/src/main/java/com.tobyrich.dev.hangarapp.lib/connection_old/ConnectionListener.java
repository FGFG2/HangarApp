package com.tobyrich.dev.hangarapp.lib.connection_old;

/**
 * Reacts on changes of the {@link ConnectionStatus}.
 * Created by Jonas on 27.05.2015.
 */
public interface ConnectionListener {

    /**
     * Called when the connection status changes
     *
     * @param connected    - {@code true} if the app is connected to the plane, {@code false} otherwise.
     * @param connectionId - the current connectionId, if connected.
     */
    public void onConnectionChanged(boolean connected, String connectionId);
}
