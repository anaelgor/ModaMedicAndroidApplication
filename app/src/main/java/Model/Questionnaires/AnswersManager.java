package Model.Questionnaires;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

import Model.Exceptions.ServerFalseException;
import Model.Users.Login;
import Model.Utils.Constants;
import Model.Utils.HttpRequests;
import Model.Utils.Urls;

public class AnswersManager {

    public static final String TAG = "AnswersManager";

    public static JSONObject createJsonAnswersOfQuestionnaire
            (Map<Long, List<Long>> questionsAndAnswers, Long questionnaireID) {
        JSONObject result = new JSONObject();
        try {
            result.put("QuestionnaireID",questionnaireID);
            result.put("ValidTime",new Date().getTime());
            JSONArray totalAnswers = new JSONArray();
            for (Long key : questionsAndAnswers.keySet()) {
                JSONObject question = new JSONObject();
                question.put("QuestionID",key);
                JSONArray answers = new JSONArray();
                for (Long answer : questionsAndAnswers.get(key))
                    answers.put(answer);
                question.put("AnswerID",answers);
                totalAnswers.put(question);
            }
            result.put("Answers",totalAnswers);
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        System.out.println(result.toString());
        return result;
    }

    public static boolean hasUserAnswered(String questionnaire_id, String days, HttpRequests httpRequests) {
        String url = Urls.urlHasBeenAnswered+Urls.getUrlHasBeenAnsweredDaysParam+days+Urls.getUrlHasBeenAnsweredQuestionnaireIDParam+questionnaire_id;
        try {
            JSONObject result = httpRequests.sendGetRequest(url, Login.getToken());
            Log.i(TAG,"sent to server");
            System.out.println(result.getString("data"));
            return result.getString("data").equals("true");
        } catch (ServerFalseException serverFalseException) {
            serverFalseException.printStackTrace();
            Log.i(TAG,"problem in asking if user has been answered to server "+ serverFalseException.getLocalizedMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }


}
