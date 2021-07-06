package com.sdp.appazul.globals;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.sdp.appazul.security.RSAHelper;
import com.sdp.appazul.utils.KeysUtils;
import com.sdp.appazul.utils.StorageUtils;

import java.nio.charset.StandardCharsets;

public class GlobalFunctions {
    private Context context;

    public GlobalFunctions(Context context) {
        this.context = context;
    }

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /*
     * Check input string is valid double or not.
     */
    public static boolean isValidDouble(String number) {
        boolean isValid;
        double aDouble;
        try {
            int places = 2;
            aDouble = Double.parseDouble(number);
            aDouble = getFormattedNumber(aDouble, places);
            isValid = !(aDouble <= 0);
        } catch (NumberFormatException e) {
            isValid = false;
        }
        return isValid;
    }

    /*
     * Return formatted number with specified places.
     */
    private static double getFormattedNumber(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    /*
     * Generate PPR string.
     */
    public String getPPRString() {
        String randomPass = "";
        String fileContent = StorageUtils.readDataFromInternalStorage(Constants.SSP_FILE, context);
        try {
            if (TextUtils.isEmpty(fileContent)) {
                StorageUtils.writeDataToInternalStorage(Constants.SSP_FILE, "", context);
                KeysUtils.createNewKeys(Constants.SSC_ALIAS, context);
                int capacity = 32;
                randomPass = KeysUtils.getRandomAlphanumDigitKey(capacity);
                String encStr = RSAHelper.rsaEncrypt(randomPass);
                if (!TextUtils.isEmpty(encStr))
                    StorageUtils.writeDataToInternalStorage(Constants.SSP_FILE, encStr, context);
            } else {
                randomPass = RSAHelper.rsaDecrypt(fileContent);
            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        return randomPass;
    }

    public String getSSCString() {
        String strSSC = "";
        try {
            String sscRF = new StringBuilder(Constants.SSC_PREF_FILE).reverse().toString();
            byte[] ddata = Base64.decode(sscRF, Base64.DEFAULT);
            strSSC = new String(ddata, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        return strSSC;
    }
}
