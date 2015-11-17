package com.tobyrich.dev.hangarapp.lib.connection;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import java.util.ArrayList;
import android.widget.Toast;
import java.util.UUID;
import de.greenrobot.event.EventBus;

import com.google.inject.Singleton;
import com.tobyrich.dev.hangarapp.lib.connection.events.*;
import com.tobyrich.dev.hangarapp.lib.utils.Consts;
import com.tobyrich.dev.hangarapp.lib.utils.PlaneState;

@Singleton
public class BluetoothService extends Service implements BluetoothAdapter.LeScanCallback  {
    private static final String TAG = "tr.lib.BluetoothService";

    private BluetoothManager manager;

    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<BluetoothDevice> mDevices;

    private BluetoothGatt mConnectedGatt;

    //private BluetoothGattCharacteristic battery, rudder, motor;

    private static final int MSG_PROGRESS = 201;
    private static final int MSG_DISMISS = 202;
    private static final int MSG_CLEAR = 301;

    public BluetoothService(){
        super();
        mDevices = new ArrayList<BluetoothDevice>();
        Log.d(TAG, "Init");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);

        manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);

        if(mBluetoothAdapter == null)
            mBluetoothAdapter = manager.getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            //Bluetooth is disabled
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableBtIntent);
        }else if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "No LE Support.", Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, "Create");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mConnectedGatt.disconnect();
        EventBus.getDefault().unregister(this);
        Log.d(TAG, "Destroy");

    }
    /*
     * Events to Handle
     */
    public void onEvent(PlaneEvent evt){
        int value = evt.getValue();
        switch (evt.getDevice()){
            case PlaneEvent.RUDDER:
                Log.d(TAG, "event-plane-Ruder: " + value);
                if(value > Consts.MAX_RUDDER_VALUE)
                    value = Consts.MAX_RUDDER_VALUE;
                else if(value < Consts.MIN_RUDDER_VALUE)
                    value = Consts.MIN_RUDDER_VALUE;
                PlaneState.getInstance().setRudder(value);
                SmartPlaneCharacteristic.getInstance().getRudder().setValue(value, BluetoothGattCharacteristic.FORMAT_SINT8, 0);
                mConnectedGatt.writeCharacteristic(SmartPlaneCharacteristic.getInstance().getRudder());
                break;
            case PlaneEvent.MOTOR:
                Log.d(TAG, "event-plane-Motor: " + value);
                if (value > Consts.MAX_MOTOR_VALUE)
                    value = Consts.MAX_MOTOR_VALUE;
                else if (value < Consts.MIN_MOTOR_VALUE)
                    value = Consts.MIN_MOTOR_VALUE;
                PlaneState.getInstance().setMotor(value);
                SmartPlaneCharacteristic.getInstance().getMotor().setValue(value, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                mConnectedGatt.writeCharacteristic(SmartPlaneCharacteristic.getInstance().getMotor());
                break;

            default:
                Log.e(TAG, "event-plane: No Device");
        }

    }

    public void onEvent(ConnectEvent evt){
        Log.d(TAG, "event-connecting: " + evt.getDevice().getAddress());
        mConnectedGatt = evt.getDevice().connectGatt(this, false, mGattCallback);
    }
    public void onEvent(ScanEvent evt){
        Log.d(TAG, "event-Scan: " + evt.getState());
        if(evt.getState())
            startScan();
        else
            stopScan();
    }
    private Runnable mEndRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG,"scan-end: ScanResult");
            EventBus.getDefault().post(new ScanResult(mDevices));
            stopScan();
        }
    };


    private static Handler mHandler = new Handler() {

    };
    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        mDevices.add(device);
        Log.d(TAG, "scan-found: " + device.getName() + " - " + device.getAddress() + " @ " + rssi);
    }

    private void startScan(){
        mBluetoothAdapter.startLeScan(this);
        mHandler.postDelayed(mEndRunnable, 10000);
    }

    private void stopScan(){
        mBluetoothAdapter.stopLeScan(this);
    }

    private BluetoothGattCallback mGattCallback = new CustomBluetoothGattCallback(mConnectedGatt);

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Bind");
        return null;
    }
}
