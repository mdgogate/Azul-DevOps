package com.sdp.appazul.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.KeyConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class StorageUtils {

    private StorageUtils() {
    }

    private static final String TAG = "StorageUtils";

    /**
     * Write data to Internal Storage
     */
    public static void writeDataToInternalStorage(String filename, String data, Context context) {
        try {
            try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
                fos.write(data.getBytes());
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    /**
     * Read data from Internal Storage
     */
    public static String readDataFromInternalStorage(String filename, Context context) {
        StringBuilder data = new StringBuilder();
        try {
            File sdDir = Environment.getExternalStorageDirectory();
            File file = new File(sdDir + filename);
            if (!file.exists()) {
               fileInputOperations(data,context,filename);
            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        return data.toString();
    }

    private static void fileInputOperations(StringBuilder data, Context context, String filename) {
        try (FileInputStream fin = context.openFileInput(filename)) {
            int c;
            while ((c = fin.read()) != -1) {
                data.append((char) c);
            }
        } catch (FileNotFoundException e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        } catch (IOException e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    public static String readDataFromInternalStorageNew(String filename, Context context) {
        StringBuilder data = new StringBuilder();
        try {
            File file = new File(context.getFilesDir() + filename);
            if (!file.exists()) {
                inputMethod(filename, data, context);
            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        return data.toString();
    }

    private static void inputMethod(String filename, StringBuilder data, Context context) {
        try (FileInputStream fin = context.openFileInput(filename)) {
            int c;
            while ((c = fin.read()) != -1) {
                data.append((char) c);

            }
        }
        catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }
}
