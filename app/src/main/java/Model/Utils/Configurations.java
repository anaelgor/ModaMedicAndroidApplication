package Model.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import Model.Exceptions.KeyIsNotExistsException;

import static android.content.Context.MODE_PRIVATE;

public class Configurations {
    private static final String timeForDailyNotification = "timeForDailyNotification";
    private static final String timeForPeriodicNotification = "timeForPeriodicNotification";
    private static final String daysWithoutAnsweringQuestionnaireBeforeSendingPeriodicNotification = "daysWithoutAnsweringQuestionnaireBeforeSendingPeriodicNotification";
    private static final String timeForMissedMetricsCheckTask = "timeForMissedMetricsCheckTask";



    private static int getNotificationHour(Context context, String type) {
        String time;
        if (type.equals("daily"))
            time = PropertiesManager.getProperty(Configurations.timeForDailyNotification, context);
        else
            time = PropertiesManager.getProperty(Configurations.timeForPeriodicNotification, context);
        assert time != null;
        return Integer.parseInt(time.split(":")[0]);
    }

    private static int getNotificationMinute(Context context, String type) {
        String time;
        if (type.equals("daily"))
            time = PropertiesManager.getProperty(Configurations.timeForDailyNotification, context);
        else
            time = PropertiesManager.getProperty(Configurations.timeForPeriodicNotification, context);
        assert time != null;
        return Integer.parseInt(time.split(":")[1]);
    }

    private static int getMetricsTaskMinute(Context context) {
        String time = PropertiesManager.getProperty(Configurations.timeForMissedMetricsCheckTask, context);
        return Integer.parseInt(time.split(":")[1]);
    }

    private static int getMetricsTaskHour(Context context) {
        String time = PropertiesManager.getProperty(Configurations.timeForMissedMetricsCheckTask, context);
        return Integer.parseInt(time.split(":")[0]);
    }

    public static void persistConfigurationsInSharedPreferences(Context context) {
        String daysWithoutAnsweringQuestionnaireBeforeSendingPeriodicNotification = PropertiesManager.getProperty(Configurations.daysWithoutAnsweringQuestionnaireBeforeSendingPeriodicNotification,context);
        int metric_hour = getMetricsTaskHour(context);
        int metric_minutes = getMetricsTaskMinute(context);
        int notification_daily_hour = getNotificationHour(context,"daily");
        int notification_daily_minutes= getNotificationMinute(context,"daily");
        int notification_periodic_hour = getNotificationHour(context,"periodic");
        int notification_periodic_minute= getNotificationMinute(context,"periodic");

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.sharedPreferencesName,MODE_PRIVATE);
        sharedPreferences.edit().putInt(Constants.MISSING_METRICS_HOUR,metric_hour).apply();
        sharedPreferences.edit().putInt(Constants.MISSING_METRICS_MINUTES,metric_minutes).apply();
        sharedPreferences.edit().putInt(Constants.DAILY_NOTIFICATIONS_HOUR,notification_daily_hour).apply();
        sharedPreferences.edit().putInt(Constants.DAILY_NOTIFICATIONS_MINUTES,notification_daily_minutes).apply();
        sharedPreferences.edit().putInt(Constants.PERIODIC_NOTIFICATIONS_HOUR,notification_periodic_hour).apply();
        sharedPreferences.edit().putInt(Constants.PERIODIC_NOTIFICATIONS_MINUTES,notification_periodic_minute).apply();
        sharedPreferences.edit().putString(Constants.DAYS_WITHOUT_ANSWERING_BEFORE_PUSH_NOTIFICATION,daysWithoutAnsweringQuestionnaireBeforeSendingPeriodicNotification).apply();
    }

    public static String getString(Context context, String key) throws KeyIsNotExistsException {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.sharedPreferencesName,MODE_PRIVATE);
        String result = sharedPreferences.getString(key,"notExists");
        if (result.equals("notExists"))
            throw new KeyIsNotExistsException("notExists");
        return result;
    }
    public static int getInt(Context context, String key) throws KeyIsNotExistsException {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.sharedPreferencesName,MODE_PRIVATE);
        int result = sharedPreferences.getInt(key,-1);
        if (result == -1)
            throw new KeyIsNotExistsException("notExists");
        return result;
    }

}
