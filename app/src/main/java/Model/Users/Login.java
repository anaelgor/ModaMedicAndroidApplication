package Model.Users;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import Model.Exceptions.ServerFalseException;
import Model.Utils.Constants;
import Model.Utils.HttpRequests;
import Model.Utils.Urls;

public class Login {

    private static String userToken;

    private static void setTokenOfUser(Activity activity) {
        String not_exists = "not exists";
        SharedPreferences sharedPref = activity.getSharedPreferences(Constants.sharedPreferencesName,Context.MODE_PRIVATE);
        String token = sharedPref.getString("token",not_exists);
        if (token.equals(not_exists))
            throw new NullPointerException("have no token");
        userToken = token;
    }


    public static String getToken() {
        return userToken;
    }

    public static boolean login(String username, String password, Activity activity, HttpRequests httpRequests) {
        JSONObject login_body  = makeBodyJson(username,password);
        SharedPreferences sharedPref = activity.getSharedPreferences(Constants.sharedPreferencesName,Context.MODE_PRIVATE);
        try {
            JSONObject response = httpRequests.sendPostRequest(login_body, Urls.urlOfLogin);
            JSONObject data = response.getJSONObject("data");
            String token = data.getString("token");
            String name = data.getString("name");
            sharedPref.edit().putString("username",username).apply();
            sharedPref.edit().putString("token",token).apply();
            sharedPref.edit().putString("name",name).apply();
            setTokenOfUser(activity);
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
