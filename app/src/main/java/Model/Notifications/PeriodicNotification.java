package Model.Notifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.modamedicandroidapplication.R;

import java.util.Calendar;
import java.util.Map;

import Model.Questionnaires.QuestionnaireSenderAndReceiver;
import Model.Utils.Constants;
import Model.Utils.HttpRequests;
import View.MainActivity;

public class PeriodicNotification extends AbstractNotification{



    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.sharedPreferencesName,Context.MODE_PRIVATE);
        long lastLogin = sharedPref.getLong("lastLogin",0);
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (lastLogin > currentTime -(10 * 60 * 1000)) {
            Log.i("Periodic","missing periodic notification because I have been in the app in the last 10 min");
            return;
        }


        Map<Long,String> Questionnaires = getAllQuestionairesOfUser(context);
        for (Long questionnaireID: Questionnaires.keySet()) {
            if (questionnaireID == 0)
                continue;
            boolean answered = HasUserAnswered(questionnaireID.toString(), context);
            if (!answered) {
                String notification_text = context.getString(R.string.periodic_questionnaire_notification_pref) + " " +
                        Questionnaires.get(questionnaireID) + context.getString(R.string.periodic_questionnaire_notification_suffix);
                int id = 100;
                notify(context, notification_text, id,questionnaireID);
                System.out.println("Periodically for questionnaire " + Questionnaires.get(questionnaireID));
                try {
                    Thread.sleep(5000); //for avoid android block our app from posting notifications
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private Map<Long, String> getAllQuestionairesOfUser(Context context) {
      return QuestionnaireSenderAndReceiver.getUserQuestionnaires(HttpRequests.getInstance(context));


}

}
