package com.tobyrich.dev.hangarapp.lib.connection.events;

import android.util.Log;

public class PlaneEvent {
    private static final String TAG = "tr.lib.e.PlaneEvent";

    // Set 300-350
    public static final int RUDDER = 301;
    public static final int MOTOR = 302;

    // Get 351-399
    public static final int BATTERY = 351;
    public static final int ACCELEROMETER = 352;


    private int device,value;


    public PlaneEvent(int device) {
        if (device > 350 && device < 400){
            this.device = device;
        }else {
            Log.e(TAG, "No GET");
        }
    }

    public PlaneEvent(int device,int value) {
        if (device >= 300 && device <= 350){
            this.device = device;
            this.value = value;
        }else {
            Log.e(TAG, "No SET");
        }
    }

    public int getDevice(){
        return device;
    }

    public int getValue(){
        return value;
    }
}