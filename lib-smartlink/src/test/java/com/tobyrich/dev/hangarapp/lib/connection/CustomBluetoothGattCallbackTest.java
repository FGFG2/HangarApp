package com.tobyrich.dev.hangarapp.lib.connection;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;

import com.google.inject.AbstractModule;
import com.tobyrich.dev.hangarapp.lib.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.LinkedList;
import java.util.UUID;

import roboguice.RoboGuice;
import roboguice.inject.RoboInjector;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class CustomBluetoothGattCallbackTest {
    private static final UUID SMARTPLANE_MOTOR = UUID.fromString("75B64E51-0010-4ED1-921A-476090D80BA7");
    private static final UUID SMARTPLANE_RUDDER = UUID.fromString("75B64E51-0021-4ED1-921A-476090D80BA7");
    private static final UUID BATTERY_characteristic = UUID.fromString("00002A19-0000-1000-8000-00805F9B34FB");
    private static final UUID DATATransfer_characteristic = UUID.fromString("75B64E51-F195-4ED1-921A-476090D80BA7");
    private static final UUID DATATransfer_descriptor = UUID.fromString("00002902-0000-1000-8000-00805F9B34FB");

    @InjectMocks
    private CustomBluetoothGattCallback customBluetoothGattCallback = new CustomBluetoothGattCallback();
    @Mock
    private BluetoothGatt bluetoothGatt;
    @Mock
    private BluetoothGattService bluetoothGattService;
    @Mock
    private BluetoothService bluetoothService = new BluetoothService();
    @Mock
    private BluetoothGattCharacteristic bluetoothGattCharacteristic;
    @Mock
    private BluetoothGattDescriptor descriptor;

    private UUID uuid;

    @Before
    public void setUp() throws Exception {
        // Create mock for private members of test
        MockitoAnnotations.initMocks(this);

        // Override injector and perform injection
        RoboGuice.overrideApplicationInjector(RuntimeEnvironment.application, new MyTestModule());
        RoboInjector injector = RoboGuice.getInjector(RuntimeEnvironment.application);
        injector.injectMembersWithoutViews(this);

        LinkedList<BluetoothGattCharacteristic> bluetoothGattCharacteristicList = new LinkedList<BluetoothGattCharacteristic>();
        LinkedList<BluetoothGattService> bluetoothGattServiceList = new LinkedList<BluetoothGattService>();

        // Preparations
        bluetoothGattServiceList.add(bluetoothGattService);
        bluetoothGattCharacteristicList.add(bluetoothGattCharacteristic);
        Mockito.when(bluetoothGatt.getServices()).thenReturn(bluetoothGattServiceList);
        Mockito.when(bluetoothGattService.getCharacteristics()).thenReturn(bluetoothGattCharacteristicList);
        Mockito.when(bluetoothGattCharacteristic.getDescriptor(DATATransfer_descriptor)).thenReturn(descriptor);

    }

    @Test
    public void testSendResultEventDataTransfer() throws Exception {
        // Given
        uuid = DATATransfer_characteristic;
        Mockito.when(bluetoothGattCharacteristic.getUuid()).thenReturn(uuid);

        String response = "[100, 1, 2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]";
        String[] byteValues = response.substring(1, response.length() - 1).split(",");
        byte[] bytes = new byte[byteValues.length];
        for (int i=0, len=bytes.length; i<len; i++) {
            bytes[i] = Byte.parseByte(byteValues[i].trim());
        }
        Mockito.when(bluetoothGattCharacteristic.getValue()).thenReturn(bytes);

        // When
        final int value = customBluetoothGattCallback.sendResultEvent("test", bluetoothGatt, bluetoothGattCharacteristic);

        // Then
        assertThat(value, is(equalTo(0)));
    }

    @Test
    public void testSendResultEventRudder() throws Exception {
        // Given
        uuid = SMARTPLANE_RUDDER;
        Mockito.when(bluetoothGattCharacteristic.getUuid()).thenReturn(uuid);

        final int rudderValue = 1;
        Mockito.when(bluetoothGattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT8, 0)).thenReturn(rudderValue);

        // When
        final int sendedValue = customBluetoothGattCallback.sendResultEvent("test", bluetoothGatt, bluetoothGattCharacteristic);

        // Then
        assertThat(sendedValue, is(equalTo(rudderValue)));
    }

    @Test
    public void testSendResultEventMotor() throws Exception {
        // Given
        uuid = SMARTPLANE_MOTOR;
        Mockito.when(bluetoothGattCharacteristic.getUuid()).thenReturn(uuid);

        final int motorValue = 2;
        Mockito.when(bluetoothGattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0)).thenReturn(motorValue);

        // When
        final int sendedValue = customBluetoothGattCallback.sendResultEvent("test", bluetoothGatt, bluetoothGattCharacteristic);

        // Then
        assertThat(sendedValue, is(equalTo(motorValue)));
    }

    @Test
    public void testSendResultEventBattery() throws Exception {
        // Given
        uuid = BATTERY_characteristic;
        Mockito.when(bluetoothGattCharacteristic.getUuid()).thenReturn(uuid);

        final int batteryValue = 3;
        Mockito.when(bluetoothGattCharacteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0)).thenReturn(batteryValue);

        // When
        final int sendedValue = customBluetoothGattCallback.sendResultEvent("test", bluetoothGatt, bluetoothGattCharacteristic);

        // Then
        assertThat(sendedValue, is(equalTo(batteryValue)));
    }

    @Test
    public void testHandleDataTransferCharacteristic() throws Exception {
        // Given
        uuid = DATATransfer_characteristic;
        Mockito.when(bluetoothGattCharacteristic.getUuid()).thenReturn(uuid);

        final int status = BluetoothGatt.GATT_SUCCESS;

        // When
        customBluetoothGattCallback.onServicesDiscovered(null, status);

        // Then
        Mockito.verify(bluetoothGatt, times(1)).writeDescriptor(descriptor);
        //Mockito.verify(bluetoothGatt, times(1)).readCharacteristic(bluetoothGattCharacteristic);
    }

    @Test
    public void testHandleBatteryCharacteristic() throws Exception {
        // Given
        uuid = BATTERY_characteristic;
        Mockito.when(bluetoothGattCharacteristic.getUuid()).thenReturn(uuid);

        final int status = BluetoothGatt.GATT_SUCCESS;

        // When
        customBluetoothGattCallback.onServicesDiscovered(null, status);

        // Then
        // Mockito.verify(bluetoothGatt, times(1)).readCharacteristic(bluetoothGattCharacteristic);
        // Mockito.verify(bluetoothGatt, times(1)).setCharacteristicNotification(bluetoothGattCharacteristic, true);

        Mockito.verify(bluetoothGattCharacteristic, times(2)).getUuid();
    }

    @Test
    public void testHandleMotorCharacteristic() throws Exception {
        // Given
        uuid = SMARTPLANE_MOTOR;
        Mockito.when(bluetoothGattCharacteristic.getUuid()).thenReturn(uuid);

        final int status = BluetoothGatt.GATT_SUCCESS;

        // When
        customBluetoothGattCallback.onServicesDiscovered(null, status);

        // Then
        Mockito.verify(bluetoothGattCharacteristic, times(2)).getUuid();
        Mockito.verify(bluetoothGatt, times(0)).setCharacteristicNotification(bluetoothGattCharacteristic, true);
    }

    @Test
    public void testHandleRudderCharacteristic() throws Exception {
        // Given
        uuid = SMARTPLANE_RUDDER;
        Mockito.when(bluetoothGattCharacteristic.getUuid()).thenReturn(uuid);

        final int status = BluetoothGatt.GATT_SUCCESS;

        // When
        customBluetoothGattCallback.onServicesDiscovered(null, status);

        // Then
        Mockito.verify(bluetoothGattCharacteristic, times(2)).getUuid();
        Mockito.verify(bluetoothGatt, times(0)).setCharacteristicNotification(bluetoothGattCharacteristic, true);
    }

    @Test
    public void testOnConnectionStateChangeConnected() throws Exception {
        // Given
        final int status = BluetoothGatt.GATT_SUCCESS;
        final int newState = BluetoothProfile.STATE_CONNECTED;

        // When
        customBluetoothGattCallback.onConnectionStateChange(bluetoothGatt, status, newState);

        // Then
        Mockito.verify(bluetoothGatt, times(1)).discoverServices();
    }

    @Test
    public void testOnConnectionStateChangeDisconnected() throws Exception {
        // Given
        final int status = BluetoothGatt.GATT_SUCCESS;
        final int newState = BluetoothProfile.STATE_DISCONNECTED;

        // When
        customBluetoothGattCallback.onConnectionStateChange(bluetoothGatt, status, newState);

        // Then
        Mockito.verify(bluetoothGatt, times(0)).discoverServices();
        Mockito.verify(bluetoothGatt, times(0)).disconnect();
    }

    @Test
    public void testOnConnectionStateChangeNoGATT() throws Exception {
        // Given
        final int status = BluetoothGatt.GATT_FAILURE;
        final int newState = BluetoothProfile.STATE_DISCONNECTED;

        // When
        customBluetoothGattCallback.onConnectionStateChange(bluetoothGatt, status, newState);

        // Then
        Mockito.verify(bluetoothGatt, times(1)).disconnect();
    }

    private class MyTestModule extends AbstractModule {
        @Override
        protected void configure() {
            // Replace injected class with mock
            bind(BluetoothGatt.class).toInstance(bluetoothGatt);
            bind(BluetoothGattService.class).toInstance(bluetoothGattService);
            bind(BluetoothGattCharacteristic .class).toInstance(bluetoothGattCharacteristic);
            bind(BluetoothGattDescriptor.class).toInstance(descriptor);
        }
    }
}
