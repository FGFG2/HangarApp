package com.tobyrich.dev.hangarapp.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    private static boolean run = true;
    @InjectView(R.id.rankingList) ListView lvRankingList;
    @InjectView(R.id.rankingLoading) ProgressBar rankingLoading;
    private List<UserProfile> oldRankingList = new ArrayList<UserProfile>();
    private List<UserProfile> rankingList = new ArrayList<UserProfile>();
    private RankingAdapter adapter;
    private boolean rankingListChanged = false;
    private String authToken;
    private String accountName;
    private RankingActivity thisActivity;
    private Context thisContext;
    private Timer timer;
    private int currentUserPosition = 0;

    // Functions -----------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        thisActivity = this;
        thisContext = getBaseContext();

        // Get the authToken in background thread. Callback in onTokenFeederComplete.
        new TokenFeeder(thisActivity).execute();

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

        // Set user position in ranking list
        for(UserProfile r: rankingList) {
            r.setPosition(rankingList.indexOf(r) + 1);
            // TODO dummy data remove
            if (accountName == null) {
                if (r.getKey().equals("Jack Johnson")) {
                    r.setCurrentUser(true);
                    currentUserPosition = r.getPosition();
                } else {
                    r.setCurrentUser(false);
                }
            } else if (r.getKey().equals(accountName)) {
                r.setCurrentUser(true);
                currentUserPosition = r.getPosition();
            } else {
                r.setCurrentUser(false);
            }
        }

        // Set the flag if the ranking were changed.
        setRankingListChanged(checkIfRankingListChanged(oldRankingList, rankingList));

        // Show ranking.
        if(adapter == null) {
            adapter = new RankingAdapter(getBaseContext(), getSubRankingList());
            lvRankingList.setAdapter(adapter);
        } else {
            if(isRankingListChanged()) {
                // Show message if achievements list is changed.
                Toast.makeText(getBaseContext(), "Ranking list was updated!", Toast.LENGTH_LONG).show();
                adapter = new RankingAdapter(getBaseContext(), getSubRankingList());
                adapter.notifyDataSetChanged();
            }
        }

        rankingLoading.setVisibility(View.GONE);
    }

    /**
     * Restrict the range of ranking list to 20.
     * @return restricted ranking list.
     */
    public List<UserProfile> getSubRankingList() {
        if(currentUserPosition != 0 ) {

            if (currentUserPosition <= 20) {
                return rankingList;
            } else if (currentUserPosition + 10 <= rankingList.size()) {
                return rankingList.subList(currentUserPosition - 10, currentUserPosition + 10);
            } else {
                return rankingList.subList(rankingList.size() - 20, rankingList.size());
            }
        }
        return rankingList;
    }

    /**
     * Check if Ranking list is changed.
     */
   private boolean checkIfRankingListChanged(List<UserProfile> oldList, List<UserProfile> newList) {
        int firstPosition = 0;
        int lastPosition = 0;
        if (oldList != null && newList != null && oldList.size() != 0 && rankingList.size() != 0) {

            if(currentUserPosition != 0 ) {

                if (currentUserPosition <= 20) {
                    firstPosition = 0;
                    lastPosition = rankingList.size();
                } else if (currentUserPosition + 10 <= rankingList.size()) {
                    firstPosition = currentUserPosition - 10;
                    lastPosition = currentUserPosition + 10;
                } else {
                    firstPosition = rankingList.size() - 20;
                    lastPosition = rankingList.size();
                }
            }

            for(int i = firstPosition; i < lastPosition; i++) {
                if(oldList.get(i).getPosition() != newList.get(i).getPosition()) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isRankingListChanged() {
        return rankingListChanged;
    }

    public void setRankingListChanged(boolean rankingListChanged) {
        this.rankingListChanged = rankingListChanged;
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
