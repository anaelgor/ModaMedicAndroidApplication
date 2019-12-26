package Model;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GPS implements LocationListener {

    private String lon;
    private String lat;

    public GPS() {
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
        return result.toString();
    }

    public String getLocationJSON(){
        String locationJSON = null;

        try {
            String start = "https://api.openweathermap.org/data/2.5/weather?";
            String params = "lat=" + lat + "&lon=" + lon + "&units=metric" + "&appid=";
            String api_key = "1386e5ee723d21215441dba004fea3de";
            final String all = start + params + api_key;

            String result = null;
            result = getHTML(all);
            Log.i("Weather", "**********" + result + "**********");

            locationJSON = result;
//
//            long sunrise = 1575865668;
//            long sunset = 1575902301;
//
//            int seconds = (int) (sunset / 1000) % 60;
//            int minutes = (int) ((sunset / (1000 * 60)) % 60);
//            int hours = (int) ((sunset / (1000 * 60 * 60)) % 24);
//            String res = ("" + hours + ":" + minutes + ":" + seconds);
//            System.out.println("Sunset Time:" + res);
        }
        catch (Exception e){
            Log.e("Location unavailable","***********CANT FIND LOCATION**********");
        }


        return locationJSON;
    }

    @Override
    public void onLocationChanged(Location location) {
        String loc = location.toString();
        Log.i("Location tracker:", loc);

        this.lat = Double.toString(location.getLatitude());
        this.lon = Double.toString(location.getLongitude());
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
}
