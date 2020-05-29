package View.ViewUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.example.modamedicandroidapplication.R;

public class MessageUtils {

    public static void showAlert(Context context, String msg) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.error)
                .setMessage(msg)
                .setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Activity activity = (Activity)context;
                        activity.finishAffinity();
                    }
                })
                .setNegativeButton(R.string.tryAgain, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Activity activity = (Activity)context;
                        Intent intent = activity.getIntent();
                        activity.finish();
                        context.startActivity(intent);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
