package com.tj.screenoff;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

/**
 * Opened from the "Remove app" item in the icon's long-press menu.
 * It gives up the lock-screen permission and then shows Android's normal
 * "Do you want to uninstall this app?" prompt, so removal takes two taps.
 */
public class UninstallActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DevicePolicyManager dpm =
                (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName admin = new ComponentName(this, AdminReceiver.class);

        try {
            if (dpm.isAdminActive(admin)) {
                dpm.removeActiveAdmin(admin);
            }
        } catch (RuntimeException ignored) {
            // If this fails, Android will explain what to do in the next step.
        }

        Intent uninstall = new Intent(Intent.ACTION_DELETE,
                Uri.parse("package:" + getPackageName()));
        try {
            startActivity(uninstall);
        } catch (ActivityNotFoundException e) {
            try {
                startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName())));
            } catch (ActivityNotFoundException ignored) {
                // Nothing else we can do.
            }
        }

        finish();
        overridePendingTransition(0, 0);
    }
}
