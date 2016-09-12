package com.ivan.library;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * the BroadcastReceiver which is used to receive the result of permission
 *
 * @author Ivan at 2016-08-13  14:00
 * @since v1.0
 * @version v1.0
 */

public class PermissionRequestResultReceiver extends BroadcastReceiver {

    public static final String ACTION_REQUEST_PERMISSION_RESULT = "com_ivan_library_PermissionRequestResultReceiver_action";
    public static final String DATA_RESULT = "com_ivan_library_PermissionRequestResultReceiver_result";

    private OnPermissionRequestResult result;

    public PermissionRequestResultReceiver(OnPermissionRequestResult result) {
        this.result = result;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        boolean permissionGranted = intent.getBooleanExtra(DATA_RESULT, false);
        int requestCode = intent.getIntExtra(PermissionActivity.EXTRA_REQUEST_CODE, 0);
        Bundle data = intent.getBundleExtra(PermissionActivity.EXTRA_DATA_BUNDLE);
        if (TextUtils.equals(action, ACTION_REQUEST_PERMISSION_RESULT) && permissionGranted) {
            result.onPermissionGranted(requestCode, data);
        } else {
            result.onPermissionDenied(requestCode, data);
        }
    }
}
