package Model.Metrics;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.modamedicandroidapplication.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;

import java.util.Calendar;

import Model.Metrics.GoogleFit.ActivitiesGoogleFit;
import Model.Metrics.GoogleFit.CaloriesGoogleFit;
import Model.Metrics.GoogleFit.DistanceGoogleFit;
import Model.Metrics.GoogleFit.SleepGoogleFit;
import Model.Metrics.GoogleFit.StepsGoogleFit;
import Model.Utils.HttpRequests;

import static android.content.Context.ALARM_SERVICE;
import static com.google.android.gms.fitness.data.DataType.TYPE_ACTIVITY_SEGMENT;
import static com.google.android.gms.fitness.data.DataType.TYPE_CALORIES_EXPENDED;
import static com.google.android.gms.fitness.data.DataType.TYPE_DISTANCE_DELTA;
import static com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA;

public class SensorData extends BroadcastReceiver {

    private StepsGoogleFit stepsGoogleFit;
    private DistanceGoogleFit distanceGoogleFit;
    private CaloriesGoogleFit caloriesGoogleFit;
    private SleepGoogleFit sleepGoogleFit;
    private ActivitiesGoogleFit activitiesGoogleFit;
    private LocationManager locationManager;
    private LocationListener gpsLocationListener;

    private Intent intent;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;

    private static final String TAG = "SensorData";

    public SensorData(Activity activity) {
        this.stepsGoogleFit = new StepsGoogleFit();
        this.distanceGoogleFit = new DistanceGoogleFit();
        this.caloriesGoogleFit = new CaloriesGoogleFit();
        this.sleepGoogleFit = new SleepGoogleFit();
        this.activitiesGoogleFit = new ActivitiesGoogleFit();
        this.locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        this.gpsLocationListener = new Weather(locationManager, activity);

        this.intent = new Intent(activity, SensorData.class);
        this.pendingIntent = PendingIntent.getBroadcast(activity, 0, this.intent,PendingIntent.FLAG_NO_CREATE);

        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        //Log.i(TAG, "SensorData: set notification & sending sensor data to: " + calendar.getTimeInMillis());
        Log.i(TAG, "SensorData: current time zone: " + calendar.getTimeZone());

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, this.pendingIntent);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void collectData(Activity activity){
        GoogleSignInOptionsExtension fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                        .addDataType(TYPE_STEP_COUNT_DELTA,FitnessOptions.ACCESS_READ)
                        .addDataType(TYPE_CALORIES_EXPENDED,FitnessOptions.ACCESS_READ)
                        .addDataType(DataType.AGGREGATE_ACTIVITY_SUMMARY, FitnessOptions.ACCESS_WRITE)
                        .addDataType(TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
                        .build();

        if (GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(activity), fitnessOptions)){
            Log.i(TAG, "collectData: sleep,activity,steps,calories,distance and weather");
            sleepGoogleFit.extractSleepData(activity);
            activitiesGoogleFit.extractActivityData(activity);
            stepsGoogleFit.getDataFromPrevDay(activity, fitnessOptions);
            caloriesGoogleFit.getDataFromPrevDay(activity, fitnessOptions);
            distanceGoogleFit.getDataFromPrevDay(activity, fitnessOptions);
            ((Weather) gpsLocationListener).extractDataForWeather();
        }
        else{
            GoogleSignIn.requestPermissions(
                    activity, // your activity
                    1,
                    GoogleSignIn.getLastSignedInAccount(activity),
                    fitnessOptions);
        }
    }

    public void sendData (){
        HttpRequests httpRequests = HttpRequests.getInstance();

        //wait until we have all data (async tasks)
        long startTime = System.currentTimeMillis();

        while (sleepGoogleFit.getJson() == null || activitiesGoogleFit.getJson() == null
                || !stepsGoogleFit.hadBeenCalc() || !caloriesGoogleFit.hadBeenCalc()
                || !distanceGoogleFit.hadBeenCalc())
        {
            //fix time issue to avoid endless loop
            long currTime = System.currentTimeMillis();
            if (currTime - startTime >= 120000)
                break;
        }
        // send data to server
        Log.i(TAG, "******* Sending metrics to server ******");
        stepsGoogleFit.sendDataToServer(httpRequests);
        caloriesGoogleFit.sendDataToServer(httpRequests);
        distanceGoogleFit.sendDataToServer(httpRequests);
        sleepGoogleFit.sendDataToServer(httpRequests);
        activitiesGoogleFit.sendDataToServer(httpRequests);
        ((Weather)gpsLocationListener).sendDataToServer(httpRequests);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "onReceive: got here !!!!!");

        collectData((Activity) context);
        sendData();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "Sensor_Data_Collecting_ID";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);
            // Configure the notification channel.
            notificationChannel.setDescription("sensor data collecting");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context.getApplicationContext(), NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.app_logo) //todo: notification logo
                .setTicker("Hearty365")
                //     .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle("Sensor Data")
                .setContentText("Your data was collected successfully :)")
                .setContentInfo("Info");

        notificationManager.notify(/*notification id*/1, notificationBuilder.build());

    }
}
