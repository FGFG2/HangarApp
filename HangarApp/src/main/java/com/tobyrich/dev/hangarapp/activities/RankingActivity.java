package com.tobyrich.dev.hangarapp.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tobyrich.dev.hangarapp.R;
import com.tobyrich.dev.hangarapp.adapters.RankingAdapter;
import com.tobyrich.dev.hangarapp.beans.api.feeders.FeedersCallback;
import com.tobyrich.dev.hangarapp.beans.api.feeders.RankingFeeder;
import com.tobyrich.dev.hangarapp.beans.api.feeders.TokenFeeder;
import com.tobyrich.dev.hangarapp.beans.api.model.Achievement;
import com.tobyrich.dev.hangarapp.beans.api.model.UserProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


/**
 * This class displays the Ranking list.
 * Worksequence:
 * onCreate ->
 * getAuthToken -> onTokenFeederComplete ->
 * Timer [getRankingLIst].
 */
@ContentView(R.layout.activity_ranking)
public class RankingActivity extends RoboActivity implements FeedersCallback{

    @InjectView(R.id.rankingList) ListView lvRankingList;
    @InjectView(R.id.rankingLoading) ProgressBar rankingLoading;

    private List<UserProfile> oldRankingList = new ArrayList<UserProfile>();
    private List<UserProfile> rankingList = new ArrayList<UserProfile>();
    private RankingAdapter adapter;
    private boolean rankingListChanged = false;
    private String authToken;
    private RankingActivity thisActivity;
    private Context thisContext;
    private Timer timer;
    private static boolean run = true;

    // Functions -----------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        thisActivity = this;
        thisContext = getBaseContext();

        // Get the authToken in background thread. Callback in onTokenFeederComplete.
        new TokenFeeder(thisActivity, thisContext).execute();

    }

    @Override
    public void onAchievementsFeederComplete(List<Achievement> achievementList) {
        // do nothing
    }

    @Override
    public void onImageFeederComplete(String key, Bitmap bm) {
        // do nothing
    }

    /**
     * When the token is received we can send a ranking request to server.
     */
    public void onTokenFeederComplete(String authToken) {
        Log.i(this.getClass().getSimpleName(), "TokenFeeder callback registered.");
        Log.i(this.getClass().getSimpleName(), "Token is: " + authToken);
        this.authToken = authToken;

        // Check if there is an Internet connection available.
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            loadRankingList();
        } else {
            // Show message if there is no Internet Connection.
            Toast.makeText(this, "Internet Connection is required.", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Load ranking list from Server asynchronously.
     * onRankingFeederComplete() will be called back when done.
     */
    private void loadRankingList() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (run) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(getClass().getSimpleName(), "Invoke RankingFeeder.");
                            oldRankingList = rankingList;
                            new RankingFeeder(thisActivity, thisContext, authToken).execute();
                        }
                    });
                } else {
                    timer.cancel();
                    timer.purge();
                }
            }
        }, 0, 15000);

        rankingLoading.setVisibility(View.VISIBLE);
    }


    /**
     * When ranking list are received from server start loading.
     */
    @Override
    public void onRankingFeederComplete(List<UserProfile> rankingList) {
        Log.i(this.getClass().getSimpleName(), "RankingFeeder callback registered.");
        Log.i(this.getClass().getSimpleName(), "Received following ranking list: "
                + System.lineSeparator() + this.rankingListToString(rankingList));
        rankingLoading.setVisibility(View.GONE);
        this.rankingList = rankingList;

        // Set the flag if the ranking were changed.
        setRankingListChanged(checkIfRankingListChanged(oldRankingList, rankingList));

        // Show ranking.
        if(adapter == null) {
            adapter = new RankingAdapter(getBaseContext(), rankingList);
            lvRankingList.setAdapter(adapter);
        } else {
            if(isRankingListChanged()) {
                // Show message if achievements list is changed.
                Toast.makeText(getBaseContext(), "Ranking list was updated!", Toast.LENGTH_LONG).show();
                adapter.notifyDataSetChanged();
            }
        }

        rankingLoading.setVisibility(View.GONE);
    }

    /**
     * Check if Ranking list is changed.
     */
   private boolean checkIfRankingListChanged(List<UserProfile> oldList, List<UserProfile> newList) {
        if (oldList != null && newList != null) {
            if (oldList.size() != newList.size()) {
                return true;
            }
        }

        return false;
    }


    public void setRankingListChanged(boolean rankingListChanged) {
        this.rankingListChanged = rankingListChanged;
    }

    public boolean isRankingListChanged() {
        return rankingListChanged;
    }


    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        run = false;
    }


    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        run = true;
        if (timer != null) {
            loadRankingList();
        }
    }


    public String rankingListToString(List<UserProfile> rankingList) {
        String returnString = "";
        for (UserProfile ranking: rankingList) {
            returnString += ranking.toString() + System.lineSeparator();
        }
        return returnString;
    }
}
