package com.tobyrich.dev.hangarapp.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.tobyrich.dev.hangarapp.R;
import com.tobyrich.dev.hangarapp.adapters.AchievementsAdapter;
import com.tobyrich.dev.hangarapp.beans.api.model.Achievement;
import com.tobyrich.dev.hangarapp.beans.api.service.AchievementService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_achievements)
public class AchievementsActivity extends RoboActivity {

    @InjectView(R.id.achievementsList) ListView lvAchievements;

    public static final String URL_ALL_ACHIEVEMENTS = "http://chaos-krauts.de/Achievement/";

    private List<Achievement> achievementList = new ArrayList<Achievement>();
    private AchievementsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        // The achievements are populated asynchronous in private class,
        // need to pass the context of the activity for the ArrayAdapter.
        new AchievementsFeeder(getApplicationContext()).execute();
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
        this.achievementList = allAchievements;
    }

    // Dummy achievements for testing purposes.
    public List<Achievement> getAchievementsList() {
        List<Achievement> fakeAchievementList = new ArrayList<Achievement>();
        fakeAchievementList.add(new Achievement("Flight duration", 100));
        fakeAchievementList.add(new Achievement("Smooth flying", 55));
        fakeAchievementList.add(new Achievement("Smooth landing", 35));

        return fakeAchievementList;
    }


    // Server call should be performed asynchronously.
    private class AchievementsFeeder extends AsyncTask<Void, Void, Void> {

        Context context;

        public AchievementsFeeder(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(URL_ALL_ACHIEVEMENTS)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .build();

                AchievementService service = retrofit.create(AchievementService.class);
                Call<List<Achievement>> call = service.getAllAchievements();
                achievementList = call.execute().body();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            adapter = new AchievementsAdapter(context, achievementList);
            lvAchievements.setAdapter(adapter);

            for (int i = 0; i < achievementList.size(); i++) System.out.println(achievementList.get(i).getName());

            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
