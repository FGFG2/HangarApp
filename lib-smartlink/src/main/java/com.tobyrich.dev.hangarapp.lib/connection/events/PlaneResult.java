package com.tobyrich.dev.hangarapp.lib.connection.events;

import android.util.Log;

/**
 * PlaneResult
 *  from bluetoothservice (response of bluetoothservice)
 * Created by geno on 10/27/15.
 */
public class PlaneResult {
    //LOG-TAG
    private static final String TAG = "tr.lib.e.PlaneResult";

    // device types
    public static final int BATTERY = 401;
    public static final int RUDDER = 402;
    public static final int MOTOR = 403;

    // device and value
    private int device,value;

    /**
     * Response constructor
     * @param device device type
     * @param value new value of device
     */
    public PlaneResult(int device,int value) {
        this.device = device;
        this.value = value;
    }

    /**
     * get device type
     * @return device type
     */
    public int getDevice(){
        return device;
    }

    /**
     * get device value
     * @return device value
     */
    public int getValue(){
        return value;
    }
}
