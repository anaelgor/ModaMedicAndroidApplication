package Model;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

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
        Log.i("PERMISSIONS", "************PERMISSIONS REQUESTED*************");
    }


}
