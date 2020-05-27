package Model.Metrics.GoogleFit;

import android.annotation.TargetApi;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import Model.Metrics.DataSender;
import Model.Users.Login;
import Model.Utils.HttpRequests;
import Model.Utils.Urls;

public class SleepGoogleFit implements DataSender {

    private List sleepDataArray;
    private long totalSleepTime;
    private JSONObject json;
    private static final String TAG = "SleepGoogleFit";
    private int extractionCounter = 0;

    public SleepGoogleFit() {
        sleepDataArray = new ArrayList();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void extractSleepData(Context context) {

        extractionCounter ++;

        Calendar midnight = Calendar.getInstance();

        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);

        long startTime = midnight.getTimeInMillis() - 14400000; //start from 20:00 of prev day

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

            // Filter the resulting list of sessions to just those that are sleep.
            List<Session> sleepSessions = response.getSessions().stream()
                    //.filter(s -> s.getActivity().equals(FitnessActivities.SLEEP))
                    .collect(Collectors.toList());

            if (sleepSessions.size() == 0){
                if (extractionCounter < 3) {
                    extractSleepData(context);
                }
                return;
            }

            extractionCounter = 0;

            for (Session session : sleepSessions) {

                this.totalSleepTime = session.getEndTime(TimeUnit.MILLISECONDS) - session.getStartTime(TimeUnit.MILLISECONDS);

                // If the sleep session has finer granularity sub-components, extract them:
                List<DataSet> dataSets = response.getDataSet(session);
                for (DataSet dataSet : dataSets) {
                    for (DataPoint point : dataSet.getDataPoints()) {
                        // The Activity defines whether this segment is light, deep, REM or awake.
                        String sleepStage = point.getValue(Field.FIELD_ACTIVITY).asActivity();

                        //ignore non sleeping data
                        if (!sleepStage.equals("sleep.deep") && !sleepStage.equals("sleep.light"))
                            continue;


                        int stateAsInt = point.getValue(Field.FIELD_ACTIVITY).asInt();

                        switch (stateAsInt) {
                            case 109:
                                sleepStage = "SLEEP_LIGHT";
                                break;
                            case 110:
                                sleepStage = "SLEEP_DEEP";
                                break;
                            case 112:
                                sleepStage = "SLEEP_AWAKE";
                                break;
                        }

                        long start = point.getStartTime(TimeUnit.MILLISECONDS);
                        long end = point.getEndTime(TimeUnit.MILLISECONDS);
                        Log.d(TAG, String.format("\t* %s between %d and %d", sleepStage, start, end));

                        JSONObject json = new JSONObject();
                        try {
                            json.put("StartTime", start);
                            json.put("EndTime", end);
                            json.put("State", sleepStage);
                            if (!this.sleepDataArray.contains(json))
                                this.sleepDataArray.add(json);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            // create a json for sending data to server
            makeBodyJson(System.currentTimeMillis());
            sendDataToServer(HttpRequests.getInstance(context));
        })
                .addOnFailureListener(response -> {
                    Log.e(TAG, "extractSleepData: failed to extract sleeping data");
                    if (extractionCounter < 3){
                        Log.i(TAG, "extractSleepData: retry extract sleeping data. counter value = " + extractionCounter);
                        extractSleepData(context);
                    }
                    else{
                        extractionCounter = 0;
                    }
                });

    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void extractSleepDataByDate(Context context, long startTime, long endTime) {
        try {
            Log.i(TAG, "extractSleepDataByDate: got startTime = " + Long.toString(startTime )+ ", endTime = " + Long.toString(endTime));

            extractionCounter ++;

            // Note: The android.permission.ACTIVITY_RECOGNITION permission is
            // required to read DataType.TYPE_ACTIVITY_SEGMENT
            SessionReadRequest request = new SessionReadRequest.Builder()
                    .readSessionsFromAllApps()
                    // Activity segment data is required for details of the fine-
                    // granularity sleep, if it is present.
                    .read(DataType.TYPE_ACTIVITY_SEGMENT)
                    .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                    .build();

            Task<SessionReadResponse> task = Fitness.getSessionsClient(context,
                    GoogleSignIn.getLastSignedInAccount(context))
                    .readSession(request);

            task.addOnSuccessListener(response -> {

                // Filter the resulting list of sessions to just those that are sleep.
                List<Session> sleepSessions = response.getSessions().stream()
                        //.filter(s -> s.getActivity().equals(FitnessActivities.SLEEP))
                        .collect(Collectors.toList());

                if (sleepSessions.size() == 0){
                    if (extractionCounter < 3) {
                        extractSleepDataByDate(context, startTime, endTime);
                    }
                    return;
                }

                extractionCounter = 0;

                for (Session session : sleepSessions) {

                    this.totalSleepTime = session.getEndTime(TimeUnit.MILLISECONDS) - session.getStartTime(TimeUnit.MILLISECONDS);

                    // If the sleep session has finer granularity sub-components, extract them:
                    List<DataSet> dataSets = response.getDataSet(session);
                    for (DataSet dataSet : dataSets) {
                        for (DataPoint point : dataSet.getDataPoints()) {
                            // The Activity defines whether this segment is light, deep, REM or awake.
                            String sleepStage = point.getValue(Field.FIELD_ACTIVITY).asActivity();

                            //ignore non sleeping data
                            if (!sleepStage.equals("sleep.deep") && !sleepStage.equals("sleep.light"))
                                continue;


                            int stateAsInt = point.getValue(Field.FIELD_ACTIVITY).asInt();

                            switch (stateAsInt) {
                                case 109:
                                    sleepStage = "SLEEP_LIGHT";
                                    break;
                                case 110:
                                    sleepStage = "SLEEP_DEEP";
                                    break;
                                case 112:
                                    sleepStage = "SLEEP_AWAKE";
                                    break;
                            }

                            long start = point.getStartTime(TimeUnit.MILLISECONDS);
                            long end = point.getEndTime(TimeUnit.MILLISECONDS);
                            Log.d(TAG, String.format("\t* %s between %d and %d", sleepStage, start, end));

                            JSONObject json = new JSONObject();
                            try {
                                json.put("StartTime", start);
                                json.put("EndTime", end);
                                json.put("State", sleepStage);
                                if (!this.sleepDataArray.contains(json))
                                    this.sleepDataArray.add(json);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                // create a json for sending data to server
                makeBodyJson(startTime);
                sendDataToServer(HttpRequests.getInstance(context));

            })
                    .addOnFailureListener(response -> {
                        Log.e(TAG, "extractSleepData: failed to extract sleeping data");
                        if (extractionCounter < 3){
                            Log.i(TAG, "extractSleepData: retry extract sleeping data. counter value = " + Integer.toString(extractionCounter));
                            extractSleepDataByDate(context, startTime, endTime);
                        }
                        else{
                            extractionCounter = 0;
                        }
                    });
        }
        catch (Exception e) {
            Log.e(TAG,"Error occured: ");
            e.printStackTrace();
        }



    }

    public void makeBodyJson(long time) {
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray(sleepDataArray);
        try {
            json.put("ValidTime", time);
            json.put("Data", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.json = json;
    }

    public JSONObject getJson() {
        return this.json;
    }

    public void clearJson() {
        this.sleepDataArray = new ArrayList();
        this.json = null;
    }

    public void sendDataToServer(HttpRequests httpRequests) {

        if (sleepDataArray.size() == 0){
            Log.e(TAG, "No data in sleep.");
            return;
        }

        try{
            httpRequests.sendPostRequest(getJson(), Urls.urlPostSleep, Login.getToken(HttpRequests.getContext()));
            clearJson();
        }
        catch (Exception e){
            Log.e(TAG, "No data in sleep.");
            e.printStackTrace();
        }
    }

}
