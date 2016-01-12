package com.tobyrich.dev.hangarapp.lib.connection;

import android.util.Log;

import com.tobyrich.dev.hangarapp.lib.connection.events.GyroscopeResult;
import com.tobyrich.dev.hangarapp.lib.connection.events.PlaneResult;
import com.tobyrich.dev.hangarapp.lib.utils.PlaneState;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import de.greenrobot.event.EventBus;

/**
 * DatatransferInterpreter is a own implementation to receive
 * extra data from the smartplane.
 * (For it Battery was removed, because there is only one BLE Notification possible.)
 *
 * Created by geno on 12/1/15.
 */
public class DatatransferInterpreter {
    //LOG-TAG
    public static final String TAG = "tr.lib.inter";

    // Data-Types of packets
    public static final byte BOOL = 10;
    public static final byte BYTE = 11;
    public static final byte INT = 12;
    public static final byte FLOAT = 13;
    public static final byte VECTOR3 = 14;

    // Plane-Types of packets
    public static final byte FUSED = 30;
    public static final byte ACCEL = 31;
    public static final byte MAGNETO = 32;
    public static final byte GYRO = 33;
    public static final byte QUATERNION = 34;
    public static final byte BATTERY = 35;

    // vars of cached packet
    private byte type;
    private byte[] msg;

    /**
     * DatatransferIntepreter with only type part of packet
     * @param type packet type
     */
    public DatatransferInterpreter(byte type){
        this(type, null);
    }

    /**
     * DatatransferInterpreter by setting packet
     * @param packet full packet
     */
    public DatatransferInterpreter(byte[] packet){
        type = packet[0];
        msg = new byte[18];
        System.arraycopy(packet, 2, msg, 0, 18);
    }

    /**
     * DatatransferInterpreter by setting parts of packet
     * @param type packet type
     * @param msg  packet message
     */
    public DatatransferInterpreter(byte type, byte[] msg){
        this.type = type;
        if(msg==null) {
            this.msg = new byte[18];
        }else {
            this.msg = msg;
        }
    }

    /**
     * get the packet from cached packet
     * @return packet full packet
     */
    public byte[] getValue(){
        byte[] tmp = new byte[20];
        System.arraycopy(msg,0,tmp,2,20);
        tmp[0] = type;
        tmp[1] = (byte)0x01;
        return tmp;
    }


    /**
     * get message from cached packet
     * @return msg packet message
     */
    public byte[] getMsg(){
        return msg;
    }


    /**
     * get type from cached packet
     * @return type packet type
     */
    public byte getType(){
        return type;
    }

    /**
     * Interprete a packet
     * @param packet byte array of the datatransfer-packet
     * @return DatatransferInterpreter of the packet
     */
    public static DatatransferInterpreter received(byte[] packet){
        DatatransferInterpreter a = new DatatransferInterpreter(packet);
        a.received();
        return a;
    }

    /**
     * Interprete cached packet
     */
    public void received() {
        ByteBuffer x,y,z;
        float x_i,y_i,z_i;
        int tmp;
        switch (type){
            case ACCEL:
                x = ByteBuffer.wrap(msg,0,4);
                x.order(ByteOrder.LITTLE_ENDIAN);
                x_i = x.getFloat();

                y = ByteBuffer.wrap(msg, 4, 4);
                y.order(ByteOrder.LITTLE_ENDIAN);
                y_i = y.getFloat();

                z = ByteBuffer.wrap(msg, 8, 4);
                z.order(ByteOrder.LITTLE_ENDIAN);
                z_i = z.getFloat();

                Log.d(TAG,"ACCEL (x:"+x_i +", y:" + y_i+", z:" + z_i+")");
                break;
            case GYRO:
                x = ByteBuffer.wrap(msg,0,4);
                x.order(ByteOrder.LITTLE_ENDIAN);
                x_i = x.getFloat();

                y = ByteBuffer.wrap(msg, 4, 4);
                y.order(ByteOrder.LITTLE_ENDIAN);
                y_i = y.getFloat();

                z = ByteBuffer.wrap(msg, 8, 4);
                z.order(ByteOrder.LITTLE_ENDIAN);
                z_i = z.getFloat();

                Log.d(TAG, "GYRO (x:" + x_i + ", y:" + y_i + ", z:" + z_i + ")");
                EventBus.getDefault().post(new GyroscopeResult(x_i, y_i, z_i));
                break;
            case BATTERY:
                x = ByteBuffer.wrap(msg,0,4);
                x.order(ByteOrder.LITTLE_ENDIAN);
                x_i = x.getFloat();
                PlaneState.getInstance().setBattery((int)x_i);
                EventBus.getDefault().post(new PlaneResult(PlaneResult.BATTERY, (int) x_i));
                Log.d(TAG, "BATTERY (" + x_i + ")");
                break;
            default:
                Log.v(TAG, "Datatransfer not defined (type:"+type+")");
        }
    }
}
