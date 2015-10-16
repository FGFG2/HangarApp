package com.tobyrich.dev.hangarapp.activities.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.inject.Inject;
import com.tobyrich.dev.hangarapp.R;
import com.tobyrich.dev.hangarapp.Renderer;
import com.tobyrich.dev.hangarapp.listener.RajawaliSurfaceOnScaleListener;
import com.tobyrich.dev.hangarapp.listener.RajawaliSurfaceOnTouchListener;

import org.rajawali3d.surface.IRajawaliSurface;
import org.rajawali3d.surface.RajawaliSurfaceView;

import roboguice.fragment.provided.RoboFragment;
import roboguice.inject.InjectView;

public class RajawaliSurfaceFragment extends RoboFragment {

    @InjectView(R.id.fragment_SurfaceView_smartPlane) RajawaliSurfaceView rajawaliSurfaceView;
    @InjectView(R.id.surface_progress_bar) ProgressBar progressBar;
    @Inject Renderer renderer;
    @Inject RajawaliSurfaceOnTouchListener onTouchListener;
    @Inject RajawaliSurfaceOnScaleListener onScaleListener;

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
        rajawaliSurfaceView.setFrameRate(60);
        rajawaliSurfaceView.setRenderMode(IRajawaliSurface.RENDERMODE_WHEN_DIRTY);
        rajawaliSurfaceView.setTransparent(true);
        rajawaliSurfaceView.setSurfaceRenderer(renderer);
        rajawaliSurfaceView.setOnClickListener(null);
        rajawaliSurfaceView.setOnTouchListener(onTouchListener);
        new ScaleGestureDetector(view.getContext(), onScaleListener);
    }
}
