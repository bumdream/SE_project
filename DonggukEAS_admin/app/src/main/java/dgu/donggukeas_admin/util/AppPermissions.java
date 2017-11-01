package dgu.donggukeas_admin.util;

/**
 * Created by hansb on 2017-09-07.
 */

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


/**
 * Created by han sb on 2017-02-17.
 */

public class AppPermissions {
    public static final String[] PHOTO_PERMISSIONS = {
            Manifest.permission.CAMERA
    };

    public static boolean hasPhotoPermissionsGranted(Context context) {
        for (String permission : PHOTO_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }



}
