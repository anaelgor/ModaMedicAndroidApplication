package Model;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import Model.Exceptions.ServerFalseException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpRequests {//TODO: make this singleton after testing

    private SendRequestHttp sendRequestHttp;
    private static final String TAG = "HttpRequests";
    //public static final String urlPrefix = "http://127.0.0.1:3000/";
    //public static final String urlPrefix = "http://10.0.2.2:3000/";
    //public static final String urlPrefix = "http://localhost:3000/";



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
                JSONObject response;
                if (urls[0].equals("POST"))
                {
                    response = PostRequest(urls[1], urls[2]);
                }
                else // get request
                {
                    response = GetRequest(urls[1]);
                }

                if (response.getBoolean("error")) {
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

    public void checkException() throws ServerFalseException {
        if (exception != null)
            throw exception;
    }
}
