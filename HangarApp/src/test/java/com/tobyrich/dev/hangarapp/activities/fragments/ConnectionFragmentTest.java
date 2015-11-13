package com.tobyrich.dev.hangarapp.activities.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.app.AlertDialog.Builder;
import com.google.inject.AbstractModule;
import com.tobyrich.dev.hangarapp.lib.BuildConfig;
import com.tobyrich.dev.hangarapp.lib.connection.events.ScanEvent;
import com.tobyrich.dev.hangarapp.lib.connection.events.ScanResult;

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

import java.util.ArrayList;

import roboguice.RoboGuice;
import roboguice.inject.RoboInjector;

import static org.mockito.Matchers.anyObject;
import static org.robolectric.util.FragmentTestUtil.startFragment;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ConnectionFragmentTest {

    @InjectMocks
    private ConnectionFragment connectionFragment = new ConnectionFragment();
    @Mock
    private ScanResult scanResult;
    @Mock
    private BluetoothDevice bluetoothDevice1;
    @Mock
    private BluetoothDevice bluetoothDevice2;
    @Mock
    private Builder builder;
    @Mock
    private Activity activity;

    private ArrayList<BluetoothDevice> bluetoothDevices;

    @Before
    public void setUp() throws Exception {
        // Create mock for private members of test
        MockitoAnnotations.initMocks(this);

        // Set up Mockito behavior
        bluetoothDevices = new ArrayList<BluetoothDevice>();
        bluetoothDevices.add(bluetoothDevice1);
        bluetoothDevices.add(bluetoothDevice2);

        // Override injector and perform injection
        RoboGuice.overrideApplicationInjector(RuntimeEnvironment.application, new MyTestModule());
        RoboInjector injector = RoboGuice.getInjector(RuntimeEnvironment.application);
        injector.injectMembersWithoutViews(this);
    }

    @Test
    public void testOnEvent() throws Exception{

        /*Mockito.when(scanResult.getResult()).thenReturn(bluetoothDevices);
        Mockito.when(Builder(anyObject())).thenReturn(builder);
        connectionFragment.onEvent(scanResult);*/

        /*ConnectionFragment spy = Mockito.spy(connectionFragment);


        Mockito.when(spy.getActivity()).thenReturn(activity);
        spy.onEvent(scanResult);*/
    }

    private class MyTestModule extends AbstractModule {
        @Override
        protected void configure() {
            // Replace injected class with mock
            bind(ScanResult.class).toInstance(scanResult);
            bind(Builder.class).toInstance(builder);

            //bind(BluetoothDevice.class).toInstance(bluetoothDevice1);
            //bind(BluetoothDevice.class).toInstance(bluetoothDevice2);

        }
    }
}
