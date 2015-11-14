package com.tobyrich.dev.hangarapp.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tobyrich.dev.hangarapp.R;
import com.tobyrich.dev.hangarapp.adapters.AchievementsAdapter;
import com.tobyrich.dev.hangarapp.beans.api.model.Achievement;
import com.tobyrich.dev.hangarapp.beans.api.service.AchievementService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_achievements)
public class AchievementsActivity extends RoboActivity {

    @InjectView(R.id.achievementsList) ListView lvAchievements;
    @InjectView(R.id.achievementDescription) TextView tvDescription;
    @InjectView(R.id.smartplaneImage) ImageView imSmartplane;

    @InjectResource(R.drawable.button) Drawable background;

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
        fakeAchievementList.add(new Achievement("Flight duration", 100, "Flight duration ksjdh sdjkh djkshf skjdh ksjdfh sdkjfhkdj."));
        fakeAchievementList.add(new Achievement("Smooth landing and a very very long string in the same time", 35, "Smooth landing ksjdh sdjkh djkshf skjdh sdkjfhkdj."));
        fakeAchievementList.add(new Achievement("Smooth flying", 55, "Smooth flying ksjdh sdjkh djkshf skjdh ksjdfh h hjsdfb " +
                "lllllllllllllll dhks dshs  dshjddd djsdh sdjhfdjskhf dsjkhfskj sjdhfks jhkj jhdskj kjshdk ksjdh ksjdh jhd" +
                "dskh kjdsh skdjh sdkjhd skjdh ksjd skjd khd hsjdbhjsb sdkjfhkdj skdhbsad hasdgsaj sajh jkshd" +
                "jskhdf sjhfs sdhfsdk kjsdhjsh ksjhd jj jshd jjj."));

        return fakeAchievementList;
    }


    // Server call should be performed asynchronously.
    private class AchievementsFeeder extends AsyncTask<Void, Void, Void> {

        Context context;
        Bitmap bm;
        BitmapFactory.Options bmOptions;

        public AchievementsFeeder(Context context) {
            this.context = context;
            bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // try {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(URL_ALL_ACHIEVEMENTS)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .build();

                AchievementService service = retrofit.create(AchievementService.class);
                Call<List<Achievement>> call = service.getAllAchievements();
                // achievementList = call.execute().body();
                achievementList = getAchievementsList();

                for (Achievement achievement: achievementList) {
                    bm = LoadImage(achievement.getImageUrl(), bmOptions);
                    achievement.setIcon(bm);
                }
            // } catch (IOException e) {
            //    e.printStackTrace();
            //}

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            adapter = new AchievementsAdapter(context, achievementList);
            lvAchievements.setAdapter(adapter);

            //define onclickListener for achievements
            setOnAchievementClickListener();

            for (int i = 0; i < achievementList.size(); i++) System.out.println(achievementList.get(i).getName());

            super.onPostExecute(result);
        }

        /**
         * Called by click on achievement.
         */
        public void setOnAchievementClickListener() {
            lvAchievements.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View view, int i, long l) {
                    Achievement item = (Achievement) adapter.getItemAtPosition(i);

                    if (imSmartplane.isShown()) {
                        imSmartplane.setVisibility(View.INVISIBLE);
                    }

                    LinearLayout listItem;

                    for (int j = 0; j < lvAchievements.getChildCount(); j++) {
                        listItem = (LinearLayout) lvAchievements.getChildAt(j);
                        if (listItem.getBackground() != null) {
                            listItem.setBackground(null);
                        }
                    }

                    tvDescription.setText(item.getDescription());
                    if (view.getBackground() == null) {
                        view.setBackgroundColor(Color.parseColor("#696969"));
                    }
                }
            });
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private Bitmap LoadImage(String URL, BitmapFactory.Options options) {
        Bitmap bitmap = null;
        InputStream in;

        try {
            in = OpenHttpConnection(URL);
            bitmap = BitmapFactory.decodeStream(in, null, options);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private InputStream OpenHttpConnection(String strURL) throws IOException{
        InputStream inputStream = null;
        URL url = new URL(strURL);
        URLConnection conn = url.openConnection();

        try {
            HttpURLConnection httpConn = (HttpURLConnection)conn;
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpConn.getInputStream();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return inputStream;
    }

}
