package com.tobyrich.dev.hangarapp.beans.api.feeders;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
 * This class loads the images from the Internet using URL. The URL Achievement is relative ist
 */
public class ImageFeeder extends SafeAsyncTask<Bitmap> {

    private String imageURL;
    private LruCache<String, Bitmap> mMemoryCache;
    private List<Achievement> achievementList;
    private Bitmap bm;
    private BitmapFactory.Options bmOptions;
    private FeedersCallback imageFeederCallback;


    // Constructors --------------------------------------------------------------------------------
    public ImageFeeder(FeedersCallback imageFeederCallback, String imageURL) {
        int size = (int) Runtime.getRuntime().maxMemory() / 1024 / 8;
        this.mMemoryCache = new LruCache<String, Bitmap>(size);
        this.imageURL = this.getImageURL(imageURL);
        this.imageFeederCallback = imageFeederCallback;
    }


    public ImageFeeder(FeedersCallback imageFeederCallback, String imageURL, LruCache<String, Bitmap> mMemoryCache) {
        this.imageURL = this.getImageURL(imageURL);
        this.mMemoryCache = mMemoryCache;
        this.imageFeederCallback = imageFeederCallback;
    }


    public ImageFeeder(FeedersCallback imageFeederCallback, List<Achievement> achievementList, LruCache<String, Bitmap> mMemoryCache) {
        this.mMemoryCache = mMemoryCache;
        this.achievementList = achievementList;
        this.imageFeederCallback = imageFeederCallback;
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
    public Bitmap call() throws Exception {
        return loadImage(this.imageURL, bmOptions);
    }


    @Override
    protected void onSuccess(Bitmap icon) {
        // do this in the UI thread if call() succeeds
        Log.i(this.getClass().getSimpleName(), "onSuccess.");
        imageFeederCallback.onImageFeederComplete(this.imageURL, icon);
    }


    @Override
    protected void onException(Exception e) {
        // do this in the UI thread if call() threw an exception
        Log.e(this.getClass().getSimpleName(), "Exception occured!");
        e.printStackTrace();
    }


    @Override
    protected void onFinally() {
        // always do this in the UI thread after calling call()
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
            HttpURLConnection httpConn = (HttpURLConnection) conn;
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


    private Optional<URL> getUrlFromString(String urlString) {
        try {
            return Optional.fromNullable(new URL(urlString));
        } catch (MalformedURLException e) {
            return Optional.absent();
        }
    }


    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }


    public Bitmap getBitmapFromMemCache(String key) {
        if (this.mMemoryCache != null) {
            return mMemoryCache.get(key);
        } else {
            return null;
        }
    }


    public String getImageURL(String imageURL) {
        if(imageURL==null||imageURL=="") {
            Log.i(this.getClass().getSimpleName(), "No valid URL string for an icon received. Substitute to default value.");
            return APIConstants.DEFAULT_ICON_URL;
        } else {
            return imageURL;
        }
    }

}
