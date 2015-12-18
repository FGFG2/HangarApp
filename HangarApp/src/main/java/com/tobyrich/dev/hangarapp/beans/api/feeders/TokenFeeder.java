package com.tobyrich.dev.hangarapp.beans.api.feeders;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;

import com.tobyrich.dev.hangarapp.beans.api.APIConstants;
import com.tobyrich.dev.hangarapp.beans.api.model.Achievement;

import org.roboguice.shaded.goole.common.base.Optional;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import roboguice.util.SafeAsyncTask;

/**
 * This class loads the images from the Internet using URL.
 */
public class TokenFeeder extends SafeAsyncTask<String> {

    private Context context;
    private FeedersCallback tokenFeederCallback;
    private String accountName;


    // Constructors --------------------------------------------------------------------------------
    public TokenFeeder(FeedersCallback tokenFeederCallback, Context context) {
        this.tokenFeederCallback = tokenFeederCallback;
        this.context = context;
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
        this.accountName = getAccountName();
        return this.getAuthToken();
    }


    @Override
    protected void onSuccess(String authToken) {
        // do this in the UI thread if call() succeeds
        Log.i(this.getClass().getSimpleName(), "onSuccess.");
        tokenFeederCallback.onTokenFeederComplete(authToken, accountName);
    }


    @Override
    protected void onException(Exception e) {
        // do this in the UI thread if call() threw an exception
        Log.e(this.getClass().getSimpleName(), "Exception occured!", e);
    }


    @Override
    protected void onFinally() {
        // always do this in the UI thread after calling call()
    }


    /**
     * Function returns an authorization token which was used by SmartPlane app.
     * No further authorization from HangarApp required.
     * @return String
     */
    @SuppressWarnings("deprecation")
    public String getAuthToken() {
        AccountManager mgr = AccountManager.get(this.context);
        Account[] accounts = mgr.getAccountsByType(APIConstants.ACCOUNT_TYPE);

        // There is just one account associated with TobyRich.
        Account account = accounts.length > 0 ? accounts[0] : null;
        Bundle result;

        try {
            AccountManagerFuture<Bundle> future = mgr.getAuthToken(account, APIConstants.AUTHTOKEN_TYPE_READ_ONLY, false, null, null);
            result = future.getResult();
            return result != null ? result.get(AccountManager.KEY_AUTHTOKEN).toString() : null;
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Token was not found.", e);
            return "A Token should have been here. Sadness :(";
        }
    }

    /**
     * Function returns an authorization account name.
     */
    public String getAccountName() {
        AccountManager mgr = AccountManager.get(this.context);
        Account[] accounts = mgr.getAccountsByType(APIConstants.ACCOUNT_TYPE);

        // There is just one account associated with TobyRich.
        Account account = accounts.length > 0 ? accounts[0] : null;
        if(account != null) {
            return account.name;
        } else {
            return null;
        }
    }

}
