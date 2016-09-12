package com.ivan.runtimepermission;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.ivan.library.OnPermissionRequestResult;
import com.ivan.library.PermissionActivity;
import com.ivan.library.PermissionRequestResultReceiver;


public class MyService extends Service implements OnPermissionRequestResult {

    private PermissionRequestResultReceiver mPermissionRequestReceiver;

    private final static String ACTION_REQUEST_PERMISSION = "com.ivan.runtimepermission.intent.action.ACTION_REQUEST_PERMISSION";
    private static final int REQUEST_CODE = 0x3000;

    public static void startService(Context context) {
        Intent intent = new Intent(context, MyService.class);
        intent.setAction(ACTION_REQUEST_PERMISSION);
        context.startService(intent);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPermissionRequestReceiver = new PermissionRequestResultReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(PermissionRequestResultReceiver.ACTION_REQUEST_PERMISSION_RESULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(mPermissionRequestReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle b = new Bundle();
        b.putString("data", "this_is_data");
        PermissionActivity.startActivity(MyService.this, b, REQUEST_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mPermissionRequestReceiver);
    }
    @Override
    public void onPermissionGranted(int requestCode, Bundle data) {
        Toast.makeText(MyService.this, "permission granted " + data.get("data"), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionDenied(int requestCode, Bundle data) {
        Toast.makeText(MyService.this, "permission denied " + data.get("data"), Toast.LENGTH_SHORT).show();
    }
}
