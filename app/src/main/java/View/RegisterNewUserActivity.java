package View;

import android.app.DatePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.modamedicandroidapplication.R;

import java.util.Calendar;
import java.util.TimeZone;

public class RegisterNewUserActivity extends AbstractActivity {

    private String TAG = "Registration";
    private Calendar chosenTime = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //todo: do something like the others
        //todo: limit the kg and cm

    }

    public void register(View view) {
        //todo: edit this
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
        int width = displayMetrics.widthPixels;
        return width;
    }

    private int getHeightOfScreen() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }
}
