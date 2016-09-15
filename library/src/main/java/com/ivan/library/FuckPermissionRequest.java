package com.ivan.library;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Android M+ 权限申请的工具类, 可以在任何地方, 包括后台和子线程中, 但是方法比较绕
 *
 * @author lijun at 2016-09-14  11:35
 * @version v1.0
 * @since v2.1
 */

public class FuckPermissionRequest {

    private Context mContext;
    private int mRequestCode;
    private Bundle mBundleData;
    private Set<String> mPermissions;
    private PermissionResultReceiver mPermissionResultReceiver;

    public FuckPermissionRequest(Context context) {
        this.mContext = context;
    }

    public static FuckPermissionRequest newInstance(Context context) {
        return new FuckPermissionRequest(context);
    }

    public FuckPermissionRequest requestPermission(String... permissions) {
        this.mPermissions = new HashSet<>();
        Collections.addAll(mPermissions, permissions);
        return this;
    }

    public FuckPermissionRequest requestCode(int requestCode) {
        this.mRequestCode = requestCode;
        return this;
    }

    public FuckPermissionRequest bundle(Bundle bundle) {
        this.mBundleData = bundle;
        return this;
    }

    public FuckPermissionRequest onPermissionGranted(OnPermissionGrantedListener listener) {
        if (mPermissionResultReceiver == null) {
            mPermissionResultReceiver = new PermissionResultReceiver(new Handler(Looper.getMainLooper()));
        }
        mPermissionResultReceiver.setOnPermissionGrantedListener(listener);
        return this;
    }

    public FuckPermissionRequest onPermissionDenied(OnPermissionDeniedListener listener) {
        if (mPermissionResultReceiver == null) {
            mPermissionResultReceiver = new PermissionResultReceiver(new Handler(Looper.getMainLooper()));
        }
        mPermissionResultReceiver.setOnPermissionDeniedListener(listener);
        return this;
    }

    public void request() {
        PermissionActivity.startActivity(mContext, mBundleData, mRequestCode, mPermissionResultReceiver, mPermissions.toArray(new String[mPermissions.size()]));
    }
}
