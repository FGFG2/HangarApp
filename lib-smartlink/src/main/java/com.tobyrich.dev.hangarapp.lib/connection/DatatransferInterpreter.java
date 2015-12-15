package com.tobyrich.dev.hangarapp.lib.connection;

/**
 * Created by geno on 12/1/15.
 */
public class DatatransferInterpreter {
    public static final byte ACCELOMETER = 100;
    private byte type;
    private byte[] msg;

    public DatatransferInterpreter(byte type){
        this(type,null);
    }
    public DatatransferInterpreter(byte[] string){
        type = string[0];
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
            case ACCELOMETER:
                return "ACCELOMETER (x:"+ msg[1]+", y:" + msg[2]+", z:" + msg[3]+")";
            default:
                return "Datatransfer not defined";
        }
    }
}
