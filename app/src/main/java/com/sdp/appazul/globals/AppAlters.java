package com.sdp.appazul.globals;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;

import com.sdp.appazul.R;
import com.sdp.appazul.activities.dashboard.DashBoardActivity;
import com.sdp.appazul.activities.dashboard.QrCode;
import com.sdp.appazul.activities.home.MainMenuActivity;
import com.sdp.appazul.activities.payment.PaymentDataValidateActivity;
import com.sdp.appazul.activities.payment.QuickPayConfirmActivity;
import com.sdp.appazul.activities.payment.QuickPayValidationActivity;
import com.sdp.appazul.activities.transactions.QrTransactions;
import com.sdp.appazul.activities.transactions.SettledTransactionsQuery;
import com.sdp.appazul.utils.NetworkAddress;


public class AppAlters {

    private AppAlters() {
    }

    @SuppressWarnings("deprecation")
    public static void showAlert(Context context, String title, String message, String buttonLbl) {
        try {
            final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle(HtmlCompat.fromHtml("<font color='#FF7F27'>' " + title + " '</font>", HtmlCompat.FROM_HTML_MODE_LEGACY));
            alertDialog.setMessage(message);
            alertDialog.setCancelable(false);
            alertDialog.setButton(buttonLbl, (dialog, which) -> alertDialog.cancel());
            alertDialog.show();
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
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
            typeface = Typeface.createFromAsset(activity.getAssets(), Constants.FONT_ROBOTO_MEDIUM);
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
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    public static void showPopupDialog(Context activity,String message) {
        try {

            Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setContentView(R.layout.custom_popup_dialog_layout);

            TextView textTitle = dialog.findViewById(R.id.textTitle);
            TextView textMsg = dialog.findViewById(R.id.textMsg);
            TextView btnAlertClose = dialog.findViewById(R.id.btnAlertClose);

            Typeface typeface;
            typeface = Typeface.createFromAsset(activity.getAssets(), Constants.FONT_ROBOTO_MEDIUM);
            textTitle.setTypeface(typeface);
            textMsg.setText(message);
            textMsg.setTypeface(typeface);
            btnAlertClose.setTypeface(typeface);

            btnAlertClose.setOnClickListener(view -> {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            });
            dialog.show();

        } catch (
                Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }


    public static void errorAlert(Context activity, int errorType) {
        try {

            Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setContentView(R.layout.feature_permission_dialog);
            TextView textTitle = dialog.findViewById(R.id.textTitle);
            TextView textMsg = dialog.findViewById(R.id.textMsg);
            TextView btnCancel = dialog.findViewById(R.id.btnCancel);

            Typeface typeface;
            typeface = Typeface.createFromAsset(activity.getAssets(), Constants.FONT_ROBOTO_MEDIUM);

            btnCancel.setTypeface(typeface);
            textMsg.setTypeface(typeface);
            textTitle.setTypeface(typeface);

            TextView btnContinue = dialog.findViewById(R.id.btnContinue);
            btnContinue.setTypeface(typeface);
            if (errorType == 4 ) {
                modifyData(activity,textTitle,textMsg,btnContinue,btnCancel);
            }else if (errorType == 5 ) {
                modifyDataFive(activity,textTitle,textMsg,btnContinue,btnCancel);
            }


            btnContinue.setOnClickListener(view -> {
                if (errorType == 4) {
                    setActivity(activity);
                }
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            });

            btnCancel.setOnClickListener(view -> {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private static void modifyData(Context activity, TextView textTitle, TextView textMsg, TextView btnContinue, TextView btnCancel) {
        textTitle.setText(activity.getResources().getString(R.string.alert_sorry_title));
        textMsg.setText(activity.getResources().getString(R.string.alert_sorry_message));
        btnContinue.setText("Aceptar");
        btnCancel.setVisibility(View.INVISIBLE);
    }

    private static void modifyDataFive(Context activity, TextView textTitle, TextView textMsg, TextView btnContinue, TextView btnCancel) {
        textTitle.setText(activity.getResources().getString(R.string.alert_sorry_title));
        textMsg.setText(activity.getResources().getString(R.string.alert_sorry_message));
        btnContinue.setText(Constants.ACCEPT_BUTTON);
        btnCancel.setVisibility(View.INVISIBLE);
    }

    private static void setActivity(Context context) {
        if (context.getClass().equals(DashBoardActivity.class)) {
            redirectToDashBoard(context);
        } else if (context.getClass().equals(SettledTransactionsQuery.class)) {
            redirectToDashBoard(context);
        } else if (context.getClass().equals(QrTransactions.class)) {
            Intent intent = new Intent(context, DashBoardActivity.class);
            context.startActivity(intent);
        } else if (context.getClass().equals(PaymentDataValidateActivity.class)) {
            redirectToDashBoard(context);
        } else if (context.getClass().equals(QrCode.class)) {
            redirectToDashBoard(context);
        } else if (context.getClass().equals(MainMenuActivity.class)) {
            Intent intent1 = new Intent(context, MainMenuActivity.class);
            context.startActivity(intent1);
        } else if (context.getClass().equals(QuickPayValidationActivity.class)) {
            Intent intent = new Intent(context, MainMenuActivity.class);
            context.startActivity(intent);
        } else if (context.getClass().equals(QuickPayConfirmActivity.class)) {
            redirectToPreLogin(context);
        }
    }

    private static void redirectToDashBoard(Context context) {
        Intent intent = new Intent(context, DashBoardActivity.class);
        context.startActivity(intent);
    }

    private static void redirectToPreLogin(Context context) {
        Intent intent = new Intent(context, MainMenuActivity.class);
        context.startActivity(intent);
    }

}
