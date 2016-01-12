package com.tobyrich.dev.hangarapp.activities;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.inject.Inject;
import com.tobyrich.dev.hangarapp.R;
import com.tobyrich.dev.hangarapp.lib.connection.BluetoothService;
import com.tobyrich.dev.hangarapp.lib.connection.events.PlaneEvent;
import com.tobyrich.dev.hangarapp.lib.connection.events.PlaneResult;
import com.tobyrich.dev.hangarapp.lib.utils.Consts;
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
    @InjectResource(R.drawable.green_progressbar) Drawable greenProgressBar;
    @InjectResource(R.drawable.yellow_progressbar) Drawable yellowProgressBar;
    @InjectResource(R.drawable.red_progressbar) Drawable redProgressBar;

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
        postPlaneEvent(PlaneEvent.MOTOR, Consts.MAX_MOTOR_VALUE);
        Log.d("tr.Battery","Plane started");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(PlaneResult evt){
        if(evt.getDevice() == PlaneResult.BATTERY)
            setCurrentBatteryCharge(evt.getValue());
    }

    /**
     * Sets the current battery charge in percent.
     */

    public void setCurrentBatteryCharge(int batteryChargeInPercent) {
        batteryProgressBar.setProgress(batteryChargeInPercent);
        tvBatteryStatus.setText(Integer.toString(batteryChargeInPercent) + "%");

        Log.d("tr.Battery", Integer.toString(batteryChargeInPercent));
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

    private void postPlaneEvent(int device, int value) {
        EventBus.getDefault().post(new PlaneEvent(device, value));
    }
}
