package com.djaphar.coffeepointsappcourier.SupportClasses.OtherClasses;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

public class PermissionDriver {

    private static final int PERMISSION_REQUEST_CODE = 123;

    public static boolean hasPerms(String[] perms, Context context) {
        int res;

        for (String perm : perms) {
            res = context.checkCallingOrSelfPermission(perm);
            if (res != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    public static void requestPerms(Activity activity, String[] perms) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(perms, PERMISSION_REQUEST_CODE);
        }
    }
}
