package Model.Notifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.modamedicandroidapplication.R;

import java.util.Calendar;

import Model.Utils.Constants;

public class DailyNotification extends AbstractNotification {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Daily","OnReceive");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sharedPref = context.getSharedPreferences(Constants.sharedPreferencesName, Context.MODE_PRIVATE);
                long lastLogin = sharedPref.getLong(Constants.LAST_LOGIN, 0);
                long currentTime = Calendar.getInstance().getTimeInMillis();
                long duration = currentTime - lastLogin;
                if (duration < ONE_MINUTE) {
                    Log.i("Daily", "missing daily notification because I have been in the app in the last 1 min");
                    return;
                }

                boolean answered = HasUserAnswered("0", context);
                if (!answered) {
                    String notification_text = context.getString(R.string.daily_questionnaire_notification);
                    int id = 101;
                    notifyUser(context, notification_text, id, 0);
                    System.out.println("Daily Questionnaire notification");
                } else {
                    Log.i("Daily", "missing daily notification because already answered today");
                }
            }
            });
        t.run();
    }
}

