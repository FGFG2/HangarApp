package com.tobyrich.dev.hangarapp.activities.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.inject.Inject;
import com.tobyrich.dev.hangarapp.R;
import com.tobyrich.dev.hangarapp.Renderer;
import com.tobyrich.dev.hangarapp.events.RajawaliSurfaceLoad;
import com.tobyrich.dev.hangarapp.lib.connection.events.GyroscopeResult;
import com.tobyrich.dev.hangarapp.lib.connection.events.PlaneResult;
import com.tobyrich.dev.hangarapp.lib.connection.events.ScanEvent;
import com.tobyrich.dev.hangarapp.lib.utils.PlaneState;
import com.tobyrich.dev.hangarapp.listener.RajawaliSurfaceOnScaleListener;
import com.tobyrich.dev.hangarapp.listener.RajawaliSurfaceOnTouchListener;

import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.surface.IRajawaliSurface;
import org.rajawali3d.surface.RajawaliSurfaceView;

import java.util.Random;

import de.greenrobot.event.EventBus;
import roboguice.fragment.provided.RoboFragment;
import roboguice.inject.InjectView;

/**
 * A fragment which holds the RajawaliSurfaceView of the planemodel, which can be displayed and
 * moved around in the app.
 */

public class RajawaliSurfaceFragment extends RoboFragment{

    private static final int FRAME_RATE = 60;

    @InjectView(R.id.fragment_SurfaceView_smartPlane) RajawaliSurfaceView rajawaliSurfaceView;
    @InjectView(R.id.surface_progress_bar) ProgressBar progressBar;
    @Inject Renderer renderer;
    @Inject RajawaliSurfaceOnTouchListener onTouchListener;
    @Inject RajawaliSurfaceOnScaleListener onScaleListener;

    private double xAxis = 0;
    private double yAxis = 0;
    private double zAxis = 0;

    /**
     * Makes the fragment visible to the user (based on its containing activity being started).
     * Registers the RajawaliSurfaceFragment to receive events.
     */
    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    /**
     * fragment is no longer visible to the user either because its activity is being stopped or a
     * fragment operation is modifying it in the activity.
     * Unregisters the RajawaliSurfaceFragment to receive events.
     */
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /**
     *  Called to do initial creation of the fragment.
     *
     * @param savedInstanceState {@link Bundle}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Creates and returns the view hierarchy associated with the fragment.
     *
     * @param inflater {@link LayoutInflater}
     * @param container {@link ViewGroup}
     * @param savedInstanceState {@link Bundle}
     * @return the {@link View}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_rajawali_view, container, false);
    }

    /**
     * Method gets called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned,
     * but before any saved state has been restored in to the view.
     * Sets up the {@link RajawaliSurfaceView}.
     *
     * @param view {@link View}
     * @param savedInstanceState {@link Bundle}
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rajawaliSurfaceView.setFrameRate(FRAME_RATE);
        rajawaliSurfaceView.setRenderMode(IRajawaliSurface.RENDERMODE_WHEN_DIRTY);
        rajawaliSurfaceView.setTransparent(true);
        rajawaliSurfaceView.setSurfaceRenderer(renderer);
        rajawaliSurfaceView.setOnClickListener(null);
        rajawaliSurfaceView.setOnTouchListener(onTouchListener);
        new ScaleGestureDetector(view.getContext(), onScaleListener);
    }

    /**
     * Method gets called on every {@link GyroscopeResult} event. The GyroscopeResult holds the change
     * value between the last postion and the current position in degrees. The values of the X-, Y-
     * and Z-axis is converted into radian and is then used to rotate the plane.
     *
     * @param evt {@link GyroscopeResult}
     */
    public void onEventMainThread(GyroscopeResult evt){
        double converter = Math.PI / 180;
        xAxis = converter * evt.getX();
        yAxis = converter * evt.getY();
        zAxis = converter * evt.getZ();
        renderer.getShownObjectOnScene().rotate(Vector3.Axis.Y, -xAxis);
        renderer.getShownObjectOnScene().rotate(Vector3.Axis.X, -yAxis);
        renderer.getShownObjectOnScene().rotate(Vector3.Axis.Z, -zAxis);
        renderer.getCurrentCamera().setLookAt(0, 0, 0);
    }

    /**
     * Gets called on every {@link RajawaliSurfaceLoad} event. If the RajawaliSurfaceView of the
     * planemodel is finished loading, the {@link ProgressBar} will be set to invisible.
     *
     * @param event {@link RajawaliSurfaceLoad}
     */
    public void onEventMainThread(RajawaliSurfaceLoad event){
        if (event.isSuccess()) {
            progressBar.setVisibility(View.GONE);
        }
    }
}
