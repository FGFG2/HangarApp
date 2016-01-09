package com.tobyrich.dev.hangarapp.lib.connection.events;

import com.google.inject.AbstractModule;
import com.tobyrich.dev.hangarapp.lib.BuildConfig;
import com.tobyrich.dev.hangarapp.lib.connection.DatatransferInterpreter;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import de.greenrobot.event.EventBus;
import roboguice.RoboGuice;
import roboguice.inject.RoboInjector;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class DatatransferInterpreterTest extends TestCase {

    private final static double VALUE_5 = 5;
    private final static double VALUE_7 = 7;
    private final static double VALUE_12 = 12;
    private final static double VALUE_444 = 444;

    private double accelerometerValueX;
    private double accelerometerValueY;
    private double accelerometerValueZ;
    private double batteryValue;

    // This might not be the best solution, but it works.
    private boolean onEventWasCalled = false;

    @Before
    public void setUp() throws Exception {
        // Create mock for private members of test
        MockitoAnnotations.initMocks(this);

        // Override injector and perform injection
        RoboGuice.overrideApplicationInjector(RuntimeEnvironment.application, new MyTestModule());
        RoboInjector injector = RoboGuice.getInjector(RuntimeEnvironment.application);
        injector.injectMembersWithoutViews(this);

        EventBus.getDefault().register(this);
    }

    @Test
    public void testReceivedGyro1() throws Exception {
        // Given
        final byte[] response = createGyroResponse(DatatransferInterpreter.GYRO);
        onEventWasCalled = false;
        accelerometerValueX = VALUE_5;
        accelerometerValueY = VALUE_7;
        accelerometerValueZ = VALUE_12;

        // When
        DatatransferInterpreter.received(response);

        // Then
        assertThat(onEventWasCalled, is(true));
    }

    @Test
    public void testReceivedGyro2() throws Exception {
        // Given
        final byte[] response = createGyroResponse(DatatransferInterpreter.GYRO);
        onEventWasCalled = false;
        accelerometerValueX = VALUE_7;
        accelerometerValueY = VALUE_5;
        accelerometerValueZ = VALUE_444;

        // When
        DatatransferInterpreter.received(response);

        // Then
        assertThat(onEventWasCalled, is(true));
    }

    @Test
    public void testReceivedGyroBattery() throws Exception {
        // Given
        final byte[] response = createBatteryResponse(DatatransferInterpreter.BATTERY);
        onEventWasCalled = false;
        batteryValue = VALUE_5;

        // When
        DatatransferInterpreter.received(response);

        // Then
        assertThat(onEventWasCalled, is(true));
    }

    private byte[] createGyroResponse(byte type){

        String typeAsString = String.valueOf(type);

        String response = "[" + typeAsString + ", " +
                (int) accelerometerValueX + ", 0, 0, 0, " +
                (int) accelerometerValueY + ", 0, 0, 0, " +
                (int) accelerometerValueZ + ", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]";
        String[] byteValues = response.substring(1, response.length() - 1).split(",");
        byte[] bytes = new byte[byteValues.length];
        for (int i=0, len=bytes.length; i<len; i++) {
            bytes[i] = Byte.parseByte(byteValues[i].trim());
        }
        return bytes;
    }

    private byte[] createBatteryResponse(byte type){

        String typeAsString = String.valueOf(type);

        String response = "[" + typeAsString + ", " +
                (int) batteryValue + ", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]";
        String[] byteValues = response.substring(1, response.length() - 1).split(",");
        byte[] bytes = new byte[byteValues.length];
        for (int i=0, len=bytes.length; i<len; i++) {
            bytes[i] = Byte.parseByte(byteValues[i].trim());
        }
        return bytes;
    }

    public void onEvent(GyroscopeResult evt){
        onEventWasCalled = true;

        double xAxis = evt.getX();
        double yAxis = evt.getY();
        double zAxis = evt.getZ();

        assertThat(xAxis, is(accelerometerValueX));
        assertThat(yAxis, is(accelerometerValueY));
        assertThat(zAxis, is(accelerometerValueZ));
    }

    public void onEvent(PlaneResult evt){
        if(PlaneResult.BATTERY == evt.getDevice()){
            onEventWasCalled = true;
            int batteryValue = evt.getValue();
            assertThat((double) batteryValue, is(accelerometerValueX));
        }
    }

    private class MyTestModule extends AbstractModule {
        @Override
        protected void configure() {
            // Replace injected class with mock
        }
    }
}
