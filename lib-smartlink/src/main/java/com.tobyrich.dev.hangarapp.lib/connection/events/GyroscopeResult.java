package com.tobyrich.dev.hangarapp.lib.connection.events;

/**
 * Created by geno on 10/27/15.
 */
public class GyroscopeResult {
    private float x,y,z;
    public GyroscopeResult(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX(){
        return x;
    }
    public float getY(){
        return y;
    }
    public float getZ(){
        return z;
    }
}
