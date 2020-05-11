package Model.Metrics;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import Model.Users.Login;
import Model.Utils.HttpRequests;
import Model.Utils.Urls;

public class Weather implements DataSender {

    private JSONObject jsonObject;
    private static final String TAG = "Weather";
    private Context context;

    public Weather(Context context) {
        this.context = context;
    }

    public String getHTML(String urlToRead) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        conn.disconnect();
        return result.toString();
    }

    public void extractDataForWeather() {

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocationGPS != null) {
                handleLocation(lastKnownLocationGPS);
            } else {
                Location loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                handleLocation(loc);
            }
        } else {
            return ;
        }

    }
    private void handleLocation(Location location){
        String lon, lat;
        try {
            lat = Double.toString(location.getLatitude());
            lon = Double.toString(location.getLongitude());

            Log.i(TAG, "handleLocation: lon = " + lon + ", lat = " + lat);

            String start = "https://api.openweathermap.org/data/2.5/weather?";
            String params = "lat=" + lat + "&lon=" + lon + "&units=metric" + "&appid=";
            String api_key = "1386e5ee723d21215441dba004fea3de";
            final String all = start + params + api_key;
            Log.i("Input string", "GOT INPUT FOR WEATHER API: " + all);

            String result = getHTML(all);

            Log.i("Weather", "**********" + result + "**********");


            JSONObject resultFromWeb = new JSONObject(result);
            resultFromWeb = new JSONObject(String.valueOf(resultFromWeb.getJSONObject("main")));

            jsonObject = new JSONObject();
            jsonObject.put("Low", resultFromWeb.getString("temp_min"));
            jsonObject.put("High", resultFromWeb.getString("temp_max"));
            jsonObject.put("Humidity", resultFromWeb.getString("humidity"));

            sendDataToServer(HttpRequests.getInstance(context));

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Location unavailable", "***********CANT FIND LOCATION**********");
        }

    }

    public JSONObject makeBodyJson() throws JSONException{
        JSONObject json = new JSONObject();
        if (this.jsonObject == null){
            throw new NullPointerException(); //no weather data
        }
        json.put("ValidTime", System.currentTimeMillis());
        json.put("Data", this.jsonObject);
        return json;
    }

    @Override
    public void sendDataToServer(HttpRequests httpRequests){
        try {
            httpRequests.sendPostRequest(makeBodyJson(), Urls.urlPostWeather, Login.getToken(HttpRequests.getContext()));
        }
        catch (Exception e){
            Log.e(TAG, "No data in weather.");
            e.printStackTrace();
        }
    }
}
