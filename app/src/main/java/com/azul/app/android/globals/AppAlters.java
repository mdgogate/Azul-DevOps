package com.azul.app.android.globals;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;


import com.azul.app.android.R;

public class AppAlters {

    private AppAlters() {
    }

    @SuppressWarnings("deprecation")
    public static void showAlert(Context context, String title, String message, String buttonLbl) {
        try {
            final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle(Html.fromHtml("<font color='#FF7F27'>' " + title + " '</font>"));
            alertDialog.setMessage(message);
            alertDialog.setCancelable(false);
            alertDialog.setButton(buttonLbl, (dialog, which) -> alertDialog.cancel());
            alertDialog.show();
        } catch (Exception e) {
            Log.e("Exception : ", Log.getStackTraceString(e));
        }
    }


    public static void showCustomAlert(Context activity, String title, String msg, String buttonLbl) {
        try {

            Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setContentView(R.layout.custom_alert_dialog_layout);
            TextView textTitle = dialog.findViewById(R.id.textTitle);
            TextView textMsg = dialog.findViewById(R.id.textMsg);
            Typeface typeface;
            typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Medium.ttf");
            textTitle.setTypeface(typeface);
            textMsg.setTypeface(typeface);
            TextView btnAlertClose = dialog.findViewById(R.id.btnAlertClose);
            textTitle.setText(title);
            textMsg.setText(msg);
            btnAlertClose.setText(buttonLbl);
            btnAlertClose.setTypeface(typeface);

            btnAlertClose.setOnClickListener(view -> {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            });
            dialog.show();

        } catch (
                Exception e) {
            Log.e("Exception : ", Log.getStackTraceString(e));
        }
    }


}
