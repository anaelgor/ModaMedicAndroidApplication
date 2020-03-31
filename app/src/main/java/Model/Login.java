package Model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import Model.Exceptions.ServerFalseException;

public class Login {



    public static boolean login(String username, String password, Activity activity, HttpRequests httpRequests) {
        JSONObject login_body  = makeBodyJson(username,password);
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        sharedPref.edit().putString("username",username).apply();
        try {
            JSONObject response = httpRequests.sendPostRequest(login_body,Urls.urlOfLogin);
            String token = response.getString("data");
            sharedPref.edit().putString("token",token).apply();
            return true;

        } catch (ServerFalseException | JSONException e) {
            e.printStackTrace();
            return false;
        }


    }
    private static JSONObject makeBodyJson(String username, String password) {
        JSONObject json = new JSONObject();
        try {
            json.put("UserID", username);
            json.put("Password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

}
