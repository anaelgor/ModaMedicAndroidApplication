package View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.example.modamedicandroidapplication.R;

import java.util.Calendar;

import Controller.AppController;
import Model.NotificationOfMichal;
import Model.Permissions;

/*
Home page screen
 */
public class MainActivity extends AppCompatActivity {
    private String CHANNEL_ID = "Main Notifications Channel";
    AlarmManager alarmManager = null;
    LocationManager locationManager;

    public Activity getContext(){
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


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
        System.out.println();



        String text = "יאללה כנס יא טמבל";
        notifications_init();
        notifications(MainActivity.class,text);
        michalnotif();
    }





    private void michalnotif() {
        alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(getApplicationContext(), NotificationOfMichal.class);
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.SECOND,+20);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.MINUTE, 24);

// setRepeating() lets you specify a precise custom interval--in this case,
// 20 minutes.

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        //todo: check why this is not working https://developer.android.com/training/scheduling/alarms#java
        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    1000 * 60 * 1, pendingIntent);
           // alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 5*1000, 1000, pendingIntent);
        }

    }

    private void notifications_init() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Create the NotificationChannel
            CharSequence name = "Basic Notifications";
            String description = "This Channel is for Notifications of the basic notification category. User sees this in the system settings.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);
        }

    }

    /*
    this method should send daily notification to user
     */
    private void notifications(Class activity_class, String text) {
        Intent intent = new Intent(getApplicationContext(), activity_class);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1,intent, 0);



        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(getApplicationContext())
                    .setContentTitle(getApplicationContext().getString(R.string.app_name))
                    .setContentText(text)
                    .setContentIntent(pendingIntent)
                    .addAction(android.R.drawable.sym_action_chat, getApplicationContext().getString(R.string.notification_action), pendingIntent)
                    .setSmallIcon(android.R.drawable.sym_def_app_icon)
                    .setChannelId(CHANNEL_ID)

                    .build();
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(1, notification);

    }
}
