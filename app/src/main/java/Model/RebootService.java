package Model;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Objects;

import Controller.AppController;

public class RebootService extends IntentService {
    private static final String TAG = "RebootService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public RebootService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String intentType = Objects.requireNonNull(Objects.requireNonNull(intent).getExtras()).getString("caller");
        if(intentType == null)
            return;
        if(intentType.equals("RebootReceiver")) {
            Log.i(TAG,"started after reboot");
            AppController appController = AppController.getController(null);
            appController.setNotifications(getApplicationContext());
            appController.setMetricsTask(getApplicationContext());
            appController.setLocationTrackerTask(getApplicationContext());

        }

    }
}
