package Model.Metrics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Model.Exceptions.ServerFalseException;
import Model.Users.Login;
import Model.Utils.HttpRequests;
import Model.Utils.Urls;

public class MetricsBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "MetricsBroadcast";
    private SensorData sensorData;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"just for debugging");
        int days = 5;
        sensorData = new SensorData();

        HttpRequests httpRequests = HttpRequests.getInstance(context);

        String url = Urls.urlGetMissingDates+Urls.getUrlGetMissingDatesDaysParam+days;
        try {
            JSONObject result = httpRequests.sendGetRequest(url, Login.getToken(context));
            Log.i(TAG,"sent to server");
            System.out.println(result.getString("data"));
            JSONArray resultJSONArray = result.getJSONArray("data");

            sensorData.sensorDataByDates(resultJSONArray, context);

        } catch (ServerFalseException serverFalseException) {
            serverFalseException.printStackTrace();
            Log.i(TAG,"problem in asking if user has been answered to server "+ serverFalseException.getLocalizedMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
