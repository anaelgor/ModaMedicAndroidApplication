package View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.modamedicandroidapplication.R;

import java.util.Calendar;

import Model.DailyNotification;
import Model.PeriodicNotification;

import static View.BindingValues.LOGGED_USERNAME;
import Controller.AppController;
import Model.GPS;
import Model.Permissions;

/*
Home page screen
 */
public class MainActivity extends AppCompatActivity {
    //todo: this should be moved to controoler
    AlarmManager alarmManager = null;

    private String username;
    private String password;
    public Activity getContext(){
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setNotifcations();


        /**
         * PERMMISIONS REQUEST
         * ALL YOU NEED TO DO IS TO INSERT THE PERMISSION NAME TO THE MANIFEST FILE
         */
        Permissions permissions = new Permissions(this);
        try {
            permissions.requestPermissions();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //end permissions requests

        AppController app = AppController.getController(this);
        Thread t_sensorData = new Thread(new Runnable() {
            @Override
            public void run() {
                AppController app = AppController.getController(getContext());
                app.ExtractSensorData();
            }
        });
        t_sensorData.start();
    }

    //todo: move this to controller. also check what happen if user open the app again,
    // probably this should be implemented from getInstance. this should be written from
    // HomePageActivity, because only there we have the logged in user.
    private void setNotifcations() {
        if (alarmManager == null)
            alarmManager = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));

        //Daily notification - one in 16:00 and one in 19:00
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR,16);
        calendar.set(Calendar.MINUTE,0);
        Calendar calendar2 = Calendar.getInstance();

        calendar2.set(Calendar.HOUR,19);
        calendar2.set(Calendar.MINUTE,0);

        setRepeatingNotification(DailyNotification.class, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY);
        setRepeatingNotification(DailyNotification.class, calendar2.getTimeInMillis(), AlarmManager.INTERVAL_DAY);

        //Periodic notification
        setRepeatingNotification(PeriodicNotification.class, calendar2.getTimeInMillis(), AlarmManager.INTERVAL_DAY);

    }

    private void setRepeatingNotification(Class notification_class, long time, long interval) {
        Intent intent = new Intent(MainActivity.this,notification_class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 111, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time,interval, pendingIntent);

    }

    //todo: implements this
    public void loginFunction(View view) {
        Log.i("Main Page","Login button clicked");
        EditText username_textfield = findViewById(R.id.username_textfield);
        EditText password_textfield = findViewById(R.id.password_textfield);

        this.username = username_textfield.getText().toString();
        this.password = password_textfield.getText().toString();

        //todo: change this the user REAL name from db. "a" and "a" only for checks
        Log.i("Main Page", "User " + username + " with password " + password + " logged in");
        if (username.equals("a") && password.equals("a")) {
            Intent intent = new Intent(this, HomePageActivity.class);
            intent.putExtra(BindingValues.LOGGED_USERNAME, username);
            startActivity(intent);
        }

    }
    //todo: implements this

    public void forgetPasswordFunction(View view) {
        Log.i("Main Page","Forgot password button clicked");

    }
}
