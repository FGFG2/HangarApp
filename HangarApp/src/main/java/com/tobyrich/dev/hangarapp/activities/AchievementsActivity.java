package com.tobyrich.dev.hangarapp.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import com.tobyrich.dev.hangarapp.R;
import com.tobyrich.dev.hangarapp.adapters.AchievementsAdapter;
import com.tobyrich.dev.hangarapp.beans.Achievement;


import java.util.ArrayList;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_achievements)
public class AchievementsActivity extends RoboActivity {

    @InjectView(R.id.achievementsList) ListView achievementsList;

    private ArrayList<Achievement> allAchievements = new ArrayList<Achievement>();
    private AchievementsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        adapter = new AchievementsAdapter(this, getAllAchievements());
        achievementsList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_achievements, menu);
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

    public void setAllAchievements(ArrayList<Achievement> allAchievements) {
        this.allAchievements = allAchievements;
    }

    public ArrayList<Achievement> getAllAchievements() {
        if(allAchievements == null || allAchievements.isEmpty()) {
            // TODO: Replace dummy values
            allAchievements.add(new Achievement("Flight duration", "1 min", 100));
            allAchievements.add(new Achievement("Smooth flying", "30 sec", 55));
        }
        return allAchievements;
    }
}
