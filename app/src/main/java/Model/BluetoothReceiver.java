package Model;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BluetoothReceiver extends BroadcastReceiver {

    public static final String TAG = "BluetoothReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    ConnectedDevices.BAND_CONNECTED = false;
                    Log.i(TAG, "onReceive: BT state is STATE_OFF");
                    Log.i(TAG, "onReceive: BAND_CONNECTED state is " + ConnectedDevices.BAND_CONNECTED);
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Log.i(TAG, "onReceive: BT state is STATE_TURNING_OFF");
                    break;
                case BluetoothAdapter.STATE_ON:
                    ConnectedDevices.BAND_CONNECTED = checkIfBandIsConnectedByBT(context);
                    Log.i(TAG, "onReceive: BT state is STATE_ON");
                    Log.i(TAG, "onReceive: BAND_CONNECTED state is " + ConnectedDevices.BAND_CONNECTED);
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    Log.i(TAG, "onReceive: BT state is STATE_TURNING_ON");
                    break;
            }
        }
    }

    public static boolean checkIfBandIsConnectedByBT(Context context) {
        BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();

//        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
//        if (pairedDevices.size() > 0) {
//            for (BluetoothDevice device : pairedDevices) {
//                int deviceClass = device.getBluetoothClass().getDeviceClass();
//                if (deviceClass == BluetoothClass.Device.WEARABLE_WRIST_WATCH || deviceClass == BluetoothClass.Device.Major.UNCATEGORIZED) {
//                    Log.d("Found", "Paired wearable: " + device.getName() + ", with address: " + device.getAddress());
//                    return true;
//                }
//            }
//        }
        return true;
    }

}
