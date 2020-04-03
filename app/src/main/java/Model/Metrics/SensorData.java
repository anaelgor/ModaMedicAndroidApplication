package Model.Metrics;

import android.app.Activity;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;

import Model.Metrics.GoogleFit.ActivitiesGoogleFit;
import Model.Metrics.GoogleFit.CaloriesGoogleFit;
import Model.Metrics.GoogleFit.DistanceGoogleFit;
import Model.Metrics.GoogleFit.SleepGoogleFit;
import Model.Metrics.GoogleFit.StepsGoogleFit;
import Model.Metrics.Weather;
import Model.Utils.HttpRequests;

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
    private LocationManager locationManager;
    private LocationListener gpsLocationListener;

    private static final String TAG = "SensorData";

    public SensorData(Activity activity) {
        this.stepsGoogleFit = new StepsGoogleFit();
        this.distanceGoogleFit = new DistanceGoogleFit();
        this.caloriesGoogleFit = new CaloriesGoogleFit();
        this.sleepGoogleFit = new SleepGoogleFit();
        this.activitiesGoogleFit = new ActivitiesGoogleFit();
        this.locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        this.gpsLocationListener = new Weather(locationManager, activity);
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
}
