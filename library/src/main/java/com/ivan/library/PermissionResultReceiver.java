package com.ivan.library;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;

/**
 * the ResultReceiver to handle runtime permission result
 *
 * @author lijun at 2016-09-14  11:51
 * @version v1.0
 * @since v1.0
 */

@SuppressLint("ParcelCreator")
public class PermissionResultReceiver extends ResultReceiver {

    private OnPermissionGrantedListener mOnPermissionGrantedListener;
    private OnPermissionDeniedListener mOnPermissionDeniedListener;

    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler handler
     */
    public PermissionResultReceiver(Handler handler) {
        super(handler);
    }

    public void setOnPermissionGrantedListener(OnPermissionGrantedListener listener) {
        this.mOnPermissionGrantedListener = listener;
    }

    public void setOnPermissionDeniedListener(OnPermissionDeniedListener listener) {
        this.mOnPermissionDeniedListener = listener;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        if (resultCode == PermissionConst.RESULT_CODE_OK) {
            int requestCode = resultData.getInt(PermissionConst.EXTRA_REQUEST_CODE, 0);
            boolean permissionGranted = resultData.getBoolean(PermissionConst.EXTRA_PERMISSION_RESULT_GRANTED, false);
            if (permissionGranted ){
                if (mOnPermissionGrantedListener != null) {
                    mOnPermissionGrantedListener.onPermissionGranted(requestCode, resultCode, resultData);
                }
            } else {
               if (mOnPermissionDeniedListener != null) {
                   mOnPermissionDeniedListener.onPermissionDenied(requestCode, resultCode, resultData);
               }
            }
        }
    }
}
