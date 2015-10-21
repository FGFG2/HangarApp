package com.tobyrich.dev.hangarapp.listener;

import android.view.ScaleGestureDetector;

import com.google.inject.Inject;
import com.tobyrich.dev.hangarapp.Renderer;

public class RajawaliSurfaceOnScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

    @Inject Renderer renderer;

    private float scaleFactor = 1f;

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        if (detector.getScaleFactor() > 0.01) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.min(Math.max(scaleFactor, 0.20f), 20);
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

    private void updateCamera() {
        renderer.getCurrentCamera().setZ(-20 / scaleFactor);
        renderer.getCurrentCamera().setLookAt(0, 0, 0);
    }
}
