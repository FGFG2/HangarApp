package com.tobyrich.dev.hangarapp.activities;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.inject.AbstractModule;
import com.tobyrich.dev.hangarapp.lib.BuildConfig;
import com.tobyrich.dev.hangarapp.lib.connection.events.PlaneResult;

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
public class BatteryDataActivityTest {

    @InjectMocks
    private BatteryDataActivity batteryDataActivity = new BatteryDataActivity();
    @Mock
    private ProgressBar batteryProgressBar;
    @Mock
    private TextView tvBatteryStatus;
    @Mock
    private PlaneResult planeResult;

    private final static int BATTERY_CHARGE_51 = 51;
    private final static int BATTERY_CHARGE_50 = 50;
    private final static int BATTERY_CHARGE_20 = 20;

    private static final String COLOR_CODE_GREEN = "#ff00aa00";
    private static final String COLOR_CODE_YELLOW = "#ffffcb00";
    private static final String COLOR_CODE_RED = "#ffee0000";

    @Before
    public void setUp() throws Exception {
        // Create mock for private members of test
        MockitoAnnotations.initMocks(this);

        // Override injector and perform injection
        RoboGuice.overrideApplicationInjector(RuntimeEnvironment.application, new MyTestModule());
        RoboInjector injector = RoboGuice.getInjector(RuntimeEnvironment.application);
        injector.injectMembersWithoutViews(this);

        // Set up Mockito behavior
        Mockito.doNothing().when(batteryProgressBar).setProgress(anyInt());
        Mockito.doNothing().when(batteryProgressBar).setProgressDrawable((Drawable) anyObject());
        Mockito.doNothing().when(tvBatteryStatus).setText(anyString());
        Mockito.doNothing().when(tvBatteryStatus).setTextColor(anyInt());
    }

    @Test
    public void testOnPlaneResultEventPositive() throws Exception{
        // Given
        Mockito.when(planeResult.getDevice()).thenReturn(PlaneResult.BATTERY);
        Mockito.when(planeResult.getValue()).thenReturn(BATTERY_CHARGE_20);

        // When
        batteryDataActivity.onEvent(planeResult);

        // Then
        Mockito.verify(planeResult, times(1)).getValue();
    }

    @Test
    public void testOnPlaneResultEventNegative() throws Exception{
        // Given
        Mockito.when(planeResult.getDevice()).thenReturn(PlaneResult.MOTOR);
        Mockito.when(planeResult.getValue()).thenReturn(BATTERY_CHARGE_20);

        // When
        batteryDataActivity.onEvent(planeResult);

        // Then
        Mockito.verify(planeResult, times(0)).getValue();
    }

    @Test
    public void testSetCurrentBatteryChargeRed() throws Exception{
        // When
        batteryDataActivity.setCurrentBatteryCharge(BATTERY_CHARGE_20);

        // Then
        Mockito.verify(tvBatteryStatus, times(1)).setTextColor(Color.parseColor(COLOR_CODE_RED));
        Mockito.verify(tvBatteryStatus, times(1)).setTextColor(anyInt());
    }

    @Test
    public void testSetCurrentBatteryChargeYellow() throws Exception{
        // When
        batteryDataActivity.setCurrentBatteryCharge(BATTERY_CHARGE_50);

        // Then
        Mockito.verify(tvBatteryStatus, times(1)).setTextColor(Color.parseColor(COLOR_CODE_YELLOW));
        Mockito.verify(tvBatteryStatus, times(1)).setTextColor(anyInt());
    }
    @Test
    public void testSetCurrentBatteryChargeGreen() throws Exception{
        // When
        batteryDataActivity.setCurrentBatteryCharge(BATTERY_CHARGE_51);

        // Then
        Mockito.verify(tvBatteryStatus, times(1)).setTextColor(Color.parseColor(COLOR_CODE_GREEN));
        Mockito.verify(tvBatteryStatus, times(1)).setTextColor(anyInt());
    }

    @Test
    public void testSetOperationalRemainedTimeRed() throws Exception{
        // Given
        Mockito.when(batteryProgressBar.getProgress()).thenReturn(BATTERY_CHARGE_20);

        // When
        batteryDataActivity.setOperationalRemainedTime(planeData);

        // Then
        Mockito.verify(tvBatteryStatus, times(1)).setTextColor(Color.parseColor(COLOR_CODE_RED));
        Mockito.verify(tvBatteryStatus, times(1)).setTextColor(anyInt());
    }

    @Test
    public void testSetOperationalRemainedTimeYellow() throws Exception{
        // Given
        Mockito.when(batteryProgressBar.getProgress()).thenReturn(BATTERY_CHARGE_50);

        // When
        batteryDataActivity.setOperationalRemainedTime(planeData);

        // Then
        Mockito.verify(tvBatteryStatus, times(1)).setTextColor(Color.parseColor(COLOR_CODE_YELLOW));
        Mockito.verify(tvBatteryStatus, times(1)).setTextColor(anyInt());
    }
    @Test
    public void testSetOperationalRemainedTimeGreen() throws Exception{
        // Given
        Mockito.when(batteryProgressBar.getProgress()).thenReturn(BATTERY_CHARGE_51);

        // When
        batteryDataActivity.setOperationalRemainedTime(planeData);

        // Then
        Mockito.verify(tvBatteryStatus, times(1)).setTextColor(Color.parseColor(COLOR_CODE_GREEN));
        Mockito.verify(tvBatteryStatus, times(1)).setTextColor(anyInt());
    }

    private class MyTestModule extends AbstractModule {
        @Override
        protected void configure() {
            // Replace injected class with mock
            bind(ProgressBar.class).toInstance(batteryProgressBar);
            bind(TextView.class).toInstance(tvBatteryStatus);
            bind(PlaneData.class).toInstance(planeData);
        }
    }
}

