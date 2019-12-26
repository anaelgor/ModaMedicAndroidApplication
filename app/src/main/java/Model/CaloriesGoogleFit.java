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

import static com.google.android.gms.fitness.data.DataType.TYPE_CALORIES_EXPENDED;
import static com.google.android.gms.fitness.data.DataType.TYPE_DISTANCE_DELTA;
import static com.google.android.gms.fitness.data.Field.FIELD_CALORIES;
import static com.google.android.gms.fitness.data.Field.FIELD_DISTANCE;
import static java.util.concurrent.TimeUnit.SECONDS;

public class CaloriesGoogleFit {
    public CaloriesGoogleFit() {
    }
    public float getDataFromPrevDay(Context context, GoogleSignInOptionsExtension fitnessOptions){


        GoogleSignInAccount googleSignInAccount =
                GoogleSignIn.getAccountForExtension(context, fitnessOptions);

        /**
         * Calories
         */
        Task<DataSet> response3 =
                Fitness.getHistoryClient(context, googleSignInAccount)
                        .readDailyTotalFromLocalDevice(TYPE_CALORIES_EXPENDED);
        DataSet totalSet3 = null;
        try {
            totalSet3 = Tasks.await(response3, 30, SECONDS);
        } catch (Exception e) {
            Log.i("Cal extraction error:", e.toString());
            e.printStackTrace();
        }

        float calories = totalSet3.isEmpty()
                ? 0
                : totalSet3.getDataPoints().get(0).getValue(FIELD_CALORIES).asFloat();


        Log.i("total cal of the day:", "************ " +Float.toString(calories)  + " *************");


        return calories;
    }
}