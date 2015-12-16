package com.tobyrich.dev.hangarapp.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
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
import com.tobyrich.dev.hangarapp.beans.api.feeders.ImageFeeder;
import com.tobyrich.dev.hangarapp.beans.api.model.Achievement;
import com.tobyrich.dev.hangarapp.beans.api.service.AchievementService;
import com.tobyrich.dev.hangarapp.beans.api.AccountConstants;
import com.tobyrich.dev.hangarapp.beans.api.feeders.AchievementsFeeder;

import org.roboguice.shaded.goole.common.base.Optional;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_achievements)
public class AchievementsActivity extends RoboActivity implements
        ImageFeeder.ImageFeederCallback,
        AchievementsFeeder.AchievementsFeederCallback {

    @InjectView(R.id.achievementsList) ListView lvAchievements;
    @InjectView(R.id.achievementDescription) TextView tvDescription;
    @InjectView(R.id.smartplaneImage) ImageView ivSmartplane;
    @InjectView(R.id.achievementsLoading) ProgressBar achievementsLoading;

    private List<Achievement> oldAchievementList = new ArrayList<Achievement>();
    private List<Achievement> achievementList = new ArrayList<Achievement>();
    private AchievementsAdapter adapter;
    private boolean achievementListChanged = false;

    // Cache for the Achievements icons.
    private LruCache<String, Bitmap> mMemoryCache;


    // Functions -----------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);
        String authToken = this.getAuthToken();

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

            loadAchievementsList();
        } else {
            // Show message if there is no Internet Connection.
            Toast.makeText(this, "Internet Connection is required.", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Load all achievements from Server asynchronously.
     * onAchievementsFeederComplete() will be called back when done.
     */
    private void loadAchievementsList() {
        new AchievementsFeeder(this, getBaseContext()).execute();
        achievementsLoading.setVisibility(View.VISIBLE);
        oldAchievementList = achievementList;
    }


    /**
     * When achievements are received from server start loading icons.
     */
    public void onAchievementsFeederComplete(List<Achievement> achievementList) {
        Log.i(this.getClass().getSimpleName(), "AchievementsFeeder callback registered.");
        achievementsLoading.setVisibility(View.GONE);
        this.achievementList = achievementList;

        // Set the flag if the achievements were changed.
        setAchievementListChanged(checkIfAchievementListChanged(oldAchievementList, achievementList));

        if (isAchievementListChanged()) {
            for (Achievement achievement : achievementList) {
                // If achievement icon is not in cache then download.
                String imageURL = achievement.getImageUrl();
                if (mMemoryCache.get(imageURL) == null) {
                    // The feeder Thread will be started for each achievement and closed after the work is done automatically.
                    new ImageFeeder(this, imageURL).execute();
                } else {
                    Bitmap icon = mMemoryCache.get(imageURL);
                    achievement.setIcon(icon);
                }
            }
        }

        // Show achievements.
        if(adapter == null) {
            adapter = new AchievementsAdapter(getBaseContext(), achievementList);
            lvAchievements.setAdapter(adapter);
        } else {
            if(isAchievementListChanged()) {
                // Show message if achievements list is changed.
                Toast.makeText(getBaseContext(), "Achievements were updated!", Toast.LENGTH_LONG).show();
                adapter.notifyDataSetChanged();
            }
        }

        achievementsLoading.setVisibility(View.GONE);

        // Define onclickListener for achievements.
        setOnAchievementClickListener();
    }

    /**
     * When an image is returned by ImageFeeder, add it to the achievement and to the cache. Refresh an ArrayAdapter.
     * @param key URL-address of the Bitmap
     * @param icon Bitmap
     */
    public void onImageFeederComplete(String key, Bitmap icon) {
        Log.i(this.getClass().getSimpleName(), "ImageFeeder callback registered.");
        addBitmapToMemoryCache(key, icon);

        for (Achievement achievement: achievementList) {
            if (achievement.getImageUrl().equals(key)) {
                achievement.setIcon(icon);
            }
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * Function returns an authorization token which was used by SmartPlane app.
     * No further authorization in HangarApp required.
     * @return String or null
     */
    public String getAuthToken() {
        AccountManager mgr = AccountManager.get(this);
        Account[] acts = mgr.getAccountsByType(null);
//        Account[] acts = mgr.getAccountsByType(AccountConstants.ACCOUNT_TYPE);

        Bundle authTokenBundle;
        String authToken = null;
        try {
            Account acct = acts[0];
            AccountManagerFuture<Bundle> accountManagerFuture = mgr.getAuthToken(
                    acct, AccountConstants.AUTHTOKEN_TYPE_READ_ONLY, null, this, null, null
            );
            authTokenBundle = accountManagerFuture.getResult();
            authToken = authTokenBundle.get(AccountManager.KEY_AUTHTOKEN).toString();
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "There are no profiles on this system.");
        }

        return authToken;
    }


    /**
     *
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (mMemoryCache.get(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }


    /**
     *
     */
    private boolean checkIfAchievementListChanged(List<Achievement> oldList, List<Achievement> newList) {
        if (oldList != null && newList != null) {
            if (oldList.size() != newList.size()) {
                return true;
            } else {
                for (int i = 0; i < newList.size(); i++) {
                    if (oldList.get(i).getProgress() != newList.get(i).getProgress()) {
                        return true;
                    }
                }
            }

            return false;
        }

        return false;
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


    public void setAchievementListChanged(boolean achievementListChanged) {
        this.achievementListChanged = achievementListChanged;
    }

    public boolean isAchievementListChanged() {
        return achievementListChanged;
    }

}
