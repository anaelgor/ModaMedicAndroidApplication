package Model.Utils;

import android.content.Context;

public class Configurations {
    private static final String timeForDailyNotification = "timeForDailyNotification";
    private static final String timeForPeriodicNotification = "timeForPeriodicNotification";
    public static final String daysWithoutAnsweringQuestionnaireBeforeSendingPeriodicNotification = "daysWithoutAnsweringQuestionnaireBeforeSendingPeriodicNotification";

    public static int getNotificationHour(Context context, String type) {
        String time;
        if (type.equals("daily"))
            time = PropertiesManager.getProperty(Configurations.timeForDailyNotification, context);
        else
            time = PropertiesManager.getProperty(Configurations.timeForPeriodicNotification, context);
        assert time != null;
        return Integer.parseInt(time.split(":")[0]);
    }

    public static int getNotificationMinute(Context context, String type) {
        String time;
        if (type.equals("daily"))
            time = PropertiesManager.getProperty(Configurations.timeForDailyNotification, context);
        else
            time = PropertiesManager.getProperty(Configurations.timeForPeriodicNotification, context);
        assert time != null;
        return Integer.parseInt(time.split(":")[1]);
    }
}
