package com.tobyrich.dev.hangarapp.beans.api.feeders;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.tobyrich.dev.hangarapp.beans.api.APIConstants;
import com.tobyrich.dev.hangarapp.beans.api.model.Achievement;
import com.tobyrich.dev.hangarapp.beans.api.model.UserProfile;
import com.tobyrich.dev.hangarapp.beans.api.service.AchievementService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import roboguice.util.SafeAsyncTask;

/**
 * This class gets ranking list from the remote Server using definite User-Token.
 */
public class RankingFeeder extends SafeAsyncTask<List<UserProfile>> {

    private List<UserProfile> userList = new ArrayList<UserProfile>();
    private Context context;
    private String authToken;
    private Handler mHandler;
    private FeedersCallback rankingFeederCallback;


    // Constructors --------------------------------------------------------------------------------
    public RankingFeeder(
            FeedersCallback rankingFeederCallback,
            Context context,
            String authToken
    ) {
        this.rankingFeederCallback = rankingFeederCallback;
        this.context = context;
        this.authToken = authToken;
        mHandler = new Handler();
    }

    // Functions -----------------------------------------------------------------------------------
    @Override
    protected void onPreExecute() {

    }


    public List<UserProfile> call() throws Exception {
        this.userList = loadRankingFromService();
        return userList;
    }


    @Override
    protected void onSuccess(List<UserProfile> result) {
        Log.i(this.getClass().getSimpleName(), "onSuccess.");
        rankingFeederCallback.onRankingFeederComplete(result);
    }


    @Override
    protected void onException(Exception e) {
        // do this in the UI thread if call() threw an exception
        mHandler.post(new ToastRunnable("The request to server did not succeed. Please try again later."));
        Log.e(this.getClass().getSimpleName(), "Exception occured!");
        e.printStackTrace();
    }


    @Override
    protected void onFinally() {
        // always do this in the UI thread after calling call()
    }


    private List<UserProfile> loadRankingFromService() {
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(APIConstants.SERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            AchievementService service = retrofit.create(AchievementService.class);
            Call<List<UserProfile>> call = service.getRankingList("Bearer " + authToken);
            userList = call.execute().body();

        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(), "Error loading ranking list.", e);
            e.printStackTrace();
            // FIXME: No fake achievements should be shown by release.
            userList = getRankingList();
        }

        if (userList == null) {
            Log.i(this.getClass().getSimpleName(), "False token.");
            mHandler.post(new ToastRunnable("Please start the SmartPlane application and input your credentials again."));
            // FIXME: No fake achievements should be shown by release.
            userList = getRankingList();
        }

        return userList;
    }


    /**
     * Dummy ranking list for testing purposes.
     */
    public List<UserProfile> getRankingList() {
        List<UserProfile> fakeUserList = new ArrayList<UserProfile>();
        fakeUserList.add(new UserProfile("John Jackson", 999));
        fakeUserList.add(new UserProfile("Jack Johnson", 666));
        fakeUserList.add(new UserProfile("Professor Taugenichts", 333));

        return fakeUserList;
    }


    /**
     * We have to run makeText on the UI thread, which the Service doesn't run on. A Handler allows
     * to post a runnable to be run on the UI thread. In this case we initialize a Handler in Constructor.
     * This way the toast will be shown in any relevant activity, where the AchievementsFeeder will be used.
     */
    private class ToastRunnable implements Runnable {
        String mText;

        public ToastRunnable(String text) {
            mText = text;
        }

        @Override
        public void run(){
            Toast.makeText(context, mText, Toast.LENGTH_LONG).show();
        }
    }
}
