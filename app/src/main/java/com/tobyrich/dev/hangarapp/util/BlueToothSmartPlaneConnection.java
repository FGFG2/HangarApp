package com.tobyrich.dev.hangarapp.util;

import com.google.inject.Inject;
import com.tobyrich.dev.hangarapp.rotation.Rotatable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlueToothSmartPlaneConnection extends Connection {

    @Inject PlaneData planeData;

    private List<Rotatable> rotatables = new ArrayList<>();

    @Override
    public void addRotatable(Rotatable rotatable) {

        this.rotatables.add(rotatable);
    }

    @Override
    public void removeRotatable(Rotatable rotatable) {

        this.rotatables.remove(rotatable);
    }

    @Override
    protected void onIncomingRotation(Map<String, String> properties) {

        float rX = extractFloat(properties.get(Connection.ROTATION_X));
        float rY = extractFloat(properties.get(Connection.ROTATION_Y));
        float rZ = extractFloat(properties.get(Connection.ROTATION_Z));
        for (Rotatable rotatable : rotatables) {
            rotatable.setOriginDeviation(rX, rY, rZ);
        }
    }

    @Override
    /**
     * This function should be triggered when the BT connection with the Plane is set.
     */
    protected void onIncomingStatistics(Map<String, String> properties) {
        planeData.setAccumulatedTime(extractInt(properties.get(Connection.ACCUMULATED_TIME)));
        planeData.setLastFlightCharge(extractDouble(properties.get(Connection.LAST_FLIGHT_CHARGE)));
        planeData.setLastG(extractInt(properties.get(Connection.LAST_G)));
        planeData.setLastHeight(extractInt(properties.get(Connection.LAST_HEIGHT)));
        planeData.setLastRPM(extractInt(properties.get(Connection.LAST_RPM)));
        planeData.setLastTime(extractInt(properties.get(Connection.LAST_TIME)));
        planeData.setMaxG(extractInt(properties.get(Connection.MAX_G)));
        planeData.setMaxHeight(extractInt(properties.get(Connection.MAX_HEIGHT)));
        planeData.setMaxRPM(extractInt(properties.get(Connection.MAX_RPM)));

        planeData.save();
    }

    @Override
    protected void sendStartFactoryTest(Map<String, String> properties) {

    }

    @Override
    protected void sendSetLimitations(Map<String, String> properties) {

    }

    private double extractDouble(String value) {

        double d;
        try {
            d = Double.parseDouble(value);
        } catch (NumberFormatException | NullPointerException e) {
            d = 0d;
        }
        return d;
    }

    private float extractFloat(String value) {

        float f;
        try {
            f = Float.parseFloat(value);
        } catch (NumberFormatException | NullPointerException e) {
            f = 0f;
        }
        return f;
    }

    private int extractInt(String value) {

        int i;
        try {
            i = Integer.parseInt(value);
        } catch (NumberFormatException | NullPointerException e) {
            i = 0;
        }
        return i;
    }
}
