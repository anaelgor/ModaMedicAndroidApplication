package Model.Metrics;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

public class LocationBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "LocationBroadcast";
    LocationManager locationManager;


    @Override
    public void onReceive(Context context, Intent intent) {

        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        Weather weather = new Weather(this.locationManager, context);

        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() < start + 10000); //wait 10 seconds until location is updating

        Log.i(TAG,"Location tracker via broadcast");

    }
}
