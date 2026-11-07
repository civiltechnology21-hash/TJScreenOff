package com.tj.screenoff;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

/**
 * Invisible activity. Every tap on the app icon ends up here:
 *
 *  - permission already granted -> lock the screen right away and close
 *  - first run                  -> show Android's own one-tap "Activate" dialog,
 *                                  then lock the screen and close
 */
public class LockActivity extends Activity {

    private static final int REQUEST_ADMIN = 1;

    private DevicePolicyManager dpm;
    private ComponentName admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        admin = new ComponentName(this, AdminReceiver.class);

        // Re-created while the permission dialog is open: wait for onActivityResult.
        if (savedInstanceState != null) {
            return;
        }

        if (dpm.isAdminActive(admin)) {
            lockAndClose();
        } else {
            requestPermission();
        }
    }

    private void requestPermission() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, admin);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                getString(R.string.admin_explanation));
        try {
            startActivityForResult(intent, REQUEST_ADMIN);
        } catch (ActivityNotFoundException e) {
            // Very rare: a phone without the standard dialog. Open the system
            // security page, where "Device admin apps" lives, as a fallback.
            try {
                startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));
            } catch (ActivityNotFoundException ignored) {
                // Nothing else we can do.
            }
            closeSilently();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADMIN && dpm.isAdminActive(admin)) {
            lockAndClose();
        } else {
            closeSilently();
        }
    }

    private void lockAndClose() {
        try {
            dpm.lockNow();
        } catch (SecurityException e) {
            // Permission was revoked in the meantime: ask for it again.
            requestPermission();
            return;
        }
        closeSilently();
    }

    private void closeSilently() {
        finish();
        overridePendingTransition(0, 0);
    }
}
