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
     *
     * @param v view
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
     *
     * @param v view
     */
    public void onRudderLeftToggleButtonClick(View v) {
        if (tbRudderRight.isChecked()) {
            tbRudderRight.setChecked(false);
            postPlaneEvent(PlaneEvent.RUDDER, Consts.MIN_RUDDER_VALUE);
        } else {
            postPlaneEvent(PlaneEvent.RUDDER, (Consts.MIN_RUDDER_VALUE + Consts.MAX_RUDDER_VALUE) / 2);
        }
    }

    /**
     * Called by click on rudder-right-toggle-buttons.
     *
     * @param v view
     */
    public void onRudderRightToggleButtonClick(View v) {
        if (tbRudderLeft.isChecked()) {
            tbRudderLeft.setChecked(false);
            postPlaneEvent(PlaneEvent.RUDDER, Consts.MIN_RUDDER_VALUE);
        } else {
            postPlaneEvent(PlaneEvent.RUDDER, (Consts.MIN_RUDDER_VALUE + Consts.MAX_RUDDER_VALUE) / 2);
        }
    }

    private void postPlaneEvent(int device, int value) {
        PlaneEvent planeEvent = new PlaneEvent(device, value);
        EventBus.getDefault().post(planeEvent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_factory_test, menu);
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
}
