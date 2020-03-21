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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.ActivitiesGoogleFit;
import Model.CaloriesGoogleFit;
import Model.DistanceGoogleFit;
import Model.Exceptions.ServerFalse;
import Model.GPS;
import Model.HttpRequests;
import Model.Questionnaires.AnswersManager;
import Model.Questionnaires.Questionnaire;
import Model.Questionnaires.QuestionnaireManager;
import Model.SleepGoogleFit;
import Model.SleepGoogleFitSecondTry;
import Model.StepsGoogleFit;

import static com.google.android.gms.fitness.data.DataType.TYPE_ACTIVITY_SEGMENT;
import static com.google.android.gms.fitness.data.DataType.TYPE_CALORIES_EXPENDED;
import static com.google.android.gms.fitness.data.DataType.TYPE_DISTANCE_DELTA;
import static com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA;

public class AppController {

    private static AppController appController;
    private StepsGoogleFit stepsGoogleFit;
    private DistanceGoogleFit distanceGoogleFit;
    private CaloriesGoogleFit caloriesGoogleFit;
    private SleepGoogleFit sleepGoogleFit;
    private Activity activity;
    private LocationManager locationManager;
    private LocationListener gpsLocationListener;
    private HttpRequests httpRequests;


    //my try
    private SleepGoogleFitSecondTry sleepGoogleFitSecondTry;
    private ActivitiesGoogleFit activitiesGoogleFit;

    private AppController(Activity activity) {
        this.activity = activity;
        this.stepsGoogleFit = new StepsGoogleFit();
        this.distanceGoogleFit = new DistanceGoogleFit();
        this.caloriesGoogleFit = new CaloriesGoogleFit();
        this.sleepGoogleFit = new SleepGoogleFit();
        this.locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        this.gpsLocationListener = new GPS(locationManager, activity);
        this.httpRequests = new HttpRequests();

        //my try
        this.sleepGoogleFitSecondTry = new SleepGoogleFitSecondTry();
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
        int steps = 0;
        float distance = 0;
        float calories = 0;

        GoogleSignInOptionsExtension fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                        .addDataType(TYPE_STEP_COUNT_DELTA,FitnessOptions.ACCESS_READ)
                        .addDataType(TYPE_CALORIES_EXPENDED,FitnessOptions.ACCESS_READ)
                        .addDataType(DataType.AGGREGATE_ACTIVITY_SUMMARY, FitnessOptions.ACCESS_WRITE)
                        .addDataType(TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
                        .build();

        if (GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this.activity), fitnessOptions)){
            sleepGoogleFitSecondTry.extractSleepData(this.activity);
            activitiesGoogleFit.extractActivityData(this.activity);
            steps = stepsGoogleFit.getDataFromPrevDay(this.activity, fitnessOptions);
            distance = distanceGoogleFit.getDataFromPrevDay(this.activity, fitnessOptions);
            calories = caloriesGoogleFit.getDataFromPrevDay(this.activity, fitnessOptions);
        }
        else{
            GoogleSignIn.requestPermissions(
                    this.activity, // your activity
                    1,
                    GoogleSignIn.getLastSignedInAccount(this.activity),
                    fitnessOptions);
        }

        //Weather
        String json =((GPS)gpsLocationListener).getLocationJSON();
        if (json == null){
            System.out.println("Did not found location");
        }

        try {
            //wait until we have all data (async tasks)
            long startTime = System.currentTimeMillis();

            while (sleepGoogleFitSecondTry.getJson() == null || activitiesGoogleFit.getJson() == null)
            {
                //fix time issue to avoid endless loop
                long currTime = System.currentTimeMillis();
                if (currTime - startTime >= 3000)
                    break;
            }
            // send data to server
            Log.i("SendMetrics", "******* Sending metrics to server ******");

            httpRequests.sendPostRequest(stepsGoogleFit.makeBodyJson(steps,""), Urls.urlPostSteps);
            httpRequests.sendPostRequest(caloriesGoogleFit.makeBodyJson(calories,""), Urls.urlPostCalories);
            httpRequests.sendPostRequest(distanceGoogleFit.makeBodyJson(distance,""), Urls.urlPostDistance);
            httpRequests.sendPostRequest(sleepGoogleFitSecondTry.getJson(), Urls.urlPostSleep);
            sleepGoogleFitSecondTry.clearJson();
            httpRequests.sendPostRequest(activitiesGoogleFit.getJson(), Urls.urlPostActivity);
            activitiesGoogleFit.clearJson();

        } catch (ServerFalse serverFalse) {
            Log.e("ServerFalse", "bug in sending metrics");
            //TODO: pop up error message to the user
            serverFalse.printStackTrace();
        }
    }

    public Questionnaire getQuestionnaire(Long questionnaire_id) {
        JSONObject jsonObject = getQuestionnaireFromDB(Urls.urlGetQuestionnaireByID+questionnaire_id);

        try {
            System.out.println(jsonObject.toString());
            Log.i("AppController", jsonObject.toString());
            jsonObject = (JSONObject) jsonObject.get("data");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }


        return QuestionnaireManager.createQuestionnaireFromJSON(jsonObject);
    }

    private JSONObject getQuestionnaireFromDB(String questionnaire_name) {
        try {
            return httpRequests.sendGetRequest(questionnaire_name);
        } catch (ServerFalse serverFalse) {
            serverFalse.printStackTrace();
        }
        return null;
    }

    public void sendAnswersToServer(Map<Long, List<Long>> questionsAndAnswers, Long questionnaireID) {
        org.json.JSONObject request = AnswersManager.createJsonAnswersOfQuestsionnaire(questionsAndAnswers,questionnaireID);
        try {
            httpRequests.sendPostRequest(request,Urls.urlSendAnswersOfQuestionnaireByID+questionnaireID);
            Log.i("AppControler","sent to server");

        } catch (ServerFalse serverFalse) {
            serverFalse.printStackTrace();
            Log.i("AppControler","problem in sending questionaire to server "+serverFalse.getLocalizedMessage());
        }
    }

    public Map<Long, String> getUserQuestionnaires(String username) {
        JSONObject user_questionnaires = null;
        Map<Long,String> result = new HashMap<>();
        //todo: add token
        try {
            username="111111111";
            user_questionnaires = httpRequests.sendGetRequest(Urls.urlGetUserQuestionnaires+username);
            JSONArray array = user_questionnaires.getJSONArray("data");
            for (int i=0; i<array.length(); i++) {
                Long id = new Long( (Integer)array.getJSONObject(i).get("QuestionnaireID"));
                String text = (String)array.getJSONObject(i).get("QuestionnaireText");
                result.put(id,text);
            }
        } catch (ServerFalse serverFalse) {
            serverFalse.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("xx");
        return result;
    }
}
