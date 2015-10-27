package com.tobyrich.dev.hangarapp.activities;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.tobyrich.dev.hangarapp.R;
import com.tobyrich.dev.hangarapp.beans.PlaneData;
import com.tobyrich.dev.hangarapp.lib.connection.BluetoothService;
import com.tobyrich.dev.hangarapp.lib.connection.events.ConnectEvent;
import com.tobyrich.dev.hangarapp.lib.connection.events.PlaneResult;
import com.tobyrich.dev.hangarapp.lib.connection.events.ScanEvent;
import com.tobyrich.dev.hangarapp.lib.connection.events.ScanResult;

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
    @InjectResource(R.drawable.green_progressbar) Drawable greenProgrssBar;
    @InjectResource(R.drawable.yellow_progressbar) Drawable yellowProgrssBar;
    @InjectResource(R.drawable.red_progressbar) Drawable redProgrssBar;
    @Inject PlaneData planeData;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, BluetoothService.class);
        startService(intent);
        EventBus.getDefault().register(this);

        EventBus.getDefault().post(new ScanEvent(true));
        Toast.makeText(this, "Start conneting", Toast.LENGTH_SHORT).show();
        setCurrentBatteryCharge(planeData);
        setOperationalRemainedTime(planeData);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }
    /*
      * TODO Test Bluetooth
      */
    public void onEvent(ScanResult evt){
        if(evt.getResult().size()>0)
            EventBus.getDefault().post(new ConnectEvent(evt.getResult().get(0)));
        else
            Toast.makeText(this, "No Device Found", Toast.LENGTH_SHORT).show();
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

        if(batteryChargeInPercent > 50) {
            batteryProgressBar.setProgressDrawable(greenProgrssBar);
            tvBatteryStatus.setTextColor(Color.parseColor("#ff00aa00"));
        } else if(batteryChargeInPercent > 20){
                batteryProgressBar.setProgressDrawable(yellowProgrssBar);
                tvBatteryStatus.setTextColor(Color.parseColor("#ffffcb00"));
            } else {
                batteryProgressBar.setProgressDrawable(redProgrssBar);
                tvBatteryStatus.setTextColor(Color.parseColor("#ffee0000"));
            }
    }

    /**
     * Sets the operational remained time in format hh:mm:ss.
     */
    public void setOperationalRemainedTime(PlaneData planeData) {
        tvBatteryRemain.setText(planeData.getOperationalRemainedTime());

        int batteryChargeInPercent = batteryProgressBar.getProgress();
        if(batteryChargeInPercent > 50) {
            tvBatteryRemain.setTextColor(Color.parseColor("#ff00aa00"));
        } else if(batteryChargeInPercent > 20){
            tvBatteryRemain.setTextColor(Color.parseColor("#ffffcb00"));
        } else {
            tvBatteryRemain.setTextColor(Color.parseColor("#ffee0000"));
        }
    }
}
