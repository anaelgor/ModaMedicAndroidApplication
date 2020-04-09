package View;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.modamedicandroidapplication.R;

import java.util.Calendar;
import java.util.TimeZone;

import Controller.AppController;
import Model.Exceptions.WrongAnswerException;

public class AnswerVerificationQuestionActivity extends AbstractActivity {

    private static final String TAG = "AnswerVerifictation";
    AppController appController;
    Calendar chosenTime = null;
    String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_verification_question);
        getVerificationQuestion();
        appController = AppController.getController(this);
    }

    private void getVerificationQuestion() {
        username = getUsername();
        String question = getQuestionFromIntent();
        TextView question_edittext = findViewById(R.id.question_textView);
        question_edittext.setText(question);
    }

    private String getQuestionFromIntent() {
        return getIntent().getStringExtra(BindingValues.QUESTION_TEXT);
    }

    private String getUsername() {
        return getIntent().getStringExtra(BindingValues.TRIED_TO_LOG_USERNAME);
    }

    public void answerQuestion(View view) {
        EditText answer_edittext = findViewById(R.id.answer_textfield);
        String answer = answer_edittext.getText().toString();
        answer = "ליאו";
        long date = (chosenTime.getTimeInMillis() / 10000) * 10000;
        boolean flag = false;
        try {
            flag = appController.checkVerificationOfAnswerToUserQuestion(username,answer,date);
            if (!flag) {
                Log.i(TAG,"something went wrong");
                ShowWrongEmailAlert(R.string.something_went_wrong);
            }
            else {
                //todo: open next page
                System.out.println("yoao this is worksssss");
            }
        } catch (WrongAnswerException e) {
            Log.i(TAG,"wrong date or answer. error: " + e.getMessage());
            ShowWrongEmailAlert(R.string.wrongAnswerOrDate);
        }


    }



    public void chooseDate(View view) {
        TextView mDisplayDate = findViewById(R.id.dateText);
        DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);
                String date = day + "/" + month + "/" + year;
                mDisplayDate.setText(date);
                chosenTime = Calendar.getInstance();
                chosenTime.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
                chosenTime.set(Calendar.YEAR,year);
                chosenTime.set(Calendar.MONTH,month-1);
                chosenTime.set(Calendar.DAY_OF_MONTH,day);
                chosenTime.set(Calendar.HOUR_OF_DAY,0);
                chosenTime.set(Calendar.MINUTE,0);
                chosenTime.set(Calendar.SECOND,0);

            }
        };


        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                AnswerVerificationQuestionActivity.this,
                android.R.style.Widget_Material,
                mDateSetListener,
                year,month,day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.show();
    }

    private void ShowWrongEmailAlert(int message) {
        new AlertDialog.Builder(AnswerVerificationQuestionActivity.this)
                .setTitle(R.string.error)
                .setMessage(message)
                .setNegativeButton(R.string.tryAgain, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
