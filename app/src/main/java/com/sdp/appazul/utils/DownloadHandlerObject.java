package com.sdp.appazul.utils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.sdp.appazul.R;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

public class DownloadHandlerObject {

    private Context context;

    public DownloadHandlerObject(Context context) {
        this.context = context;
    }

    private String downloadFileStorage = "file:///downloadFileStorage/fileName.png";

    public void saveImage(Bitmap finalBitmap, String fileName, int shareFlag) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                downloadFileStorage = "/storage/emulated/0/DCIM/Merchant-" + fileName + ".png";
                Log.i("TAG", "saveImage: New File Path " + downloadFileStorage);
                ContentResolver resolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Merchant-" + fileName + ".png");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                OutputStream fileOutputStream = resolver.openOutputStream(imageUri);
                finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                if (shareFlag == 1) {
                    shareDownloadedLayoutQRCode(fileName);
                }

            } catch (Exception exception) {
                Log.e("CodigoDownloadActivity", "saveImage: " + exception.getLocalizedMessage());
            }
        }
    }

    @SuppressLint("IntentReset")
    public void shareDownloadedLayoutQRCode(String fileNameToShare) {
        downloadFileStorage = "/storage/emulated/0/DCIM/Merchant-" + fileNameToShare + ".png";
        Log.i("TAG", "setShareCodigo: Sharing File Path " + downloadFileStorage);
        File downloadFile = new File(downloadFileStorage);
        Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", downloadFile);
        Intent intentShare = new Intent(Intent.ACTION_SEND);
        intentShare.putExtra(Intent.EXTRA_STREAM, fileUri);
        intentShare.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.pay_mail_body));
        intentShare.setData(fileUri);
        intentShare.setType("image/png");
        intentShare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent chooserIntentShareFile = Intent.createChooser(intentShare, "Share Codigo QR");
        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(chooserIntentShareFile, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        context.startActivity(chooserIntentShareFile);

    }


}
