package com.tobyrich.dev.hangarapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.tobyrich.dev.hangarapp.util.PlaneData;


public class StatisticsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        PlaneData planeData = PlaneData.getInstance();
        setTotalFlightDuration(planeData);
        setLastFlightDuration(planeData);
        setMaxRelativeHeight(planeData);
        setLastRelativeHeight(planeData);
        setMaxRPM(planeData);
        setLastRPM(planeData);
        setMaxG(planeData);
        setLastG(planeData);
        setEcoRanking(planeData);
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

    /**
     * Sets the total flight duration in format hh:mm:ss.
     */
    public void setTotalFlightDuration(PlaneData planeData) {
        ((TextView)findViewById(R.id.totalFlightDuration)).setText(planeData.getTotalFlightDuration());
    }

    /**
     * Sets the last flight duration in format hh:mm:ss.
     */
    public void setLastFlightDuration(PlaneData planeData) {
        ((TextView)findViewById(R.id.lastFlightDuration)).setText(planeData.getLastFlightDuration());
    }

    /**
     * Sets the max relative height of the flight in meters.
     */
    public void setMaxRelativeHeight(PlaneData planeData) {
        ((TextView)findViewById(R.id.maxHeight)).setText(planeData.getMaxRelativeHeight());
    }

    /**
     * Sets the last relative height of the flight in meters.
     */
    public void setLastRelativeHeight(PlaneData planeData) {
        ((TextView)findViewById(R.id.lastHeight)).setText(planeData.getLastRelativeHeight());
    }

    /**
     * Sets the max RPM.
     */
    public void setMaxRPM(PlaneData planeData) {
        ((TextView)findViewById(R.id.maxRpm)).setText(planeData.getMaxRPM());
    }

    /**
     * Sets the last RPM.
     */
    public void setLastRPM(PlaneData planeData) {
        ((TextView)findViewById(R.id.lastRpm)).setText(planeData.getLastRPM());
    }

    /**
     * Sets the max G-overload.
     */
    public void setMaxG(PlaneData planeData) {
        ((TextView)findViewById(R.id.maxGForce)).setText(planeData.getMaxG());
    }

    /**
     * Sets the last G-overload.
     */
    public void setLastG(PlaneData planeData) {
        ((TextView)findViewById(R.id.lastGForce)).setText(planeData.getLastG());
    }

    /**
     * Sets the eco-ranking based on flight duration and battery charge quotient.
     */
    public void setEcoRanking(PlaneData planeData) {
        ((TextView)findViewById(R.id.ecoRanking)).setText(planeData.getEcoRanking());
    }
}
