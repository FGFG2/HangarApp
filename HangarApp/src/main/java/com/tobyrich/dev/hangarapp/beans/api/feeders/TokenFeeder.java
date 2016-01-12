package com.tobyrich.dev.hangarapp.beans.api.feeders;

import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.tobyrich.dev.hangarapp.beans.api.APIConstants;

import roboguice.util.SafeAsyncTask;

/**
 * This class loads the images from the Internet using URL.
 */
public class TokenFeeder extends SafeAsyncTask<String> {

    private Activity callingActivity;
    private FeedersCallback tokenFeederCallback;

    // Constructors --------------------------------------------------------------------------------
    public TokenFeeder(Activity callingActivity) {
        this.tokenFeederCallback = (FeedersCallback) callingActivity;
        this.callingActivity = callingActivity;
    }

    // Functions -----------------------------------------------------------------------------------
    @Override
    protected void onPreExecute() {
        // do this in the UI thread before executing call()
    }

    /**
     * call() always executes in the background.  See java.util.concurrent.Callable.
     * Everything else (eg. onSuccess, etc.) will execute in the UI thread.
     */
    public String call() throws Exception {
        return this.getAuthToken();
    }

    @Override
    protected void onSuccess(String authToken) {
        // do this in the UI thread if call() succeeds
        Log.i(this.getClass().getSimpleName(), "onSuccess.");
        tokenFeederCallback.onTokenFeederComplete(authToken);
    }

    @Override
    protected void onException(Exception e) {
        // do this in the UI thread if call() threw an exception
        Log.e(this.getClass().getSimpleName(), "Exception occured!", e);
    }

    /**
     * Function returns an authorization token which was used by SmartPlane app.
     * No further authorization from HangarApp required.
     * @return String
     */
    public String getAuthToken() {
        AccountManager mgr = AccountManager.get(this.callingActivity);
        try {
            AccountManagerFuture<Bundle> future = mgr.getAuthTokenByFeatures(APIConstants.ACCOUNT_TYPE, APIConstants.AUTHTOKEN_TYPE_READ_ONLY, null, this.callingActivity, null, null, null, null);
            Bundle bnd = future.getResult();
            Log.d(this.getClass().getSimpleName(), "Got token --> Successfully authenticated: " + bnd);
            return bnd.getString(AccountManager.KEY_AUTHTOKEN);
        } catch (OperationCanceledException e) {
            Log.i(this.getClass().getSimpleName(), "User canceled login --> no token present for data sending");
            return "A Token should have been here. Sadness :(";
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "No authentication token present!", e);
            return "A Token should have been here. Sadness :(";
        }
    }
}