package com.tobyrich.dev.hangarapp.beans.api;

/**
 * This class should have the same constants as in SmartPlane application
 * app-smartplane-android/SmartPlane/src/main/java/com/tobyrich/app/SmartPlane/account/AccountConstants.java
 * to use the token received during Smartplane session.
 * (as per 14.12.2016).
 */
public class AccountConstants {

    public static final String ACCOUNT_TYPE = "com.tobyrich.app.SmartPlane";

    /**
     * Auth token types
     */
    public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
    public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to an SmartPlane account";

    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to an SmartPlane account";
}
