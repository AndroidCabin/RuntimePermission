package com.ivan.library;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.os.ResultReceiver;
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
    private ResultReceiver mPermissionResultReceiver;

    public static final int REQUEST_CODE_PERMISSION = 0x1001;

    /**
     * 启动Activity
     *
     * @param context   Context
     * @param requestCode 请求码
     * @param permission 要申请的权限
     */
    public static void startActivity(Context context, int requestCode, String... permission) {
        startActivity(context, requestCode, null, permission);
    }

    /**
     * 启动Activity
     *
     * @param context  Context
     * @param data    存放一些额外的数据
     * @param requestCode 请求码
     * @param permission 要申请的权限
     */
    public static void startActivity(Context context, Bundle data, int requestCode, String... permission) {
        startActivity(context, data, requestCode, null, permission);
    }

    /**
     * 启动Activity
     *
     * @param context  Context
     * @param requestCode 请求码
     * @param resultReceiver   接受结果的ResultReceiver对象
     * @param permission 要申请的权限
     */
    public static void startActivity(Context context, int requestCode, PermissionResultReceiver resultReceiver, String... permission) {
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PermissionConst.EXTRA_REQUEST_CODE, requestCode);
        intent.putExtra(PermissionConst.EXTRA_PERMISSIONS, permission);
        intent.putExtra(PermissionConst.EXTRA_RESULT_RECEIVER, resultReceiver);
        context.startActivity(intent);
    }

    /**
     * 启动Activity
     *
     * @param context  Context
     * @param data    存放一些额外的数据
     * @param requestCode 请求码
     * @param resultReceiver   接受结果的ResultReceiver对象
     * @param permission 要申请的权限
     */
    public static void startActivity(Context context, Bundle data, int requestCode, PermissionResultReceiver resultReceiver, String... permission) {
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PermissionConst.EXTRA_REQUEST_CODE, requestCode);
        intent.putExtra(PermissionConst.EXTRA_PERMISSIONS, permission);
        intent.putExtra(PermissionConst.EXTRA_DATA_BUNDLE, data);
        intent.putExtra(PermissionConst.EXTRA_RESULT_RECEIVER, resultReceiver);
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
        if (intent == null) {
            return;
        }
        if (intent.hasExtra(PermissionConst.EXTRA_PERMISSIONS)) {
            mPermissions = new HashSet<>();
            mRequestCode = intent.getIntExtra(PermissionConst.EXTRA_REQUEST_CODE, 0);
            mBundleData = intent.getBundleExtra(PermissionConst.EXTRA_DATA_BUNDLE);
            if (mBundleData == null) {
                mBundleData = new Bundle();
            }
            Collections.addAll(mPermissions, intent.getStringArrayExtra(PermissionConst.EXTRA_PERMISSIONS));
            mPermissionResultReceiver = intent.getParcelableExtra(PermissionConst.EXTRA_RESULT_RECEIVER);
        } else {
            Intent i = new Intent(PermissionRequestResultReceiver.ACTION_REQUEST_PERMISSION_RESULT);
            i.putExtra(PermissionRequestResultReceiver.DATA_RESULT, true);
            LocalBroadcastManager.getInstance(PermissionActivity.this).sendBroadcast(i);
            mPermissionResultReceiver = intent.getParcelableExtra(PermissionConst.EXTRA_RESULT_RECEIVER);
            if (mPermissionResultReceiver != null) {
                Bundle b = new Bundle();
                b.putInt(PermissionConst.EXTRA_REQUEST_CODE, intent.getIntExtra(PermissionConst.EXTRA_REQUEST_CODE, 0));
                b.putBoolean(PermissionConst.EXTRA_PERMISSION_RESULT_GRANTED, true);
                mPermissionResultReceiver.send(PermissionConst.RESULT_CODE_OK, b);
            }
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
                    intent.putExtra(PermissionConst.EXTRA_REQUEST_CODE, mRequestCode);
                    intent.putExtra(PermissionConst.EXTRA_DATA_BUNDLE, mBundleData);
                    LocalBroadcastManager.getInstance(PermissionActivity.this).sendBroadcast(intent);

                    if (mPermissionResultReceiver != null) {
                        Bundle b = new Bundle();
                        b.putInt(PermissionConst.EXTRA_REQUEST_CODE, mRequestCode);
                        b.putBoolean(PermissionConst.EXTRA_PERMISSION_RESULT_GRANTED, true);
                        mPermissionResultReceiver.send(PermissionConst.RESULT_CODE_OK, b);
                    }

                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    Intent intent = new Intent(PermissionRequestResultReceiver.ACTION_REQUEST_PERMISSION_RESULT);
                    intent.putExtra(PermissionRequestResultReceiver.DATA_RESULT, false);
                    intent.putExtra(PermissionConst.EXTRA_REQUEST_CODE, mRequestCode);
                    intent.putExtra(PermissionConst.EXTRA_DATA_BUNDLE, mBundleData);
                    LocalBroadcastManager.getInstance(PermissionActivity.this).sendBroadcast(intent);

                    if (mPermissionResultReceiver != null) {
                        Bundle b = new Bundle();
                        b.putInt(PermissionConst.EXTRA_REQUEST_CODE, mRequestCode);
                        b.putBoolean(PermissionConst.EXTRA_PERMISSION_RESULT_GRANTED, false);
                        mPermissionResultReceiver.send(PermissionConst.RESULT_CODE_OK, b);
                    }
                }
            }
        }
        finish();
    }

    @Override
    public void overridePendingTransition(int enterAnim, int exitAnim) {
        super.overridePendingTransition(R.anim.no_anim, R.anim.no_anim);
    }
}