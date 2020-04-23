package View;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import com.example.modamedicandroidapplication.R;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import Controller.AppController;
import Model.Users.User;
import View.ViewUtils.InputFilterMinMax;
import View.ViewUtils.MultiSelectionSpinner;

public class RegisterNewUserActivity extends AbstractActivity {

    private String TAG = "Registration";
    private Calendar chosenTime = null;
    private Calendar chosenSurgeryTime = null;
    private EditText email = null;
    private EditText password = null;
    private EditText passwordAgain = null;
    private EditText phoneNumber = null;
    private RadioGroup gender = null;
    private RadioGroup smoker = null;
    private RadioGroup surgery = null;
    private Spinner eduction = null;
    private EditText weight = null;
    private EditText height = null;
    private EditText specialCode = null;
    private AppController appController;
    private String[] questionsText = null;
    private Map<String, Integer> questionToIDS = null;
    private Spinner verificationQuestion = null;
    private EditText verificationAnswer = null;
    private MultiSelectionSpinner questionnaires = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_patient);
        appController = AppController.getController(this);
        initializeAllFields();
        getVerificationQuestions();
        getOptionalQuestionnaires();
        limitFields();
    }

    private void getOptionalQuestionnaires() {
        List<String> items = appController.get
        questionnaires.setItems(items,x,this);
    }

    private void getVerificationQuestions() {
        Map<Integer,String> questions = appController.getAllVerificationQuestions();
        questionsText = new String[questions.size()];
        questionToIDS = new HashMap<>();

        int i=0;
        for (Map.Entry<Integer,String> entry : questions.entrySet()) {
            questionsText[i++] = entry.getValue();
            questionToIDS.put(entry.getValue(),entry.getKey());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, questionsText);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        verificationQuestion.setAdapter(adapter);
    }

    private void limitFields() {
        weight.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "200")});
     //   height.setFilters(new InputFilter[]{ new InputFilterMinMax(30, 250)});
    }

    private void initializeAllFields() {
        email = findViewById(R.id.email_address);
        password = findViewById(R.id.newPassword);
        passwordAgain = findViewById(R.id.newPasswordAgain);
        phoneNumber = findViewById(R.id.phoneNumber);
        gender = findViewById(R.id.radioSex);
        smoker = findViewById(R.id.radioSmoke);
        surgery = findViewById(R.id.radioSurgery);
        eduction = findViewById(R.id.education_spinner);
        weight = findViewById(R.id.weight);
        height = findViewById(R.id.height);
        specialCode = findViewById(R.id.specialCodeRegistration);
        verificationQuestion = findViewById(R.id.verification_question_spinner);
        verificationAnswer = findViewById(R.id.verification_answer);
        questionnaires = findViewById(R.id.questionnaires_spinner);

    }

    public void register(View view) {
        boolean flag = verifyInputs();
        if (flag) {
            User user = new User(email.getText().toString(),password.getText().toString(),
                    phoneNumber.getText().toString(),getRadioButtonResult(gender),
                    getRadioButtonResult(smoker),getRadioButtonResult(surgery),
                    eduction.getSelectedItem().toString(),
                    Integer.parseInt(weight.getText().toString()),
                    Integer.parseInt(height.getText().toString()),
                    chosenTime.getTimeInMillis(),specialCode.getText().toString(),
                    questionToIDS.get(verificationQuestion.getSelectedItem().toString()),
                    verificationAnswer.getText().toString());
            String msg = appController.register(user);
            boolean flag2 = showWrongInfo(msg);
            if (flag2) {
                openSurgeryInfoActivity();
            }
        }
    }

    private void openSurgeryInfoActivity() {
        //todo: implement
    }

    private boolean showWrongInfo(String msg) {
        //todo: implement with OR
        if (msg.equals("Taken Email")) {
            showAlert(R.string.error_taken_email);
            return false;
        }
        if (msg.equals("Wrong Code")) {
            showAlert(R.string.error_registrationCode);
            return false;
        }
        return true;
    }

    private String getRadioButtonResult(RadioGroup radioGroup) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton selectedButton = findViewById(selectedId);
        return selectedButton.getText().toString();
    }

    private boolean verifyInputs() {
        if (specialCode.length() == 0) {
            showAlert(R.string.fill_registrationCode);
            return false;
        }
        if (email.length() ==0) {
            showAlert(R.string.wrongEmailAddress);
            return false;
        }
        if (password.length() == 0 || passwordAgain.length() == 0) {
            showAlert(R.string.fill_password);
            return false;
        }
        if (phoneNumber.length() == 0) {
            showAlert(R.string.fill_phone);
            return false;
        }
        if (gender.getCheckedRadioButtonId() == -1) {
            showAlert(R.string.fill_gender);
            return false;
        }
        if (smoker.getCheckedRadioButtonId() == -1) {
            showAlert(R.string.fill_smoke);
            return false;
        }
        if (surgery.getCheckedRadioButtonId() == -1) {
            showAlert(R.string.fill_surgery);
            return false;
        }
        if ( eduction.getSelectedItem() == null) {
            showAlert(R.string.fill_education);
            return false;
        }
        if (weight.length() == 0) {
            showAlert(R.string.fill_weight);
            return false;
        }
        if (height.length() == 0) {
            showAlert(R.string.fill_height);
            return false;
        }
        if (chosenTime == null) {
            showAlert(R.string.fill_date);
            return false;
        }
        if (chosenSurgeryTime == null) {
            showAlert(R.string.fill_surgery_date);
            return false;
        }
        if (verificationQuestion.getSelectedItem() == null) {
            showAlert(R.string.choose_verification_question);
            return false;
        }

        if (verificationAnswer.length() == 0) {
            showAlert(R.string.fill_verification_answer);
            return false;
        }

        if (!email.getText().toString().contains("@") || !email.getText().toString().contains(".")) {
            showAlert(R.string.wrongEmailAddress);
            return false;
        }
        if (!password.getText().toString().equals(passwordAgain.getText().toString())) {
            showAlert(R.string.password_are_not_equal);
            return false;
        }
        return true;
    }

    private void showAlert(int msg) {
        new AlertDialog.Builder(RegisterNewUserActivity.this)
                .setTitle(R.string.error)
                .setMessage(msg)
                .setNegativeButton(R.string.FixAndTryAgain, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void chooseDate(View view) {
        Button mDisplayDate = findViewById(R.id.chooseDateRegister);
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
                RegisterNewUserActivity.this,
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
        return displayMetrics.widthPixels;
    }

    private int getHeightOfScreen() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public void chooseDateOfSurgery(View view) {
        Button mDisplayDate = findViewById(R.id.chooseDateOfSurgery);
        DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);
                String date = day + "/" + month + "/" + year;
                mDisplayDate.setText(date);
                chosenSurgeryTime = Calendar.getInstance();
                chosenSurgeryTime.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
                chosenSurgeryTime.set(Calendar.YEAR,year);
                chosenSurgeryTime.set(Calendar.MONTH,month-1);
                chosenSurgeryTime.set(Calendar.DAY_OF_MONTH,day);
                chosenSurgeryTime.set(Calendar.HOUR_OF_DAY,0);
                chosenSurgeryTime.set(Calendar.MINUTE,0);
                chosenSurgeryTime.set(Calendar.SECOND,0);

            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                RegisterNewUserActivity.this,
                android.R.style.Widget_Material,
                mDateSetListener,
                year,month,day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorPrimary)));
        dialog.getWindow().setNavigationBarColor((getColor(R.color.colorAccent)));
        dialog.getWindow().setLayout(getWidthOfScreen(),getHeightOfScreen());
        dialog.show();
    }
}
