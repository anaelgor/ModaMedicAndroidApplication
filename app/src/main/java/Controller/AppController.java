package Controller;

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

import java.util.List;
import java.util.Map;

import Model.ActivitiesGoogleFit;
import Model.CaloriesGoogleFit;
import Model.DistanceGoogleFit;
import Model.HttpRequests;
import Model.Login;
import Model.QuestionnaireSenderAndReceiver;
import Model.Questionnaires.Questionnaire;
import Model.SleepGoogleFit;
import Model.StepsGoogleFit;
import Model.Weather;

import static com.google.android.gms.fitness.data.DataType.TYPE_ACTIVITY_SEGMENT;
import static com.google.android.gms.fitness.data.DataType.TYPE_CALORIES_EXPENDED;
import static com.google.android.gms.fitness.data.DataType.TYPE_DISTANCE_DELTA;
import static com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA;

public class AppController {

    private static AppController appController;
    private StepsGoogleFit stepsGoogleFit;
    private DistanceGoogleFit distanceGoogleFit;
    private CaloriesGoogleFit caloriesGoogleFit;
    private Activity activity;
    private LocationManager locationManager;
    private LocationListener gpsLocationListener;
    private HttpRequests httpRequests;

    private static final String TAG = "AppController";


    //my try
    private SleepGoogleFit sleepGoogleFit;
    private ActivitiesGoogleFit activitiesGoogleFit;

    private AppController(Activity activity) {
        this.activity = activity;
        this.stepsGoogleFit = new StepsGoogleFit();
        this.distanceGoogleFit = new DistanceGoogleFit();
        this.caloriesGoogleFit = new CaloriesGoogleFit();
        this.locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        this.gpsLocationListener = new Weather(locationManager, activity);
        this.httpRequests = new HttpRequests();

        //my try
        this.sleepGoogleFit = new SleepGoogleFit();
        this.activitiesGoogleFit = new ActivitiesGoogleFit();

    }

    public static AppController getController(Activity activity){
        if (appController == null){
            appController = new AppController(activity);
        }
        return appController;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void SendSensorData(){
        System.out.println("token: " +Login.getToken());

        long time = System.currentTimeMillis();

        while(System.currentTimeMillis() <= time + 60000);

        GoogleSignInOptionsExtension fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                        .addDataType(TYPE_STEP_COUNT_DELTA,FitnessOptions.ACCESS_READ)
                        .addDataType(TYPE_CALORIES_EXPENDED,FitnessOptions.ACCESS_READ)
                        .addDataType(DataType.AGGREGATE_ACTIVITY_SUMMARY, FitnessOptions.ACCESS_WRITE)
                        .addDataType(TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
                        .build();

        if (GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this.activity), fitnessOptions)){
            sleepGoogleFit.extractSleepData(this.activity);
            activitiesGoogleFit.extractActivityData(this.activity);
            stepsGoogleFit.getDataFromPrevDay(this.activity, fitnessOptions);
            caloriesGoogleFit.getDataFromPrevDay(this.activity, fitnessOptions);
            distanceGoogleFit.getDataFromPrevDay(this.activity, fitnessOptions);
            ((Weather) gpsLocationListener).extractDataForWeather();
        }
        else{
            GoogleSignIn.requestPermissions(
                    this.activity, // your activity
                    1,
                    GoogleSignIn.getLastSignedInAccount(this.activity),
                    fitnessOptions);
        }


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
        Log.i("SendMetrics", "******* Sending metrics to server ******");
        stepsGoogleFit.sendDataToServer(httpRequests);
        caloriesGoogleFit.sendDataToServer(httpRequests);
        distanceGoogleFit.sendDataToServer(httpRequests);
        sleepGoogleFit.sendDataToServer(httpRequests);
        activitiesGoogleFit.sendDataToServer(httpRequests);
        ((Weather)gpsLocationListener).sendDataToServer(httpRequests);
    }

    public Questionnaire getQuestionnaire(Long questionnaire_id) {
        return QuestionnaireSenderAndReceiver.getUserQuestionnaireById(questionnaire_id, httpRequests);
    }

    public void sendAnswersToServer(Map<Long, List<Long>> questionsAndAnswers, Long questionnaireID) {
        QuestionnaireSenderAndReceiver.sendAnswers(questionsAndAnswers,questionnaireID, httpRequests);
    }

    public Map<Long, String> getUserQuestionnaires(String username) {
        return QuestionnaireSenderAndReceiver.getUserQuestionnaires(username, httpRequests);
    }

    public boolean login(String username, String password, Activity activity) {
        return Login.login(username,password,activity, httpRequests);
    }
}
