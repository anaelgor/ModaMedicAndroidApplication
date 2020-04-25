package Model.Notifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.modamedicandroidapplication.R;

import java.util.Calendar;

import Model.Utils.Constants;
import View.MainActivity;

public class DailyNotification extends AbstractNotification {


    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.sharedPreferencesName,Context.MODE_PRIVATE);
        long lastLogin = sharedPref.getLong("lastLogin",0);
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (lastLogin > currentTime -(10 * 60 * 1000)) {
            Log.i("Daily","missing daily notification because I have been in the app in the last 10 min");
            return;
        }


        boolean answered = HasUserAnswered("0", context);
        if (!answered) {
            String notification_text = context.getString(R.string.daily_questionnaire_notification);
            int id = 101;
            notify(context,notification_text,id, 0);
            System.out.println("Daily Questionnaire notification");
        }

    }



}
