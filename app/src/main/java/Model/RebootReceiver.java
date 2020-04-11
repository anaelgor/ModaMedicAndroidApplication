package Model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RebootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, RebootService.class);
        serviceIntent.putExtra("caller", "RebootReceiver");
        context.startService(serviceIntent);
    }
}
