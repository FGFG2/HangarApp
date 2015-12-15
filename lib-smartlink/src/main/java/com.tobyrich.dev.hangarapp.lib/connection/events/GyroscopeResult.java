package com.tobyrich.dev.hangarapp.lib.connection.events;

/**
 * Created by geno on 10/27/15.
 */
public class GyroscopeResult {
    private int x,y,z;
    public GyroscopeResult(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public int getZ(){
        return z;
    }
}
