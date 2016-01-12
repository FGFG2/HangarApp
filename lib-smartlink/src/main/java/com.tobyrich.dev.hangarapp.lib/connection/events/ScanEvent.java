package com.tobyrich.dev.hangarapp.lib.connection.events;

/**
 * ScanEvent
 *  a event to requested for a scan of devices
 * Created by geno on 10/23/15.
 */
public class ScanEvent {
    // state of scan (true:start,false:stop)
    private boolean state;

    /**
     * set state of scan as start or stop (by constructor)
     * @param state (true:start,false:stop)
     */
    public ScanEvent(boolean state) {
        this.state = state;
    }

    /**
     * get state of scan as start or stop
     * @return state
     */
    public boolean getState(){
        return state;
    }
}
