package com.tobyrich.dev.hangarapp.listener;

import android.view.ScaleGestureDetector;

import com.google.inject.Inject;
import com.tobyrich.dev.hangarapp.Renderer;

/**
 * Listener, which handles the scale of the plane model, when it gets touched.
 */
public class RajawaliSurfaceOnScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

    @Inject Renderer renderer;

    private float scaleFactor = 1f;
    private float minValueToUpdateCamera = 0.01f;
    private float maxScaleFactor = 20f;
    private float minScaleFactor = 0.20f;

    /**
     * Responds to scaling events for a gesture in progress. Reported by pointer motion.
     * Sets the scaleFactor based on the detected scaleFactor and the min and max scaleFactor.
     *
     * @param detector {@link ScaleGestureDetector}
     * @return whether or not the plane model has been scaled.
     */
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        if (detector.getScaleFactor() > minValueToUpdateCamera) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.min(Math.max(scaleFactor, minScaleFactor), maxScaleFactor);
            updateCamera();
            return true;
        }
        return false;
    }

    /**
     * Responds to the beginning of a scaling gesture. Reported by new pointers going down.
     * @param detector {@link ScaleGestureDetector}
     * @return success
     */
    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    /**
     * Responds to the end of a scale gesture. Reported by existing pointers going up.
     *
     * @param detector {@link ScaleGestureDetector}
     */
    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
    }

    /**
     * Updates the Z-axis of the camera by the scaleFactor and sets the camera to look at the
     * zero point.
     */
    private void updateCamera() {
        renderer.getCurrentCamera().setZ(-maxScaleFactor / scaleFactor);
        renderer.getCurrentCamera().setLookAt(0, 0, 0);
    }
}
