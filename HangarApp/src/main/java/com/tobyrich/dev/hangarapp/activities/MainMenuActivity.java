package com.tobyrich.dev.hangarapp.activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.tobyrich.dev.hangarapp.R;
import com.tobyrich.dev.hangarapp.activities.fragments.ConnectionFragment;
import com.tobyrich.dev.hangarapp.activities.fragments.RajawaliSurfaceFragment;
import com.tobyrich.dev.hangarapp.lib.connection.BluetoothService;
import com.tobyrich.dev.hangarapp.lib.connection.events.ConnectEvent;
import com.tobyrich.dev.hangarapp.lib.connection.events.ConnectResult;
import com.tobyrich.dev.hangarapp.lib.connection.events.ScanEvent;
import com.tobyrich.dev.hangarapp.lib.utils.PlaneState;

import de.greenrobot.event.EventBus;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;

@ContentView(R.layout.activity_main_menu)
public class MainMenuActivity extends RoboActivity{

    private static final String TAG = "tr.MainMenuActivity";

    @Inject RajawaliSurfaceFragment rajawaliSurfaceFragment;
    @Inject ConnectionFragment connectionFragment;

    Button menu_batteryData;
    Button menu_factoryTest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);

        Intent bluetoothIntent = new Intent(this, BluetoothService.class);
        startService(bluetoothIntent);

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

        if (getIntent().getBooleanExtra("EXIT", false)) {
            EventBus.getDefault().post(new ScanEvent(false));
            stopService(bluetoothIntent);
            finish();
        }

        menu_factoryTest = (Button) findViewById(R.id.menu_factoryTest);
        menu_batteryData = (Button) findViewById(R.id.menu_batteryData);

        bluetoothButtonState(PlaneState.getInstance().isConnected());
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }
    public void onEventMainThread(ConnectResult evt) {
        bluetoothButtonState(evt.getState());
    }
    private void bluetoothButtonState(boolean bool){
        menu_batteryData.setEnabled(bool);
        menu_factoryTest.setEnabled(bool);
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
                intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                break;
            }
            default:
                break;

        }
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogAlertTheme);
        builder.setMessage("Are you sure you want to exit the Hangar App?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("No", null);

        AlertDialog alert = builder.create();
        alert.show();
    }
}
