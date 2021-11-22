package com.sdp.appazul.activities.transactions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.sdp.appazul.R;

import com.sdp.appazul.globals.AppAlters;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.KeyConstants;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ResendPaymentLink extends AppCompatActivity {
    TextView tvLinkTitleQuickSale;
    TextView tvLink;
    RelativeLayout linkInfo;
    TextView btnClose;
    Button btnNextSale;
    String locationJson;
    String link;
    RelativeLayout copyLayout;
    RelativeLayout shareIngLayout;
    TextView tvFinalAmount;
    String amountToDisplay;
    String selectedCurrency;
    String selectedLocation;
    ImageView btnCopyLink;
    Dialog cancelAlertDialog;
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);

    DecimalFormat amountDecimalFormat = new DecimalFormat("#,##0.00",symbols);
    RelativeLayout activity_resend_link;
    static int SNACK_LENGTH = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resend_payment_link);
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
        copyLayout = findViewById(R.id.copyLayout);
        activity_resend_link = findViewById(R.id.activity_resend_link);
        btnClose = findViewById(R.id.btnClose);
        tvFinalAmount = findViewById(R.id.tvFinalAmount);
        btnCopyLink = findViewById(R.id.btnCopyLink);
        btnNextSale = findViewById(R.id.btnNextSale);
        tvLink = findViewById(R.id.tvLink);
        shareIngLayout = findViewById(R.id.shareingLayout);
        tvLinkTitleQuickSale = findViewById(R.id.tvLinkTitleQuickSale);
        linkInfo = findViewById(R.id.linkInfo);
        Intent dataIntent = getIntent();
        locationJson = dataIntent.getStringExtra(Constants.LOCATION_RESPONSE);
        link = dataIntent.getStringExtra("RESPONSE_LINK");
        amountToDisplay = dataIntent.getStringExtra("DISPLAY_AMOUNT");
        selectedCurrency = dataIntent.getStringExtra("CURRENCY");

        showAnimation();
        setData(link, amountToDisplay);
        copyLayout.setOnClickListener(imgCopyLinkView -> {

            if (!TextUtils.isEmpty(link)) {
                btnNextSale.setEnabled(true);
                btnNextSale.setBackgroundResource(R.drawable.link_button_background);
                btnCopyLink.setColorFilter(ContextCompat.getColor(this, R.color.gradient_button_right));
                copyLinkToClipboard(link);
            }

            Snackbar snackbar = Snackbar.make(activity_resend_link,
                    this.getResources().getString(R.string.link_copied), SNACK_LENGTH)
                    .setBackgroundTint(Color.parseColor("#0091DF"));
            snackbar.show();
        });
        btnClose.setOnClickListener(btnCloseView ->
                cancelAlert(ResendPaymentLink.this)
        );
        shareIngLayout.setOnClickListener(shareIngLayoutView -> {
            if (!TextUtils.isEmpty(link)) {
                btnNextSale.setEnabled(true);
                btnNextSale.setBackgroundResource(R.drawable.link_button_background);
                shareLink(link, selectedLocation);
            }
        });
    }

    private void copyLinkToClipboard(String copiedLink) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Azul Link", copiedLink);
        clipboard.setPrimaryClip(clip);
    }

    private void shareLink(String link, String selectedLocation) {
        try {
            String subjectText = "Link de Pagos" + " " + selectedLocation;
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subjectText);
            String shareMessage = getResources().getString(R.string.payment_link_label_message) + link + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.payment_link_label)));
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void showAnimation() {
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);

        tvLinkTitleQuickSale.startAnimation(aniFade);
        linkInfo.startAnimation(aniFade);
        btnClose.startAnimation(aniFade);
    }

    private void setData(String link, String givenAmount) {
        if (link != null && link.length() > 0) {
            tvLink.setText(link);

            tvLink.setOnClickListener(tvLinkView -> {
                if (link.startsWith("https://") || link.startsWith("http://")) {
                    Uri uri = Uri.parse(link);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } else {
                    Toast.makeText(ResendPaymentLink.this, "Invalid Url", Toast.LENGTH_SHORT).show();
                }

            });

        } else {
            String dummyUrl = "https://pagos.azul.com.do/ba780bd0";
            tvLink.setText(dummyUrl);
        }


        if (!TextUtils.isEmpty(givenAmount)) {
            double amount = Double.parseDouble(givenAmount);
            String amountToShow= null;
            if (selectedCurrency.equalsIgnoreCase("USD")){
                amountToShow = Constants.CURRENCY_FORMAT_USD + "" + amountDecimalFormat.format(amount);
            }else {
                amountToShow = Constants.CURRENCY_FORMAT + "" + amountDecimalFormat.format(amount);
            }
            tvFinalAmount.setText(amountToShow);
        }

    }


    public void cancelAlert(Context activity) {
        try {

            cancelAlertDialog = new Dialog(activity);
            cancelAlertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            cancelAlertDialog.setCancelable(false);
            cancelAlertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            cancelAlertDialog.setContentView(R.layout.cancel_dialog_layout);
            TextView textTitle = cancelAlertDialog.findViewById(R.id.textTitle);
            TextView textMsg = cancelAlertDialog.findViewById(R.id.textMsg);
            Typeface typeface;
            typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Medium.ttf");
            textTitle.setTypeface(typeface);
            textTitle.setText(getResources().getString(R.string.close));
            textMsg.setTypeface(typeface);
            textMsg.setText(getResources().getString(R.string.close_button_message));
            TextView btnNo = cancelAlertDialog.findViewById(R.id.btnNo);
            TextView btnYes = cancelAlertDialog.findViewById(R.id.btnYes);
            btnYes.setTypeface(typeface);

            btnYes.setOnClickListener(view -> {

                Intent intent = new Intent(ResendPaymentLink.this, PaymentLinkTransactions.class);
                intent.putExtra(Constants.LOCATION_RESPONSE, locationJson);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                this.overridePendingTransition(R.anim.animation_leave,
                        R.anim.slide_nothing);
                if (cancelAlertDialog != null && cancelAlertDialog.isShowing()) {
                    cancelAlertDialog.dismiss();
                }
            });
            btnNo.setOnClickListener(view -> {
                if (cancelAlertDialog != null && cancelAlertDialog.isShowing()) {
                    cancelAlertDialog.dismiss();
                }
            });
            cancelAlertDialog.show();

        } catch (
                Exception e) {
            Log.e("Exception : ", Log.getStackTraceString(e));
        }
    }
}