package Model;


import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import org.json.JSONException;
import org.json.JSONObject;

import static com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA;
import static com.google.android.gms.fitness.data.Field.FIELD_STEPS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class StepsGoogleFit {

    public StepsGoogleFit() {
    }

    public int getDataFromPrevDay(Context context, GoogleSignInOptionsExtension fitnessOptions){


        GoogleSignInAccount googleSignInAccount =
                GoogleSignIn.getAccountForExtension(context, fitnessOptions);

        /**
         * steps
         */
        Task<DataSet> response2 =
                Fitness.getHistoryClient(context, googleSignInAccount)
                        .readDailyTotalFromLocalDevice(TYPE_STEP_COUNT_DELTA); //computed from midnight of the current day on the device's current timezone
        DataSet totalSet = null;
        try {
            totalSet = Tasks.await(response2, 30, SECONDS);
        } catch (Exception e) {
            Log.i("Steps extraction error:", e.toString());
            e.printStackTrace();
        }
        int steps = totalSet.isEmpty() // TYPE_STEPS_DELTA
                ? 0
                : totalSet.getDataPoints().get(0).getValue(FIELD_STEPS).asInt();

        Log.i("Total steps of the day:", "************ " + Integer.toString(steps) + " *************");
        return steps;
    }

    public JSONObject makeBodyJson(int steps, String userID){
        userID = "111111111";
        JSONObject json = new JSONObject();
        try {
            json.put("Data", steps);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

}
