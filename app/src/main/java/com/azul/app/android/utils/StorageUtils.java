package com.azul.app.android.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
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
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
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
                FileInputStream fin = context.openFileInput(filename);
                int c;
                while ((c = fin.read()) != -1) {
                    data.append((char) c);
                }
                fin.close();
            }
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return data.toString();
    }
}
