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

@ContentView(R.layout.fragment_connection)
public class ConnectionFragment extends RoboFragment implements View.OnClickListener{

    private static final String TAG = "tr.fragment.connection";

    //@InjectView(R.id.connect_button)
    ToggleButton tbConnect;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        EventBus.getDefault().register(this);

        View view = inflater.inflate(
                R.layout.fragment_connection, container, false);

        tbConnect = (ToggleButton) view.findViewById(R.id.connect_button);
        tbConnect.setOnClickListener(this);
        tbConnect.setChecked(PlaneState.getInstance().isConnected());
        tbConnect.setEnabled(true);

        return view;
    }

    /*
  * TODO Test Bluetooth
  */
    public void onEvent(final ScanResult evt){
        Log.d(TAG, "receive: ScanResult");
        if(evt.getResult().size()>0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Make your selection");
            String[] devices = new String[evt.getResult().size()];
            for(int i=0;i<evt.getResult().size();i++) {
                BluetoothDevice dev =  evt.getResult().get(i);
                devices[i] =dev.getName() + " ("+dev.getAddress()+")";
            }
            builder.setItems(devices, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    EventBus.getDefault().post(new ConnectEvent(evt.getResult().get(item)));
                    //workaround onEvent(ConnectResult evt)
                    tbConnect.setChecked(true);
                    //workaround onEvent(ConnectResult evt)
                    tbConnect.setEnabled(true);
                    Toast.makeText(getActivity(), "Start conneting...", Toast.LENGTH_SHORT).show();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }else{
            //workaround onEvent(ConnectResult evt)
            tbConnect.setChecked(false);
            tbConnect.setEnabled(true);
            Toast.makeText(getActivity(), "No Device Found!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onEvent(ConnectResult evt){
        Log.d(TAG, "receive: ConnectResult");
        /*
        Did not work, no idea
        tbConnect.setChecked(evt.getState());
        tbConnect.setEnabled(true);
        */
        if(!evt.getState()){
            Toast.makeText(getActivity(), "Conneting lost!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.connect_button:
                if(PlaneState.getInstance().isConnected()) {
                    tbConnect.setChecked(false);
                    EventBus.getDefault().post(new ScanEvent(false));
                    Log.d(TAG, "send: ScanEvent-disconnect");
                }else{
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
