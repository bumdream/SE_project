package dgu.donggukeas_client.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by hanseungbeom on 2017. 11. 19..
 */

public class AppPermissions {
    public static final String[] APP_PERMISSION = {
            Manifest.permission.GET_TASKS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
    };

    public static boolean hasAppPermission(Context context) {
        for (String permission : APP_PERMISSION) {
            if (ActivityCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
