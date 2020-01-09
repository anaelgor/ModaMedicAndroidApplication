package Model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getTimeInstance;
import static java.util.concurrent.TimeUnit.SECONDS;

public class SleepGoogleFit {
    public SleepGoogleFit() {
    }

    private static List<Tuple<Integer,Tuple<Long,Long>>>sleepSegments;
    private static JSONObject sleepPost;
    public static final String TAG = "SleepData";
    private static Boolean hasSleep = false;

    private static long lightSleepTotal = 0;
    private static long deepSleepTotal = 0;
    private static long awakeTotal = 0;



    /**
     * Asynchronous task to read the sleep data. When the task succeeds, it will print out the data.
     */
    public Task<DataReadResponse> readSleepData(Context context, GoogleSignInOptionsExtension fitnessOptions) {
        DataReadRequest readRequest = querySleepData();

        GoogleSignInAccount googleSignInAccount =
                GoogleSignIn.getAccountForExtension(context, fitnessOptions);

        return Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context))
                .readData(readRequest)
                .addOnSuccessListener(
                        new OnSuccessListener<DataReadResponse>() {
                            @Override
                            public void onSuccess(DataReadResponse dataReadResult) {
                                try {
                                    makeSleepSegments(dataReadResult);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "There was a problem reading the data.", e);
                                    }
                                });
    }

    /** Returns a {@link DataReadRequest} for all sleep count changes in the past day. */
    public static DataReadRequest querySleepData() {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 day before this moment.
        long endTime = System.currentTimeMillis();
        long startTime = endTime-86400000 * 7;

        java.text.DateFormat dateFormat = getDateInstance();
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));

        // [END build_read_data_request]

        return new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .bucketByTime(1, TimeUnit.MINUTES)
                .build();
    }

    @SuppressLint("DefaultLocale")
    public static void makeSleepSegments(DataReadResponse dataReadResult) throws JSONException {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.

        DateFormat dateFormat = getTimeInstance();
        if (dataReadResult.getBuckets().size() > 0) {
            sleepSegments=new LinkedList<>();

            sleepPost = new JSONObject();
            JSONArray sleepSegmentss = new JSONArray();

            int lastSleepStep=0;
            long start=0;
            long end=0;
            boolean find=false;


            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    for (DataPoint dp : dataSet.getDataPoints()) {
                        for (Field field : dp.getDataType().getFields()) {
                            if(field.getName().equals("activity")&&
                                    (dp.getValue(field).asInt()==(109)||//light
                                            dp.getValue(field).asInt()==(110)||//deep
                                            dp.getValue(field).asInt()==(112))){//awake
                                find=!find;
                                if(dp.getValue(field).asInt()==lastSleepStep){
                                    end=dp.getEndTime(TimeUnit.MILLISECONDS);
                                }else {
                                    if(lastSleepStep!=0){
                                        end=dp.getStartTime(TimeUnit.MILLISECONDS);
                                        JSONObject segment = new JSONObject();
                                        switch (lastSleepStep){
                                            case 109:
                                                segment.put("Type", "Light");

                                                lightSleepTotal += (end - start);

                                                break;
                                            case 110:
                                                segment.put("Type", "Deep");

                                                deepSleepTotal += (end - start);

                                                break;
                                            case 112:
                                                segment.put("Type", "Awake");

                                                awakeTotal += (end - start);

                                                break;
                                        }
                                        segment.put("StartTime",start);
                                        segment.put("EndTime",end);

                                        sleepSegmentss.put(segment);


                                        sleepSegments.add(new Tuple<Integer,Tuple<Long,Long>>(lastSleepStep,
                                                new Tuple<Long,Long>(start,end)));
                                        Log.i(TAG, "Type: " + lastSleepStep);
                                        Log.i(TAG, "\tStart: " + dateFormat.format(start));
                                        Log.i(TAG, "\tEnd: " + dateFormat.format(end));
                                    }
                                    lastSleepStep=dp.getValue(field).asInt();
                                    start=dp.getStartTime(TimeUnit.MILLISECONDS);
                                    end=dp.getEndTime(TimeUnit.MILLISECONDS);
                                }
                            }
                        }
                        if(find){
                            find=!find;
                            break;
                        }
                    }
                }
            }

            if(!find){

                JSONObject segment = new JSONObject();
                switch (lastSleepStep){
                    case 109:
                        segment.put("Type", "Light");
                        lightSleepTotal += (end - start);
                        break;
                    case 110:
                        segment.put("Type", "Deep");
                        deepSleepTotal += (end - start);
                        break;
                    case 112:
                        segment.put("Type", "Awake");
                        awakeTotal += (end - start);
                        break;
                }
                segment.put("StartTime",start);
                segment.put("EndTime",end);

                sleepSegmentss.put(segment);

                sleepSegments.add(new Tuple<Integer,Tuple<Long,Long>>(lastSleepStep,
                        new Tuple<Long,Long>(start,end)));
                Log.i(TAG, "Type: " + lastSleepStep);
                Log.i(TAG, "\tStart: " + dateFormat.format(start));
                Log.i(TAG, "\tEnd: " + dateFormat.format(end));
            }

            if(sleepSegmentss.length()==0)
                return;

            hasSleep = true;

            sleepPost.put("Sleep",sleepSegmentss);

        }

        int lightSleepSeconds = (int) (lightSleepTotal / 1000) % 60 ;
        int lightSleepMinutes = (int) ((lightSleepTotal / (1000*60)) % 60);
        int lightSleepHours   = (int) ((lightSleepTotal / (1000*60*60)) % 24);

        int deepSleepSeconds = (int) (deepSleepTotal / 1000) % 60 ;
        int deepSleepMinutes = (int) ((deepSleepTotal / (1000*60)) % 60);
        int deepSleepHours   = (int) ((deepSleepTotal / (1000*60*60)) % 24);

        int awakeSeconds = (int) (awakeTotal / 1000) % 60 ;
        int awakeMinutes = (int) ((awakeTotal / (1000*60)) % 60);
        int awakeHours   = (int) ((awakeTotal / (1000*60*60)) % 24);

        Log.i(TAG, "Total light sleep time:" + lightSleepHours + ":" + lightSleepMinutes + ":" + lightSleepSeconds);
        Log.i(TAG, "Total deep sleep time:" + deepSleepHours + ":" + deepSleepMinutes + ":" + deepSleepSeconds);
        Log.i(TAG, "Total awake time:" + awakeHours + ":" + awakeMinutes + ":" + awakeSeconds);

    }

    public JSONObject makeJsonBody(String userID){
        JSONObject json = new JSONObject();
        try {
            json.put("UserName", userID);
            json.put("LightSleep",lightSleepTotal);
            json.put("DeepSleep", deepSleepTotal);
            json.put("Awake", awakeTotal);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static class Tuple<X, Y> {
        public final X x;
        public final Y y;
        public Tuple(X x, Y y) {
            this.x = x;
            this.y = y;
        }
    }

}
