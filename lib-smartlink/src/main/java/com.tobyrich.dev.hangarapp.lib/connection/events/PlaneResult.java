package com.tobyrich.dev.hangarapp.lib.connection.events;

import android.util.Log;

/**
 * Created by geno on 10/27/15.
 */
public class PlaneResult {
    private static final String TAG = "tr.lib.e.PlaneResult";

    public static final int BATTERY = 401;
    public static final int RUDDER = 402;
    public static final int MOTOR = 403;


    private int device,value;


    public PlaneResult(int device,int value) {
        this.device = device;
        this.value = value;
    }

    public int getDevice(){
        return device;
    }

    public int getValue(){
        return value;
    }
}
