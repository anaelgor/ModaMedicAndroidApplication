package Model.Notifications;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.modamedicandroidapplication.R;

import View.MainActivity;

public class DailyNotification extends AbstractNotification {


    //TODO: change Main Activity to Daily Question activity
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean answered = checkIfUserAnsweredToday("");
        if (!answered) {
            String notification_text = context.getString(R.string.daily_questionnaire_notification);
            int daily_id = 2;
            notify(MainActivity.class, context,notification_text,daily_id);
            Log.i("Line22DailyNotification", "onReceive: ");
        }

    }
    // TODO: implement checkIfUserAnsweredToday with db
    private boolean checkIfUserAnsweredToday(String username) {
        return false;
    }




}
