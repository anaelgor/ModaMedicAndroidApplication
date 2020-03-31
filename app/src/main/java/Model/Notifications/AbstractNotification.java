package Model.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.widget.DialogTitle;

import com.example.modamedicandroidapplication.R;

public abstract class AbstractNotification extends BroadcastReceiver {

    String CHANNEL_ID = "Main AbstractNotification Channel";

    /*
   this method should send daily notification to user
    */
    protected void notify(Class activity_class, Context context, String notification_text, int id) {
        Intent intent = new Intent(context, activity_class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1,intent, PendingIntent.FLAG_ONE_SHOT);

        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(context)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(notification_text)
                    .setContentIntent(pendingIntent)
                    .addAction(android.R.drawable.sym_action_chat, context.getString(R.string.notification_action), pendingIntent)
                    .setSmallIcon(android.R.drawable.sym_def_app_icon)
                    .setChannelId(CHANNEL_ID)
                    .build();
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
        System.out.println("maor12345");


    }


}
