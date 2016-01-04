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

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_rajawali_view, container, false);
    }

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

    public void onEventMainThread(RajawaliSurfaceLoad event){
        if (event.isSuccess()) {
            progressBar.setVisibility(View.GONE);
        }
    }
}
