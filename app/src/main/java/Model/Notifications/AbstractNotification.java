package Model.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.modamedicandroidapplication.R;

import Controller.AppController;
import Model.Questionnaires.AnswersManager;
import Model.Questionnaires.Questionnaire;
import Model.Utils.HttpRequests;
import View.QuestionnaireActivity;
import View.ViewUtils.BindingValues;

public abstract class AbstractNotification extends BroadcastReceiver {

    protected static String CHANNEL_ID = "MainChannel";
    public static long ONE_MINUTE = -1; // 1 * 60 * 1000;


    /*
   this method should send notifications to user
    */
    public static void notifyUser(Context context, String notification_text, int id, long questionnaire_id) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = setQuestionnaireActivity(questionnaire_id,context);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, intent, PendingIntent.FLAG_ONE_SHOT);

                Notification notification = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setContentTitle(context.getString(R.string.app_name))
                            .setContentText(context.getString(R.string.reminder))
                            .setContentIntent(pendingIntent)
                            .addAction(android.R.drawable.sym_action_chat, context.getString(R.string.notification_action), pendingIntent)
                            .setSmallIcon(R.drawable.notif_icon)
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

    protected static boolean HasUserAnswered(String questionnaire_id, Context context) {
        if (true)
            return false;
        String days;
        if (questionnaire_id.equals("0")) // daily questionnaire
            days = "0";
        else
          days = "3";
        //days = PropertiesManager.getProperty(Configurations.daysWithoutAnsweringQuestionnaireBeforeSendingPeriodicNotification,context);
        return AnswersManager.hasUserAnswered(questionnaire_id,days, HttpRequests.getInstance(context));
    }

    private static Intent setQuestionnaireActivity(Long questionnaire_id, Context context) {
        AppController appController = AppController.getController(null);
        Questionnaire questionnaire = appController.getQuestionnaire(questionnaire_id);
        Intent intent = null;
        intent = new Intent(context, QuestionnaireActivity.class);
        if (intent.hasExtra(BindingValues.REQUESTED_QUESTIONNAIRE))
            intent.removeExtra(BindingValues.REQUESTED_QUESTIONNAIRE);
        intent.putExtra(BindingValues.REQUESTED_QUESTIONNAIRE, questionnaire);
        return intent;
    }


}
