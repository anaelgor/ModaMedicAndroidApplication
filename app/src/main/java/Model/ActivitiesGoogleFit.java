package Model;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.SessionReadResponse;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ActivitiesGoogleFit {

    private List activityArray;
    private JSONObject json;

    public ActivitiesGoogleFit() {
        activityArray = new ArrayList();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void extractActivityData(Context context){

        long endTime = System.currentTimeMillis();
        long startTime = endTime-86400000;

        // Note: The android.permission.ACTIVITY_RECOGNITION permission is
        // required to read DataType.TYPE_ACTIVITY_SEGMENT
        SessionReadRequest request = new SessionReadRequest.Builder()
                .readSessionsFromAllApps()
                // Activity segment data is required for details of the fine-
                // granularity sleep, if it is present.
                .read(DataType.TYPE_ACTIVITY_SEGMENT)
                .setTimeInterval(startTime, System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .build();

        Task<SessionReadResponse> task = Fitness.getSessionsClient(context,
                GoogleSignIn.getLastSignedInAccount(context))
                .readSession(request);

        task.addOnSuccessListener(response -> {

            List<Session> sleepSessions = response.getSessions().stream()
                    .collect(Collectors.toList());

            for (Session session : sleepSessions) {
                Log.d("AppName", String.format("Activities between %d and %d",
                        session.getStartTime(TimeUnit.MILLISECONDS),
                        session.getEndTime(TimeUnit.MILLISECONDS)));


                // If the sleep session has finer granularity sub-components, extract them:
                List<DataSet> dataSets = response.getDataSet(session);
                for (DataSet dataSet : dataSets) {
                    for (DataPoint point : dataSet.getDataPoints()) {
                        String activity = point.getValue(Field.FIELD_ACTIVITY).asActivity();
                        long start = point.getStartTime(TimeUnit.MILLISECONDS);
                        long end = point.getEndTime(TimeUnit.MILLISECONDS);
                        Log.d("AppName",
                                String.format("\t* %s between %d and %d", activity, start, end));

                        //ignore sleeping data
                        if (activity.equals("sleep.deep") || activity.equals("sleep.light"))
                            continue;

                        JSONObject json = new JSONObject();
                        try {
                            json.put("StartTime", start);
                            json.put("EndTime", end);
                            json.put("State", activity);

                            activityArray.add(json);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
            makeBodyJson();
        });
    }

    public void makeBodyJson(){
        JSONObject json = new JSONObject();
        String userID = "1111111111";
        try {
            json.put("UserID", userID);
            json.put("Activity", activityArray);
            json.put("ValidateTime", System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.json = json;
    }

    public JSONObject getJson(){
        return this.json;
    }

}