package Model.Metrics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class MetricsBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "MissingMetricsBroadcast";
    private SensorData sensorData;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {

        sensorData = new SensorData();
        sensorData.collectData(context);

    }
}
