package View;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.modamedicandroidapplication.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import Controller.AppController;
import Model.Questionnaires.Questionnaire;
import Model.Utils.Constants;
import View.ViewUtils.DateUtils;

public class SettingsActivity extends AbstractActivity {

    private static final String TAG = "SettingsActivity";
    private static AppController appController;
    private Map<Integer, String> allQquestionnaireIDtoText = null;
    private List<Questionnaire> chosenQuestionnaires = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        appController = AppController.getController(this);
        ShowCurrentDateOfSurgery(false,null);
    }

    public void chooseDateOfSurgery(View view) {
        DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: dd/mm/yyy: " + day + "/" + month + "/" + year);
                String date = day + "/" + month + "/" + year;
                Calendar chosenTime = Calendar.getInstance();
                chosenTime.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
                chosenTime.set(Calendar.YEAR,year);
                chosenTime.set(Calendar.MONTH,month-1);
                chosenTime.set(Calendar.DAY_OF_MONTH,day);
                chosenTime.set(Calendar.HOUR_OF_DAY,0);
                chosenTime.set(Calendar.MINUTE,0);
                chosenTime.set(Calendar.SECOND,0);

                boolean flag = appController.setSurgeryDate(DateUtils.changeDateTo00AM(chosenTime.getTimeInMillis()));
                if (flag) {
                    ShowCurrentDateOfSurgery(true,date);
                    String msg = getString(R.string.succesfull_date_change_to) + " " + date;
                    showInfo(msg);
                }
                else {
                    showAlert(R.string.error_on_server);
                }
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                SettingsActivity.this,
                android.R.style.Widget_Material,
                mDateSetListener,
                year,month,day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorPrimary)));
        dialog.getWindow().setNavigationBarColor((getColor(R.color.colorAccent)));
        dialog.getWindow().setLayout((int) (0.9*getWidthOfScreen()),4*getHeightOfScreen()/5);
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.NoSurgeryDate), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean flag = appController.setSurgeryDate(0);
                if (flag) {
                    ShowCurrentDateOfSurgery(true,"0");
                    String msg = getString(R.string.succesfull_date_change_to) + " " + getString(R.string.unknown_surgery_day);
                    showInfo(msg);
                }
                else {
                    showAlert(R.string.error_on_server);
                }
            }
        });
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(getColor(R.color.white));
                dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setBackgroundColor(getColor(R.color.white));
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(getColor(R.color.white));            }
        });
        dialog.show();
    }

    private void showAlert(int msg) {
        new AlertDialog.Builder(SettingsActivity.this)
                .setTitle(R.string.error)
                .setMessage(msg)
                .setNegativeButton(R.string.tryAgain, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void chooseQuestionnaires(View view) {
        getOptionalQuestionnaires();
        setCurrentUserQuestionnaires();
        // Set up the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.choose_your_questionnaires));

        // Add a checkbox list
        int size;
        try {
            size = allQquestionnaireIDtoText.size();
        }
        catch (NullPointerException e) {
            size = 0;
        }
        String[] items = new String[size];
        int[] itemsIndexes  = new int[size];

        int i=0;
        for (Map.Entry<Integer,String> entry : allQquestionnaireIDtoText.entrySet()) {
            items[i] = entry.getValue();
            itemsIndexes[i] = entry.getKey();
            i++;
        }
        boolean[] checkedItems = new boolean[items.length];

        for (int j=0; j<checkedItems.length; j++)
            checkedItems[j] = isChecked(itemsIndexes[j]);

        builder.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                Questionnaire questionnaire = new Questionnaire();
                questionnaire.setTitle(items[which]);
                questionnaire.setQuestionaireID(itemsIndexes[which]);
                if (isChecked)
                    chosenQuestionnaires.add(questionnaire);
                else
                    chosenQuestionnaires.remove(questionnaire);
            }
        });

// Add OK and Cancel buttons
        builder.setPositiveButton(getString(R.string.choose), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean at_least_one = isAtLeastOneQuestionnaireIsChecked();
                if (at_least_one) {
                    boolean flag = appController.setUserQuestionnaires(chosenQuestionnaires);
                    if (flag) {
                        showInfo(R.string.questionnaires_has_been_changed);
                        SharedPreferences sharedPreferences = getSharedPreferences(Constants.sharedPreferencesName,MODE_PRIVATE);
                        sharedPreferences.edit().putBoolean(Constants.CHANGED_QUESTIONNAIRES,true).apply();
                    }
                    else {
                        showAlert(R.string.error_on_server);
                    }
                }
                else {
                    showAlert(R.string.you_have_to_choose_at_least_one);
                }

            }

            private boolean isAtLeastOneQuestionnaireIsChecked() {
                for (int i = 0; i < checkedItems.length; i++)
                    if (checkedItems[i])
                        return true;

                return false;
            }
        });

// Create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showInfo(int msg) {
        new AlertDialog.Builder(SettingsActivity.this)
                .setTitle(R.string.succes)
                .setMessage(msg)
                .setNegativeButton(R.string.succes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    private void showInfo(String msg) {
        new AlertDialog.Builder(SettingsActivity.this)
                .setTitle(R.string.succes)
                .setMessage(msg)
                .setNegativeButton(R.string.succes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    private boolean isChecked(long id) {
        for (int i=0; chosenQuestionnaires != null && i<chosenQuestionnaires.size(); i++) {
            Questionnaire x = chosenQuestionnaires.get(i);
            if (x.getQuestionaireID() == id)
                return true;
        }
        return false;
    }

    private void setCurrentUserQuestionnaires() {
        chosenQuestionnaires = new ArrayList<>();
        Map<Long,String> userQuestionnaires = appController.getUserQuestionnaires();
        for (Map.Entry<Long,String> entry: userQuestionnaires.entrySet()) {
            if (entry.getKey() == 0 || entry.getKey() == 6)
                continue;
            Questionnaire questionnaire = new Questionnaire();
            questionnaire.setTitle(entry.getValue());
            questionnaire.setQuestionaireID(entry.getKey());
            chosenQuestionnaires.add(questionnaire);
        }
    }


    public void goHomePage(View view) {
        finish();
    }

    private void getOptionalQuestionnaires() {
        allQquestionnaireIDtoText = appController.getAllQuestionnairesInSystem();
    }

    private void ShowCurrentDateOfSurgery(boolean update, String date) {
        TextView date_tv = findViewById(R.id.your_current_surgery_day);
        if (!update) {
            long currentDate = appController.getSurgeryDate();
            if (currentDate == 0){
                String msg = getString(R.string.your_current_surgery_day_full);
                date_tv.setText(String.format("%s", msg));
            }
            else{
                date_tv.setText(String.format("%s %s", date_tv.getText(), getStringDate(currentDate)));
            }
        }
        else {
            if (date.equals("0")) {
                String msg = getString(R.string.your_current_surgery_day_full);
                date_tv.setText(String.format("%s", msg));
            }
            else
                date_tv.setText(String.format("%s %s", getString(R.string.your_current_surgery_day), date));
        }
    }

    private String getStringDate(long current_date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(current_date);
        int year = cal.get(Calendar.YEAR);
        int month = 1 + cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return day + "/" + month + "/" + year;
    }

    private int getWidthOfScreen() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    private int getHeightOfScreen() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }
}
