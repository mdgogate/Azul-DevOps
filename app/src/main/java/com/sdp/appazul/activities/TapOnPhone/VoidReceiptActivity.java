package com.sdp.appazul.activities.TapOnPhone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdp.appazul.R;

import com.sdp.appazul.classes.TapOnPhone;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;


import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import digital.paynetics.phos.sdk.entities.Transaction;

public class VoidReceiptActivity extends AppCompatActivity {


    ImageView btnClose;
    ImageView imgLogoVoid;
    Dialog cancelAlertDialog;
    String locationJson;
    Transaction tap;
    TextView tvTotalAmount;
    TextView tvCardNumber;
    TextView tvTime;
    TextView tvApprovedNo;
    TextView tvTrnType;
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
    DecimalFormat currFormat = new DecimalFormat("#,##0.00", symbols);
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy - hh:mm aaa");
    DateFormatSymbols dateSymbols = new DateFormatSymbols(Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_void_receipt);

        locationJson = ((AzulApplication) this.getApplicationContext()).getLocationDataShare();
        tap = ((AzulApplication) this.getApplicationContext()).getTransaction();
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvCardNumber = findViewById(R.id.tvCardNumber);
        tvTime = findViewById(R.id.tvTime);
        tvApprovedNo = findViewById(R.id.tvApprovedNo);
        tvTrnType = findViewById(R.id.tvTrnType);
        btnClose = findViewById(R.id.btnClose);
        imgLogoVoid = findViewById(R.id.imgLogoVoid);


        setUiData();

        if (tap.getCardType().equalsIgnoreCase("MASTERCARD")) {
            imgLogoVoid.setImageResource(R.drawable.ic_marca_mastercard);
        } else if (tap.getCardType().equalsIgnoreCase("VISA")) {
            imgLogoVoid.setImageResource(R.drawable.ic_marca_visa);
        } else if (tap.getCardType().equalsIgnoreCase("")) {
            imgLogoVoid.setImageResource(R.drawable.no_card_presenet);
        }
        btnClose.setOnClickListener(btnCloseView -> {
            cancelAlert(this);
        });


    }

    private void setUiData() {

        tvCardNumber.setText(tap.getCard());
        tvApprovedNo.setText("Aprobada " + tap.getAuthCode());
        setType(tap);
        tvTotalAmount.setText(Constants.CURRENCY_FORMAT + currFormat.format(tap.getAmount()));
        Date trnDate;

        if (tap != null) {
            trnDate = tap.getDate();
            dateSymbols.setAmPmStrings(new String[]{"a.m.", "p.m."});
            format.setDateFormatSymbols(dateSymbols);
            String toDate = format.format(trnDate);
            tvTime.setText(toDate.toLowerCase());
        }
    }

    private void setType(Transaction tap) {
        if (tap.getType().getType().equalsIgnoreCase("sale")) {
            tvTrnType.setText("Venta");
        } else if (tap.getType().getType().equalsIgnoreCase("refund")) {
            tvTrnType.setText("Devolución");
        } else if (tap.getType().getType().equalsIgnoreCase("void")) {
            tvTrnType.setText("Anulación");
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

                callBackScreen();
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

    private void callBackScreen() {
        Intent setIntent = new Intent(this, TapTransactions.class);
        startActivity(setIntent);
        this.overridePendingTransition(R.anim.animation_leave,
                R.anim.slide_nothing);
    }
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}