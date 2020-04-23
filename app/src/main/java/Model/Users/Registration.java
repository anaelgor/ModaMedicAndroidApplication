package Model.Users;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import Model.Exceptions.ServerFalseException;
import Model.Utils.HttpRequests;
import Model.Utils.Urls;

public class Registration {

    private static final String TAG = "REGISTRATION";



    public static String register(User user, HttpRequests httpRequests) {
        Log.i(TAG, String.format("Registering %s to system as patient", user.getEmail()));
        JSONObject body = getBodyOfUser(user);
        try {
            JSONObject response = httpRequests.sendPostRequest(body, Urls.urlOfRegister);
            Log.i(TAG, "registering user with the following body:  " + body);
            Log.i(TAG, "response:  " + response);
            return "OK";
        }catch (ServerFalseException e) {
            Log.e(TAG, "failing in registration, error: ");
            if (Objects.requireNonNull(e.getMessage()).equals("Wrong Code")) {
                return "Wrong Code";
            } else if (e.getMessage().equals("Taken Email")) {
                return "Taken Email";
            }
            e.printStackTrace();
        }
        return null;
    }

    public static Map<Integer, String> getAllVerificationQuestions(HttpRequests httpRequests) {
        try {
            Map<Integer, String> result = new HashMap<Integer, String>();
            JSONObject response = httpRequests.sendGetRequest(Urls.urlOfGetAllVerificationQuestions);
            JSONArray array = response.getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = (JSONObject) array.get(i);
                result.put(object.getInt("QuestionID"), object.getString("QuestionText"));
            }
            return result;
        } catch (ServerFalseException | JSONException e) {
            Log.i(TAG, "failing in getting all verification questions, error: ");
            e.printStackTrace();
            return null;
        }
    }

    private static JSONObject getBodyOfUser(User user) {
        JSONObject body = new JSONObject();
        try {
            body.put("UserID", user.getEmail());
            body.put("Password",user.getPassword());
            body.put("First_Name",user.getFirstName());
            body.put("Last_Name",user.getLastName());
            body.put("Phone_Number", user.getPhoneNumber());
            body.put("Smoke",user.getSmoke());
            body.put("Gender",user.getGender());
            body.put("SurgeryType",user.getSurgeryType());
            body.put("Education",user.getEducation());
            body.put("Height",user.getHeight());
            body.put("Weight",user.getWeight());
            body.put("Code",user.getCode());
            body.put("BirthDate",user.getBirthday());
            body.put("DateOfSurgery",user.getSurgeryDate());
            body.put("VerificationQuestion",user.getVerificationQuestion());
            body.put("VerificationAnswer",user.getVerificationAnswer());
            body.put("ValidTime",System.currentTimeMillis());
            body.put("BMI",user.getBmi());

            JSONArray questionnaireArray = new JSONArray();
            for (int i = 0; i<user.getQuestionnaires().size(); i++) {
                JSONObject questionnaire = new JSONObject();
                questionnaire.put("QuestionnaireID",user.getQuestionnaires().get(i).getQuestionaireID());
                questionnaire.put("QuestionnaireText",user.getQuestionnaires().get(i).getTitle());
                questionnaireArray.put(questionnaire);
                if (user.getQuestionnaires().get(i).getQuestionaireID() == 5) { //special for question 5 and 6 eq5
                    JSONObject questionnaire2 = new JSONObject();
                    questionnaire2.put("QuestionnaireID",6);
                    questionnaire2.put("QuestionnaireText","דירוג איכות חיים");
                    questionnaireArray.put(questionnaire2);
                }
            }
            body.put("Questionnaires",questionnaireArray);

        } catch (JSONException e) {
            Log.i(TAG, "failing in creating user body, error: ");
            e.printStackTrace();
        }

        return body;
    }

}
