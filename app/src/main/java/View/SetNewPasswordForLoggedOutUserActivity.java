package View;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.modamedicandroidapplication.R;

import Controller.AppController;
import Model.Exceptions.InvalidTokenException;

public class SetNewPasswordForLoggedOutUserActivity extends AbstractActivity {

    AppController appController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_password_for_logged_out_user);
        appController = AppController.getController(this);
    }

    public void setNewPassword(View view) {

        EditText password_tf = findViewById(R.id.newPassword);
        String newPassword = password_tf.getText().toString();
        EditText password_tf_again = findViewById(R.id.newPasswordAgain);
        String newPasswordAgain = password_tf_again.getText().toString();
        String valid_msg = checkPassword(newPassword,newPasswordAgain);
        if (valid_msg.equals("OK")) {
            boolean flag = false;
            try {
                flag = appController.setNewPasswordForLoggedOutUser(newPassword);
            } catch (InvalidTokenException e) {
                showAlert(R.string.specialTokenInvalid);
                return;
            }
            if (!flag) {
                showAlert(R.string.error_on_server);
            } else {
                showInfo(R.string.password_has_changed);
            }
        }
        else {
            if (valid_msg.equals("PasswordAreNotEqual")) {
                showAlert(R.string.password_are_not_equal);
            }
        }
    }

    private void showAlert(int msg) {
        new AlertDialog.Builder(SetNewPasswordForLoggedOutUserActivity.this)
                .setTitle(R.string.error)
                .setMessage(msg)
                .setNegativeButton(R.string.tryAgain, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showInfo (int msg) {
        new AlertDialog.Builder(SetNewPasswordForLoggedOutUserActivity.this)
                .setTitle(R.string.succes)
                .setMessage(msg)
                .setNegativeButton(R.string.gohome, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openMainActivity();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private String checkPassword(String newPassword, String newPasswordAgain) {
        if (newPassword.equals(newPasswordAgain))
            return "OK";
        else
            return "PasswordAreNotEqual";
    }
}
