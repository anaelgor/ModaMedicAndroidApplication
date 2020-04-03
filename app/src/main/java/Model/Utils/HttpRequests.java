package Model.Utils;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.concurrent.ExecutionException;

import Model.Exceptions.ServerFalseException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpRequests {

    private SendRequestHttp sendRequestHttp;
    private static final String TAG = "HttpRequests";

    private static HttpRequests httpRequests;

    private HttpRequests(){}

    public static HttpRequests getInstance(){
        if (httpRequests == null){
            httpRequests = new HttpRequests();
        }
        return httpRequests;
    }

    public JSONObject sendPostRequest(JSONObject jsonPost, String url) throws ServerFalseException {
        sendRequestHttp = new SendRequestHttp();
        try {
            JSONObject jo = sendRequestHttp.execute("POST", Constants.urlPrefix + url, jsonPost.toString()).get();
            sendRequestHttp.checkException();
            return jo;

        }catch (ExecutionException | InterruptedException e) {
            throw new ServerFalseException("Problem in the application, try again");
        }
    }

    public JSONObject sendPostRequest(JSONObject jsonPost, String url, String token) throws ServerFalseException {
        sendRequestHttp = new SendRequestHttp();
        try {
            JSONObject jo = sendRequestHttp.execute("POST_TOKEN", Constants.urlPrefix + url, jsonPost.toString(), token).get();
            sendRequestHttp.checkException();
            return jo;

        }catch (ExecutionException | InterruptedException e) {
            throw new ServerFalseException("Problem in the application, try again");
        }
    }

    public JSONObject sendGetRequest(String url) throws ServerFalseException {
        sendRequestHttp = new SendRequestHttp();
        try {
            JSONObject jo = sendRequestHttp.execute("GET", Constants.urlPrefix + url).get();
            sendRequestHttp.checkException();
            return jo;
        }catch (ExecutionException | InterruptedException e) {
            throw new ServerFalseException("Problem in the application, try again");
        }
    }

    public JSONObject sendGetRequest(String url, String token) throws ServerFalseException {
        sendRequestHttp = new SendRequestHttp();
        try {
            JSONObject jo = sendRequestHttp.execute("GET_TOKEN", Constants.urlPrefix + url,token).get();
            sendRequestHttp.checkException();
            return jo;
        }catch (ExecutionException | InterruptedException e) {
            throw new ServerFalseException("Problem in the application, try again");
        }
    }
}

class SendRequestHttp extends AsyncTask<String, Void, JSONObject> {

    private static final String TAG = "SendRequestHttp";
    private ServerFalseException exception;
    private final OkHttpClient client;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public SendRequestHttp() {
        client = new OkHttpClient();
    }

    @Override
    protected JSONObject doInBackground(String... urls) {

        try {
            JSONObject ans = null;
            try {
                JSONObject response = null;
                if (urls[0].equals("POST"))
                {
                    response = PostRequest(urls[1], urls[2]);
                }
                else if (urls[0].equals("POST_TOKEN")) {
                    response = PostRequest(urls[1], urls[2], urls[3]);

                }
                else if (urls[0].equals("GET"))
                {
                    response = GetRequest(urls[1]);
                }

                else if (urls[0].equals("GET_TOKEN")){
                    response = GetRequest(urls[1], urls[2]);
                }

                if (response!= null && response.getBoolean("error")) {
                    exception = new ServerFalseException(response.getString("message"));
                } else {
                    ans = response;
                }

            } catch (IOException e) {
                e.printStackTrace();
                exception = new ServerFalseException("Problem in connection to server");
            } catch (JSONException e) {
                exception = new ServerFalseException("Problem in the application, try again");
            }
            return ans;
        } catch (Exception e) {
            //e.printStackTrace();
            exception = new ServerFalseException("Problem in connection to server");
            return null;
        }
    }

    /**
     * make a post request
     * @param url post request url
     * @param json json for the post
     * @return json
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject PostRequest(String url,String json) throws IOException, JSONException {

        Log.i(TAG, String.format("sending to: " + url + "   body: " + json));

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        return new JSONObject(response.body().string());
    }

    public JSONObject PostRequest(String url,String json, String token) throws IOException, JSONException {

        Log.i(TAG, MessageFormat.format("sending with token: {0} to: {1}   body: {2}", token, url, json));

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("x-auth-token", token)
                .build();

        Response response = client.newCall(request).execute();
        return new JSONObject(response.body().string());
    }

    /**
     * make a get request
     * @param url get request url
     * @return json
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject GetRequest(String url) throws IOException, JSONException {

        Log.i(TAG, String.format("sending to: " + url ));

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return new JSONObject(response.body().string());
    }

    public JSONObject GetRequest(String url, String token) throws IOException, JSONException {

        Log.i(TAG, String.format("sending with token: %s to %s", token, url));

        Request request = new Request.Builder()
                .url(url)
                .header("x-auth-token", token)
                .build();

        Response response = client.newCall(request).execute();
        return new JSONObject(response.body().string());
    }

    public void checkException() throws ServerFalseException {
        if (exception != null)
            throw exception;
    }
}
