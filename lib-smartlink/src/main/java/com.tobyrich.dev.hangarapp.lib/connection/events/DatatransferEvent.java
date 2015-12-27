package com.tobyrich.dev.hangarapp.lib.connection.events;

import android.util.Base64;

/**
 * Created by geno on 12/1/15.
 */
public class DatatransferEvent {
    public static final byte ACCELOMETER = 100;
    private byte type;
    private byte[] msg;

    public  DatatransferEvent(byte type){
        this(type,null);
    }

    public  DatatransferEvent(byte[] string){
        type = string[0];

        if(msg==null) {
            this.msg = new byte[18];
        }

        System.arraycopy(string, 2, msg, 0, 18);
    }
    public  DatatransferEvent(byte type, byte[] msg){
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
            case ACCELOMETER:
                return "ACCELOMETER (x:"+ msg[1]+", y:" + msg[2]+", x:" + msg[3]+")";
            default:
                return "Datatransfer not defined";
        }
    }
}
