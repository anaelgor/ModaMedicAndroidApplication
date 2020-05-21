package View;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.core.content.ContextCompat;

import com.example.modamedicandroidapplication.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import Controller.AppController;
import Model.Questionnaires.Questionnaire;
import Model.Users.User;
import View.ViewUtils.DateUtils;
import View.ViewUtils.InputFilterMinMax;

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
    private Map<Integer, String> questionnaireIDtoText = null;
    private List<Questionnaire> chosenQuestionnaires = null;
    private EditText firstName = null;
    private EditText lastName = null;
    private boolean surgeryUnknown;
    private CheckBox termsNconditions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_patient);
        appController = AppController.getController(this);
        initializeAllFields();
        getVerificationQuestions();
        limitFields();
    }

    private void getOptionalQuestionnaires() {
        questionnaireIDtoText = appController.getAllQuestionnairesInSystem();
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
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        termsNconditions = findViewById(R.id.agree_terms);
        chosenQuestionnaires = new ArrayList<>();
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
                    DateUtils.changeDateTo00AM(chosenTime.getTimeInMillis()),
                    specialCode.getText().toString(),
                    questionToIDS.get(verificationQuestion.getSelectedItem().toString()),
                    verificationAnswer.getText().toString(),
                    (chosenSurgeryTime != null ? DateUtils.changeDateTo00AM(chosenSurgeryTime.getTimeInMillis()) : 0),
                    chosenQuestionnaires,
                    firstName.getText().toString(),
                    lastName.getText().toString());
            String msg = appController.register(user);
            boolean flag2 = showWrongInfo(msg);
            if (flag2)
                success();
        }
    }

    private void success() {
            new AlertDialog.Builder(RegisterNewUserActivity.this)
                    .setTitle(R.string.succes)
                    .setMessage(R.string.succesfull_registarion)
                    .setNegativeButton(R.string.movehome, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openHomePage();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        }
    private void openHomePage() {
        boolean flag = appController.login(email.getText().toString(),password.getText().toString(), RegisterNewUserActivity.this);
        if (flag) {
            Intent intent = new Intent(this, HomePageActivity.class);
            finish();
            startActivity(intent);
        }
        else {
            Log.e(TAG,"FATAL ERROR OCCURRED, can't log in with the registration details");
        }
    }

    private boolean showWrongInfo(String msg) {
        if (msg!=null && msg.equals("Taken Email")) {
            showAlert(R.string.error_taken_email);
            return false;
        }
        else if (msg!=null && msg.equals("Wrong Code")) {
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

    @SuppressLint("ResourceType")
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
        if (eduction.getSelectedItem() == null) {
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
        if (chosenSurgeryTime == null && surgery.indexOfChild(findViewById(surgery.getCheckedRadioButtonId())) != 2 && !surgeryUnknown) {
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
        if (firstName.length() == 0) {
            showAlert(R.string.fill_firstName);
            return false;
        }
        if (lastName.length() == 0) {
            showAlert(R.string.fill_lastName);
            return false;
        }
        if (chosenQuestionnaires == null || chosenQuestionnaires.isEmpty()) {
            showAlert(R.string.choose_at_least_one_questionnaire);
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
        if (!termsNconditions.isChecked()) {
            showAlert(R.string.fill_terms);
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
                android.R.style.Widget_DeviceDefault,
                mDateSetListener,
                year,month,day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorPrimary)));
        dialog.getWindow().setNavigationBarColor((getColor(R.color.colorAccent)));
        dialog.getWindow().setLayout((int) (getWidthOfScreen()*0.9),4*getHeightOfScreen()/5);
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(getColor(R.color.white));
                dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setBackgroundColor(getColor(R.color.white));
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(getColor(R.color.white));            }
        });
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

    public void disableDatePickerForSurgery(View view){
        Button chooseDateOfSurgery = findViewById(R.id.chooseDateOfSurgery);
        chooseDateOfSurgery.setClickable(false);
        chooseDateOfSurgery.setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.custom_unclickable_button));
        chosenSurgeryTime = null;
    }

    public void enableDatePickerForSurgery(View view){
        Button chooseDateOfSurgery = findViewById(R.id.chooseDateOfSurgery);
        chooseDateOfSurgery.setClickable(true);
        chooseDateOfSurgery.setBackground(ContextCompat.getDrawable(view.getContext(),R.drawable.custom_system_button));
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
        dialog.getWindow().setLayout((int) (getWidthOfScreen()*0.9),4*getHeightOfScreen()/5);
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.NoSurgeryDate), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                chosenSurgeryTime = null;
                Button mDisplayDate = findViewById(R.id.chooseDateOfSurgery);
                mDisplayDate.setText(getString(R.string.NoSurgeryDate));
                surgeryUnknown = true;
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

    public void chooseQuestionnaires(View view) {
        getOptionalQuestionnaires();
        // Set up the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.choose_your_questionnaires));

// Add a checkbox list
        String[] items = new String[questionnaireIDtoText.size()];
        int[] itemsIndexes  = new int[questionnaireIDtoText.size()];

        int i=0;
        for (Map.Entry<Integer,String> entry : questionnaireIDtoText.entrySet()) {
            items[i] = entry.getValue();
            itemsIndexes[i] = entry.getKey();
            i++;
        }
        boolean[] checkedItems = new boolean[items.length];


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
                //do nothing
            }
        });

// Create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void openTerms(View view) {
        CheckBox checkBox = (CheckBox)view;
        if (checkBox.isChecked()) {
            String msg = getString(R.string.full_terms_and_conditions);
            new AlertDialog.Builder(RegisterNewUserActivity.this)
                    .setTitle(R.string.termsAndConditions)
                    .setMessage(msg)
                    .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do nothing
                        }
                    })
                    .setIcon(R.drawable.notif_icon)
                    .show();
        }

    }
}
