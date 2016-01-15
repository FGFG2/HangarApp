package com.tobyrich.dev.hangarapp.activities.fragments;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.tobyrich.dev.hangarapp.R;

import com.tobyrich.dev.hangarapp.lib.connection.events.ConnectEvent;
import com.tobyrich.dev.hangarapp.lib.connection.events.ConnectResult;
import com.tobyrich.dev.hangarapp.lib.connection.events.ScanResult;
import com.tobyrich.dev.hangarapp.lib.connection.events.ScanEvent;
import com.tobyrich.dev.hangarapp.lib.utils.PlaneState;

import de.greenrobot.event.EventBus;
import roboguice.fragment.provided.RoboFragment;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
/**
 * A fragment which holds the connection button, which enables the app to connect and disconnect to
 * a device.
 */
@ContentView(R.layout.fragment_connection)
public class ConnectionFragment extends RoboFragment implements View.OnClickListener{

    private static final String TAG = "tr.fragment.connection";

    ToggleButton tbConnect;
    ProgressBar progressBar;

    /**
     * Makes the fragment visible to the user (based on its containing activity being started).
     * Registers the ConnectionFragment to receive events.
     */
    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    /**
     * fragment is no longer visible to the user either because its activity is being stopped or a
     * fragment operation is modifying it in the activity.
     * Unregisters the ConnectionFragment to receive events.
     */
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /**
     * Creates and returns the view hierarchy associated with the fragment.
     * Overrides the onCreateView method of the super class. Adds a {@link ToggleButton}, which will
     * start to find BLE devices on click. Adds a {@link ProgressBar}, which will displayed when
     * the ToggleButton gets clicked.
     *
     * @param inflater {@link LayoutInflater}
     * @param container {@link ViewGroup}
     * @param savedInstanceState {@link Bundle}
     * @return the {@link View}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(
                R.layout.fragment_connection, container, false);

        tbConnect = (ToggleButton) view.findViewById(R.id.connect_button);
        tbConnect.setOnClickListener(this);
        tbConnect.setChecked(PlaneState.getInstance().isConnected());
        tbConnect.setEnabled(true);

        progressBar = (ProgressBar) view.findViewById(R.id.connection_ProgressBar);
        progressBar.setVisibility(View.INVISIBLE);
        return view;
    }

    /**
     * Method gets called on every {@link ScanResult} event. Displays a "No Device Found" dialog, if there weren't
     * any devices found. If there were divices found, a dialog is displayed, which shows the
     * devices. When the user clicks on one devices, a new {@link ConnectEvent} is fired.
     *
     * @param evt {@link ScanResult}
     */
    public void onEventMainThread(final ScanResult evt){
        Log.d(TAG, "receive: ScanResult");
        if(evt.getResult().size()>0) {

            progressBar.setVisibility(View.GONE);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Make your selection");
            String[] devices = new String[evt.getResult().size()];
            for(int i=0;i<evt.getResult().size();i++) {
                BluetoothDevice dev =  evt.getResult().get(i);
                devices[i] =dev.getName() + " ("+dev.getAddress()+")";
            }
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    tbConnect.setChecked(false);
                    tbConnect.setEnabled(true);
                }
            });
            builder.setItems(devices, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    EventBus.getDefault().post(new ConnectEvent(evt.getResult().get(item)));
                    //workaround onEvent(ConnectResult evt)
                    //tbConnect.setChecked(true);
                    //workaround onEvent(ConnectResult evt)
                    //tbConnect.setEnabled(true);
                    Toast.makeText(getActivity(), "Start connecting...", Toast.LENGTH_SHORT).show();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }else{
            tbConnect.setChecked(false);
            tbConnect.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "No Device Found!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method gets called on every {@link ConnectResult} event. Enables the tbConnect{@link ToggleButton}
     * and sets the checked state based on the ConnectResult.
     *
     * @param evt {@link ConnectResult}
     */
    public void onEventMainThread(ConnectResult evt){
        boolean bool = evt.getState();

        tbConnect.setEnabled(true);
        if(!bool){
            Toast.makeText(getActivity(), "Connection lost!", Toast.LENGTH_SHORT).show();
        }
        tbConnect.setChecked(bool);
    }

    /**
     * Method gets called when the tbConnect{@link ToggleButton} gets clicked. If the app is already connected
     * to a device, it will disconnect the connection. If the app isn't connected to a device, a
     * {@link ScanEvent} is fired.
     *
     * @param view {@link View}
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.connect_button:
                if(PlaneState.getInstance().isConnected()) {
                    tbConnect.setChecked(false);
                    EventBus.getDefault().post(new ScanEvent(false));
                    Log.d(TAG, "send: ScanEvent-disconnect");
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    tbConnect.setChecked(false);
                    tbConnect.setEnabled(false);
                    Log.d(TAG, "send: ScanEvent-connect");
                    Toast.makeText(getActivity(), "Start scanning...", Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post(new ScanEvent(true));
                }
                break;
            default:
                break;
        }
    }
}
