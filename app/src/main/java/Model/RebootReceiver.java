package Model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import Controller.AppController;
import Model.Notifications.NotificationsManager;

public class RebootReceiver extends BroadcastReceiver {
    private static final String TAG = "RebootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            NotificationsManager notificationsManager = new NotificationsManager(context);
            notificationsManager.setNotifications();
            AppController appController = AppController.getController(null);
            appController.setMissingMetricsTask(context);
            appController.setMetricsTask(context);
            Log.i(TAG,"started after reboot. all of tasks has been set!");
        }

    }
}
