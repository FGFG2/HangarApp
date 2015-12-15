package com.tobyrich.dev.hangarapp.lib.connection;

import android.util.Log;

import com.tobyrich.dev.hangarapp.lib.connection.events.GyroscopeResult;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import de.greenrobot.event.EventBus;

/**
 * Created by geno on 12/1/15.
 */
public class DatatransferInterpreter {
    public static final String TAG = "tr.lib.inter";

    public static final byte BOOL = 10;
    public static final byte BYTE = 11;
    public static final byte INT = 12;
    public static final byte FLOAT = 13;
    public static final byte VECTOR3 = 14;
    public static final byte FUSED = 30;
    public static final byte ACCEL = 31;
    public static final byte MAGNETO = 32;
    public static final byte GYRO = 33;
    public static final byte QUATERNION = 34;
    public static final byte BATTERY = 35;
    private byte type;
    private byte[] msg;

    public DatatransferInterpreter(byte type){
        this(type, null);
    }
    public DatatransferInterpreter(byte[] string){
        type = string[0];
        msg = new byte[18];
        System.arraycopy(string, 2, msg, 0, 18);
    }
    public DatatransferInterpreter(byte type, byte[] msg){
        this.type = type;
        if(msg==null) {
            this.msg = new byte[18];
        }else {
            this.msg = msg;
        }
    }
    public byte[] getValue(){
        byte[] tmp = new byte[20];
        System.arraycopy(msg,0,tmp,2,20);
        tmp[0] = type;
        tmp[1] = (byte)0x01;
        return tmp;
    }

    @Override
    public String toString() {
        switch (type){
            case ACCEL:
                return "ACCEL (x:"+ msg[1]+", y:" + msg[2]+", z:" + msg[3]+")";
            case GYRO:
                ByteBuffer x,y,z;
                x = ByteBuffer.wrap(msg,0,4);
                x.order(ByteOrder.LITTLE_ENDIAN);

                y = ByteBuffer.wrap(msg,4,4);
                y.order(ByteOrder.LITTLE_ENDIAN);

                z = ByteBuffer.wrap(msg,8,4);
                z.order(ByteOrder.LITTLE_ENDIAN);

                EventBus.getDefault().post(new GyroscopeResult(x.getFloat(),y.getFloat(),z.getFloat()));
                return "GYRO (x:"+x.getFloat() +", y:" + y.getFloat()+", z:" + z.getFloat()+")";
            default:
                return "Datatransfer not defined";
        }
    }
}
