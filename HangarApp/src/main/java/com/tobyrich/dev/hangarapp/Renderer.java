package com.tobyrich.dev.hangarapp;

import android.content.Context;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.google.inject.Inject;
import com.tobyrich.dev.hangarapp.events.RajawaliSurfaceLoad;
import com.tobyrich.dev.hangarapp.lib.connection_old.ConnectionStatus;
import com.tobyrich.dev.hangarapp.listener.rotation.Rotatable;
import com.tobyrich.dev.hangarapp.listener.rotation.RotationListener;

import org.rajawali3d.Object3D;
import org.rajawali3d.lights.ALight;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.loader.ALoader;
import org.rajawali3d.loader.LoaderAWD;
import org.rajawali3d.loader.async.IAsyncLoaderCallback;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.renderer.RajawaliRenderer;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import roboguice.inject.ContextSingleton;

@ContextSingleton
public class Renderer extends RajawaliRenderer implements
        IAsyncLoaderCallback {

    private List<RotatableComponent> rotatables = new ArrayList<RotatableComponent>();

    private ScaleGestureDetector scaleGestureDetector;
    private RotationListener rotationListener;

    private float scaleFactor = 1f;
    private Object3D shownObjectOnScene;

    @Inject
    public Renderer(Context context, ConnectionStatus connectionStatus) {
        super(context.getApplicationContext());

        rotationListener = new RotationListener((SensorManager) context.getSystemService(
                context.SENSOR_SERVICE));
        RotatableComponent r1 = new RotatableComponent(this);
        rotationListener.addRotatable(r1);
        rotatables.add(r1);
        RotatableComponent r2 = new RotatableComponent(this);

        //TODO: REACTIVATE IF BLUETOOTH LIB WORKS
        //connectionStatus.getConnection().addRotatable(r2);
        rotatables.add(r2);
    }

    @Override
    protected void initScene() {
        // add directional light for scene
        getCurrentScene().addLight(constructLight());

        // load model asynchronously
        loadAwd(R.raw.smart_plane_mesh);
    }

    private ALight constructLight() {
        ALight directionalLight = new DirectionalLight(1f, -0.5f, 1f);
        directionalLight.setColor(1.0f, 1.0f, 1.0f);
        directionalLight.setPower(1);
        return directionalLight;
    }

    private void updateShownObject(Object3D objectToShow) {
        shownObjectOnScene = objectToShow;
        getCurrentScene().clearChildren();
        getCurrentScene().addChild(objectToShow);
        updateCamera();
    }

    private void updateCamera() {
        getCurrentCamera().setZ(-20 / scaleFactor);
        getCurrentCamera().setLookAt(0, 0, 0);
    }

    /**
     * Asynchronous load of resource
     */
    private void loadAwd(int resourceId) {
        ALoader loader = new LoaderAWD(getContext().getResources(), getTextureManager(), resourceId);
        loadModel(loader, this, resourceId);
    }

    @Override
    public void onModelLoadComplete(ALoader aLoader) {
        final LoaderAWD loader = (LoaderAWD) aLoader;
        updateShownObject(loader.getParsedObject());
        EventBus.getDefault().post(new RajawaliSurfaceLoad("Finished loading model!", true));
    }

    @Override
    public void onModelLoadFailed(ALoader aLoader) {
        EventBus.getDefault().post(new RajawaliSurfaceLoad("Error while loading Model!", false));
    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
        // not implemented yet
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
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

        double deltaX = calculateDelta(x, shownObjectOnScene.getRotX());
        double deltaY = calculateDelta(y, shownObjectOnScene.getRotY());
        double deltaZ = calculateDelta(z, shownObjectOnScene.getRotZ());

        shownObjectOnScene.rotate(Vector3.Axis.Y, deltaX);
        shownObjectOnScene.rotate(Vector3.Axis.X, deltaY);
        shownObjectOnScene.rotate(Vector3.Axis.Z, -deltaZ);
    }

    public void onRotationUpdateDuplicate() {

        float x = 0f;
        float y = 0f;
        float z = 0f;
        for (RotatableComponent r : rotatables) {

            x += r.x;
            y += r.y;
            z += r.z;
        }

        double deltaX = calculateDelta(x, shownObjectOnScene.getRotX());
        double deltaY = calculateDelta(y, shownObjectOnScene.getRotY());
        double deltaZ = calculateDelta(z, shownObjectOnScene.getRotZ());

        shownObjectOnScene.rotate(Vector3.Axis.Y, deltaX);
        shownObjectOnScene.rotate(Vector3.Axis.X, deltaY);
        shownObjectOnScene.rotate(Vector3.Axis.Z, -deltaZ);
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

    public Object3D getShownObjectOnScene() {
        return shownObjectOnScene;
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
