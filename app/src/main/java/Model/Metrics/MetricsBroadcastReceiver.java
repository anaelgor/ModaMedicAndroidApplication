package Model.Metrics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MetricsBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "MetricsBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"just for debugging");

    }
}
