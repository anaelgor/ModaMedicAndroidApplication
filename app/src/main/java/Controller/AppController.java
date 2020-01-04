package Controller;

import android.app.Activity;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.FitnessOptions;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import Model.CaloriesGoogleFit;
import Model.DistanceGoogleFit;
import Model.Exceptions.ServerFalse;
import Model.GPS;
import Model.Questionnaires.Questionnaire;
import Model.Questionnaires.QuestionnaireManager;
import Model.HttpRequests;
import Model.StepsGoogleFit;

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


    private AppController(Activity activity) {
        this.activity = activity;
        this.stepsGoogleFit = new StepsGoogleFit();
        this.distanceGoogleFit = new DistanceGoogleFit();
        this.caloriesGoogleFit = new CaloriesGoogleFit();
        //TODO: need to ask for permission before this command
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
                        .build();

        if (GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this.activity), fitnessOptions)){
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
            httpRequests.sendPostRequest(stepsGoogleFit.makeBodyJson(steps,""), "metrics/steps");
            httpRequests.sendPostRequest(caloriesGoogleFit.makeBodyJson(calories,""), "metrics/calories");
            httpRequests.sendPostRequest(distanceGoogleFit.makeBodyJson(distance,""), "metrics/distance");

            Log.i("SendMetrics", "******* metrics had been sent successfully ******");

        } catch (ServerFalse serverFalse) {
            Log.e("ServerFalse", "bug in sending metrics");
            //TODO: pop up error message to the user
            serverFalse.printStackTrace();
        }
    }

    public Questionnaire getQuestionnaire(org.json.simple.JSONObject jsonObject) {

        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse("\n" +
                    "        {\n" +
                    "            \"_id\": \"5e0f41875a17c9f806166050\",\n" +
                    "            \"QuestionnaireText\": \"Daily\",\n" +
                    "            \"Questions\": [\n" +
                    "                {\n" +
                    "                    \"Answers\": [\n" +
                    "                        {\n" +
                    "                            \"answerText\": \"0\",\n" +
                    "                            \"answerID\": 0\n" +
                    "                        },\n" +
                    "                        {\n" +
                    "                            \"answerText\": \"1\",\n" +
                    "                            \"answerID\": 1\n" +
                    "                        },\n" +
                    "                        {\n" +
                    "                            \"answerText\": \"2\",\n" +
                    "                            \"answerID\": 2\n" +
                    "                        },\n" +
                    "                        {\n" +
                    "                            \"answerText\": \"3\",\n" +
                    "                            \"answerID\": 3\n" +
                    "                        },\n" +
                    "                        {\n" +
                    "                            \"answerText\": \"4\",\n" +
                    "                            \"answerID\": 4\n" +
                    "                        },\n" +
                    "                        {\n" +
                    "                            \"answerText\": \"5\",\n" +
                    "                            \"answerID\": 5\n" +
                    "                        },\n" +
                    "                        {\n" +
                    "                            \"answerText\": \"6\",\n" +
                    "                            \"answerID\": 6\n" +
                    "                        },\n" +
                    "                        {\n" +
                    "                            \"answerText\": \"7\",\n" +
                    "                            \"answerID\": 7\n" +
                    "                        },\n" +
                    "                        {\n" +
                    "                            \"answerText\": \"8\",\n" +
                    "                            \"answerID\": 8\n" +
                    "                        },\n" +
                    "                        {\n" +
                    "                            \"answerText\": \"9\",\n" +
                    "                            \"answerID\": 9\n" +
                    "                        },\n" +
                    "                        {\n" +
                    "                            \"answerText\": \"10\",\n" +
                    "                            \"answerID\": 10\n" +
                    "                        }\n" +
                    "                    ],\n" +
                    "                    \"QuestionID\": 0,\n" +
                    "                    \"Type\": \"VAS\",\n" +
                    "                    \"QuestionText\": \"מהי רמת הכאב הנוכחית שלך?\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"Answers\": [\n" +
                    "                        {\n" +
                    "                            \"answerText\": \"בסיסית\",\n" +
                    "                            \"answerID\": 0\n" +
                    "                        },\n" +
                    "                        {\n" +
                    "                            \"answerText\": \"מתקדמת\",\n" +
                    "                            \"answerID\": 1\n" +
                    "                        },\n" +
                    "                        {\n" +
                    "                            \"answerText\": \"נרקוטית\",\n" +
                    "                            \"answerID\": 2\n" +
                    "                        },\n" +
                    "                        {\n" +
                    "                            \"answerText\": \"לא נטלתי\",\n" +
                    "                            \"answerID\": 3\n" +
                    "                        }\n" +
                    "                    ],\n" +
                    "                    \"QuestionID\": 1,\n" +
                    "                    \"Type\": \"multi\",\n" +
                    "                    \"QuestionText\": \"איזה סוג תרופה נטלת היום?\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"QuestionnaireID\": 0\n" +
                    "        }\n" +
                    "   ");
            jsonObject = (org.json.simple.JSONObject) obj;
            System.out.println(jsonObject.toString());
            Log.i("AppController",jsonObject.toString());

          //  Questionnaire dailyQuestionnaire = QuestionnaireManager.createQuestionnaireFromJSON(jsonObject);
            //todo: continue from here. parse the object into this. change types of jsonObject in all classes.
            //todo: don't forget to work only with phone for now.

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
