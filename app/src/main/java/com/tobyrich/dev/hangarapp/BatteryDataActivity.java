package com.tobyrich.dev.hangarapp;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tobyrich.dev.hangarapp.util.PlaneData;


public class BatteryDataActivity extends Activity {

    private ProgressBar batteryProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_data);

        PlaneData planeData = PlaneData.getInstance();
        setCurrentBatteryCharge(planeData);
        setOperationalRemainedTime(planeData);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_battery_data, menu);
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
     * Sets the current battery charge in percent.
     */
    public void setCurrentBatteryCharge(PlaneData planeData) {
        int batteryChargeInPercent = planeData.getCurrentBatteryCharge();
        batteryProgressBar = (ProgressBar) findViewById(R.id.batteryProgressBar);
        batteryProgressBar.setProgress(batteryChargeInPercent);
        TextView tv = (TextView) findViewById(R.id.currentChargeValue);
        tv.setText(Integer.toString(batteryChargeInPercent) + "%");

        if(batteryChargeInPercent > 50) {
            batteryProgressBar.setProgressDrawable(getDrawable(R.drawable.green_progressbar));
            tv.setTextColor(Color.parseColor("#ff00aa00"));
        } else if(batteryChargeInPercent > 20){
                batteryProgressBar.setProgressDrawable(getDrawable(R.drawable.yellow_progressbar));
                tv.setTextColor(Color.parseColor("#ffffcb00"));
            } else {
                batteryProgressBar.setProgressDrawable(getDrawable(R.drawable.red_progressbar));
                tv.setTextColor(Color.parseColor("#ffee0000"));
            }
    }

    /**
     * Sets the operational remained time in format hh:mm:ss.
     */
    public void setOperationalRemainedTime(PlaneData planeData) {
        TextView tv = (TextView) findViewById(R.id.timeRemainedValue);
        tv.setText(planeData.getOperationalRemainedTime());

        int batteryChargeInPercent = ((ProgressBar) findViewById(R.id.batteryProgressBar)).getProgress();
        if(batteryChargeInPercent > 50) {
            tv.setTextColor(Color.parseColor("#ff00aa00"));
        } else if(batteryChargeInPercent > 20){
            tv.setTextColor(Color.parseColor("#ffffcb00"));
        } else {
            tv.setTextColor(Color.parseColor("#ffee0000"));
        }
    }
}
