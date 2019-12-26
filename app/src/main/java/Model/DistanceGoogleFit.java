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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.google.android.gms.fitness.data.DataType.TYPE_DISTANCE_DELTA;
import static com.google.android.gms.fitness.data.Field.FIELD_DISTANCE;
import static java.util.concurrent.TimeUnit.SECONDS;

public class DistanceGoogleFit {
    public DistanceGoogleFit() {
    }
    public float getDataFromPrevDay(Context context, GoogleSignInOptionsExtension fitnessOptions){


        GoogleSignInAccount googleSignInAccount =
                GoogleSignIn.getAccountForExtension(context, fitnessOptions);

        /**
         * Distance
         */
        Task<DataSet> response =
                Fitness.getHistoryClient(context, googleSignInAccount)
                        .readDailyTotalFromLocalDevice(TYPE_DISTANCE_DELTA);
        DataSet totalSet = null;
        try {
            totalSet = Tasks.await(response, 30, SECONDS);
        } catch (Exception e) {
            Log.i("Dist extraction error:", e.toString());
            e.printStackTrace();
        }
        float dist = totalSet.isEmpty() // TYPE_DISTANCE_DELTA
                ? 0
                : totalSet.getDataPoints().get(0).getValue(FIELD_DISTANCE).asFloat();


        Log.i("total dist of the day:", "************ " +Float.toString(dist)  + " *************");

        return dist;

    }
}
