package com.tobyrich.dev.hangarapp;

import android.content.Context;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.google.inject.Inject;
import com.tobyrich.dev.hangarapp.rotation.Rotatable;
import com.tobyrich.dev.hangarapp.rotation.RotationListener;
import com.tobyrich.dev.hangarapp.util.ConnectionStatus;

import org.rajawali3d.Object3D;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.loader.LoaderAWD;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.renderer.RajawaliRenderer;

import java.util.ArrayList;
import java.util.List;

public class Renderer extends RajawaliRenderer implements View.OnTouchListener,
        ScaleGestureDetector.OnScaleGestureListener {

    private List<RotatableComponent> rotatables = new ArrayList<>();

    private final static String TAG = "Renderer";
    private DirectionalLight directionalLight;
    private Sphere earthSphere;
    private Object3D object;
    private ScaleGestureDetector scaleGestureDetector;
    private RotationListener rotationListener;

    private float scaleFactor = 1f;

    @Inject
    public Renderer(Context context, ConnectionStatus connectionStatus) {
        super(context);
        setFrameRate(60);
        scaleGestureDetector = new ScaleGestureDetector(context, this);
        rotationListener = new RotationListener((SensorManager) context.getSystemService(
                context.SENSOR_SERVICE));
        RotatableComponent r1 = new RotatableComponent(this);
        rotationListener.addRotatable(r1);
        rotatables.add(r1);
        RotatableComponent r2 = new RotatableComponent(this);
        connectionStatus.getConnection().addRotatable(r2);
        rotatables.add(r2);
    }

    @Override
    protected void initScene() {
        directionalLight = new DirectionalLight(1f, -0.5f, 1f);
        directionalLight.setColor(1.0f, 1.0f, 1.0f);
        directionalLight.setPower(2);
        getCurrentScene().addLight(directionalLight);

        Material material = new Material();
        material.enableLighting(true);
        material.setDiffuseMethod(new DiffuseMethod.Lambert());
        material.setColor(236);
        //object = loadObj(R.raw.blender_obj);
        object = loadAwd(R.raw.smart_plane_mesh);
        if (object == null) {
            //loading failed, show sphere instead :)
            earthSphere = new Sphere(1, 24, 24);
            earthSphere.setMaterial(material);
            getCurrentScene().addChild(earthSphere);
        } else {
            getCurrentScene().addChild(object);
        }
        updateCamera();
        rotationListener.start();
    }

    @Override
    public void onOffsetsChanged(float v, float v1, float v2, float v3, int i, int i1) {

    }

    @Override
    public void onTouchEvent(MotionEvent motionEvent) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getHistorySize() > 0) {
                    float histX = event.getHistoricalAxisValue(MotionEvent.AXIS_X, Math.min(event.getHistorySize() - 1, 0));
                    float histY = event.getHistoricalAxisValue(MotionEvent.AXIS_Y, Math.min(event.getHistorySize() - 1, 0));
                    float currX = event.getAxisValue(MotionEvent.AXIS_X);

                    float currY = event.getAxisValue(MotionEvent.AXIS_Y);
                    float deltaX = currX - histX;
                    float deltaY = currY - histY;
                    object.rotate(Vector3.Axis.Y, -deltaX);
                    object.rotate(Vector3.Axis.X, -deltaY);
                    getCurrentCamera().setLookAt(0, 0, 0);
                }
                return true;
            case MotionEvent.ACTION_UP:
                //
                break;
            default:
                return false;
        }
        return false;
    }

    private void updateCamera() {
        getCurrentCamera().setZ(-20 / scaleFactor);
        getCurrentCamera().setLookAt(0,0,0);
    }


    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        if (detector.getScaleFactor() > 0.01) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.min(Math.max(scaleFactor, 0.20f), 20);
//            Log.d("Scale","scalefactor is "+scaleFactor);
            updateCamera();
            return true;
        }
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {

        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
    }

    public void onRotationUpdate() {

        float x = 0f;
        float y = 0f;
        float z = 0f;
        for (RotatableComponent r : rotatables) {

            x += r.x;
            y += r.y;
            z += r.z;
        }

        double deltaX = calculateDelta(x, object.getRotX());
        double deltaY = calculateDelta(y, object.getRotY());
        double deltaZ = calculateDelta(z, object.getRotZ());

        object.rotate(Vector3.Axis.Y, deltaX);
        object.rotate(Vector3.Axis.X, deltaY);
        object.rotate(Vector3.Axis.Z, -deltaZ);
    }

    private double calculateDelta(float f, double rot) {
        double result = f * 180f / (float) Math.PI - rot;
        if (result < 4 && result > -4) {
            result = 0;
        }
        if (result > 2) {
            result = 2;
        }
        if (result < -2) {
            result = -2;
        }
        return result;
    }

    private Object3D loadObj(int resourceId) {
        LoaderOBJ loader = new LoaderOBJ(this, resourceId);

        try {
            loader.parse();
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        return loader.getParsedObject();
    }

    private Object3D loadAwd(int resourceId) {
        LoaderAWD loader = new LoaderAWD(getContext().getResources(), getTextureManager(), resourceId);

        try {
            loader.parse();
        } catch (ParsingException e) {
            e.printStackTrace();
        }
        return loader.getParsedObject();
    }

    private class RotatableComponent implements Rotatable {

        private float x = 0f;
        private float y = 0f;
        private float z = 0f;

        private Renderer renderer;

        private RotatableComponent(Renderer renderer) {

            this.renderer = renderer;
        }

        @Override
        public void setOriginDeviation(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
            renderer.onRotationUpdate();
        }
    }

}
