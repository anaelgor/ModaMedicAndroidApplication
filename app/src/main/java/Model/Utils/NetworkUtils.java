package Model.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {

    public static boolean hasInternetConnection(Context context) {
    boolean internet_status = false;
    ConnectivityManager check = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (check != null) {
        NetworkInfo[] info = check.getAllNetworkInfo();
        if (info != null)
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    internet_status = true;
                }
            }
    }
        return internet_status;
}

}

