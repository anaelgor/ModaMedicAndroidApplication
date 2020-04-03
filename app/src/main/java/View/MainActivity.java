package View;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.modamedicandroidapplication.R;

import java.util.Calendar;

import Model.DailyNotification;
import Model.PeriodicNotification;

import Controller.AppController;
import Model.Permissions;

/*
Home page screen
 */
public class MainActivity extends AppCompatActivity {
    //todo: this should be moved to controoler
    AlarmManager alarmManager = null;

    private String username;
    private String password;

    public Activity getContext() {
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setHideKeyBoard();


        /**
         * PERMMISIONS REQUEST
         * ALL YOU NEED TO DO IS TO INSERT THE PERMISSION NAME TO THE MANIFEST FILE
         */
        Permissions permissions = new Permissions(this);
        try {
            permissions.requestPermissions();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //end permissions requests


    }

    private void setHideKeyBoard() {
        EditText password_textfield = findViewById(R.id.password_textfield);
        View.OnFocusChangeListener ofcListener = new MyFocusChangeListener();
        password_textfield.setOnFocusChangeListener(ofcListener);

    }

    private class MyFocusChangeListener implements View.OnFocusChangeListener {

        public void onFocusChange(View v, boolean hasFocus){

            if((v.getId() == R.id.password_textfield )&& !hasFocus ) {

                InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            }
        }
    }

    //todo: move this to controller. also check what happen if user open the app again,
    // probably this should be implemented from getInstance. this should be written from
    // HomePageActivity, because only there we have the logged in user.
    private void setNotifcations() {
        if (alarmManager == null)
            alarmManager = (AlarmManager) (this.getSystemService(Context.ALARM_SERVICE));

        //Daily notification - one in 16:00 and one in 19:00
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 16);
        calendar.set(Calendar.MINUTE, 0);
        Calendar calendar2 = Calendar.getInstance();

        calendar2.set(Calendar.HOUR, 19);
        calendar2.set(Calendar.MINUTE, 0);

        setRepeatingNotification(DailyNotification.class, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY);
        setRepeatingNotification(DailyNotification.class, calendar2.getTimeInMillis(), AlarmManager.INTERVAL_DAY);

        //Periodic notification
        setRepeatingNotification(PeriodicNotification.class, calendar2.getTimeInMillis(), AlarmManager.INTERVAL_DAY);

    }

    private void setRepeatingNotification(Class notification_class, long time, long interval) {
        Intent intent = new Intent(MainActivity.this, notification_class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 111, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, interval, pendingIntent);

    }

    public void loginFunction(View view) {
        Log.i("Main Page", "Login button clicked");
        EditText username_textfield = findViewById(R.id.username_textfield);
        EditText password_textfield = findViewById(R.id.password_textfield);

        this.username = username_textfield.getText().toString();
        this.password = password_textfield.getText().toString();

        Log.i("Main Page", "User " + username + " with password " + password + " logged in");
        AppController controller = AppController.getController(this);
        boolean logged = controller.login(username, password, this);
        if (logged) {
            Intent intent = new Intent(this, HomePageActivity.class);
            intent.putExtra(BindingValues.LOGGED_USERNAME, username);
            startActivity(intent);
        } else {
            Log.i("Main Page", "wrong password or user name for username: " + username);
            WrongDetailsMessage();

        }
    }
    //todo: implements this

    public void forgetPasswordFunction(View view) {
        Log.i("Main Page", "Forgot password button clicked");
    }

    //todo: add forgot password button to here
    private void WrongDetailsMessage() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.error)
                .setMessage(R.string.wrongDetails)
                .setNegativeButton(R.string.tryAgain, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
