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

public class PeriodicNotification extends AbstractNotification{



    @Override
    public void onReceive(Context context, Intent intent) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPref = context.getSharedPreferences(Constants.sharedPreferencesName,Context.MODE_PRIVATE);
                long lastLogin = sharedPref.getLong(Constants.LAST_LOGIN,0);
                long currentTime = Calendar.getInstance().getTimeInMillis();
                long duration = currentTime - lastLogin;
                if (duration < ONE_MINUTE) {
                    Log.i("Periodic","missing periodic notification because I have been in the app in the last 1 min");
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
                        notifyUser(context, notification_text, id,questionnaireID);
                        Log.i("Periodically","sending notification for questionnaire " + Questionnaires.get(questionnaireID));
                        break;
                    }
                    else {
                        Log.i("Daily",String.format("missing periodic %s notification because already answered in the last days",Questionnaires.get(questionnaireID)));
                    }
                }
            }
        });
        t.run();

    }


    private Map<Long, String> getAllQuestionairesOfUser(Context context) {
      return QuestionnaireSenderAndReceiver.getUserQuestionnaires(HttpRequests.getInstance(context));


}

}
