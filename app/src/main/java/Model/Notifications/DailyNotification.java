package Model.Notifications;

import android.content.Context;
import android.content.Intent;

import com.example.modamedicandroidapplication.R;

import View.MainActivity;

public class DailyNotification extends AbstractNotification {


    @Override
    public void onReceive(Context context, Intent intent) {
        boolean answered = HasUserAnswered("0", context);
        if (!answered) {
            String notification_text = context.getString(R.string.daily_questionnaire_notification);
            int id = 101;
            notify(context,notification_text,id, 0);
            System.out.println("Daily Questionnaire notification");
        }

    }



}
