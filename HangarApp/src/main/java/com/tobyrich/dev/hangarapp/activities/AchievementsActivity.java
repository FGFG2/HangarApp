package com.tobyrich.dev.hangarapp.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    @InjectView(R.id.smartplaneImage) ImageView ivSmartplane;

    @InjectResource(R.drawable.button) Drawable background;

    public static final String URL_ALL_ACHIEVEMENTS = "http://chaos-krauts.de/Achievement/";

    private List<Achievement> achievementList = new ArrayList<Achievement>();
    private AchievementsAdapter adapter;

    // Cache for the Achievements icons.
    private LruCache<String, Bitmap> mMemoryCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        // Check if there is an Internet connection available.
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {

            // Setup cache.
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            final int cacheSize = maxMemory / 8;
            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getByteCount() / 1024;
                }
            };

            // The achievements are populated asynchronous in private class,
            // need to pass the context of the activity for the ArrayAdapter.
            new AchievementsFeeder(getApplicationContext()).execute();
        } else {
            // Show message if there is no Internet Connection.
            Toast.makeText(this, "Internet Connection is required.", Toast.LENGTH_LONG).show();
        }

    }

    public void setAllAchievements(ArrayList<Achievement> allAchievements) {
        this.achievementList = allAchievements;
    }

    // Dummy achievements for testing purposes.
    public List<Achievement> getAchievementsList() {
        List<Achievement> fakeAchievementList = new ArrayList<Achievement>();
        fakeAchievementList.add(new Achievement("Longest flight ever", 100, "To get this achievement you need to flight for 3 Minutes without landing or crashing."));
        fakeAchievementList.add(new Achievement("Smooth landing and a very very long string in the same time to test the adapters wrapping", 35, "Try to land so smooth as possible."));
        fakeAchievementList.add(new Achievement("Smooth flying", 55, "Prove your top flight skills being able to control the plane with no rush movements. " +
                "This achievement is secured if there is no jolting during at least 1 Minute flight."));

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
            } finally {
                for (Achievement achievement: achievementList) {
                    bm = LoadImage(achievement.getImageUrl(), bmOptions);

                    if (bm != null) {
                        achievement.setIcon(bm);
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            adapter = new AchievementsAdapter(context, achievementList);
            lvAchievements.setAdapter(adapter);

            // Define onclickListener for achievements.
            setOnAchievementClickListener();

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

                    if (ivSmartplane.isShown()) {
                        ivSmartplane.setVisibility(View.INVISIBLE);
                    }

                    LinearLayout listItem;

                    for (int j = 0; j < lvAchievements.getChildCount(); j++) {
                        listItem = (LinearLayout) lvAchievements.getChildAt(j);
                        if (listItem.getBackground() != null) {
                            listItem.setBackground(null);
                        }
                    }

                    tvDescription.setText(item.getDescription());
                    tvDescription.setTypeface(tvDescription.getTypeface(), Typeface.NORMAL);

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

        private Bitmap LoadImage(String URL, BitmapFactory.Options options) {

            Bitmap bitmap = getBitmapFromMemCache(URL);
            if (bitmap == null) {
                InputStream in;

                try {
                    in = OpenHttpConnection(URL);
                    bitmap = BitmapFactory.decodeStream(in, null, options);
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                addBitmapToMemoryCache(URL, bitmap);
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

        public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
            if (getBitmapFromMemCache(key) == null) {
                mMemoryCache.put(key, bitmap);
            }
        }

        public Bitmap getBitmapFromMemCache(String key) {
            return mMemoryCache.get(key);
        }
    }

}
