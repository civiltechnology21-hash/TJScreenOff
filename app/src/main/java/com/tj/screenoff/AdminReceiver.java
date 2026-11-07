package com.tj.screenoff;

import android.app.admin.DeviceAdminReceiver;

/**
 * Required by Android so the app can be granted the "lock the screen" permission.
 * It needs no logic of its own.
 */
public class AdminReceiver extends DeviceAdminReceiver {
}
