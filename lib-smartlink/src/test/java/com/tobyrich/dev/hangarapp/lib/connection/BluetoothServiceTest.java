package com.tobyrich.dev.hangarapp.lib.connection;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;

import com.google.inject.AbstractModule;
import com.tobyrich.dev.hangarapp.lib.BuildConfig;
import com.tobyrich.dev.hangarapp.lib.connection.events.PlaneEvent;
import com.tobyrich.dev.hangarapp.lib.connection.events.ScanEvent;
import com.tobyrich.dev.hangarapp.lib.utils.Consts;

import junit.framework.TestCase;

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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class BluetoothServiceTest extends TestCase {
    @InjectMocks
    private BluetoothService bluetoothService = new BluetoothService();
    @Mock
    private PlaneEvent planeEvent;
    @Mock
    private ScanEvent scanEvent;
    @Mock
    private BluetoothGattCharacteristic bluetoothGattCharacteristic;
    @Mock
    private BluetoothGatt mConnectedGatt;
    @Mock
    private BluetoothAdapter mBluetoothAdapter;
    @Mock
    private BluetoothManager manager;

    @Before
    public void setUp() throws Exception {
        // Create mock for private members of test
        MockitoAnnotations.initMocks(this);

        // Override injector and perform injection
        RoboGuice.overrideApplicationInjector(RuntimeEnvironment.application, new MyTestModule());
        RoboInjector injector = RoboGuice.getInjector(RuntimeEnvironment.application);
        injector.injectMembersWithoutViews(this);

        PlaneConnections.getInstance().setRudder(bluetoothGattCharacteristic);
        PlaneConnections.getInstance().setMotor(bluetoothGattCharacteristic);
    }

    @Test
    public void testOnCreate() throws Exception {
        /*BluetoothManager mockManager = Mockito.mock(BluetoothManager.class);
        Application application = (Application) ShadowApplication.getInstance().getApplicationContext();
        ShadowContextImpl shadowContext = (ShadowContextImpl) ShadowExtractor.extract(application.getBaseContext());
        shadowContext.setSystemService(Context.BLUETOOTH_SERVICE, mockManager);

        BluetoothManager locationManager = (BluetoothManager) RuntimeEnvironment.application.getSystemService(Context.BLUETOOTH_SERVICE);

        BluetoothService spy = Mockito.spy(bluetoothService);

        Mockito.when(spy.getSystemService(Context.BLUETOOTH_SERVICE)).thenReturn(manager);

        spy.onCreate();*/
    }

    @Test
    public void testOnEventScanEventStart() throws Exception {
        // Given
        Mockito.when(scanEvent.getState()).thenReturn(true);
        Mockito.when(mBluetoothAdapter.startLeScan(bluetoothService)).thenReturn(true);

        // When
        bluetoothService.onEvent(scanEvent);

        // Then
        Mockito.verify(mBluetoothAdapter, times(1)).startLeScan(bluetoothService);
    }

    @Test
    public void testOnEventScanEventStop() throws Exception {
        // Given
        Mockito.when(scanEvent.getState()).thenReturn(false);
        Mockito.doNothing().when(mBluetoothAdapter).stopLeScan(bluetoothService);

        // When
        bluetoothService.onEvent(scanEvent);

        // Then
        Mockito.verify(mBluetoothAdapter, times(1)).stopLeScan(bluetoothService);
    }

    @Test
    public void testOnEventPlaneEventRudder() throws Exception {
        // Given
        Mockito.when(planeEvent.getDevice()).thenReturn(PlaneEvent.RUDDER);
        // MAX_VALUE + 1
        Mockito.when(planeEvent.getValue()).thenReturn(Consts.MAX_RUDDER_VALUE + 1);
        Mockito.when(bluetoothGattCharacteristic.setValue(anyInt(), anyInt(), anyInt())).thenReturn(true);
        Mockito.when(mConnectedGatt.writeCharacteristic(bluetoothGattCharacteristic)).thenReturn(true);

        // When
        bluetoothService.onEvent(planeEvent);

        // Then
        Mockito.verify(bluetoothGattCharacteristic, times(1)).setValue(eq(Consts.MAX_RUDDER_VALUE), anyInt(), anyInt());
        Mockito.verify(mConnectedGatt, times(1)).writeCharacteristic(bluetoothGattCharacteristic);

        // Given
        // MIN_VALUE - 1
        Mockito.when(planeEvent.getValue()).thenReturn(Consts.MIN_RUDDER_VALUE - 1);
        Mockito.when(bluetoothGattCharacteristic.setValue(anyInt(), anyInt(), anyInt())).thenReturn(true);
        Mockito.when(mConnectedGatt.writeCharacteristic(bluetoothGattCharacteristic)).thenReturn(true);

        // When
        bluetoothService.onEvent(planeEvent);

        // Then
        Mockito.verify(bluetoothGattCharacteristic, times(1)).setValue(eq(Consts.MAX_RUDDER_VALUE), anyInt(), anyInt());
        Mockito.verify(mConnectedGatt, times(2)).writeCharacteristic(bluetoothGattCharacteristic);

        // Given
        // NORMAL_VALUE
        final int NORMAL_VALUE = 20;
        Mockito.when(planeEvent.getValue()).thenReturn(NORMAL_VALUE);
        Mockito.when(bluetoothGattCharacteristic.setValue(anyInt(), anyInt(), anyInt())).thenReturn(true);
        Mockito.when(mConnectedGatt.writeCharacteristic(bluetoothGattCharacteristic)).thenReturn(true);

        // When
        bluetoothService.onEvent(planeEvent);

        // Then
        Mockito.verify(bluetoothGattCharacteristic, times(0)).setValue(eq(NORMAL_VALUE - 1), anyInt(), anyInt());
        Mockito.verify(bluetoothGattCharacteristic, times(1)).setValue(eq(NORMAL_VALUE), anyInt(), anyInt());
        Mockito.verify(bluetoothGattCharacteristic, times(0)).setValue(eq(NORMAL_VALUE + 1), anyInt(), anyInt());
        Mockito.verify(mConnectedGatt, times(3)).writeCharacteristic(bluetoothGattCharacteristic);
    }

    @Test
    public void testOnEventPlaneEventMotor() throws Exception {
        // Given
        Mockito.when(planeEvent.getDevice()).thenReturn(PlaneEvent.MOTOR);
        // MAX_VALUE + 1
        Mockito.when(planeEvent.getValue()).thenReturn(Consts.MAX_MOTOR_VALUE + 1);
        Mockito.when(bluetoothGattCharacteristic.setValue(anyInt(), anyInt(), anyInt())).thenReturn(true);
        Mockito.when(mConnectedGatt.writeCharacteristic(bluetoothGattCharacteristic)).thenReturn(true);

        // When
        bluetoothService.onEvent(planeEvent);

        // Then
        Mockito.verify(bluetoothGattCharacteristic, times(1)).setValue(eq(Consts.MAX_MOTOR_VALUE), anyInt(), anyInt());
        Mockito.verify(mConnectedGatt, times(1)).writeCharacteristic(bluetoothGattCharacteristic);

        // Given
        // MIN_VALUE - 1
        Mockito.when(planeEvent.getValue()).thenReturn(Consts.MIN_MOTOR_VALUE - 1);
        Mockito.when(bluetoothGattCharacteristic.setValue(anyInt(), anyInt(), anyInt())).thenReturn(true);
        Mockito.when(mConnectedGatt.writeCharacteristic(bluetoothGattCharacteristic)).thenReturn(true);

        // When
        bluetoothService.onEvent(planeEvent);

        // Then
        Mockito.verify(bluetoothGattCharacteristic, times(1)).setValue(eq(Consts.MAX_MOTOR_VALUE), anyInt(), anyInt());
        Mockito.verify(mConnectedGatt, times(2)).writeCharacteristic(bluetoothGattCharacteristic);

        // Given
        // NORMAL_VALUE
        final int NORMAL_VALUE = 20;
        Mockito.when(planeEvent.getValue()).thenReturn(NORMAL_VALUE);
        Mockito.when(bluetoothGattCharacteristic.setValue(anyInt(), anyInt(), anyInt())).thenReturn(true);
        Mockito.when(mConnectedGatt.writeCharacteristic(bluetoothGattCharacteristic)).thenReturn(true);

        // When
        bluetoothService.onEvent(planeEvent);

        // Then
        Mockito.verify(bluetoothGattCharacteristic, times(0)).setValue(eq(NORMAL_VALUE - 1), anyInt(), anyInt());
        Mockito.verify(bluetoothGattCharacteristic, times(1)).setValue(eq(NORMAL_VALUE), anyInt(), anyInt());
        Mockito.verify(bluetoothGattCharacteristic, times(0)).setValue(eq(NORMAL_VALUE + 1), anyInt(), anyInt());
        Mockito.verify(mConnectedGatt, times(3)).writeCharacteristic(bluetoothGattCharacteristic);
    }

    private class MyTestModule extends AbstractModule {
        @Override
        protected void configure() {
            // Replace injected class with mock
            bind(PlaneEvent.class).toInstance(planeEvent);
            bind(BluetoothGattCharacteristic.class).toInstance(bluetoothGattCharacteristic);
            bind(BluetoothGatt.class).toInstance(mConnectedGatt);
            bind(ScanEvent.class).toInstance(scanEvent);
            bind(BluetoothAdapter.class).toInstance(mBluetoothAdapter);
            bind(BluetoothManager.class).toInstance(manager);
        }
    }
}
