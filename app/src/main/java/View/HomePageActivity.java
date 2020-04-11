package View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.modamedicandroidapplication.R;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import Controller.AppController;
import Model.Questionnaires.Questionnaire;
import Model.Utils.Constants;

/*
Home page screen
 */
public class HomePageActivity extends AbstractActivity {
    private static final String TAG = "HomePageActivity";
    Map<Long,String> questionnaires; //key: questID, value: questionnaire Text
    String username;
    AppController appController;

    public static boolean BAND_CONNECTED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        username = getUserName();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        appController = AppController.getController(this);
        appController.setNotifications(getApplicationContext());
        appController.setMetricsTask(getApplicationContext());
        appController.setLocationTrackerTask(getApplicationContext());

        checkIfBandIsConnected();

        Thread t_sensorData = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                appController.SendSensorData();
            }
        });
        t_sensorData.start();

        questionnaires = getAllQuestionnaires();

        String not_exists = "not exists";
        SharedPreferences sharedPref = this.getSharedPreferences(Constants.sharedPreferencesName,Context.MODE_PRIVATE);
        String name = sharedPref.getString("name",not_exists);
        if (name.equals(not_exists)) {
            throw new NullPointerException("can't find username");
        }
        TextView good_eve = findViewById(R.id.good_evening_textView);
        good_eve.setText(String.format("%s %s", this.getString(R.string.hello), name));
        createAllButtons();
        updateBTState();

    }

    private String getUserName() {
        String not_exists = "not exists";
        SharedPreferences sharedPref = this.getSharedPreferences(Constants.sharedPreferencesName,Context.MODE_PRIVATE);
        String name = sharedPref.getString("username",not_exists);
        if (name.equals(not_exists))
            throw new NullPointerException("huge problem in getIUserName");
        return name;
    }

    private void createAllButtons() {
        LinearLayout  layout =  findViewById(R.id.lin_layout);

//
//
//        Button daily_questionnaire_button = new Button(this);
//        daily_questionnaire_button.setText(this.getString(R.string.daily_questionnaire));
//        setButtonConfiguration(daily_questionnaire_button);
//        daily_questionnaire_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openQuestionnaireActivity("daily");
//            }
//        });
//        layout.addView(daily_questionnaire_button);

        Button[] questionnaire_buttons = new Button[questionnaires.size()];
        for (int i=0; i<questionnaire_buttons.length; i++) {
            questionnaire_buttons[i] = new Button(this);
            final int finalI = i;
            final Long QuestionnaireID = new Long(finalI);
            questionnaire_buttons[i].setText(questionnaires.get(QuestionnaireID));
            questionnaire_buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openQuestionnaireActivity(questionnaires.get(QuestionnaireID),QuestionnaireID);
                }
            });
            setButtonConfiguration(questionnaire_buttons[i]);

            layout.addView(questionnaire_buttons[i]);
        }

    }

    private void setButtonConfiguration(Button b) {
        LinearLayout.LayoutParams params = new LinearLayout .LayoutParams(
                LinearLayout .LayoutParams.WRAP_CONTENT, LinearLayout .LayoutParams.WRAP_CONTENT);
        params.setMargins(10,10, 10, 10);
        b.setGravity(Gravity.CENTER);
        b.setLayoutParams(params);
    }

    private void openQuestionnaireActivity(String questionnaire_name, Long questionnaire_id) {
        Log.i("Home Page","questionnaire " + questionnaire_name + " has been opened");
        Questionnaire questionnaire = appController.getQuestionnaire(questionnaire_id);
        Intent intent = new Intent(this, QuestionnaireActivity.class);
        intent.putExtra(BindingValues.REQUESTED_QUESTIONNAIRE, questionnaire);
        startActivity(intent);


    }

    private Map<Long,String> getAllQuestionnaires() {
        AppController appController = AppController.getController(this);
        Map<Long, String> questionnaires  = appController.getUserQuestionnaires();
        return questionnaires;
    }

    public void changePasswordFunction(View view) {
            Intent intent = new Intent(this, SetNewPasswordForLoggedInUserActivity.class);
            startActivity(intent);
    }


    public void checkIfBandIsConnected(){
        appController.checkIfBandIsConnected();
    }

    public void showBTInfo(View view) {
        if (BAND_CONNECTED)
            Toast.makeText(view.getContext(),R.string.watch_on , Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(view.getContext(),R.string.watch_off , Toast.LENGTH_SHORT).show();
    }

    public void updateBTState() {
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                TextView bt_state = findViewById(R.id.bt_state);
                if (BAND_CONNECTED) {
                    Log.i(TAG,"Band is checked at " + Calendar.getInstance().getTime().toString() + " and this is Connected");
                    bt_state.setBackgroundResource(R.drawable.green_circle);
                    bt_state.setText(getString(R.string.short_watch_on));
                }
                else {
                    Log.i(TAG,"Band is checked at " + Calendar.getInstance().getTime().toString() + " and this is Disconnected");

                    bt_state.setBackgroundResource(R.drawable.red_circle);
                    bt_state.setText(getString(R.string.short_watch_off));
                }            }
        }, 0, 5, TimeUnit.SECONDS);

    }


}
