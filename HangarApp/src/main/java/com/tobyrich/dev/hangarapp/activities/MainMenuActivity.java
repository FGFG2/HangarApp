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

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;

@ContentView(R.layout.activity_main_menu)
public class MainMenuActivity extends RoboActivity{

    @Inject RajawaliSurfaceFragment rajawaliSurfaceFragment;
    @Inject ConnectionFragment connectionFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    /**
     * Called by click on menu-buttons.
     * @param v view
     */
    public void onMenuItemClick(View v) {
        Intent intent = new Intent();

        switch (v.getId()) {
            case R.id.menu_batteryData: {
                intent = new Intent(this, BatteryDataActivity.class);
                break;
            }
            case R.id.menu_factoryTest: {
                intent = new Intent(this, FactoryTestActivity.class);
                break;
            }
            case R.id.menu_achievements: {
                intent = new Intent(this, AchievementsActivity.class);
                break;
            }
            case R.id.menu_exit: {
                this.finish();
            }
            default:
                break;

        }

        startActivity(intent);

    }
}
