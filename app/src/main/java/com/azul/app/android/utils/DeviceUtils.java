package com.azul.app.android.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

public class DeviceUtils {

    private DeviceUtils() {
    }

    public static String getAppVersion(Context context) {
        String versionCode = "2.1.6";
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            Log.e("Exception : ", Log.getStackTraceString(e));
        }
        return versionCode;
    }

    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

}
