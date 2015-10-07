package com.tobyrich.dev.hangarapp.util;

import com.tobyrich.dev.hangarapp.rotation.Rotatable;

import java.util.Map;

/**
 * Represents a connection with an external device.
 * Created by Jonas on 10.06.2015.
 */
public abstract class Connection {

    public enum IncomingDataType {Rotation, Statistic}
    public enum OutgoingDataType {StartFactoryTest, SetLimitations}

    public static final String ROTATION_X = "ROTATION_X";
    public static final String ROTATION_Y = "ROTATION_Y";
    public static final String ROTATION_Z = "ROTATION_Z";

    public static final String ACCUMULATED_TIME = "ACCUMULATED_TIME";
    public static final String LAST_FLIGHT_CHARGE = "LAST_FLIGHT_CHARGE";
    public static final String LAST_G = "LAST_G";
    public static final String LAST_HEIGHT = "LAST_HEIGHT";
    public static final String LAST_RPM = "LAST_RPM";
    public static final String LAST_TIME = "LAST_TIME";
    public static final String MAX_G = "MAX_G";
    public static final String MAX_HEIGHT = "MAX_HEIGHT";
    public static final String MAX_RPM = "MAX_RPM";

    protected void handleIncomingData(IncomingDataType type, Map<String, String> properties) {

        switch (type) {

            case Rotation:
                onIncomingRotation(properties);
                break;
            case Statistic:
                onIncomingStatistics(properties);
                break;
            default:
                break;
        }
    }

    protected void handleOutgoingData(OutgoingDataType type, Map<String, String> properties) {

        switch (type) {

            case StartFactoryTest:
                sendStartFactoryTest(properties);
                break;
            case SetLimitations:
                sendSetLimitations(properties);
                break;
            default:
                break;
        }
    }

    protected abstract void onIncomingRotation(Map<String, String> properties);
    protected abstract void onIncomingStatistics(Map<String, String> properties);
    protected abstract void sendStartFactoryTest(Map<String, String> properties);
    protected abstract void sendSetLimitations(Map<String, String> properties);
    public abstract void addRotatable(Rotatable rotatable);
    public abstract void removeRotatable(Rotatable rotatable);
}
