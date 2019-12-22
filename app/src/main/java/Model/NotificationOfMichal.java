package Model;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.modamedicandroidapplication.R;
import View.MainActivity;

import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class NotificationOfMichal extends BroadcastReceiver {

    String CHANNEL_ID = "Main Notifications Channel";



    @Override
    public void onReceive(Context context, Intent intent) {
        String text = "יאללה כנס יא טמבלול8888";
        notifications_init(context);
        notifications(MainActivity.class,text, context);
        System.out.println("MAOR");

//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        Intent repeating_intent = new Intent(context, MainActivity.class);
//        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,repeating_intent,0);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
//                .setContentIntent(pendingIntent)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle(context.getString(R.string.app_name))
//                .setContentText(context.getString(R.string.notificationText))
//                .setAutoCancel(true);
//
//        notificationManager.notify(1,builder.build());

    }


    private void notifications_init(Context context) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Create the NotificationChannel
            CharSequence name = "Basic Notifications";
            String description = "This Channel is for Notifications of the basic notification category. User sees this in the system settings.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);
        }

    }

    /*
    this method should send daily notification to user
     */
    private void notifications(Class activity_class, String text, Context context) {
        Intent intent = new Intent(context, activity_class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1,intent, PendingIntent.FLAG_ONE_SHOT);



        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(context)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(text)
                    .setContentIntent(pendingIntent)
                    .addAction(android.R.drawable.sym_action_chat, context.getString(R.string.notification_action), pendingIntent)
                    .setSmallIcon(android.R.drawable.sym_def_app_icon)
                    .setChannelId(CHANNEL_ID)

                    .build();
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        notificationManager.notify(2, notification);

    }


}
