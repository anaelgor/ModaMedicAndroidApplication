package Model.Metrics;

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
        Weather weather = new Weather(context);
        weather.extractDataForWeather();

        Log.i(TAG,"Location tracker via broadcast");

    }
}
