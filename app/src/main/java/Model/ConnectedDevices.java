package Model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.Set;

import View.HomePageActivity;

public class ConnectedDevices {

    private static final String TAG = "ConnectedDevices";
    public static boolean BAND_CONNECTED = false;
    public static boolean TO_STOP = false;

    public static BroadcastReceiver checkIfBTIsOn(Context context) {
        BroadcastReceiver mReceiver = new BluetoothReceiver();
        if (!TO_STOP) {
            TO_STOP = true;
            context.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
            //first init
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth
            } else {
                if (mBluetoothAdapter.isEnabled()) {
                    BAND_CONNECTED = BluetoothReceiver.checkIfBandIsConnectedByBT(context);
                    Log.i(TAG, "BT state is STATE_ON");
                    Log.i(TAG, "BAND_CONNECTED state is " + HomePageActivity.BAND_CONNECTED);
                }
                else {
                    BAND_CONNECTED = false;
                    Log.i(TAG, "BT state is STATE_OFF");
                    Log.i(TAG, "BAND_CONNECTED state is " + HomePageActivity.BAND_CONNECTED);
                }
        }

        }
        else {
            Log.i(TAG,"not registering BT Broadcast anymore");
        }

        return mReceiver;

    }


}
