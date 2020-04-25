package Model.Users;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import Model.Exceptions.ServerFalseException;
import Model.Questionnaires.Questionnaire;
import Model.Utils.HttpRequests;
import Model.Utils.Urls;

public class Settings {

    private final static String TAG = "Settings";

    public static long getSurgeryDate(HttpRequests httpRequests) {
        String token = Login.getToken(HttpRequests.getContext());
        try {
            JSONObject response = httpRequests.sendGetRequest(Urls.urlOfGetSurgeryDate,token);
            return response.getLong("data");
        } catch (ServerFalseException | JSONException e) {
            Log.i(TAG,"Error in getting surgery date of user with token " + token);
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean setSurgeryDate(HttpRequests httpRequests, long surgeryDate) {
        String token = Login.getToken(HttpRequests.getContext());

        JSONObject body = new JSONObject();
        try {
            body.put("DateOfSurgery", surgeryDate);
            JSONObject response = httpRequests.sendPostRequest(body, Urls.urlOfSetSurgeryDate, token);
            if (response.getString("error").equals("false"))
                return true;
            else {
                Log.i(TAG,"Error in setting surgery date of user with token " + token + " to " + surgeryDate);
                return false;
            }
        } catch (JSONException | ServerFalseException e) {
            Log.i(TAG,"Error in setting surgery date of user with token " + token + " to " + surgeryDate);
            e.printStackTrace();
            return false;
        }
    }

    public static boolean setUserQuestionnaires(HttpRequests httpRequests, List<Questionnaire> questionnaires) {
        String token = Login.getToken(HttpRequests.getContext());

        JSONObject body = new JSONObject();
        try {
            JSONArray questionnaireArray = new JSONArray();
            for (int i = 0; i<questionnaires.size(); i++) {
                JSONObject questionnaire = new JSONObject();
                questionnaire.put("QuestionnaireID",questionnaires.get(i).getQuestionaireID());
                questionnaire.put("QuestionnaireText",questionnaires.get(i).getTitle());
                questionnaireArray.put(questionnaire);
                if (questionnaires.get(i).getQuestionaireID() == 5) { //special for question 5 and 6 eq5
                    JSONObject questionnaire2 = new JSONObject();
                    questionnaire2.put("QuestionnaireID",6);
                    questionnaire2.put("QuestionnaireText","דירוג איכות חיים");
                    questionnaireArray.put(questionnaire2);
                }
            }
            body.put("Questionnaires",questionnaireArray);
            httpRequests.sendPostRequest(body,Urls.urlOfSetNewQuestionnaires,token);
            return true;
        } catch (JSONException | ServerFalseException e) {
            Log.i(TAG,"Error in setting new questionnaires of user with token " + token);
            e.printStackTrace();
            return false;
        }
    }

}
