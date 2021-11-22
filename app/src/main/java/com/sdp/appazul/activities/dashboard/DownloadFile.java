package com.sdp.appazul.activities.dashboard;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.sdp.appazul.R;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.KeyConstants;
import com.sdp.appazul.security.RSAHelper;
import com.sdp.appazul.utils.DeviceUtils;

import java.io.File;
import java.io.IOException;

public class DownloadFile extends AsyncTask<String, Void, File> {

    private Context context;
    ProgressDialog progressDialog;

    public DownloadFile(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context, null,
                "Descargando estado…", false, false);
        progressDialog.setCancelable(false);
    }

    @Override
    protected File doInBackground(String... strings) {

        String randomeFileName = String.valueOf(System.currentTimeMillis());

        String fileUrl = strings[0];   // -> URL to download the File
        String jsonObject = strings[1];  // -> Parameter to request
        String fileName = "Azul-Statement-" + randomeFileName + ".pdf";

        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        File pdfFile = new File(folder, fileName);

        try {
            boolean fileStatus = pdfFile.createNewFile();
            Log.d("TAG", "doInBackground: " + fileStatus);
        } catch (IOException e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }

        String authTokenHeader = ((AzulApplication) context.getApplicationContext()).getAuthData();
        String deviceId = RSAHelper.ecryptRSA(context, DeviceUtils.getDeviceId(context));
        FileDownloader.downloadFile(fileUrl, pdfFile, jsonObject, authTokenHeader, deviceId);

        return pdfFile;
    }

    @Override
    protected void onPostExecute(File aVoid) {
        shareDownloadedLayoutQRCode(aVoid);
        try {
            if ((progressDialog != null) && progressDialog.isShowing())
                progressDialog.dismiss();
        } catch (final Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        } finally {
            this.progressDialog = null;
        }
    }

    public void shareDownloadedLayoutQRCode(File downloadFile) {
        if (downloadFile.exists()) Log.e("TAG", "shareDownloadedLayoutQRCode: " + "File Exist! ");

        Intent intentShareFile = new Intent(Intent.ACTION_VIEW);
        Uri bmpUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", downloadFile);
        if (downloadFile.exists()) {
            intentShareFile.setDataAndType(bmpUri, "application/pdf");
            intentShareFile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intentShareFile.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(Intent.createChooser(intentShareFile, "Abrir archivo PDF usando ..."));

        }
    }


}
