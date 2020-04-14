package Model.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.modamedicandroidapplication.R;

import Model.Questionnaires.AnswersManager;
import Model.Utils.Configurations;
import Model.Utils.HttpRequests;
import Model.Utils.PropertiesManager;

public abstract class AbstractNotification extends BroadcastReceiver {

    String CHANNEL_ID = "MainChannel";

    /*
   this method should send notifications to user
    */
    public void notify(Class activity_class, Context context, String notification_text, int id) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, activity_class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, intent, PendingIntent.FLAG_ONE_SHOT);

                Notification notification = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setContentTitle(context.getString(R.string.app_name))
                            .setContentText(context.getString(R.string.reminder))
                            .setContentIntent(pendingIntent)
                            .addAction(android.R.drawable.sym_action_chat, context.getString(R.string.notification_action), pendingIntent)
                            .setSmallIcon(android.R.drawable.sym_def_app_icon)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(notification_text))
                            .build();
                }

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                notificationManager.notify(id, notification);
            }
        });

        t.start();


    }

    protected boolean HasUserAnswered(String questionnaire_id, Context context) {
        String days = PropertiesManager.getProperty(Configurations.daysWithoutAnsweringQuestionnaireBeforeSendingPeriodicNotification,context);
         return AnswersManager.hasUserAnswered(questionnaire_id,days, HttpRequests.getInstance(context));
    }


}
