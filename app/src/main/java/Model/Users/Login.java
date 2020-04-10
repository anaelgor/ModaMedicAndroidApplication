package Model.Users;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import Model.Exceptions.InvalidTokenException;
import Model.Exceptions.ServerFalseException;
import Model.Exceptions.WrongAnswerException;
import Model.Utils.Constants;
import Model.Utils.HttpRequests;
import Model.Utils.Urls;

import static Model.Utils.Urls.urlOfGetVerificationQuestion;

public class Login {

    private static String userToken;
    private static String specialToken = null; //for forgot password

    private static void setTokenOfUser(Context context) {
        String not_exists = "not exists";
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.sharedPreferencesName, Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", not_exists);
        if (token.equals(not_exists))
            throw new NullPointerException("have no token");
        userToken = token;
    }


    public static String getToken(Context context) {
        if (userToken == null || userToken.equals("")) {
            setTokenOfUser(context);
        }
        return userToken;
    }

    public static boolean login(String username, String password, Context context, HttpRequests httpRequests) {
        JSONObject login_body = makeBodyJson(username, password);
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.sharedPreferencesName, Context.MODE_PRIVATE);
        try {
            JSONObject response = httpRequests.sendPostRequest(login_body, Urls.urlOfLogin);
            JSONObject data = response.getJSONObject("data");
            String token = data.getString("token");
            String name = data.getString("name");
            JSONArray type = data.getJSONArray("type");
            boolean type_flag = false;
            for (int i = 0; i < type.length(); i++) {
                if (type.get(i).equals("patient"))
                    type_flag = true;
            }
            if (type_flag) {
                sharedPref.edit().putString("username", username).apply();
                sharedPref.edit().putString("token", token).apply();
                sharedPref.edit().putString("name", name).apply();
                setTokenOfUser(context);
            }

            return type_flag;

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

    public static String getVerificationQuestion(String username, HttpRequests httpRequest) {
        JSONObject body = new JSONObject();
        try {
            body.put("UserID", username);
            JSONObject response = httpRequest.sendPostRequest(body, Urls.urlOfForgotPassword);
            String question_id = response.getString("data");
            JSONObject question_res = httpRequest.sendGetRequest(Urls.urlOfGetVerificationQuestion + question_id);
            return question_res.getJSONObject("data").getString("QuestionText");

        } catch (ServerFalseException | JSONException e) {
            e.printStackTrace();
        }
        return Constants.USER_NOT_EXISTS;
    }

    public static boolean checkVerificationOfAnswerToUserQuestion(String username, long date, String answer, HttpRequests httpRequests) throws WrongAnswerException {
        JSONObject body = new JSONObject();
        try {
            body.put("UserID", username);
            body.put("VerificationAnswer", answer);
            body.put("BirthDate", date);
            JSONObject response = httpRequests.sendPostRequest(body, Urls.urlOfCheckVerificationAnswer);
            if (response.has("message") && response.getString("message").equals("Incorrect"))
                throw new WrongAnswerException();
            specialToken = response.getString("data");
            return true;
        } catch (ServerFalseException | JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean setNewPasswordForLoggedOutUser(String newPassword, HttpRequests httpRequests) throws InvalidTokenException {
        JSONObject body = new JSONObject();
        try {
            body.put("NewPassword", newPassword);
            JSONObject response = httpRequests.sendPostRequest(body, Urls.urlOfSetNewPassword, specialToken);
            if (response.has("message") && response.getString("message").equals("Password Changed"))
                return true;
        } catch (ServerFalseException | JSONException e) {
            e.printStackTrace();
            throw new InvalidTokenException();
        }
        return false;

    }
}
