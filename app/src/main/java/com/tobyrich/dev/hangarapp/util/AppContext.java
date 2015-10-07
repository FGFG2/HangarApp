package com.tobyrich.dev.hangarapp.util;

import android.content.Context;

/**
 * This class gives an app-context to the non-Activity classes, e.g. DBHandler.
 * Created by Alex on 30.06.2015.
 */
public class AppContext extends android.app.Application {

    private static AppContext instance;

    public AppContext() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

}
