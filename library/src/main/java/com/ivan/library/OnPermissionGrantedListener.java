package com.ivan.library;

import android.os.Bundle;

/**
 * 运行时权限获取到运行时权限的回调
 *
 * @author lijun at 2016-09-14  11:39
 * @version v1.0
 * @since v1.0
 */

public interface OnPermissionGrantedListener {
    void onPermissionGranted(int requestCode, int resultCode, Bundle data);
}
