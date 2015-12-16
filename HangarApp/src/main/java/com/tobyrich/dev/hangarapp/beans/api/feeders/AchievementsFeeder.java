package com.tobyrich.dev.hangarapp.beans.api.feeders;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.tobyrich.dev.hangarapp.beans.api.APIConstants;
import com.tobyrich.dev.hangarapp.beans.api.model.Achievement;
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
 * This class gets achievements from the remote Server using definite User-Token.
 */
public class AchievementsFeeder extends SafeAsyncTask<List<Achievement>> {

    private List<Achievement> achievementList = new ArrayList<Achievement>();
    private Context context;
    private String authToken;
    private Handler mHandler;
    private AchievementsFeederCallback achievementsFeederCallback;


    public interface AchievementsFeederCallback {
        void onAchievementsFeederComplete(List<Achievement> achievementList);
    }


    // Constructors --------------------------------------------------------------------------------
    public AchievementsFeeder(AchievementsFeederCallback achievementsFeederCallback) {
        this.achievementsFeederCallback = achievementsFeederCallback;
        mHandler = new Handler();
    }


    public AchievementsFeeder(AchievementsFeederCallback achievementsFeederCallback, Context context) {
        this.achievementsFeederCallback = achievementsFeederCallback;
        this.context = context;
        mHandler = new Handler();
    }


    public AchievementsFeeder(
            AchievementsFeederCallback achievementsFeederCallback,
            Context context,
            String authToken
    ) {
        this.achievementsFeederCallback = achievementsFeederCallback;
        this.context = context;
        this.authToken = authToken;
        mHandler = new Handler();
    }

    // Functions -----------------------------------------------------------------------------------
    @Override
    protected void onPreExecute() {

    }


    public List<Achievement> call() throws Exception {
        this.achievementList = loadAchievementsFromService();
        return achievementList;
    }


    @Override
    protected void onSuccess(List<Achievement> result) {
        Log.i(this.getClass().getSimpleName(), "onSuccess.");
        achievementsFeederCallback.onAchievementsFeederComplete(result);
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


    private List<Achievement> loadAchievementsFromService() {
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(APIConstants.URL_ALL_ACHIEVEMENTS)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            AchievementService service = retrofit.create(AchievementService.class);
            Call<List<Achievement>> call = service.getAllAchievements(authToken);
            achievementList = call.execute().body();

        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(), "Error loading achievements.", e);
            e.printStackTrace();
            // FIXME: No fake achievements should be shown by release.
            achievementList = getAchievementsList();
        }

        if (achievementList == null) {
            Log.i(this.getClass().getSimpleName(), "False token.");
            mHandler.post(new ToastRunnable("Please start the SmartPlane application and input your credentials again."));
            // FIXME: No fake achievements should be shown by release.
            achievementList = getAchievementsList();
        }

        return achievementList;
    }


    /**
     * Dummy achievements for testing purposes.
     */
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

