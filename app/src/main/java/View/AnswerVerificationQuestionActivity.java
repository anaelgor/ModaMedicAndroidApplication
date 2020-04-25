package View;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.modamedicandroidapplication.R;

import java.util.Calendar;
import java.util.TimeZone;

import Controller.AppController;
import Model.Exceptions.WrongAnswerException;
import View.ViewUtils.BindingValues;
import View.ViewUtils.DateUtils;

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
        setHideKeyBoard();
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
        long date = DateUtils.changeDateTo00AM(chosenTime.getTimeInMillis());
        boolean flag = false;
        try {
            flag = appController.checkVerificationOfAnswerToUserQuestion(username,answer,date);
            if (!flag) {
                Log.i(TAG,"wrong date or answer. error");
                ShowWrongEmailAlert(R.string.wrongAnswerOrDate);
            }
            else {
                openNewPasswordActivity();
            }
        } catch (WrongAnswerException e) {
            Log.i(TAG,"wrong date or answer. error: " + e.getMessage());
            ShowWrongEmailAlert(R.string.wrongAnswerOrDate);
        }
        catch (Exception e) {
            Log.e(TAG,"something went wrong");
            ShowWrongEmailAlert(R.string.something_went_wrong);
        }


    }

    private void openNewPasswordActivity() {
            Intent intent = new Intent(this, SetNewPasswordForLoggedOutUserActivity.class);
            finish();
            startActivity(intent);
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
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorPrimary)));
        dialog.getWindow().setNavigationBarColor((getColor(R.color.colorAccent)));
        dialog.getWindow().setLayout(getWidthOfScreen(),getHeightOfScreen());
        dialog.show();
    }

    private int getWidthOfScreen() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        return width;
    }

    private int getHeightOfScreen() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }


    private void ShowWrongEmailAlert(int message) {
        new AlertDialog.Builder(AnswerVerificationQuestionActivity.this)
                .setTitle(R.string.error)
                .setMessage(message)
                .setNegativeButton(R.string.tryAgain, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void setHideKeyBoard() {
        EditText answer_textfield = findViewById(R.id.answer_textfield);
        View.OnFocusChangeListener ofcListener = new MyFocusChangeListener();
        answer_textfield.setOnFocusChangeListener(ofcListener);
    }

    private class MyFocusChangeListener implements View.OnFocusChangeListener {

        public void onFocusChange(View v, boolean hasFocus) {

            if ((v.getId() == R.id.answer_textfield) && !hasFocus) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }
}
