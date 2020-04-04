package Model.Notifications;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class NotificationsManager {

    AlarmManager alarmManager;
    Context context;

    public NotificationsManager(Context context) {
        this.context = context;
    }

    //todo: maybe this should be written only after HomePageActivity, because only there we have the logged in user.
    //todo: also, add an option to configure the time by a configurations file
    public void setNotifications() {
        createNotificationChannel("MainChannel");
        if (alarmManager == null)
            alarmManager = (AlarmManager) (context.getSystemService(ALARM_SERVICE));

        //Daily notification - one in 16:00 and one in 19:00
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 19);
        calendar.set(Calendar.MINUTE, 15);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(System.currentTimeMillis());
        calendar2.set(Calendar.HOUR_OF_DAY, 19);
        calendar2.set(Calendar.MINUTE, 10);

        setRepeatingNotification(DailyNotification.class, calendar.getTimeInMillis() , AlarmManager.INTERVAL_DAY);
        setRepeatingNotification(DailyNotification.class, calendar2.getTimeInMillis(), AlarmManager.INTERVAL_DAY);
        //Periodic notification
        setRepeatingNotification(PeriodicNotification.class, calendar2.getTimeInMillis(), AlarmManager.INTERVAL_DAY);
    }

    private void setRepeatingNotification(Class notification_class, long time, long interval) {
        Intent intent = new Intent(context, notification_class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, interval, pendingIntent);

    }


    private void createNotificationChannel(String CHANNEL_ID) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MainChanneel";
            String description = "MainChanneel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
