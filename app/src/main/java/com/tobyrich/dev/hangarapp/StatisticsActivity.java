package com.tobyrich.dev.hangarapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.inject.Inject;
import com.tobyrich.dev.hangarapp.util.PlaneData;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_statistics)
public class StatisticsActivity extends RoboActivity {

    @InjectView(R.id.totalFlightDuration) TextView tvTotalFlightDuration;
    @InjectView(R.id.lastFlightDuration) TextView tvLastFlightDuration;
    @InjectView(R.id.maxHeight) TextView tvMaxHeight;
    @InjectView(R.id.lastHeight) TextView tvLastHeight;
    @InjectView(R.id.maxRpm) TextView tvMaxRpm;
    @InjectView(R.id.lastRpm) TextView tvLastRpm;
    @InjectView(R.id.maxGForce) TextView tvMaxGForce;
    @InjectView(R.id.lastGForce) TextView tvLastGForce;
    @InjectView(R.id.ecoRanking) TextView tvEcoRanking;
    @Inject PlaneData planeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tvTotalFlightDuration.setText(planeData.getTotalFlightDuration());
        tvLastFlightDuration.setText(planeData.getLastFlightDuration());
        tvMaxHeight.setText(planeData.getMaxRelativeHeight());
        tvLastHeight.setText(planeData.getLastRelativeHeight());
        tvMaxRpm.setText(planeData.getMaxRPM());
        tvLastRpm.setText(planeData.getLastRPM());
        tvMaxGForce.setText(planeData.getMaxG());
        tvLastGForce.setText(planeData.getLastG());
        tvEcoRanking.setText(planeData.getEcoRanking());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_statistics, menu);
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
