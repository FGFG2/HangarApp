package com.tobyrich.dev.hangarapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.inject.Inject;

import org.rajawali3d.surface.IRajawaliSurface;
import org.rajawali3d.surface.RajawaliSurfaceView;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_calibrate_sensors)
public class CalibrateSensorsActivity extends RoboActivity {

    @InjectView(R.id.calibrateSensors_SurfaceView_smartPlane) RajawaliSurfaceView rajawaliSurfaceView;
    @Inject Renderer renderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rajawaliSurfaceView.setFrameRate(60);
        rajawaliSurfaceView.setRenderMode(IRajawaliSurface.RENDERMODE_WHEN_DIRTY);
        rajawaliSurfaceView.setTransparent(true);
        rajawaliSurfaceView.setSurfaceRenderer(renderer);
        rajawaliSurfaceView.setOnTouchListener(renderer);
        rajawaliSurfaceView.setOnClickListener(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calibrate_sensors, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
