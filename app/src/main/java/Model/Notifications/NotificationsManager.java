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

import Model.Exceptions.KeyIsNotExistsException;
import Model.Utils.Configurations;
import Model.Utils.Constants;

import static android.content.Context.ALARM_SERVICE;

public class NotificationsManager {

    AlarmManager alarmManager;
    Context context;
    private static final String TAG = "NotificationsManager";


    public NotificationsManager(Context context) {
        this.context = context;
    }

    //todo: maybe this should be written only after HomePageActivity, because only there we have the logged in user.
    //todo: also, add an option to configure the time by a configurations file
    public void setNotifications() {
        createNotificationChannel("MainChannel");
        if (alarmManager == null)
            alarmManager = (AlarmManager) (context.getSystemService(ALARM_SERVICE));

        try {
//            int periodic_minute = Configurations.getInt(context, Constants.PERIODIC_NOTIFICATIONS_MINUTES);
//            int periodic_hour =  Configurations.getInt(context, Constants.PERIODIC_NOTIFICATIONS_HOUR);
//            int daily_minute = Configurations.getInt(context,Constants.DAILY_NOTIFICATIONS_MINUTES);
//            int daily_hour = Configurations.getInt(context,Constants.DAILY_NOTIFICATIONS_HOUR);


            int periodic_minute =35;
            int periodic_hour =  15;
            int daily_minute = 35;
            int daily_hour = 15;
            Calendar daily_calendar = Calendar.getInstance();
            daily_calendar.setTimeInMillis(System.currentTimeMillis());
            daily_calendar.set(Calendar.HOUR_OF_DAY, daily_hour);
            daily_calendar.set(Calendar.MINUTE, daily_minute);

            Calendar periodic_calendar = Calendar.getInstance();
            periodic_calendar.setTimeInMillis(System.currentTimeMillis());
            periodic_calendar.set(Calendar.HOUR_OF_DAY, periodic_hour);
            periodic_calendar.set(Calendar.MINUTE, periodic_minute);

            setRepeatingNotification(DailyNotification.class, daily_calendar.getTimeInMillis() + randomTime(), AlarmManager.INTERVAL_DAY);
            //Periodic notification
            setRepeatingNotification(PeriodicNotification.class, periodic_calendar.getTimeInMillis() + randomTime(), AlarmManager.INTERVAL_DAY);
            throw new KeyIsNotExistsException("log");
        } catch (KeyIsNotExistsException e) {
            Log.e(TAG,"Can't find keys of configuration. fatal error!");
            e.printStackTrace();
        }



    }

    private void setRepeatingNotification(Class notification_class, long time, long interval) {
        Intent intent = new Intent(context, notification_class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, interval, pendingIntent);
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
        if (true)
            return 0;
        return min + (long) (Math.random() * (max - min));
    }

}
