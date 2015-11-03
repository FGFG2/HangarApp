package com.tobyrich.dev.hangarapp.lib.utils;


/**
 * Created by geno on 11/3/15.
 */

public class PlaneState {
    private boolean connected;
    private int battery;
    private int motor;
    private int rudder;

    private static PlaneState instance;

    public static PlaneState getInstance() {
        if(instance==null)
            instance = new PlaneState();
        return instance;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int getMotor() {
        return motor;
    }

    public void setMotor(int motor) {
        this.motor = motor;
    }

    public int getRudder() {
        return rudder;
    }

    public void setRudder(int rudder) {
        this.rudder = rudder;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
