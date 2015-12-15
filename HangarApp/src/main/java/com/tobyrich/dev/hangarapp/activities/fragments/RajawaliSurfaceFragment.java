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

public class RajawaliSurfaceFragment extends RoboFragment implements View.OnClickListener{

    @InjectView(R.id.fragment_SurfaceView_smartPlane) RajawaliSurfaceView rajawaliSurfaceView;
    @InjectView(R.id.surface_progress_bar) ProgressBar progressBar;
    @Inject Renderer renderer;
    @Inject RajawaliSurfaceOnTouchListener onTouchListener;
    @Inject RajawaliSurfaceOnScaleListener onScaleListener;

    Button testButton;

    private double xAxis = 0;
    private double yAxis = 0;
    private double zAxis = 0;

    private static int x = 0;
    private static int y = 0;
    private static int z = 0;

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

        View view = inflater.inflate(
                R.layout.fragment_rajawali_view, container, false);

        testButton = (Button) view.findViewById(R.id.test_button);
        testButton.setOnClickListener(this);

        return inflater.inflate(R.layout.fragment_rajawali_view, container, false);
        //return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rajawaliSurfaceView.setFrameRate(60);
        rajawaliSurfaceView.setRenderMode(IRajawaliSurface.RENDERMODE_WHEN_DIRTY);
        rajawaliSurfaceView.setTransparent(true);
        rajawaliSurfaceView.setSurfaceRenderer(renderer);
        rajawaliSurfaceView.setOnClickListener(null);
        rajawaliSurfaceView.setOnTouchListener(onTouchListener);
        new ScaleGestureDetector(view.getContext(), onScaleListener);
    }

    public void onEventMainThread(GyroscopeResult evt){
        xAxis = Math.PI / 180 * evt.getX();
        yAxis = Math.PI / 180 * evt.getY();
        zAxis = Math.PI / 180 * evt.getZ();
        //Toast.makeText(getActivity(), "x: " + xAxis + " | y: " + yAxis + " | z: " + zAxis, Toast.LENGTH_SHORT).show();
        renderer.getShownObjectOnScene().rotate(Vector3.Axis.Y, -xAxis);
        renderer.getShownObjectOnScene().rotate(Vector3.Axis.X, -yAxis);
        renderer.getShownObjectOnScene().rotate(Vector3.Axis.Z, -zAxis);

        renderer.getCurrentCamera().setLookAt(0, 0, 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.test_button:
                /*int min = 0;
                int max = 360;
                int x = min + (int)(Math.random() * ((max - min) + 1));
                int y = min + (int)(Math.random() * ((max - min) + 1));
                int z = min + (int)(Math.random() * ((max - min) + 1));*/
                z = z+5;
                EventBus.getDefault().post(new GyroscopeResult(x,y,z));
                break;
            default:
                break;
        }
    }


    public void onEventMainThread(RajawaliSurfaceLoad event){
        if (event.isSuccess()) {
            progressBar.setVisibility(View.GONE);
        }
    }
}
