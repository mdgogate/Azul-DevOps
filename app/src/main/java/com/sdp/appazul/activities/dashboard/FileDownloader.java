package com.sdp.appazul.activities.dashboard;


import android.util.Log;

import com.sdp.appazul.globals.KeyConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class FileDownloader {
    private FileDownloader() {
    }

    private static final int MEGABYTE = 1024 * 1024;

    public static void downloadFile(String fileUrl, File directory, String jsonObject, String authTokenHeader, String deviceId) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(directory)) {
            URL url = new URL(fileUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Accept", "*/*");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("x-ssc-key", "ssc-mobile");
            urlConnection.setRequestProperty("X-AUTH-TOKEN", "ssc-mobile");
            urlConnection.setRequestProperty("Authorization", authTokenHeader);
            urlConnection.setRequestProperty("device", deviceId);

            OutputStream os = urlConnection.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            osw.write(jsonObject);
            osw.flush();
            osw.close();
            os.close();

            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();


            byte[] buffer = new byte[MEGABYTE];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, bufferLength);
            }

        } catch (IOException e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }
}