package com.tobyrich.dev.hangarapp.activities;

import android.widget.ListView;
import android.widget.TextView;

import com.google.inject.AbstractModule;
import com.tobyrich.dev.hangarapp.lib.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

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
    @Mock private ListView lvAchievements;
    @Mock private TextView tvDescription;

    @Before
    public void setUp() throws Exception {
        // Create mock for private members of test
        MockitoAnnotations.initMocks(this);

        // Override injector and perform injection
        RoboGuice.overrideApplicationInjector(RuntimeEnvironment.application, new MyTestModule());
        RoboInjector injector = RoboGuice.getInjector(RuntimeEnvironment.application);
        injector.injectMembersWithoutViews(this);

        // Set up Mockito behavior
        doNothing().when(tvDescription).setText(anyString());
    }

    @Test
    public void testOnAchievementClick() throws Exception{
        // Stubbing
        tvDescription.setText("Test text...");

        // Verify
        verify(tvDescription).setText("Test text...");
    }

    private class MyTestModule extends AbstractModule {
        @Override
        protected void configure() {
            // Replace injected class with mock
            bind(TextView.class).toInstance(tvDescription);
        }
    }
}

