package com.tobyrich.dev.hangarapp.lib.connection;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import java.util.ArrayList;
import android.widget.Toast;
import de.greenrobot.event.EventBus;

import com.google.inject.Singleton;
import com.tobyrich.dev.hangarapp.lib.connection.events.*;
import com.tobyrich.dev.hangarapp.lib.utils.Consts;
import com.tobyrich.dev.hangarapp.lib.utils.PlaneState;

@Singleton
public class BluetoothService extends Service implements BluetoothAdapter.LeScanCallback  {
    //LOG-TAG
    private static final String TAG = "tr.lib.BluetoothService";

    // Android bluetooth manager and devices
    private BluetoothManager manager;
    private BluetoothAdapter mBluetoothAdapter;

    // cache for founded devices
    private ArrayList<BluetoothDevice> mDevices;

    // connected gatter
    private BluetoothGatt mConnectedGatt;
    // own Callback for bluetoothgatter - to manage smartplanes
    private CustomBluetoothGattCallback mGattCallback;

    /**
     * Constructer on init BluetoothSerivce
     */
    public BluetoothService(){
        super();
        mDevices = new ArrayList<BluetoothDevice>();
        Log.d(TAG, "Init");
    }

    /**
     * manage start of this service
     */
    private void startService(){
        manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);

        if(mBluetoothAdapter == null)
            mBluetoothAdapter = manager.getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            //Bluetooth is disabled
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableBtIntent);
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                PlaneState.getInstance().setConnected(false);
                EventBus.getDefault().post(new ConnectResult(false));
            }
        }else if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "No LE Support.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    /**
     * on create this service
     * (start it)
     */
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        startService();
        Log.d(TAG, "Create");
    }
    @Override
    /**
     * on destroy this service
     * (disconnect and so on)
     */
    public void onDestroy() {
        super.onDestroy();
        if(mConnectedGatt != null) {
            mConnectedGatt.disconnect();
            mConnectedGatt.close();
            mConnectedGatt = null;
        }
        PlaneState.getInstance().setConnected(false);
        EventBus.getDefault().unregister(this);
        Log.d(TAG, "Destroy");
    }

    /**
     * send data wiht Datatransfer to Plane
     * @param evt Datatransver
     */
    //TODO not ready (nothing send to plane on datatransfer by now)
    public void onEvent(DatatransferInterpreter evt) {
        byte[] value = evt.getValue();
        Log.d(TAG, "event-datatransfer: " + value);
        PlaneConnections.getInstance().getDatatransfer().setValue(value);
        mConnectedGatt.writeCharacteristic(PlaneConnections.getInstance().getDatatransfer());
    }

    /**
     * on PlaneEvent send rudder or motor value
     * @param evt PlaneEvent
     */
    public void onEvent(PlaneEvent evt){
        int value = evt.getValue();
        switch (evt.getDevice()){
            case PlaneEvent.RUDDER:
                Log.d(TAG, "event-plane-Rudder: " + value);
                if(value > Consts.MAX_RUDDER_VALUE)
                    value = Consts.MAX_RUDDER_VALUE;
                else if(value < Consts.MIN_RUDDER_VALUE)
                    value = Consts.MIN_RUDDER_VALUE;
                PlaneState.getInstance().setRudder(value);
                PlaneConnections.getInstance().getRudder().setValue(value, BluetoothGattCharacteristic.FORMAT_SINT8, 0);
                mConnectedGatt.writeCharacteristic(PlaneConnections.getInstance().getRudder());
                break;
            case PlaneEvent.MOTOR:
                Log.d(TAG, "event-plane-Motor: " + value);
                if (value > Consts.MAX_MOTOR_VALUE)
                    value = Consts.MAX_MOTOR_VALUE;
                else if (value < Consts.MIN_MOTOR_VALUE)
                    value = Consts.MIN_MOTOR_VALUE;
                PlaneState.getInstance().setMotor(value);
                PlaneConnections.getInstance().getMotor().setValue(value, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                mConnectedGatt.writeCharacteristic(PlaneConnections.getInstance().getMotor());
                break;

            default:
                Log.e(TAG, "event-plane: No Device");
        }

    }

    /**
     * on ConnectEvent connect with device
     * @param evt ConnectEvent
     */
    public void onEvent(ConnectEvent evt){
        Log.d(TAG, "event-connecting: " + evt.getDevice().getAddress());
        if(mGattCallback==null)
            mGattCallback = new CustomBluetoothGattCallback();
        mConnectedGatt = evt.getDevice().connectGatt(this, false, mGattCallback);
        mGattCallback.setConnectedGatt(mConnectedGatt);
    }

    /**
     * on ScanEvent start or stop scanning
     * @param evt ScanEvent
     */
    public void onEvent(ScanEvent evt){
        Log.d(TAG, "event-Scan: " + evt.getState());
        if(evt.getState()) {
            startService();
            if(mBluetoothAdapter !=null && mBluetoothAdapter.isEnabled()){
                mDevices = new ArrayList<BluetoothDevice>();
                startScan();
            }else{
                PlaneState.getInstance().setConnected(false);
            }
        }else {
            stopScan();
            if(mConnectedGatt != null){
                mConnectedGatt.disconnect();
            }
        }
    }

    /**
     * event on end scan (after timeout)
     */
    private Runnable mEndRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG,"scan-end: ScanResult");
            EventBus.getDefault().post(new ScanResult(mDevices));
            stopScan();
        }
    };

    /**
     * Handler for asynchrone scanning
     */
    private static Handler mHandler = new Handler() {

    };
    @Override
    /**
     * event on found asynchrone devices
     */
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if(mDevices.contains(device))
            return;
        mDevices.add(device);
        Log.d(TAG, "scan-found: " + device.getName() + " - " + device.getAddress() + " @ " + rssi);
    }

    /**
     * start scanning
     */
    private void startScan(){
        mBluetoothAdapter.startLeScan(this);
        PlaneState.getInstance().setConnected(false);
        mHandler.postDelayed(mEndRunnable, 10000);
    }

    /**
     * stop scanning
     */
    private void stopScan(){
        mBluetoothAdapter.stopLeScan(this);
    }

    @Override
    /**
     * Binding intent
     */
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Bind");
        return null;
    }
}
