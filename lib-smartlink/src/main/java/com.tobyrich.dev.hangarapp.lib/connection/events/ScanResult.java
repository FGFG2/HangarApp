package com.tobyrich.dev.hangarapp.lib.connection.events;
import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;

/**
 * ScanEvent
 *  a event with all founded devices (response of bluetoothservice)
 * Created by geno on 10/23/15.
 */
public class ScanResult {
    // store all devices
    private ArrayList<BluetoothDevice> result;

    /**
     * set list of devices (by constructor as)
     * @param result
     */
    public ScanResult(ArrayList<BluetoothDevice> result) {
        this.result = result;
    }

    /**
     * get a list of devices
     * @return list of devices
     */
    public ArrayList<BluetoothDevice> getResult(){
        return result;
    }
}
