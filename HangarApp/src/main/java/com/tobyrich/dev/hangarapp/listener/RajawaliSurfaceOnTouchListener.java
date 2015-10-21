package com.tobyrich.dev.hangarapp.listener;

import android.view.MotionEvent;
import android.view.View;

import com.google.inject.Inject;
import com.tobyrich.dev.hangarapp.Renderer;

import org.rajawali3d.math.vector.Vector3;

public class RajawaliSurfaceOnTouchListener implements View.OnTouchListener {
    @Inject Renderer renderer;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getHistorySize() > 0) {
                    float histX = event.getHistoricalAxisValue(MotionEvent.AXIS_X, Math.min(event.getHistorySize() - 1, 0));
                    float histY = event.getHistoricalAxisValue(MotionEvent.AXIS_Y, Math.min(event.getHistorySize() - 1, 0));
                    float currX = event.getAxisValue(MotionEvent.AXIS_X);

                    float currY = event.getAxisValue(MotionEvent.AXIS_Y);
                    float deltaX = currX - histX;
                    float deltaY = currY - histY;
                    renderer.getShownObjectOnScene().rotate(Vector3.Axis.Y, -deltaX);
                    renderer.getShownObjectOnScene().rotate(Vector3.Axis.X, -deltaY);
                    renderer.getCurrentCamera().setLookAt(0, 0, 0);
                }
                return true;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return false;
    }
}
