package Model;


import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.fitness.data.Field.FIELD_STEPS;

public class StepsGoogleFit {

    private static final String TAG = "StepsGoogleFit";
    private int steps = 0;
    private boolean calculated = false;

    public StepsGoogleFit() {
    }

    public void getDataFromPrevDay(Context context, GoogleSignInOptionsExtension fitnessOptions) {


        GoogleSignInAccount googleSignInAccount =
                GoogleSignIn.getAccountForExtension(context, fitnessOptions);

        /**
         * steps
         */
        Calendar midnight = Calendar.getInstance();

        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);

        long endTime = System.currentTimeMillis();
        long startTime = midnight.getTimeInMillis();

        DataReadRequest request = new DataReadRequest.Builder()
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .read(DataType.TYPE_STEP_COUNT_DELTA)
                .build();

        HistoryClient historyClient = Fitness.getHistoryClient(context, googleSignInAccount);
        Task<DataReadResponse> task = historyClient.readData(request); //computed from midnight of the current day on the device's current timezone

        task.addOnSuccessListener(response -> {
            DataSet dataset = response.getDataSets().get(0);

            for (DataPoint datapoint :
                    dataset.getDataPoints()) {
                steps += datapoint.getValue(FIELD_STEPS).asInt();
            }

            calculated = true;

            Log.i("Total steps of the day:", "************ " + Integer.toString(steps) + " *************");
        })
                .addOnFailureListener(response -> {

                    calculated = true;

                    Log.e(TAG, "Could not extract steps data.");
                });

    }

    public JSONObject makeBodyJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("ValidTime", System.currentTimeMillis());
            json.put("Data", this.steps);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public boolean hadBeenCalc() {
        return calculated;
    }

    public void sendDataToServer(HttpRequests httpRequests) {
        try {
            httpRequests.sendPostRequest(makeBodyJson(), Urls.urlPostSteps);
        }
        catch (Exception e){
            Log.e(TAG, "No data in steps.");
            e.printStackTrace();
        }
    }
}
