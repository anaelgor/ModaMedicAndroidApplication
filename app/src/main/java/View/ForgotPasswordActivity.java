package View;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.modamedicandroidapplication.R;

import Controller.AppController;
import Model.Utils.Constants;
import View.ViewUtils.BindingValues;

public class ForgotPasswordActivity extends AbstractActivity {

    String username;
    AppController appController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        appController = AppController.getController(this);
        username = getUsername();
        if (username != null && !username.equals("")) {
            EditText email_tf = findViewById(R.id.email_tf);
            email_tf.setText(username);
        }

    }


    public void OpenQuestionPage(View view) {
        EditText username_textfield = findViewById(R.id.email_tf);
        this.username = username_textfield.getText().toString();
        if (this.username.equals("")) {
           ShowWrongEmailAlert(R.string.wrongEmailAddress);
        }
        else {
            String question = appController.getVerificationQuestion(username);
            if (question.equals(Constants.USER_NOT_EXISTS))
                ShowWrongEmailAlert(R.string.userNonExists);
            else {
                Intent intent = new Intent(this, AnswerVerificationQuestionActivity.class);
                intent.putExtra(BindingValues.TRIED_TO_LOG_USERNAME, username);
                intent.putExtra(BindingValues.QUESTION_TEXT,question);
                startActivity(intent);
            }

        }

    }

    private String getUsername() {
        return getIntent().getStringExtra(BindingValues.TRIED_TO_LOG_USERNAME);
    }

    private void ShowWrongEmailAlert(int message) {
        new AlertDialog.Builder(ForgotPasswordActivity.this)
                .setTitle(R.string.error)
                .setMessage(message)
                .setNegativeButton(R.string.tryAgain, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


}
