package Model.Metrics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import Model.Utils.Constants;

public class LocationBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "LocationBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.sharedPreferencesName,Context.MODE_PRIVATE);

        //todo: do your metric job here and save it on sharedPref by using this:
        // sharedPref.edit().putString("location",location).apply();


        Log.i(TAG,"just for debugging");

    }
}
