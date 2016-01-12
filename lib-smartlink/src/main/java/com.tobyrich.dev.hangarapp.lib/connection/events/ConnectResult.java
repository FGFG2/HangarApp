package com.tobyrich.dev.hangarapp.lib.connection.events;

import android.bluetooth.BluetoothDevice;

/**
 * ConnectResult
 *  a event of connection state change (response of bluetoothservice)
 * Created by geno on 10/23/15.
 */
public class ConnectResult {
    // state of connect (true:connected,false:disconnected)
    private boolean state;

    /**
     * set connection state on init
     * @param state
     */
    public ConnectResult(boolean state) {
        this.state = state;
    }

    /**
     * get connection state
     * @return state
     */
    public boolean getState(){
        return state;
    }
}
