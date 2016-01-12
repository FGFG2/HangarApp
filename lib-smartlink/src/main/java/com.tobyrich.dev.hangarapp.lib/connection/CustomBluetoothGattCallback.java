package com.tobyrich.dev.hangarapp.lib.connection;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import com.tobyrich.dev.hangarapp.lib.connection.events.ConnectResult;
import com.tobyrich.dev.hangarapp.lib.connection.events.PlaneResult;
import com.tobyrich.dev.hangarapp.lib.utils.PlaneState;

import java.util.UUID;

import de.greenrobot.event.EventBus;

/**
 * Own BluetoothGattCallback to manage Smartplanes
 */
public class CustomBluetoothGattCallback extends BluetoothGattCallback {
    //LOG-TAG
    private static final String TAG = "tr.lib.CBtGattCallback";

    // connected Gatt
    private BluetoothGatt mConnectedGatt;

    // UUIDs of different Services and Gatt

    // Default Motor-characteristic
    private static final UUID SMARTPLANE_MOTOR = UUID.fromString("75B64E51-0010-4ED1-921A-476090D80BA7");
    // Default Rudder-characteristic
    private static final UUID SMARTPLANE_RUDDER = UUID.fromString("75B64E51-0021-4ED1-921A-476090D80BA7");
    // Default BATTERY-characteristic for old version of firmware on plane
    private static final UUID BATTERY_characteristic = UUID.fromString("00002A19-0000-1000-8000-00805F9B34FB");

    // Datatransfer characteristic and descriptor
    private static final UUID DATATransfer_characteristic = UUID.fromString("75B64E51-F195-4ED1-921A-476090D80BA7");
    private static final UUID DATATransfer_descriptor = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public void setConnectedGatt(BluetoothGatt mConnectedGatt) {
        this.mConnectedGatt = mConnectedGatt;
    }

    @Override
    /**
     * onConnectionStateChange scan for services or disconnect
     */
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        Log.d(TAG, "Connection State Change: " + status + " -> " + connectionState(newState));
        if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
            EventBus.getDefault().post(new ConnectResult(true));
            PlaneState.getInstance().setConnected(true);
            gatt.discoverServices();
        } else if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
            EventBus.getDefault().post(new ConnectResult(false));
            PlaneState.getInstance().setConnected(false);
        } else if (status != BluetoothGatt.GATT_SUCCESS) {
            EventBus.getDefault().post(new ConnectResult(false));
            PlaneState.getInstance().setConnected(false);
            gatt.disconnect();
        }
    }

    @Override
    /**
     * scan all services and characteristics and map them to placeholder/variables
     */
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        Log.d(TAG, "Services Discovered: " + status + ":" + mConnectedGatt.getServices());
        if (status == BluetoothGatt.GATT_SUCCESS) {
            for(BluetoothGattService service : mConnectedGatt.getServices()){
                Log.v(TAG, "Services: " + service.getUuid() + ":");
                for(BluetoothGattCharacteristic characteristic : service.getCharacteristics()){
                    UUID c = characteristic.getUuid();
                    Log.v(TAG, "Characteristic: " + c + ":");
                    if(c.equals(DATATransfer_characteristic)) {
                        PlaneConnections.getInstance().setDatatransfer(characteristic);
                        mConnectedGatt.setCharacteristicNotification(characteristic, true);
                        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(DATATransfer_descriptor);
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        mConnectedGatt.writeDescriptor(descriptor);
                        Log.d(TAG, "Characteristic-found: Datatransfer - " + characteristic.getUuid());
                    }else if(c.equals(BATTERY_characteristic)) {
                        Log.d(TAG, "Characteristic-found: Battery - " + characteristic.getUuid()+": deprecated-> use DATATransfer_characteristic");
                    }else if (c.equals(SMARTPLANE_MOTOR)) {
                        PlaneConnections.getInstance().setMotor(characteristic);
                        Log.d(TAG, "Characteristic-found: Motor - "+characteristic.getUuid());
                    }else if(c.equals(SMARTPLANE_RUDDER)) {
                        PlaneConnections.getInstance().setRudder(characteristic);
                        Log.d(TAG, "Characteristic-found: Rudder - " + characteristic.getUuid());
                    }else{
                        Log.v(TAG, "onCharacteristicRead: Unknown - "+ characteristic.getUuid());
                    }
                }
            }
        }
    }

    @Override
    /**
     * onCharacteristicRead send to sendResultEvent
     */
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        sendResultEvent("onCharacteristicRead", gatt, characteristic);
    }

    @Override
    /**
     * onCharacteristicWrite send to sendResultEvent
     */
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        sendResultEvent("onCharacteristicWrite", gatt, characteristic);
    }

    @Override
    /**
     * onCharacteristicChanged send to sendResultEvent
     */
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        sendResultEvent("onCharacteristicChange", gatt, characteristic);
    }

    @Override
    /**
     * onDescriptorWrite send to sendResultEvent
     */
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        Log.v(TAG, "Descriptor: written");
    }

    @Override
    /**
     * Just Log (if them happen)
     */
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        Log.v(TAG, "Remote RSSI: " + rssi);
    }

    /**
     * method to manage characteristic changes on all bluetooth events
     * @param s Prefix for Log
     * @param gatt BluetoothGatt
     * @param characteristic BluetoothGattCharacteristic
     * @return fetched in value (if it was one)
     */
    public int sendResultEvent(String s,BluetoothGatt gatt, BluetoothGattCharacteristic characteristic){
        UUID c = characteristic.getUuid();
        int value = 0;
        if(c.equals(DATATransfer_characteristic)) {
            DatatransferInterpreter.received(characteristic.getValue());
        }else if(c.equals(SMARTPLANE_RUDDER)) {
            value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT8, 0);
            PlaneState.getInstance().setRudder(value);
            EventBus.getDefault().post(new PlaneResult(PlaneResult.RUDDER, value));
            Log.d(TAG, s + ": Rudder - "+value);
        }else if(c.equals(SMARTPLANE_MOTOR)) {
            value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            PlaneState.getInstance().setMotor(value);
            EventBus.getDefault().post(new PlaneResult(PlaneResult.MOTOR, value));
            Log.d(TAG, s + ": Motor - "+value);
        }else //If for old version of firmware on plane
        if(c.equals(BATTERY_characteristic)) {
            value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            PlaneState.getInstance().setBattery(value);
            EventBus.getDefault().post(new PlaneResult(PlaneResult.BATTERY, value));
            Log.d(TAG, s + ": Battery - "+value);
        }else{
            Log.v(TAG, s + ": Unknown - " + c);
        }
        return value;
    }

    /**
     * get string/label for status-values
     * @param status BluetoothProfil status id
     * @return string label of status
     */
    private String connectionState(int status) {
        switch (status) {
            case BluetoothProfile.STATE_CONNECTED:
                return "Connected";
            case BluetoothProfile.STATE_DISCONNECTED:
                return "Disconnected";
            case BluetoothProfile.STATE_CONNECTING:
                return "Connecting";
            case BluetoothProfile.STATE_DISCONNECTING:
                return "Disconnecting";
            default:
                return String.valueOf(status);
        }
    }
}
