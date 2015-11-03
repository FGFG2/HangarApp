package com.tobyrich.dev.hangarapp.lib.connection.events;

import android.bluetooth.BluetoothDevice;

/**
 * Created by geno on 10/23/15.
 */
public class ConnectResult {
    private boolean state;

    public ConnectResult(boolean state) {
        this.state = state;
    }

    public boolean getState(){
        return state;
    }
}
