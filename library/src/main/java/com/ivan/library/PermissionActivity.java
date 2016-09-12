package com.ivan.library;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Ivan at 2016-08-13 11:22
 * @version v1.0
 * @since v1.0
 * <p>
 * This activity is used to request runtime permission in Android M+. It has a transparent theme,
 * and the result will be delivered by a BroadcastReceiver.
 */

public class PermissionActivity extends AppCompatActivity {

    private Set<String> mPermissions;
    private int mRequestCode;
    private Bundle mBundleData;

    public static final int REQUEST_CODE_PERMISSION = 0x1001;
    private static final String EXTRA_PERMISSIONS = "com_ivan_library_PermissionActivity_permissions";
    public static final String EXTRA_REQUEST_CODE = "com_ivan_library_PermissionActivity_request_code";
    public static final String EXTRA_DATA_BUNDLE = "com_ivan_library_PermissionActivity_data_bundle";

    public static void startActivity(Context context, int requestCode, String... permission) {
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_REQUEST_CODE, requestCode);
        intent.putExtra(EXTRA_PERMISSIONS, permission);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, Bundle data, int requestCode, String... permission) {
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_REQUEST_CODE, requestCode);
        intent.putExtra(EXTRA_PERMISSIONS, permission);
        intent.putExtra(EXTRA_DATA_BUNDLE, data);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getParameters(getIntent());
        requestRequiredPermissions();
    }

    /**
     * to request permission
     */
    private void requestRequiredPermissions() {
        Set<String> permissionNeed = getPermissionNeeded(mPermissions);
        ActivityCompat.requestPermissions(PermissionActivity.this, permissionNeed.toArray(new String[permissionNeed.size()]), REQUEST_CODE_PERMISSION);
    }

    /**
     * get parameter
     *
     * @param intent intent
     */
    private void getParameters(Intent intent) {
        if (intent != null && intent.hasExtra(EXTRA_PERMISSIONS)) {
            mPermissions = new HashSet<>();
            mRequestCode = intent.getIntExtra(EXTRA_REQUEST_CODE, 0);
            mBundleData = intent.getBundleExtra(EXTRA_DATA_BUNDLE);
            if (mBundleData == null) {
                mBundleData = new Bundle();
            }
            Collections.addAll(mPermissions, intent.getStringArrayExtra(EXTRA_PERMISSIONS));
        } else {
            Intent i = new Intent(PermissionRequestResultReceiver.ACTION_REQUEST_PERMISSION_RESULT);
            i.putExtra(PermissionRequestResultReceiver.DATA_RESULT, true);
            LocalBroadcastManager.getInstance(PermissionActivity.this).sendBroadcast(i);
            finish();
        }
    }

    /**
     * you could give several permissions as parameter, but some of them may have been granted. Since
     * there is no need to request permission that have been granted, the method returns a Set contains
     * the permissions which have NOT been granted.
     *
     * @param reqPermissions the permissions given
     * @return the permission need to be requested
     */
    private Set<String> getPermissionNeeded(@NonNull final Set<String> reqPermissions) {
        final Set<String> permissionNeeded = new HashSet<>(reqPermissions.size());
        try {
            for (String reqPermission : reqPermissions) {
                if (ContextCompat.checkSelfPermission(PermissionActivity.this, reqPermission)
                        != PackageManager.PERMISSION_GRANTED) {
                    permissionNeeded.add(reqPermission);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return permissionNeeded;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the task you need to do.
                    Intent intent = new Intent(PermissionRequestResultReceiver.ACTION_REQUEST_PERMISSION_RESULT);
                    intent.putExtra(PermissionRequestResultReceiver.DATA_RESULT, true);
                    intent.putExtra(EXTRA_REQUEST_CODE, mRequestCode);
                    intent.putExtra(EXTRA_DATA_BUNDLE, mBundleData);
                    LocalBroadcastManager.getInstance(PermissionActivity.this).sendBroadcast(intent);
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    Intent intent = new Intent(PermissionRequestResultReceiver.ACTION_REQUEST_PERMISSION_RESULT);
                    intent.putExtra(PermissionRequestResultReceiver.DATA_RESULT, false);
                    intent.putExtra(EXTRA_REQUEST_CODE, mRequestCode);
                    intent.putExtra(EXTRA_DATA_BUNDLE, mBundleData);
                    LocalBroadcastManager.getInstance(PermissionActivity.this).sendBroadcast(intent);
                }
            }
        }
        finish();
    }
}
