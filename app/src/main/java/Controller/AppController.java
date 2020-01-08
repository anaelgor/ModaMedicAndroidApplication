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
            Log.i("SendMetrics", "******* Sending metrics to server ******");
            httpRequests.sendPostRequest(stepsGoogleFit.makeBodyJson(steps,""), "metrics/steps");
            httpRequests.sendPostRequest(caloriesGoogleFit.makeBodyJson(calories,""), "metrics/calories");
            httpRequests.sendPostRequest(distanceGoogleFit.makeBodyJson(distance,""), "metrics/distance");



        } catch (ServerFalse serverFalse) {
            Log.e("ServerFalse", "bug in sending metrics");
            //TODO: pop up error message to the user
            serverFalse.printStackTrace();
        }

    }

    public Questionnaire getQuestionnaire(String questionnaire_name) {
        JSONObject jsonObject = getQuestionnaireFromDB("questionnaires/daily_questionnaire");
        //todo: remove this and get it from server
//        String daily_from_server = "{\n" +
//                "    \"error\": false,\n" +
//                "    \"message\": null,\n" +
//                "    \"data\": [\n" +
//                "        {\n" +
//                "            \"_id\": \"5e15f343d90bac1bdb0326cf\",\n" +
//                "            \"QuestionnaireID\": 0,\n" +
//                "            \"QuestionnaireText\": \"Daily\",\n" +
//                "            \"Questions\": [\n" +
//                "                {\n" +
//                "                    \"Answers\": [\n" +
//                "                        {\n" +
//                "                            \"answerID\": 0,\n" +
//                "                            \"answerText\": \"0\"\n" +
//                "                        },\n" +
//                "                        {\n" +
//                "                            \"answerID\": 1,\n" +
//                "                            \"answerText\": \"1\"\n" +
//                "                        },\n" +
//                "                        {\n" +
//                "                            \"answerID\": 2,\n" +
//                "                            \"answerText\": \"2\"\n" +
//                "                        },\n" +
//                "                        {\n" +
//                "                            \"answerID\": 3,\n" +
//                "                            \"answerText\": \"3\"\n" +
//                "                        },\n" +
//                "                        {\n" +
//                "                            \"answerID\": 4,\n" +
//                "                            \"answerText\": \"4\"\n" +
//                "                        },\n" +
//                "                        {\n" +
//                "                            \"answerID\": 5,\n" +
//                "                            \"answerText\": \"5\"\n" +
//                "                        },\n" +
//                "                        {\n" +
//                "                            \"answerID\": 6,\n" +
//                "                            \"answerText\": \"6\"\n" +
//                "                        },\n" +
//                "                        {\n" +
//                "                            \"answerID\": 7,\n" +
//                "                            \"answerText\": \"7\"\n" +
//                "                        },\n" +
//                "                        {\n" +
//                "                            \"answerID\": 8,\n" +
//                "                            \"answerText\": \"8\"\n" +
//                "                        },\n" +
//                "                        {\n" +
//                "                            \"answerID\": 9,\n" +
//                "                            \"answerText\": \"9\"\n" +
//                "                        },\n" +
//                "                        {\n" +
//                "                            \"answerID\": 10,\n" +
//                "                            \"answerText\": \"10\"\n" +
//                "                        }\n" +
//                "                    ],\n" +
//                "                    \"QuestionID\": 0,\n" +
//                "                    \"QuestionText\": \"מהי רמת הכאב הנוכחית שלך?\",\n" +
//                "                    \"Type\": \"VAS\",\n" +
//                "                    \"Best\": \"אין כאב בכלל\",\n" +
//                "                    \"Worst\": \"כאב בלתי נסבל\"\n" +
//                "                },\n" +
//                "                {\n" +
//                "                    \"Answers\": [\n" +
//                "                        {\n" +
//                "                            \"answerID\": 0,\n" +
//                "                            \"answerText\": \"לא נטלתי\"\n" +
//                "                        },\n" +
//                "                        {\n" +
//                "                            \"answerID\": 1,\n" +
//                "                            \"answerText\": \"בסיסית\"\n" +
//                "                        },\n" +
//                "                        {\n" +
//                "                            \"answerID\": 2,\n" +
//                "                            \"answerText\": \"מתקדמת\"\n" +
//                "                        },\n" +
//                "                        {\n" +
//                "                            \"answerID\": 3,\n" +
//                "                            \"answerText\": \"נרקוטית\"\n" +
//                "                        }\n" +
//                "                    ],\n" +
//                "                    \"QuestionID\": 1,\n" +
//                "                    \"QuestionText\": \"איזה סוג תרופה נטלת היום?\",\n" +
//                "                    \"Type\": \"multi\",\n" +
//                "                    \"Alone\": [\n" +
//                "                        0\n" +
//                "                    ]\n" +
//                "                }\n" +
//                "            ]\n" +
//                "        }\n" +
//                "    ]\n" +
//                "}";
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
