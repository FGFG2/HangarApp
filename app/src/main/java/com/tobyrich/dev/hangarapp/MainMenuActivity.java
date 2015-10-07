package com.tobyrich.dev.hangarapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.tobyrich.dev.hangarapp.util.ConnectionListener;
import com.tobyrich.dev.hangarapp.util.ConnectionStatus;

import org.rajawali3d.surface.IRajawaliSurface;
import org.rajawali3d.surface.RajawaliSurfaceView;


public class MainMenuActivity extends Activity implements ConnectionListener {

    private RajawaliSurfaceView rajawaliSurfaceView;
    Renderer renderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        rajawaliSurfaceView = (RajawaliSurfaceView) findViewById(R.id.mainMenu_SurfaceView_smartPlane);
        rajawaliSurfaceView.setFrameRate(60);
        rajawaliSurfaceView.setRenderMode(IRajawaliSurface.RENDERMODE_WHEN_DIRTY);
        rajawaliSurfaceView.setTransparent(true);
        renderer = new Renderer(this);
        rajawaliSurfaceView.setSurfaceRenderer(renderer);
        rajawaliSurfaceView.setOnTouchListener(renderer);
        rajawaliSurfaceView.setOnClickListener(null);

        ConnectionStatus.getInstance().addConnectionListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        int x = 5;
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
            default: break;
        }

    }

    @Override
    public void onConnectionChanged(boolean connected, String connectionId) {

    }
}
