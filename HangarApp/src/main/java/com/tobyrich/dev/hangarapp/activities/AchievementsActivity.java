package com.tobyrich.dev.hangarapp.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.tobyrich.dev.hangarapp.beans.api.APIConstants;
import com.tobyrich.dev.hangarapp.beans.api.feeders.ImageFeeder;
import com.tobyrich.dev.hangarapp.beans.api.model.Achievement;
import com.tobyrich.dev.hangarapp.beans.api.feeders.AchievementsFeeder;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
    private String authToken;
    private AchievementsActivity thisActivity;
    private Context thisContext;
    private Timer timer;

    // Cache for the Achievements icons.
    private LruCache<String, Bitmap> mMemoryCache;

    // Functions -----------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);
        thisActivity = this;
        thisContext = getBaseContext();

        // FIXME: authentification-routine probably needs to be delegated to another manager class.
        authToken = this.getAuthToken();
        Log.i(this.getClass().getSimpleName(), "Token is: [" + authToken + "]");


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
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(this.getClass().getSimpleName(), "Invoke AchievementFeeder.");
                        new AchievementsFeeder(thisActivity, thisContext, authToken).execute();
                        oldAchievementList = achievementList;
                    }
                });
            }
        }, 0, 15000);

        achievementsLoading.setVisibility(View.VISIBLE);
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
     * No further authorization from HangarApp required.
     * @return String
     */
    public String getAuthToken() {
        AccountManager mgr = AccountManager.get(this);
        Account[] aсcounts = mgr.getAccountsByType(APIConstants.ACCOUNT_TYPE);

        Bundle authTokenBundle;
        String authToken = null;
        try {
            // There is just one account associated with TobyRich.
            Account acct = aсcounts[0];
            AccountManagerFuture<Bundle> accountManagerFuture = mgr.getAuthToken(
                    acct, APIConstants.AUTHTOKEN_TYPE_READ_ONLY, null, this, null, null
            );
            authTokenBundle = accountManagerFuture.getResult();
            authToken = authTokenBundle.get(AccountManager.KEY_AUTHTOKEN).toString();
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "There are no profiles on this system.", e);
            authToken = "A Token should be here.";
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


    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        if (timer != null) {
            timer.cancel();
        }
    }


    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        if (timer == null) {
            loadAchievementsList();
        }
    }
}
