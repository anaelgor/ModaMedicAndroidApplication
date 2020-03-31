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

import Controller.Urls;

import static com.google.android.gms.fitness.data.Field.FIELD_CALORIES;

public class CaloriesGoogleFit {

    private static final String TAG = "CaloriesGoogleFit";
    private float calories = 0;
    private boolean calculated = false;

    public CaloriesGoogleFit() {
    }

    public void getDataFromPrevDay(Context context, GoogleSignInOptionsExtension fitnessOptions){


        GoogleSignInAccount googleSignInAccount =
                GoogleSignIn.getAccountForExtension(context, fitnessOptions);

        Calendar midnight = Calendar.getInstance();

        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);

        long endTime = System.currentTimeMillis();
        long startTime = midnight.getTimeInMillis();

        /**
         * Calories
         */

        DataReadRequest request = new DataReadRequest.Builder()
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .read(DataType.TYPE_CALORIES_EXPENDED)
                .build();

        HistoryClient historyClient = Fitness.getHistoryClient(context, googleSignInAccount);
        Task<DataReadResponse> task =historyClient.readData(request);

        task.addOnSuccessListener(response -> {

            DataSet dataset = response.getDataSets().get(0);

            for (DataPoint datapoint:
                 dataset.getDataPoints()) {
                calories += datapoint.getValue(FIELD_CALORIES).asFloat();
            }

            calculated = true;

            Log.i("Total cal of the day:", "************ " + Float.toString(calories) + " *************");
        })
        .addOnFailureListener(response -> {

            calculated = true;

            Log.e(TAG, "Could not extract calories data.");
        });

    }

    public JSONObject makeBodyJson(){
        JSONObject json = new JSONObject();
        try {
            json.put("ValidTime", System.currentTimeMillis());
            json.put("Data", this.calories);
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
            httpRequests.sendPostRequest(makeBodyJson(), Urls.urlPostCalories);
        }
        catch (Exception e){
            Log.e(TAG, "No data in calories.");
            e.printStackTrace();
        }
    }
}
