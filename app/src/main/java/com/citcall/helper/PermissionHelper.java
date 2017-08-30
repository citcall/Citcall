package com.citcall.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by Agustya on 9/1/16.
 */
public class PermissionHelper {
    public static boolean isGranted(final Activity activity, final String permission, final String[] permissions,
                                    final int request, String descString){
        int permissionCheck = ActivityCompat.checkSelfPermission(activity, permission);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)){
                new MaterialDialog.Builder(activity)
                        .content(descString)
                        .positiveText("OK")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                ActivityCompat.requestPermissions(activity, permissions, request);
                            }
                        }).show();
            }else{
                ActivityCompat.requestPermissions(activity, permissions, request);
            }

            return false;
        }else{
            return true;
        }
    }
}
