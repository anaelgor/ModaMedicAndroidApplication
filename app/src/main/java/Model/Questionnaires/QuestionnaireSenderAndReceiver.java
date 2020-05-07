package Model.Questionnaires;

import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.Exceptions.ServerFalseException;
import Model.Users.Login;
import Model.Utils.HttpRequests;
import Model.Utils.Urls;

public class QuestionnaireSenderAndReceiver {

    private static final String TAG = "QuestionnaireSender";
    public static void sendAnswers(Map<Long, List<Long>> questionsAndAnswers, Long questionnaireID, HttpRequests httpRequests) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject request = AnswersManager.createJsonAnswersOfQuestionnaire(questionsAndAnswers,questionnaireID);
                try {
                    httpRequests.sendPostRequest(request, Urls.urlPostAnswersOfQuestionnaireByID, Login.getToken(HttpRequests.getContext()));
                    Log.i(TAG,"sent to server");
                } catch (ServerFalseException serverFalseException) {
                    serverFalseException.printStackTrace();
                    Log.i(TAG,"problem in sending questionaire to server "+ serverFalseException.getLocalizedMessage());
                }
            }
        });
        t.start();
    }

    public static Map<Long, String> getUserQuestionnaires(HttpRequests httpRequests) {
        JSONObject user_questionnaires;
        Map<Long,String> result = new HashMap<>();
        try {
           user_questionnaires = httpRequests.sendGetRequest(Urls.urlGetUserQuestionnaires, Login.getToken(HttpRequests.getContext()) );

            JSONArray array = user_questionnaires.getJSONArray("data");
            for (int i=0; i<array.length(); i++) {
                Long id = Long.valueOf( (Integer)array.getJSONObject(i).get("QuestionnaireID"));
                String text = (String)array.getJSONObject(i).get("QuestionnaireText");
                if (id!=6)
                    result.put(id,text);
                else
                    System.out.println("skipping questionnaire 6");
            }
        } catch (ServerFalseException | JSONException serverFalseException) {
            serverFalseException.printStackTrace();
        }

        return result;
    }

    public static Questionnaire getUserQuestionnaireById(Long questionnaire_id, HttpRequests httpRequests) {
        JSONObject jsonObject = getQuestionnaireFromDB(Urls.urlGetQuestionnaireByID+questionnaire_id, httpRequests);

        try {
            assert jsonObject != null;
            Log.i(TAG, jsonObject.toString());
            jsonObject = (JSONObject) jsonObject.get("data");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return QuestionnaireManager.createQuestionnaireFromJSON(jsonObject);
    }

    private static JSONObject getQuestionnaireFromDB(String questionnaire_name, HttpRequests httpRequests) {
        try {
            return httpRequests.sendGetRequest(questionnaire_name);
        } catch (ServerFalseException serverFalseException) {
            serverFalseException.printStackTrace();
        }
        return null;
    }

    public static Map<Integer, String> getAllQuestionnaires(HttpRequests httpRequests) {
        Map<Integer, String> result = new HashMap<>();
        try {
            JSONObject response = httpRequests.sendGetRequest(Urls.urlOfGetAllQuestionnaires);
            JSONArray array = response.getJSONArray("data");
            for (int i=0; i<array.length(); i++) {
                JSONObject question = (JSONObject) array.get(i);
                int id = question.getInt("QuestionnaireID");
                if (id ==6 || id == 0) //daily and EQ5 special question should not be setted
                    continue;
                String text = question.getString("QuestionnaireText");
                result.put(id,text);
            }
        } catch (ServerFalseException | JSONException e) {
            Log.i(TAG,"error in getting all questionnaies:");
            e.printStackTrace();
            result=null;
        }
        return result;


    }
}
