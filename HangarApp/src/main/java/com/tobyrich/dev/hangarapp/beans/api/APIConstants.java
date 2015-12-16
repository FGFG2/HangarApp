package com.tobyrich.dev.hangarapp.beans.api;

/**
 * Here are the constants used to communicate with a remote server.
 * This class should have the same tokenType and accountType constants as in SmartPlane application
 * app-smartplane-android/SmartPlane/src/main/java/com/tobyrich/app/SmartPlane/account/APIConstants.java
 * to use the token received during Smartplane session.
 * (as per 14.12.2016).
 */
public class APIConstants {

    public static final String URL_ALL_ACHIEVEMENTS = "http://chaos-krauts.de/Achievement/";
    public static final String DEFAULT_ICON_URL = "http://4.bp.blogspot.com/_C5a2qH8Y_jk/StYXDpZ9-WI/AAAAAAAAAJQ/sCgPx6jfWPU/S1600-R/android.png";

    public static final String ACCOUNT_TYPE = "com.tobyrich.app.SmartPlane";

    /**
     * Auth token types
     */
    public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
    public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to an SmartPlane account";

    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to an SmartPlane account";
}
