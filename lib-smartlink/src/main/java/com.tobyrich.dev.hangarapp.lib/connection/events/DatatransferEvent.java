package com.tobyrich.dev.hangarapp.lib.connection.events;

/**
 * Created by geno on 12/1/15.
 */
public class DatatransferEvent {
    public static final byte SEND_PROBE_START= 0x21;
    private byte type;
    private byte[] msg;

    public  DatatransferEvent(byte type){
        this(type,null);
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
}
