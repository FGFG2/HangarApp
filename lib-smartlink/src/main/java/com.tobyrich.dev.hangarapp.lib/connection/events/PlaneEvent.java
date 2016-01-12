package com.tobyrich.dev.hangarapp.lib.connection.events;

import android.util.Log;

/**
 * PlaneEvent
 *  to control the bluetoothservice (request of bluetoothservice)
 */
public class PlaneEvent {
    //LOG-TAG
    private static final String TAG = "tr.lib.e.PlaneEvent";

    // device types to set value (range: 300-350)
    public static final int RUDDER = 301;
    public static final int MOTOR = 302;

    // device types to get value (range: 351-399)
    public static final int BATTERY = 351;
    public static final int ACCELEROMETER = 352;


    // device and value
    private int device,value;

    /**
     * Constructor for request device
     * @param device device type
     */
    public PlaneEvent(int device) {
        if (device > 350 && device < 400){
            this.device = device;
        }else {
            Log.e(TAG, "No GET");
        }
    }

    /**
     * Constructor for setting value
     * @param device device type
     * @param value new value of device
     */
    public PlaneEvent(int device,int value) {
        if (device >= 300 && device <= 350){
            this.device = device;
            this.value = value;
        }else {
            Log.e(TAG, "No SET");
        }
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