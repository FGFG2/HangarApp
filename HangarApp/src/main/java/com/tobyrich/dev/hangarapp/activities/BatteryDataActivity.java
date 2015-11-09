package com.tobyrich.dev.hangarapp.activities;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.inject.Inject;
import com.tobyrich.dev.hangarapp.R;
import com.tobyrich.dev.hangarapp.beans.PlaneData;
import com.tobyrich.dev.hangarapp.lib.connection.BluetoothService;
import com.tobyrich.dev.hangarapp.lib.connection.events.PlaneResult;
import com.tobyrich.dev.hangarapp.lib.utils.PlaneState;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

import de.greenrobot.event.EventBus;

@ContentView(R.layout.activity_battery_data)
public class BatteryDataActivity extends RoboActivity {

    @InjectView(R.id.batteryProgressBar) ProgressBar batteryProgressBar;
    @InjectView(R.id.currentChargeValue) TextView tvBatteryStatus;
    @InjectView(R.id.timeRemainedValue) TextView tvBatteryRemain;
    @InjectResource(R.drawable.green_progressbar) Drawable greenProgressBar;
    @InjectResource(R.drawable.yellow_progressbar) Drawable yellowProgressBar;
    @InjectResource(R.drawable.red_progressbar) Drawable redProgressBar;
    @Inject PlaneData planeData;

    private static final int PERCENTAGE_TO_CHANGE_TO_YELLOW = 50;
    private static final int PERCENTAGE_TO_CHANGE_TO_RED = 20;
    private static final String COLOR_CODE_GREEN = "#ff00aa00";
    private static final String COLOR_CODE_YELLOW = "#ffffcb00";
    private static final String COLOR_CODE_RED = "#ffee0000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, BluetoothService.class);
        startService(intent);
        EventBus.getDefault().register(this);

        setCurrentBatteryCharge(PlaneState.getInstance().getBattery());
        setOperationalRemainedTime(planeData);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(PlaneResult evt){
        if(evt.getDevice() == PlaneResult.BATTERY)
            setCurrentBatteryCharge(evt.getValue());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_battery_data, menu);
        return super.onCreateOptionsMenu(menu);
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
     * Sets the current battery charge in percent.
     */
    public void setCurrentBatteryCharge(PlaneData planeData) {
        setCurrentBatteryCharge(planeData.getCurrentBatteryCharge());
    }

    public void setCurrentBatteryCharge(int batteryChargeInPercent) {
        batteryProgressBar.setProgress(batteryChargeInPercent);
        tvBatteryStatus.setText(Integer.toString(batteryChargeInPercent) + "%");

        if(batteryChargeInPercent > PERCENTAGE_TO_CHANGE_TO_YELLOW) {
            batteryProgressBar.setProgressDrawable(greenProgressBar);
            tvBatteryStatus.setTextColor(Color.parseColor(COLOR_CODE_GREEN));
            return;
        }
        if(batteryChargeInPercent > PERCENTAGE_TO_CHANGE_TO_RED){
            batteryProgressBar.setProgressDrawable(yellowProgressBar);
            tvBatteryStatus.setTextColor(Color.parseColor(COLOR_CODE_YELLOW));
            return;
        }

        batteryProgressBar.setProgressDrawable(redProgressBar);
        tvBatteryStatus.setTextColor(Color.parseColor(COLOR_CODE_RED));
    }

    /**
     * Sets the operational remained time in format hh:mm:ss.
     */
    public void setOperationalRemainedTime(PlaneData planeData) {
        tvBatteryRemain.setText(planeData.getOperationalRemainedTime());

        int batteryChargeInPercent = batteryProgressBar.getProgress();

        if(batteryChargeInPercent > PERCENTAGE_TO_CHANGE_TO_YELLOW) {
            tvBatteryRemain.setTextColor(Color.parseColor(COLOR_CODE_GREEN));
            return;
        }
        if(batteryChargeInPercent > PERCENTAGE_TO_CHANGE_TO_RED){
            tvBatteryRemain.setTextColor(Color.parseColor(COLOR_CODE_YELLOW));
            return;
        }
        tvBatteryRemain.setTextColor(Color.parseColor(COLOR_CODE_RED));
    }
}
