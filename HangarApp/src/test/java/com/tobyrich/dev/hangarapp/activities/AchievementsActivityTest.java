package com.tobyrich.dev.hangarapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.inject.AbstractModule;
import com.tobyrich.dev.hangarapp.R;
import com.tobyrich.dev.hangarapp.adapters.AchievementsAdapter;
import com.tobyrich.dev.hangarapp.beans.api.feeders.AchievementsFeeder;
import com.tobyrich.dev.hangarapp.beans.api.model.Achievement;
import com.tobyrich.dev.hangarapp.lib.BuildConfig;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowConnectivityManager;
import org.robolectric.shadows.ShadowListView;
import org.robolectric.shadows.ShadowNetworkInfo;

import java.util.ArrayList;
import java.util.List;

import roboguice.RoboGuice;
import roboguice.inject.RoboInjector;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class AchievementsActivityTest extends Mockito {

    @InjectMocks private AchievementsActivity achievementsActivity = new AchievementsActivity();

    @Mock private TextView tvDescription;
    @Mock private ImageView ivSmartplane;
    @Mock private ProgressBar achievementsLoading;

    @Mock private View v;
    @Mock private LruCache<String, Bitmap> mMemoryCache;

    private AchievementsAdapter adapter;
    private List<Achievement> achievementList = new ArrayList<Achievement>();
    private ConnectivityManager connectivityManager;
    private ShadowConnectivityManager shadowConnectivityManager;
    private ShadowNetworkInfo shadowOfActiveNetworkInfo;
    private AchievementsFeeder achievementsFeeder;

    private AchievementsActivity activity;

    private ListView lvAchievements;

    @Before
    public void setUp() throws Exception {
        // Create mock for private members of test
        MockitoAnnotations.initMocks(this);

        // Override injector and perform injection
        RoboGuice.overrideApplicationInjector(RuntimeEnvironment.application, new MyTestModule());
        RoboInjector injector = RoboGuice.getInjector(RuntimeEnvironment.application);
        injector.injectMembersWithoutViews(this);

        // Set up Mockito behavior
        // TODO Ressource not found exception
        //activity = Robolectric.setupActivity(AchievementsActivity.class);

        lvAchievements = Mockito.spy(new ListView(achievementsActivity.getApplicationContext()));
        connectivityManager = (ConnectivityManager) RuntimeEnvironment.application.getSystemService(Context.CONNECTIVITY_SERVICE);
        shadowConnectivityManager = Shadows.shadowOf(connectivityManager);
        shadowOfActiveNetworkInfo = Shadows.shadowOf(connectivityManager.getActiveNetworkInfo());
        achievementsFeeder = new AchievementsFeeder(achievementsActivity,achievementsActivity.getApplicationContext(),"1111");
        achievementList = achievementsFeeder.getAchievementsList();
        adapter = new AchievementsAdapter(achievementsActivity.getApplicationContext(),achievementList);
        lvAchievements.setAdapter(adapter);
        //lvAchievements.addView(adapter.getView(0, LayoutInflater.from(achievementsActivity.getApplicationContext()).inflate(R.layout.achievement, null), null));
    }

    @Ignore
    public void testLoadAchievementsList() {

        //When
        achievementsActivity.loadAchievementsList();

        //Then
        verify(achievementsLoading, times(1)).setVisibility(View.VISIBLE);
        Assert.assertFalse(lvAchievements.getChildCount() == 0);

        testClickOnAchievement();
    }

    public void testClickOnAchievement() {
        //When
        View child0 = lvAchievements.getChildAt(0);
        child0.performClick();

        //Then
        Assert.assertFalse(ivSmartplane.isShown());
        Assert.assertFalse(tvDescription.getText().toString().isEmpty());
        verify(ivSmartplane).setVisibility(View.INVISIBLE);
        verify(tvDescription).setText(anyString());
        verify(child0).setBackgroundColor(Color.parseColor("#696969"));
    }

    @Test
    public void testIsInternetConnectionAvailableTrue() {
        //When
        shadowOfActiveNetworkInfo.setConnectionStatus(true);

        //Then
        Assert.assertNotNull(connectivityManager);
        Assert.assertNotNull(connectivityManager.getActiveNetworkInfo());
        Assert.assertTrue(connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected());
    }

    @Test
    public void testIsInternetConnectionAvailableFalse() {
        //When
        shadowOfActiveNetworkInfo.setConnectionStatus(false);

        //Then
        Assert.assertNotNull(connectivityManager);
        Assert.assertNotNull(connectivityManager.getActiveNetworkInfo());
        Assert.assertFalse(connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected());

        //When
        shadowConnectivityManager.setActiveNetworkInfo(null);

        //Then
        Assert.assertNull(connectivityManager.getActiveNetworkInfo());
        Assert.assertFalse(connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected());
    }

    @Test
    public void testSetupCache() {
        //When
        achievementsActivity.setupCache();

        //Then
        Assert.assertNotNull(mMemoryCache);
    }

    private class MyTestModule extends AbstractModule {
        @Override
        protected void configure() {
            // Replace injected class with mock
            bind(TextView.class).toInstance(tvDescription);
            bind(ImageView.class).toInstance(ivSmartplane);
            bind(ProgressBar.class).toInstance(achievementsLoading);
            bind(LruCache.class).toInstance(mMemoryCache);
        }
    }
}

