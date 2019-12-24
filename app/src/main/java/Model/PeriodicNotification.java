package Model;

import android.content.Context;
import android.content.Intent;

import com.example.modamedicandroidapplication.R;

import java.util.ArrayList;
import java.util.List;

import View.MainActivity;

public class PeriodicNotification extends AbstractNotification{



    //TODO: change Main Activity to this Question activity
    @Override
    public void onReceive(Context context, Intent intent) {
        List<String> Questionnaires = getAllQuestionairesOfUser("");
        for ( String questionnaire: Questionnaires) {
            boolean answered = checkIfUserAnsweredInLast21Days("",questionnaire);
            if (!answered) {
                String notification_text = context.getString(R.string.periodic_questionnaire_notification_pref) +
                        questionnaire + context.getString(R.string.periodic_questionnaire_notification_suffix);
                int id = 3;
                notify(MainActivity.class, context, notification_text, id);
                System.out.println("Periodically");
            }
        }


    }

    // TODO: implement with db
    private boolean checkIfUserAnsweredInLast21Days(String username, String questionnaire_name) {
        return false;
    }

    // TODO: implement with db
    private List<String> getAllQuestionairesOfUser(String usrrname) {
        List<String> res =  new ArrayList<>();
        res.add("Pain");
        return res;


}

}
