package com.tobyrich.dev.hangarapp.listener.rotation;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Reacts on rotation of the device.
 * Created by Jonas on 02.06.2015.
 */
public class RotationListener extends Thread {

    private final SensorManager sensorManager;
    private final Sensor gyroscope;
    private final SensorListener sensorListener;
    private List<Rotatable> rotatables = new ArrayList<Rotatable>();

    public RotationListener(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorListener = new SensorListener();
        sensorListener.start();
        sensorManager.registerListener(sensorListener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void addRotatable(Rotatable rotatable) {
        this.rotatables.add(rotatable);
    }

    public void removeRotatable(Rotatable rotatable) {

        this.rotatables.remove(rotatable);
    }


    @Override
    public void run() {

        while (true) {

            for (Rotatable r : rotatables) {
                r.setOriginDeviation(sensorListener.rotationX, sensorListener.rotationY, sensorListener.rotationZ);
            }
            try {
                sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
    private class SensorListener extends Thread implements SensorEventListener {

        private volatile float rotationX = 0f;
        private volatile float rotationY = 0f;
        private volatile float rotationZ = 0f;

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            rotationX = sensorEvent.values[0];
            rotationY = sensorEvent.values[1];
            rotationZ = sensorEvent.values[2];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }

    }
}
