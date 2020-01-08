package Model;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.FitnessOptions;

import static com.google.android.gms.fitness.data.DataType.TYPE_CALORIES_EXPENDED;
import static com.google.android.gms.fitness.data.DataType.TYPE_DISTANCE_DELTA;
import static com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA;

public class Permissions {

    private AppCompatActivity app;
    final private int ALL_PERMISSIONS = 101;

    public Permissions(AppCompatActivity app) {
        this.app = app;
    }

    public void requestPermissions() throws PackageManager.NameNotFoundException {
        PackageInfo info = app.getPackageManager().getPackageInfo(app.getPackageName(), PackageManager.GET_PERMISSIONS);
        String[] permissions = info.requestedPermissions;//This array contains the requested permissions.
        Log.i("PERMISSIONS", "************PERMISSIONS LIST*************");

        ActivityCompat.requestPermissions(app, permissions, ALL_PERMISSIONS);

        GoogleSignInOptionsExtension fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(TYPE_STEP_COUNT_DELTA,FitnessOptions.ACCESS_READ)
                        .addDataType(TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                        .addDataType(TYPE_CALORIES_EXPENDED,FitnessOptions.ACCESS_READ)
                        .build();

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(app), fitnessOptions)) {
            //todo: catch this GoogleSignIn object, pass this to thread with Datatype, Field and more.
            GoogleSignIn.requestPermissions(
                    app, // your activity
                    1,
                    GoogleSignIn.getLastSignedInAccount(app),
                    fitnessOptions);
        }

        Log.i("PERMISSIONS", "************PERMISSIONS REQUESTED*************");
    }


}
