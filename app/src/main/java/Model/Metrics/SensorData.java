package Model.Metrics;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Iterator;

import Model.Metrics.GoogleFit.ActivitiesGoogleFit;
import Model.Metrics.GoogleFit.CaloriesGoogleFit;
import Model.Metrics.GoogleFit.DistanceGoogleFit;
import Model.Metrics.GoogleFit.SleepGoogleFit;
import Model.Metrics.GoogleFit.StepsGoogleFit;
import Model.Utils.Configurations;
import Model.Utils.HttpRequests;
import Model.Utils.TimeUtils;

import static android.content.Context.ALARM_SERVICE;
import static com.google.android.gms.fitness.data.DataType.TYPE_ACTIVITY_SEGMENT;
import static com.google.android.gms.fitness.data.DataType.TYPE_CALORIES_EXPENDED;
import static com.google.android.gms.fitness.data.DataType.TYPE_DISTANCE_DELTA;
import static com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA;

public class SensorData {

    private StepsGoogleFit stepsGoogleFit;
    private DistanceGoogleFit distanceGoogleFit;
    private CaloriesGoogleFit caloriesGoogleFit;
    private SleepGoogleFit sleepGoogleFit;
    private ActivitiesGoogleFit activitiesGoogleFit;
    private Weather gpsLocationListener;
    private Activity activity;

    private static final String TAG = "SensorData";

    public SensorData(Activity activity) {
        this.activity = activity;
        this.stepsGoogleFit = new StepsGoogleFit();
        this.distanceGoogleFit = new DistanceGoogleFit();
        this.caloriesGoogleFit = new CaloriesGoogleFit();
        this.sleepGoogleFit = new SleepGoogleFit();
        this.activitiesGoogleFit = new ActivitiesGoogleFit();
        this.gpsLocationListener = new Weather(activity);
    }

    public SensorData(){
        this.stepsGoogleFit = new StepsGoogleFit();
        this.distanceGoogleFit = new DistanceGoogleFit();
        this.caloriesGoogleFit = new CaloriesGoogleFit();
        this.sleepGoogleFit = new SleepGoogleFit();
        this.activitiesGoogleFit = new ActivitiesGoogleFit();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void collectData(Context context) {
        GoogleSignInOptionsExtension fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                        .addDataType(TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                        .addDataType(TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                        .addDataType(DataType.AGGREGATE_ACTIVITY_SUMMARY, FitnessOptions.ACCESS_WRITE)
                        .addDataType(TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
                        .build();

        try {

            if (GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(context), fitnessOptions)) {
                Log.i(TAG, "collectData: sleep,activity,steps,calories,distance and weather");
                sleepGoogleFit.extractSleepData(context);
                activitiesGoogleFit.extractActivityData(context);
                stepsGoogleFit.getDataFromPrevDay(context, fitnessOptions);
                caloriesGoogleFit.getDataFromPrevDay(context, fitnessOptions);
                distanceGoogleFit.getDataFromPrevDay(context, fitnessOptions);
                gpsLocationListener.extractDataForWeather();
            }
        }catch (Exception e){
            Log.e(TAG, "collectData: could not extract data from GoogleFitAPI");
        }

    }
    public void sendData(Context context){
        HttpRequests httpRequests = HttpRequests.getInstance(context);

        gpsLocationListener.sendDataToServer(httpRequests);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sensorDataByDates(JSONArray jsonArray, Context context){

        GoogleSignInOptionsExtension fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                        .addDataType(TYPE_STEP_COUNT_DELTA,FitnessOptions.ACCESS_READ)
                        .addDataType(TYPE_CALORIES_EXPENDED,FitnessOptions.ACCESS_READ)
                        .addDataType(DataType.AGGREGATE_ACTIVITY_SUMMARY, FitnessOptions.ACCESS_WRITE)
                        .addDataType(TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
                        .build();

        //parse json
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Iterator<String> keys = jsonObject.keys();
                JSONArray times;
                while(keys.hasNext()) {
                    String key = keys.next();
                    System.out.println(key);
                    switch (key){
                        case "Steps":
                            times = (JSONArray) jsonObject.get("Steps");
                            for (int t = 0; t< times.length() ; t++){
                                this.stepsGoogleFit.getDataByDate(context, fitnessOptions,(long)times.get(t),(long)times.get(t) + 86400000);
                            }
                            break;
                        case "Calories":
                            times = (JSONArray) jsonObject.get("Calories");
                            for (int t = 0; t< times.length() ; t++){
                                this.caloriesGoogleFit.getDataByDate(context, fitnessOptions,(long)times.get(t),(long)times.get(t) + 86400000);
                            }
                            break;
                        case "Distance":
                            times = (JSONArray) jsonObject.get("Distance");
                            for (int t = 0; t< times.length() ; t++){
                                this.distanceGoogleFit.getDataByDate(context, fitnessOptions,(long)times.get(t),(long)times.get(t) + 86400000);
                            }
                            break;
                        case "Sleep":
                            times = (JSONArray) jsonObject.get("Sleep");
                            for (int t = 0; t< times.length() ; t++){
                                //extract from 20:00 prev day to 20:00 day after
                                this.sleepGoogleFit.extractSleepDataByDate(context,(long)times.get(t) - 14400000,(long)times.get(t) + 86400000 - 14400000);
                            }
                            break;
                        case "Accelerometer":
                            //no way to collect accelerometer data
                            break;
                        case "Weather":
                            //no way to collect weather data
                            break;
                        case "Activity":
                            times = (JSONArray) jsonObject.get("Activity");
                            for (int t = 0; t< times.length() ; t++){
                                this.activitiesGoogleFit.extractActivityDataByDate(context,(long)times.get(t),(long)times.get(t) + 86400000);
                            }
                            break;

                        default:
                            Log.w(TAG, "sensorDataByDates: did not recognize metric " + key);
                            break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setMissingMetricsTask(Context context) {
        AlarmManager alarmManager = (AlarmManager) (context.getSystemService(ALARM_SERVICE));
        Intent intent = new Intent(context, MissingMetricsBroadcastReceiver.class);
        int hour = Configurations.getMetricsTaskHour(context);
        int minute = Configurations.getMetricsTaskMinute(context);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        Log.i(TAG, "setMissingMetricsTask: set setMissingMetricsTask to " + hour + ":" + minute);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 102, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        assert alarmManager != null;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+ TimeUtils.randomTime(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void setMetricsTask(Context context) {
        AlarmManager alarmManager = (AlarmManager) (context.getSystemService(ALARM_SERVICE));
        Intent intent = new Intent(context, MetricsBroadcastReceiver.class);
        int hour = 23;
        int minute = 29;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        Log.i(TAG, "setMissingMetricsTask: set setMetricsTask to " + hour + ":" + minute);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 103, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        assert alarmManager != null;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+ TimeUtils.randomTime(), AlarmManager.INTERVAL_HOUR, pendingIntent);

    }
}
