package com.ivan.library;

import android.os.Bundle;

/**
 *
 *
 * @author Ivan at 2016-08-13 11:25
 * @version v1.0
 * @since v1.0
 */

public interface OnPermissionRequestResult {
    void onPermissionGranted(int requestCode, Bundle data);
    void onPermissionDenied(int requestCode, Bundle data);
}
