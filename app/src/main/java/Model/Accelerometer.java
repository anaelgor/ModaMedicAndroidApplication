package Model;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Accelerometer implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private List data;

    private static final String TAG = "Accelerometer";

    public Accelerometer(Context context) {
        this.data = new ArrayList<>();
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        JSONObject json = new JSONObject();
        try {
            json.put("Timestamp", System.currentTimeMillis());
            json.put("X", event.values[0]);
            json.put("Y", event.values[1]);
            json.put("Z", event.values[2]);

            this.data.add(json);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // will not be implemented
    }

    public JSONObject getData(){
        JSONObject json = new JSONObject();

        try {
            json.put("UserId", "111111111");
            json.put("ValidateTime", System.currentTimeMillis());
            json.put("Data", this.data.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.data.clear();

        return json;

    }
}
