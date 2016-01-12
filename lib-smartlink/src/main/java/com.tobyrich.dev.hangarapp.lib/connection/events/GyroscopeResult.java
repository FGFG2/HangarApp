package com.tobyrich.dev.hangarapp.lib.connection.events;

/**
 * GyroscopeResult
 *  current position change event (response of bluetoothservice)
 * Created by geno on 10/27/15.
 */
public class GyroscopeResult {
    // store of value for different axis
    private float x,y,z;

    /**
     * Constructer filled by bluetoothservice this result
     * @param x
     * @param y
     * @param z
     */
    public GyroscopeResult(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * get x-axis position change
     * @return x-axis
     */
    public float getX(){
        return x;
    }

    /**
     * get y-axis position change
     * @return y-axis
     */
    public float getY(){
        return y;
    }

    /**
     * get z-axis position change
     * @return z-axis
     */
    public float getZ(){
        return z;
    }
}
