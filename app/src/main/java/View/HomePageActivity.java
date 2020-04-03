package View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.modamedicandroidapplication.R;

import java.util.Map;

import Controller.AppController;
import Model.Utils.Constants;
import Model.Questionnaires.Questionnaire;

/*
Home page screen
 */
public class HomePageActivity extends AbstractActivity {
    Map<Long,String> questionnaires; //key: questID, value: questionnaire Text
    String username;
    AppController appController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        username = getUserName();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        appController = AppController.getController(this);

        Thread t_sensorData = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                appController.SendSensorData();
            }
        });
        t_sensorData.start();

        questionnaires = getAllQuestionnaires(username);

        String not_exists = "not exists";
        SharedPreferences sharedPref = this.getSharedPreferences(Constants.sharedPreferencesName,Context.MODE_PRIVATE);
        String name = sharedPref.getString("name",not_exists);
        if (name.equals(not_exists)) {
            throw new NullPointerException("can't find username");
        }
        TextView good_eve = findViewById(R.id.good_evening_textView);
        good_eve.setText(String.format("%s %s", this.getString(R.string.hello), name));
        createAllButtons();

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

    private Map<Long,String> getAllQuestionnaires(String username) {
        AppController appController = AppController.getController(this);
        Map<Long, String> questionnaires  = appController.getUserQuestionnaires(username);
        return questionnaires;
    }





    //todo: implements this

    public void changePasswordFunction(View view) {
        Log.i("Home Page","change password button clicked");

    }
}
