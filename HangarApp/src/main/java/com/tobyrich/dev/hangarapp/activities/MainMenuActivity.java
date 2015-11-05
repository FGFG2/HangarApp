package com.tobyrich.dev.hangarapp.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.inject.Inject;
import com.tobyrich.dev.hangarapp.R;
import com.tobyrich.dev.hangarapp.activities.fragments.ConnectionFragment;
import com.tobyrich.dev.hangarapp.activities.fragments.RajawaliSurfaceFragment;
import com.tobyrich.dev.hangarapp.lib.connection.BluetoothService;
import com.tobyrich.dev.hangarapp.lib.connection_old.ConnectionListener;
import com.tobyrich.dev.hangarapp.lib.connection_old.ConnectionStatus;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;

@ContentView(R.layout.activity_main_menu)
public class MainMenuActivity extends RoboActivity implements ConnectionListener {

    @Inject ConnectionStatus connectionStatus;
    @Inject RajawaliSurfaceFragment rajawaliSurfaceFragment;
    @Inject ConnectionFragment connectionFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionStatus.addConnectionListener(this);

        Intent intent = new Intent(this, BluetoothService.class);
        startService(intent);

        if (savedInstanceState == null) {
            // During initial inject rajawaliSurfaceFragment

            FragmentManager fManager = getFragmentManager();
            FragmentTransaction fTransaction = fManager.beginTransaction();

            fTransaction.add(R.id.fragment_container, rajawaliSurfaceFragment).commit();

            Fragment connectionFragment = fManager.findFragmentByTag("connectionFragment");

            if (connectionFragment == null) {
                fTransaction.add(R.id.fragment_container, new ConnectionFragment(), "connectionFragment");
            }
            else {
                fTransaction.replace(R.id.fragment_container, connectionFragment, "connectionFragment");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
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
     * Called by click on menu-buttons.
     * @param v view
     */
    public void onMenuItemClick(View v) {
        switch(v.getId()) {
            case R.id.menu_statistics: {
                Intent intent = new Intent(this, StatisticsActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.menu_batteryData: {
                Intent intent = new Intent(this, BatteryDataActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.menu_factoryTest: {
                Intent intent = new Intent(this, FactoryTestActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.menu_calibrateSensors: {
                Intent intent = new Intent(this, CalibrateSensorsActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.menu_limitSetup: {
                Intent intent = new Intent(this, LimitSetupActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.menu_exit: {
                this.finish();
            }

            case R.id.menu_achievements: {
                Intent intent = new Intent(this, AchievementsActivity.class);
                startActivity(intent);
                break;
            }
            default: break;
        }

    }

    @Override
    public void onConnectionChanged(boolean connected, String connectionId) {

    }
}
