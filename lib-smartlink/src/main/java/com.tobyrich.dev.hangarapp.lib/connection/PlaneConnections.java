package com.tobyrich.dev.hangarapp.lib.connection;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Singelton Pattern to manage current bluetooth connection
 */
public class PlaneConnections {

    // stpre of charactiertic
    private BluetoothGattCharacteristic battery;
    private BluetoothGattCharacteristic rudder;
    private BluetoothGattCharacteristic motor;
    private BluetoothGattCharacteristic datatransfer;

    // singleton variable
    private static PlaneConnections instance;

    /**
     * get singleton pattern
     * @return get singlepattern instance
     */
    public static PlaneConnections getInstance() {
        if(instance==null)
            instance = new PlaneConnections();
        return instance;
    }

    /**
     * getter for motor characteristic
     * @return motor characteristic
     */
    public BluetoothGattCharacteristic getMotor() {
        return motor;
    }

    /**
     * setter for motor characteristic
     * @param motor characteristic
     */
    public void setMotor(BluetoothGattCharacteristic motor) {
        this.motor = motor;
    }


    /**
     * getter for rudder characteristic
     * @return rudder characteristic
     */
    public BluetoothGattCharacteristic getRudder() {
        return rudder;
    }

    /**
     * setter for rudder characteristic
     * @param rudder characteristic
     */
    public void setRudder(BluetoothGattCharacteristic rudder) {
        this.rudder = rudder;
    }

    /**
     * getter for battery characteristic
     * @return battery characteristic
     */
    public BluetoothGattCharacteristic getBattery() {
        return battery;
    }

    /**
     * setter for battery characteristic
     * @param battery characteristic
     */
    public void setBattery(BluetoothGattCharacteristic battery) {
        this.battery = battery;
    }

    /**
     * getter for datatransfer characteristic
     * @return datatransfer characteristic
     */
    public BluetoothGattCharacteristic getDatatransfer() {
        return datatransfer;
    }

    /**
     * setter for datatransfer characteristic
     * @param datatransfer characteristic
     */
    public void setDatatransfer(BluetoothGattCharacteristic datatransfer) {
        this.datatransfer = datatransfer;
    }
}
