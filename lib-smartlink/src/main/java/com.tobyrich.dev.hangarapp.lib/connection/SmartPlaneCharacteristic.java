package com.tobyrich.dev.hangarapp.lib.connection;

import android.bluetooth.BluetoothGattCharacteristic;

public class SmartPlaneCharacteristic {
    private BluetoothGattCharacteristic battery;
    private BluetoothGattCharacteristic rudder;
    private BluetoothGattCharacteristic motor;

    private static SmartPlaneCharacteristic instance;

    public static SmartPlaneCharacteristic getInstance() {
        if(instance==null)
            instance = new SmartPlaneCharacteristic();
        return instance;
    }

    public BluetoothGattCharacteristic getMotor() {
        return motor;
    }
    public void setMotor(BluetoothGattCharacteristic motor) {
        this.motor = motor;
    }

    public BluetoothGattCharacteristic getRudder() {
        return rudder;
    }

    public void setRudder(BluetoothGattCharacteristic rudder) {
        this.rudder = rudder;
    }

    public BluetoothGattCharacteristic getBattery() {
        return battery;
    }

    public void setBattery(BluetoothGattCharacteristic battery) {
        this.battery = battery;
    }
}
