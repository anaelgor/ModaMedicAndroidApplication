package Model.Metrics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import Model.Users.Login;
import Model.Utils.Constants;
import Model.Utils.HttpRequests;
import Model.Utils.Urls;

public class Weather implements LocationListener, DataSender {

    private String lon;
    private String lat;
    private JSONObject jsonObject;
    private static final String TAG = "Weather";
    private Context context;

    @SuppressLint("MissingPermission")
    public Weather(LocationManager locationManager, Context context) {
        this.context = context;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5, this);
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

    public String extractDataForWeather(){
        String locationJSON = null;

        if (lat == null || lon == null){
            SharedPreferences sharedPref = context.getSharedPreferences(Constants.sharedPreferencesName,Context.MODE_PRIVATE);
            String lat = sharedPref.getString("lat", "0");
            String lon = sharedPref.getString("lon", "0");
            if (lat.equals("0") || lon.equals("0")){
                Log.w("Location unavailable","***********CANT FIND LOCATION**********");
                return null;
            }
        }

        try {
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

            locationJSON = result;

        }
        catch (Exception e){
            e.printStackTrace();
            Log.e("Location unavailable","***********CANT FIND LOCATION**********");
        }


        return locationJSON;
    }

    @Override
    public void onLocationChanged(Location location) {
        String loc = location.toString();
        Log.i("Location tracker:", loc);

        SharedPreferences sharedPref = context.getSharedPreferences(Constants.sharedPreferencesName, Context.MODE_PRIVATE);

        if (Double.toString(location.getLatitude()) != null && Double.toString(location.getLongitude()) !=null)
        {
            this.lat = Double.toString(location.getLatitude());
            this.lon = Double.toString(location.getLongitude());

            sharedPref.edit().putString("lat",lat).apply();
            sharedPref.edit().putString("lon",lon).apply();

        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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
            httpRequests.sendPostRequest(makeBodyJson(), Urls.urlPostWeather, Login.getToken());
        }
        catch (Exception e){
            Log.e(TAG, "No data in weather.");
            e.printStackTrace();
        }
    }
}
