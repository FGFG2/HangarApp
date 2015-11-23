package com.tobyrich.dev.hangarapp.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tobyrich.dev.hangarapp.R;
import com.tobyrich.dev.hangarapp.adapters.AchievementsAdapter;
import com.tobyrich.dev.hangarapp.beans.api.model.Achievement;
import com.tobyrich.dev.hangarapp.beans.api.service.AchievementService;

import org.roboguice.shaded.goole.common.base.Optional;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

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

    public static final String URL_ALL_ACHIEVEMENTS = "http://chaos-krauts.de/Achievement/";
    @InjectView(R.id.achievementsList) ListView lvAchievements;
    @InjectView(R.id.achievementDescription) TextView tvDescription;
    @InjectView(R.id.smartplaneImage) ImageView ivSmartplane;
    @InjectView(R.id.achievementsLoading) ProgressBar achievementsLoading;
    @InjectResource(R.drawable.button) Drawable background;
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
        protected void onPreExecute() {
            achievementsLoading.setVisibility(View.VISIBLE);
            super.onPreExecute();
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
                Log.e(this.getClass().getSimpleName(), "Error loading achievements.", e);
            }

            if (achievementList.isEmpty()) {
                achievementList = getAchievementsList();
                Log.i(this.getClass().getSimpleName(), "No achievements loaded. Using fallback.");
            }
            loadImages();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            adapter = new AchievementsAdapter(context, achievementList);
            lvAchievements.setAdapter(adapter);

            achievementsLoading.setVisibility(View.GONE);

            // Define onclickListener for achievements.
            setOnAchievementClickListener();

            super.onPostExecute(result);
        }

        private void loadImages() {
            for (Achievement achievement : achievementList) {
                Optional<URL> url = getUrlFromString(achievement.getImageUrl());
                if (url.isPresent()) {
                    bm = loadImage(achievement.getImageUrl(), bmOptions);
                    achievement.setIcon(bm);
                }
            }
        }

        private Optional<URL> getUrlFromString(String urlString) {
            try {
                return Optional.fromNullable(new URL(urlString));
            } catch (MalformedURLException e) {
                return Optional.absent();
            }
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
                    if (view.getBackground() == null) {
                        view.setBackgroundColor(Color.parseColor("#696969"));
                    }
                }
            });

        }

        private Bitmap loadImage(String URL, BitmapFactory.Options options) {

            Bitmap bitmap = getBitmapFromMemCache(URL);
            if (bitmap == null) {
                InputStream in;

                try {
                    in = openHttpConnection(URL);
                    bitmap = BitmapFactory.decodeStream(in, null, options);
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                addBitmapToMemoryCache(URL, bitmap);
            }

            return bitmap;
        }

        private InputStream openHttpConnection(String strURL) throws IOException {
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
