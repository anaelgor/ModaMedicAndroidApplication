package View;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.modamedicandroidapplication.R;

import java.util.Calendar;

import Model.DailyNotification;
import Model.PeriodicNotification;

/*
Home page screen
 */
public class MainActivity extends AppCompatActivity {
    //todo: this should be moved to controoler
    AlarmManager alarmManager = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setNotifcations();
    }

    //todo: move this to controller. also check what happen if user open the app again,
    // probably this should be implemented from getInstance
    private void setNotifcations() {
        if (alarmManager == null)
            alarmManager = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));

        //Daily notification - one in 16:00 and one in 19:00
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR,16);
        calendar.set(Calendar.MINUTE,0);
        Calendar calendar2 = Calendar.getInstance();

        calendar2.set(Calendar.HOUR,19);
        calendar2.set(Calendar.MINUTE,0);

        setRepeatingNotification(DailyNotification.class, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY);
        setRepeatingNotification(DailyNotification.class, calendar2.getTimeInMillis(), AlarmManager.INTERVAL_DAY);

        //Periodic notification
        setRepeatingNotification(PeriodicNotification.class, calendar2.getTimeInMillis(), AlarmManager.INTERVAL_DAY);

    }

    private void setRepeatingNotification(Class notification_class, long time, long interval) {
        Intent intent = new Intent(MainActivity.this,notification_class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 111, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time,interval, pendingIntent);

    }

}
