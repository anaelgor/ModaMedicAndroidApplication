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
    private static final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        HomePageActivity.BAND_CONNECTED = false;
                        Log.i(TAG, "onReceive: BT state is STATE_OFF");
                        Log.i(TAG, "onReceive: BAND_CONNECTED state is " + HomePageActivity.BAND_CONNECTED);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.i(TAG, "onReceive: BT state is STATE_TURNING_OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        HomePageActivity.BAND_CONNECTED = checkIfBandIsConnectedByBT(context);
                        Log.i(TAG, "onReceive: BT state is STATE_ON");
                        Log.i(TAG, "onReceive: BAND_CONNECTED state is " + HomePageActivity.BAND_CONNECTED);
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.i(TAG, "onReceive: BT state is STATE_TURNING_ON");
                        break;
                }
            }
        }
    };

    public static boolean checkIfBandIsConnectedByBT(Context context) {
        BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                int deviceClass = device.getBluetoothClass().getDeviceClass();
                if (deviceClass == BluetoothClass.Device.WEARABLE_WRIST_WATCH || deviceClass == BluetoothClass.Device.Major.UNCATEGORIZED) {
                    Log.d("Found", "Paired wearable: " + device.getName() + ", with address: " + device.getAddress());
                    return true;
                }
            }
        }
        return false;
    }

    public static void checkIfBTIsOn(Context context) {
        context.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        //first init
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                HomePageActivity.BAND_CONNECTED = checkIfBandIsConnectedByBT(context);
                Log.i(TAG, "BT state is STATE_ON");
                Log.i(TAG, "BAND_CONNECTED state is " + HomePageActivity.BAND_CONNECTED);
            }
            else {
                HomePageActivity.BAND_CONNECTED = false;
                Log.i(TAG, "BT state is STATE_OFF");
                Log.i(TAG, "BAND_CONNECTED state is " + HomePageActivity.BAND_CONNECTED);
            }
        }


    }


}
