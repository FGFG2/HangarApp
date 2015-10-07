package com.tobyrich.dev.hangarapp;

import org.rajawali3d.surface.IRajawaliSurface;
import org.rajawali3d.surface.RajawaliSurfaceView;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class FactoryTestActivity extends Activity {

    private RajawaliSurfaceView rajawaliSurfaceView;
    Renderer renderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factory_test);

        // Set the custom font.
        TextView tv = (TextView)findViewById(R.id.checkView);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/impact.ttf");
        tv.setTypeface(face);
        // Set the text size.
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text));

        rajawaliSurfaceView = (RajawaliSurfaceView) findViewById(R.id.factoryTest_SurfaceView_smartPlane);
        rajawaliSurfaceView.setFrameRate(60);
        rajawaliSurfaceView.setRenderMode(IRajawaliSurface.RENDERMODE_WHEN_DIRTY);
        rajawaliSurfaceView.setTransparent(true);
        renderer = new Renderer(this);
        rajawaliSurfaceView.setSurfaceRenderer(renderer);
        rajawaliSurfaceView.setOnTouchListener(renderer);
        rajawaliSurfaceView.setOnClickListener(null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_factory_test, menu);
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
}
