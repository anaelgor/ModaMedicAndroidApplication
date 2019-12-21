package com.example.modamedicandroidapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

/*
Home page screen
 */
public class MainActivity extends AppCompatActivity {
    private String CHANNEL_ID = "Main Notifications Channel";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String text = "יאללה כנס יא טמבל";
        String title = "Come answer Your daily Questions !";
        notifications_init();
        notifications(MainActivity.class,title,text);
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
    private void notifications(Class activity_class, String title, String text) {
        Intent intent = new Intent(getApplicationContext(), activity_class);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1,intent, 0);



        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(getApplicationContext())
                    .setContentTitle(title)
                    .setContentText(text)
                    .setContentIntent(pendingIntent)
                    .addAction(android.R.drawable.sym_action_chat, "Chat", pendingIntent)
                    .setSmallIcon(android.R.drawable.sym_def_app_icon)
                    .setChannelId(CHANNEL_ID)

                    .build();
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(1, notification);

    }
}
