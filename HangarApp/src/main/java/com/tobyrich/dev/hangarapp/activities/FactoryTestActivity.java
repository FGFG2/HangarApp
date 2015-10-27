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
import com.tobyrich.dev.hangarapp.lib.connection.events.ScanEvent;
import com.tobyrich.dev.hangarapp.lib.connection.events.ScanResult;
import com.tobyrich.dev.hangarapp.util.Consts;

import de.greenrobot.event.EventBus;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;

@ContentView(R.layout.activity_factory_test)
public class FactoryTestActivity extends RoboActivity{

    @Inject RajawaliSurfaceFragment rajawaliSurfaceFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, BluetoothService.class);
        startService(intent);
        EventBus.getDefault().register(this);

        EventBus.getDefault().post(new ScanEvent(true));

        if (savedInstanceState == null) {
            // During initial inject rajawaliSurfaceFragment
            getFragmentManager().beginTransaction().add(R.id.fragment_container, rajawaliSurfaceFragment).commit();
        }

        Toast.makeText(this, "Start conneting", Toast.LENGTH_SHORT).show();
    }

    /**
     * Called by click on toggle-buttons.
     * @param v view
     */
    public void onToggleButtonClick(View v) {
        switch(v.getId()) {
            case R.id.check_engine: {
                ToggleButton tbCheckEngine = (ToggleButton) findViewById(R.id.check_engine);
                if(tbCheckEngine.isChecked()){
                    EventBus.getDefault().post(new PlaneEvent(PlaneEvent.MOTOR, Consts.MAX_MOTOR_VALUE));
                }
                else{
                    EventBus.getDefault().post(new PlaneEvent(PlaneEvent.MOTOR, Consts.MIN_MOTOR_VALUE));
                }
                break;
            }
            case R.id.rudder_left: {
                ToggleButton tbLeft = (ToggleButton) findViewById(R.id.rudder_left);
                ToggleButton tbRight = (ToggleButton) findViewById(R.id.rudder_right);

                if(tbLeft.isChecked()){
                    tbRight.setChecked(false);
                    EventBus.getDefault().post(new PlaneEvent(PlaneEvent.RUDDER, Consts.MIN_MOTOR_VALUE));
                }
                else{
                    EventBus.getDefault().post(new PlaneEvent(PlaneEvent.RUDDER, (Consts.MIN_MOTOR_VALUE + Consts.MAX_MOTOR_VALUE) / 2));
                }
                break;
            }
            case R.id.rudder_right: {
                ToggleButton tbLeft = (ToggleButton) findViewById(R.id.rudder_left);
                ToggleButton tbRight = (ToggleButton) findViewById(R.id.rudder_right);

                if(tbRight.isChecked()){
                    tbLeft.setChecked(false);
                    EventBus.getDefault().post(new PlaneEvent(PlaneEvent.RUDDER, Consts.MAX_MOTOR_VALUE));
                }
                else{
                    EventBus.getDefault().post(new PlaneEvent(PlaneEvent.RUDDER, (Consts.MIN_MOTOR_VALUE + Consts.MAX_MOTOR_VALUE) / 2));
                }
                break;
            }
            default: break;
        }
    }

    public void onEvent(ScanResult evt){
        if(evt.getResult().size()>0)
            EventBus.getDefault().post(new ConnectEvent(evt.getResult().get(0)));
        else
            Toast.makeText(this, "No Device Found", Toast.LENGTH_SHORT).show();
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
