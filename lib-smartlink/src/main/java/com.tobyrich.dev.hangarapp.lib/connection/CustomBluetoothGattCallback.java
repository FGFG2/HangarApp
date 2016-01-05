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

public class CustomBluetoothGattCallback extends BluetoothGattCallback {

    private BluetoothGatt mConnectedGatt;
    // MAC: 5C:31:3E:4D:33:49
    //private static final String PLAN_PART = "TobyRich";
    private static final UUID HANGARSERVICE = UUID.fromString("75B64E51-6000-4ED1-921A-476090D80BA7");
    private static final UUID GET_X = UUID.fromString("75B64E51-0010-4ED1-921A-476090D80BA7");
    private static final UUID GET_Y = UUID.fromString("75B64E51-0020-4ED1-921A-476090D80BA7");
    private static final UUID GET_Z = UUID.fromString("75B64E51-0021-4ED1-921A-476090D80BA7");

    //private static final UUID SMARTPLANESERVICE1 = UUID.fromString("75B64E51-F171-4ED1-921A-476090D80BA7");
    //private static final UUID SMARTPLANESERVICE2 = UUID.fromString("75B64E51-E171-4ED1-921A-476090D80BA7");
    private static final UUID SMARTPLANE_MOTOR = UUID.fromString("75B64E51-0010-4ED1-921A-476090D80BA7");
    private static final UUID SMARTPLANE_RUDDER = UUID.fromString("75B64E51-0021-4ED1-921A-476090D80BA7");

    private static final UUID BATTERYSERVICE = UUID.fromString("0000180F-0000-1000-8000-00805F9B34FB");
    private static final UUID BATTERY_characteristic = UUID.fromString("00002A19-0000-1000-8000-00805F9B34FB");
    private static final UUID BATTERY_descriptor = UUID.fromString("00002902-0000-1000-8000-00805F9B34FB");


    private static final UUID DATATransferSERVICE = UUID.fromString("75B64E51-F191-4ED1-921A-476090D80BA7");
    private static final UUID DATATransfer_characteristic = UUID.fromString("75B64E51-F195-4ED1-921A-476090D80BA7");
    private static final UUID DATATransfer_descriptor = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private static final String TAG = "tr.lib.CBtGattCallback";

    public void setConnectedGatt(BluetoothGatt mConnectedGatt) {
        this.mConnectedGatt = mConnectedGatt;
    }

    @Override
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
                        //NEED to get CharacteristicRead Result
                        mConnectedGatt.setCharacteristicNotification(characteristic, true);
                        //ENABLE Autonotify
                        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(DATATransfer_descriptor);
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        mConnectedGatt.writeDescriptor(descriptor);
                        //mConnectedGatt.readCharacteristic(characteristic);
                        //TODO Test
                        Log.d(TAG, "Characteristic-found: Datatransfer - " + characteristic.getUuid());
                    }else if(c.equals(BATTERY_characteristic)) {
                        /*PlaneConnections.getInstance().setBattery(characteristic);
                        //NEED to get CharacteristicRead Result
                        mConnectedGatt.setCharacteristicNotification(characteristic, true);
                        //ENABLE Autonotify

                        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(BATTERY_descriptor);
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        mConnectedGatt.writeDescriptor(descriptor);*/

                        //TODO: Did not work
                        //GET FIRST-Value
                        //mConnectedGatt.readCharacteristic(characteristic);
                        /*
                        int value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                        PlaneState.getInstance().setBattery(value);
                        EventBus.getDefault().post(new PlaneResult(PlaneResult.BATTERY, value));
                        */
                        Log.d(TAG, "Characteristic-found: Battery - " + characteristic.getUuid()+": without Value ");
                    }else if(c.equals(SMARTPLANE_MOTOR)) {
                        PlaneConnections.getInstance().setMotor(characteristic);
                        //mConnectedGatt.readCharacteristic(characteristic);
                        Log.d(TAG, "Characteristic-found: Motor - "+characteristic.getUuid());
                    }else if(c.equals(SMARTPLANE_RUDDER)) {
                        PlaneConnections.getInstance().setRudder(characteristic);
                        //mConnectedGatt.readCharacteristic(characteristic);
                        Log.d(TAG, "Characteristic-found: Rudder - " + characteristic.getUuid());
                    }else{
                        Log.v(TAG, "onCharacteristicRead: Unknown - "+ characteristic.getUuid());
                    }
                }
            }
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        sendResultEvent("onCharacteristicRead",gatt,characteristic);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        sendResultEvent("onCharacteristicWrite",gatt,characteristic);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        sendResultEvent("onCharacteristicChange",gatt,characteristic);
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        Log.v(TAG, "Descriptor: written");
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        Log.v(TAG, "Remote RSSI: " + rssi);
    }

    public void sendResultEvent(String s,BluetoothGatt gatt, BluetoothGattCharacteristic characteristic){
        UUID c = characteristic.getUuid();
        int value;
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
        }else if(c.equals(BATTERY_characteristic)) {
            value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            PlaneState.getInstance().setBattery(value);
            EventBus.getDefault().post(new PlaneResult(PlaneResult.BATTERY, value));
            Log.d(TAG, s + ": Battery - "+value);
        }else{
            Log.v(TAG, s + ": Unknown - " + c);
        }
    }

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