package com.tobyrich.dev.hangarapp.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.tobyrich.dev.hangarapp.adapters.RankingAdapter;
import com.tobyrich.dev.hangarapp.beans.api.feeders.AchievementsFeeder;
import com.tobyrich.dev.hangarapp.beans.api.feeders.FeedersCallback;
import com.tobyrich.dev.hangarapp.beans.api.feeders.ImageFeeder;
import com.tobyrich.dev.hangarapp.beans.api.feeders.TokenFeeder;
import com.tobyrich.dev.hangarapp.beans.api.model.Achievement;
import com.tobyrich.dev.hangarapp.beans.api.model.Ranking;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


/**
 * This class displays the Ranking.
 * Worksequence:
 * onCreate ->
 * getAuthToken -> onTokenFeederComplete ->
 * Timer [getRankingLIst].
 */
@ContentView(R.layout.activity_ranking)
public class RankingActivity extends RoboActivity{

    @InjectView(R.id.rankingList) ListView lvRankingList;
    @InjectView(R.id.smartplaneImage) ImageView ivSmartplane;
    //@InjectView(R.id.rankingLoading) ProgressBar rankingLoading;

    private List<Ranking> oldRankingList = new ArrayList<Ranking>();
    private List<Ranking> rankingList = new ArrayList<Ranking>();
    private RankingAdapter adapter;
    private boolean rankingListChanged = false;
    private String authToken;
    private RankingActivity thisActivity;
    private Context thisContext;
    private Timer timer;
    private static boolean run = true;

    // Functions -----------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        thisActivity = this;
        thisContext = getBaseContext();

        // Get the authToken in background thread. Callback in onTokenFeederComplete.
        //new TokenFeeder(thisActivity, thisContext).execute();

        // Get dummy data
        adapter = new RankingAdapter(thisContext, getRankingList());
        lvRankingList.setAdapter(adapter);
    }

    /**
     * Dummy ranking for testing purposes.
     */
    public List<Ranking> getRankingList() {
        List<Ranking> fakeRankingList = new ArrayList<Ranking>();
        fakeRankingList.add(new Ranking("Thomas", 200));
        fakeRankingList.add(new Ranking("Tim", 1000));
        fakeRankingList.add(new Ranking("Maria", 450));

        return fakeRankingList;
    }

    /**
     * When the token is received we can send an achievement request to server.
     */
    public void onTokenFeederComplete(String authToken) {
        Log.i(this.getClass().getSimpleName(), "TokenFeeder callback registered.");
        Log.i(this.getClass().getSimpleName(), "Token is: " + authToken);
        this.authToken = authToken;

        // Check if there is an Internet connection available.
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            //loadAchievementsList();
        } else {
            // Show message if there is no Internet Connection.
            Toast.makeText(this, "Internet Connection is required.", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Load all achievements from Server asynchronously.
     * onRankingFeederComplete() will be called back when done.
     */
/*    private void loadAchievementsList() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (run) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(getClass().getSimpleName(), "Invoke AchievementsFeeder.");
                            new AchievementsFeeder(thisActivity, thisContext, authToken).execute();
                            oldAchievementList = achievementList;
                        }
                    });
                } else {
                    timer.cancel();
                    timer.purge();
                }
            }
        }, 0, 15000);

        achievementsLoading.setVisibility(View.VISIBLE);
    }
*/

    /**
     * When achievements are received from server start loading.
     */
/*    public void onRankingFeederComplete(List<Ranking> rankingList) {
        Log.i(this.getClass().getSimpleName(), "RankingFeeder callback registered.");
        Log.i(this.getClass().getSimpleName(), "Received following ranking list: "
                + System.lineSeparator() + this.rankingListToString(rankingList));
        rankingLoading.setVisibility(View.GONE);
        this.rankingList = rankingList;

        // Set the flag if the ranking were changed.
        setRankingListChanged(checkIfRankingListChanged(oldRankingList, rankingList));


        // Show ranking.
        if(adapter == null) {
            adapter = new AchievementsAdapter(getBaseContext(), rankingList);
            lvRankingList.setAdapter(adapter);
        } else {
            if(isRankingListChanged()) {
                // Show message if achievements list is changed.
                Toast.makeText(getBaseContext(), "Achievements were updated!", Toast.LENGTH_LONG).show();
                adapter.notifyDataSetChanged();
            }
        }

        rankingLoading.setVisibility(View.GONE);
    }
*/

    /**
     *
     */
/*    private boolean checkIfRankingListChanged(List<Ranking> oldList, List<Ranking> newList) {
        if (oldList != null && newList != null) {
            if (oldList.size() != newList.size()) {
                return true;
            }

            return false;
        }

        return false;
    }
*/

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
            //loadAchievementsList();
        }
    }


/*    public String rankingListToString(List<Ranking> rankingList) {
        String returnString = "";
        for (Ranking ranking: rankingList) {
            returnString += ranking.toString() + System.lineSeparator();;
        }
        return returnString;
    }*/
}
