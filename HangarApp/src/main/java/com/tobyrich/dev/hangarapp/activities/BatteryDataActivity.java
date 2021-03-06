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
import com.tobyrich.dev.hangarapp.lib.connection.BluetoothService;
import com.tobyrich.dev.hangarapp.lib.connection.events.GyroscopeResult;
import com.tobyrich.dev.hangarapp.lib.connection.events.PlaneResult;
import com.tobyrich.dev.hangarapp.lib.utils.PlaneState;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

import de.greenrobot.event.EventBus;

/**
 * Activity to display the battery data.
 */
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

    /**
     * Called when the activity is first created.
     * Starts the {@link BluetoothService} and registers the class to receive events.
     * The current battery charge gets set.
     *
     * @param savedInstanceState {@link Bundle}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, BluetoothService.class);
        startService(intent);
        EventBus.getDefault().register(this);

        setCurrentBatteryCharge(PlaneState.getInstance().getBattery());
    }

    /**
     * fragment is no longer visible to the user either because its activity is being stopped or a
     * fragment operation is modifying it in the activity.
     * Unregisters the class to receive events.
     */
    @Override
    protected void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * Method gets called on every {@link PlaneResult} event. If the PlaneResult is of type battery,
     * the current battery charge gets updated.
     *
     * @param evt {@link PlaneResult}
     */
    public void onEventMainThread(PlaneResult evt){
        if(evt.getDevice() == PlaneResult.BATTERY)
            setCurrentBatteryCharge(evt.getValue());
    }

    /**
     * Sets the current battery charge in percent.
     * Changes the progress and color of the progressbar based on the battery charge.
     *
     * @param batteryChargeInPercent the value to set the batterydata to.
     */
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
}
