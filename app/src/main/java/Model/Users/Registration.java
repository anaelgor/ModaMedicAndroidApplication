package Model.Users;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Model.Exceptions.ServerFalseException;
import Model.Utils.HttpRequests;
import Model.Utils.Urls;

public class Registration {

    private static final String TAG = "REGISTRATION";

    public static String register(User user, HttpRequests httpRequests) {
        Log.i(TAG, String.format("Registering %s to system as patient", user.getEmail()));
        //todo: implement
        return null;

    }

    private JSONObject getBodyOfUser(User user) {
        JSONObject body = new JSONObject();
        //todo: wait for OR
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
}
