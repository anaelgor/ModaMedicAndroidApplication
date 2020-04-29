package Model.Notifications;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;
import java.util.Random;

import Model.Utils.Configurations;

import static android.content.Context.ALARM_SERVICE;

public class NotificationsManager {

    AlarmManager alarmManager;
    Context context;
    private static final String TAG = "NotificationsManager";
    public NotificationsManager(Context context) {
        this.context = context;
    }

    public void setNotifications() {
        createNotificationChannel("MainChannel");
        if (alarmManager == null)
            alarmManager = (AlarmManager) (context.getSystemService(ALARM_SERVICE));

        int daily_minute = Configurations.getNotificationMinute(context,"daily");
        int daily_hour = Configurations.getNotificationHour(context,"daily");
        int periodic_minute = Configurations.getNotificationMinute(context,"periodic");
        int periodic_hour =  Configurations.getNotificationHour(context,"periodic");
        //Daily notification
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, daily_hour);
        calendar.set(Calendar.MINUTE, daily_minute);
        long dailyTime = calendar.getTimeInMillis() + randomTime();
      //  dailyTime = Calendar.getInstance().getTimeInMillis() +20000;

        //Periodic notification
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(System.currentTimeMillis());
        calendar2.set(Calendar.HOUR_OF_DAY, periodic_hour);
        calendar2.set(Calendar.MINUTE, periodic_minute);
        long periodicTime = calendar2.getTimeInMillis() + randomTime();
     //   periodicTime = Calendar.getInstance().getTimeInMillis() + 22000;

        setRepeatingNotification(DailyNotification.class, dailyTime , AlarmManager.INTERVAL_DAY);
        setRepeatingNotification(PeriodicNotification.class, periodicTime, AlarmManager.INTERVAL_DAY);
    }

    private void setRepeatingNotification(Class notification_class, long time, long interval) {
        Intent intent = new Intent(context, notification_class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, time, interval, pendingIntent);
        Log.i(TAG,String.format("notification %s has been set to %s", notification_class.toString(), time));
    }


    private void createNotificationChannel(String CHANNEL_ID) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MainChannel";
            String description = "MainChannel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private long randomTime() {
        long min = -600000;
        long max = 600000;
        return min + (long) (Math.random() * (max - min));
    }

}
