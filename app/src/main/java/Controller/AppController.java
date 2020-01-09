package Controller;

import android.app.Activity;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.JsonReader;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.CaloriesGoogleFit;
import Model.DistanceGoogleFit;
import Model.Exceptions.ServerFalse;
import Model.GPS;
import Model.HttpRequests;
import Model.SleepGoogleFit;
import Model.Questionnaires.AnswersManager;
import Model.Questionnaires.Questionnaire;
import Model.Questionnaires.QuestionnaireManager;
import Model.StepsGoogleFit;

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


    private AppController(Activity activity) {
        this.activity = activity;
        this.stepsGoogleFit = new StepsGoogleFit();
        this.distanceGoogleFit = new DistanceGoogleFit();
        this.caloriesGoogleFit = new CaloriesGoogleFit();
        this.sleepGoogleFit = new SleepGoogleFit();
        this.locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        this.gpsLocationListener = new GPS(locationManager, activity);
        this.httpRequests = new HttpRequests();
    }

    public static AppController getController(Activity activity){
        if (appController == null){
            appController = new AppController(activity);
        }
        return appController;
    }

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
                        .build();

        if (GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this.activity), fitnessOptions)){
            sleepGoogleFit.readSleepData(this.activity, fitnessOptions);
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
            // send data to server
            Log.i("SendMetrics", "******* Sending metrics to server ******");
            httpRequests.sendPostRequest(stepsGoogleFit.makeBodyJson(steps,""), "metrics/steps");
            httpRequests.sendPostRequest(caloriesGoogleFit.makeBodyJson(calories,""), "metrics/calories");
            httpRequests.sendPostRequest(distanceGoogleFit.makeBodyJson(distance,""), "metrics/distance");
            //httpRequests.sendPostRequest(sleepGoogleFit.makeJsonBody(""), "metrics/sleep");



        } catch (ServerFalse serverFalse) {
            Log.e("ServerFalse", "bug in sending metrics");
            //TODO: pop up error message to the user
            serverFalse.printStackTrace();
        }
    }

    public Questionnaire getQuestionnaire(String questionnaire_name) {
        JSONObject jsonObject = getQuestionnaireFromDB("questionnaires/daily_questionnaire");

        try {
            // jsonObject = new JSONObject(daily_from_server);
            System.out.println(jsonObject.toString());
            Log.i("AppController", jsonObject.toString());
            JSONArray jssonArray = (JSONArray) jsonObject.get("data");
            jsonObject = (JSONObject) jssonArray.get(0);
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
            //todo: not hard coded!!!!!!!!!! anael
            httpRequests.sendPostRequest(request,"answers/"+"daily_answers");
            Log.i("AppControler","sent to server");

        } catch (ServerFalse serverFalse) {
            serverFalse.printStackTrace();
            Log.i("AppControler","problem in sending questionaire to server "+serverFalse.getLocalizedMessage());
        }
    }
}
