package Model.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.modamedicandroidapplication.R;

import Model.Questionnaires.AnswersManager;
import Model.Questionnaires.Questionnaire;
import Model.Questionnaires.QuestionnaireSenderAndReceiver;
import Model.Utils.Configurations;
import Model.Utils.HttpRequests;
import Model.Utils.PropertiesManager;
import View.QuestionnaireActivity;
import View.ViewUtils.BindingValues;

public abstract class AbstractNotification extends BroadcastReceiver {

    protected static String CHANNEL_ID = "MainChannel";
    public static long ONE_MINUTE = 1 * 60 * 1000;
    private final static String TAG = "Notification";

    /*
   this method should send notifications to user
    */
    public void notifyUser(Context context, String notification_text, int id, long questionnaire_id) {
        Log.i(TAG,"notifyUser");
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
        Log.i(TAG,"end notifyUser");

    }

    protected boolean HasUserAnswered(String questionnaire_id, Context context) {
        String days;
        if (questionnaire_id.equals("0")) // daily questionnaire
            days = "0";
        else
        days = PropertiesManager.getProperty(Configurations.daysWithoutAnsweringQuestionnaireBeforeSendingPeriodicNotification,context);
        return AnswersManager.hasUserAnswered(questionnaire_id,days, HttpRequests.getInstance(context));
    }

    private Intent setQuestionnaireActivity(Long questionnaire_id, Context context) {
        Questionnaire questionnaire = QuestionnaireSenderAndReceiver.getUserQuestionnaireById(questionnaire_id, HttpRequests.getInstance(context));
        Intent intent;
        intent = new Intent(context, QuestionnaireActivity.class);
        if (intent.hasExtra(BindingValues.REQUESTED_QUESTIONNAIRE))
            intent.removeExtra(BindingValues.REQUESTED_QUESTIONNAIRE);
        intent.putExtra(BindingValues.REQUESTED_QUESTIONNAIRE, questionnaire);
        return intent;
    }


}
