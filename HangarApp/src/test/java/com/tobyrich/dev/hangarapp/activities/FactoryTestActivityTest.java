package com.tobyrich.dev.hangarapp.activities;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.inject.AbstractModule;
import com.tobyrich.dev.hangarapp.R;
import com.tobyrich.dev.hangarapp.beans.PlaneData;
import com.tobyrich.dev.hangarapp.lib.BuildConfig;
import com.tobyrich.dev.hangarapp.lib.connection.events.PlaneEvent;
import com.tobyrich.dev.hangarapp.lib.utils.Consts;

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

import de.greenrobot.event.EventBus;
import roboguice.RoboGuice;
import roboguice.inject.RoboInjector;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class FactoryTestActivityTest {
    @InjectMocks
    private FactoryTestActivity factoryTestActivity = new FactoryTestActivity();
    @Mock
    private View v;
    @Mock
    private ToggleButton toggleButton;
    @Mock
    private EventBus eventBus;
    @Mock
    private Bundle savedInstanceState;
    @Before
    public void setUp() throws Exception {
        // Create mock for private members of test
        MockitoAnnotations.initMocks(this);

        // Override injector and perform injection
        RoboGuice.overrideApplicationInjector(RuntimeEnvironment.application, new MyTestModule());
        RoboInjector injector = RoboGuice.getInjector(RuntimeEnvironment.application);
        injector.injectMembersWithoutViews(this);

    }

    @Test
    public void testOnRudderLeftToggleButtonClickIsChecked() throws Exception{
        // Given
        Mockito.when(toggleButton.isChecked()).thenReturn(true);

        // When
        factoryTestActivity.onRudderLeftToggleButtonClick(v);

        // Then
        Mockito.verify(toggleButton, times(1)).setChecked(false);
    }

    @Test
    public void testOnRudderLeftToggleButtonClickIsNotChecked() throws Exception{
        // Given
        Mockito.when(toggleButton.isChecked()).thenReturn(false);

        // When
        factoryTestActivity.onRudderLeftToggleButtonClick(v);

        // Then
        Mockito.verify(toggleButton, times(0)).setChecked(false);
    }

    @Test
    public void testOnRudderRightToggleButtonClickIsChecked() throws Exception{
        // Given
        Mockito.when(toggleButton.isChecked()).thenReturn(true);

        // When
        factoryTestActivity.onRudderRightToggleButtonClick(v);

        // Then
        Mockito.verify(toggleButton, times(1)).setChecked(false);
    }

    @Test
    public void testOnRudderRightToggleButtonClickIsNotChecked() throws Exception{
        // Given
        Mockito.when(toggleButton.isChecked()).thenReturn(false);

        // When
        factoryTestActivity.onRudderRightToggleButtonClick(v);

        // Then
        Mockito.verify(toggleButton, times(0)).setChecked(false);
    }

    private class MyTestModule extends AbstractModule {
        @Override
        protected void configure() {
            // Replace injected class with mock
            bind(ToggleButton.class).toInstance(toggleButton);
            bind(EventBus.class).toInstance(eventBus);
            bind(Bundle.class).toInstance(savedInstanceState);
        }
    }
}
