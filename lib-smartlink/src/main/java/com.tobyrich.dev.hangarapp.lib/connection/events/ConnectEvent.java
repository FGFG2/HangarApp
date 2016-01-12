package com.tobyrich.dev.hangarapp.lib.connection.events;

import android.bluetooth.BluetoothDevice;

/**
 * ConnectEvent
 *  a event to connect to requested device (request of bluetoothservice)
 * Created by geno on 10/23/15.
 */
public class ConnectEvent {
    // cached device
    private BluetoothDevice device;

    /**
     * Set device to connected to (by constructor as)
     * @param device
     */
    public ConnectEvent(BluetoothDevice device) {
        this.device = device;
    }

    /**
     * Get device to connected to
     * @return device
     */
    public BluetoothDevice getDevice(){
        return device;
    }
}
