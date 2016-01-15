package com.tobyrich.dev.hangarapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.inject.Inject;
import com.tobyrich.dev.hangarapp.R;
import com.tobyrich.dev.hangarapp.activities.fragments.RajawaliSurfaceFragment;
import com.tobyrich.dev.hangarapp.lib.connection.BluetoothService;
import com.tobyrich.dev.hangarapp.lib.connection.events.ConnectEvent;
import com.tobyrich.dev.hangarapp.lib.connection.events.PlaneEvent;
import com.tobyrich.dev.hangarapp.lib.utils.Consts;
import com.tobyrich.dev.hangarapp.lib.utils.PlaneState;

import de.greenrobot.event.EventBus;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Activity to display the factory test options, like setting the rudder or motor value.
 */

@ContentView(R.layout.activity_factory_test)
public class FactoryTestActivity extends RoboActivity {

    @Inject
    RajawaliSurfaceFragment rajawaliSurfaceFragment;
    @InjectView(R.id.check_engine)
    ToggleButton tbCheckEngine;
    @InjectView(R.id.rudder_left)
    ToggleButton tbRudderLeft;
    @InjectView(R.id.rudder_right)
    ToggleButton tbRudderRight;

    /**
     * Called when the activity is first created.
     * Starts the {@link BluetoothService}.
     * Checks the {@link ToggleButton} for rudder and motor based on the current value.
     *
     * @param savedInstanceState {@link Bundle}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, BluetoothService.class);
        startService(intent);

        if (savedInstanceState == null) {
            // During initial inject rajawaliSurfaceFragment
            getFragmentManager().beginTransaction().add(R.id.fragment_container, rajawaliSurfaceFragment).commit();
        }

        if (PlaneState.getInstance().getMotor() != 0)
            tbCheckEngine.setChecked(true);
        if (PlaneState.getInstance().getRudder() < 0)
            tbRudderLeft.setChecked(true);
        if (PlaneState.getInstance().getRudder() > 0)
            tbRudderRight.setChecked(true);

        Toast.makeText(this, "Start conneting", Toast.LENGTH_SHORT).show();
    }

    /**
     * Called by click on check-engine-toggle-buttons.
     * Sets the motor value to max, when the tbCheckEngine {@link ToggleButton} is checked,
     * otherwise the motor value is set to min value.
     *
     * @param v {@link View}
     */
    public void onCheckEngineToggleButtonClick(View v) {
        if (tbCheckEngine.isChecked()) {
            postPlaneEvent(PlaneEvent.MOTOR, Consts.MAX_MOTOR_VALUE);
        } else {
            postPlaneEvent(PlaneEvent.MOTOR, Consts.MIN_MOTOR_VALUE);
        }
    }

    /**
     * Called by click on rudder-left-toggle-buttons.
     * Sets the rudder value to min, when the tbRudderRight {@link ToggleButton} is checked,
     * otherwise the motor value is set to zero value.
     *
     * @param v {@link View}
     */
    public void onRudderLeftToggleButtonClick(View v) {
        if (tbRudderRight.isChecked() || tbRudderLeft.isChecked()) {
            tbRudderRight.setChecked(false);
            postPlaneEvent(PlaneEvent.RUDDER, Consts.MIN_RUDDER_VALUE);
        } else {
            postPlaneEvent(PlaneEvent.RUDDER, (Consts.MIN_RUDDER_VALUE + Consts.MAX_RUDDER_VALUE) / 2);
        }
    }

    /**
     * Called by click on rudder-right-toggle-buttons.
     * Sets the rudder value to max, when the tbRudderLeft {@link ToggleButton} is checked,
     * otherwise the motor value is set to zero value.
     *
     * @param v {@link View}
     */
    public void onRudderRightToggleButtonClick(View v) {
        if (tbRudderRight.isChecked() || tbRudderLeft.isChecked()) {
            tbRudderLeft.setChecked(false);
            postPlaneEvent(PlaneEvent.RUDDER, Consts.MAX_RUDDER_VALUE);
        } else {
            postPlaneEvent(PlaneEvent.RUDDER, (Consts.MIN_RUDDER_VALUE + Consts.MAX_RUDDER_VALUE) / 2);
        }
    }

    /**
     * Gets called by the onClickEvents of the {@link ToggleButton} and post a new
     * {@link PlaneEvent}.
     *
     * @param device type of plane data.
     * @param value to set.
     */
    private void postPlaneEvent(int device, int value) {
        EventBus.getDefault().post(new PlaneEvent(device, value));
    }
}
