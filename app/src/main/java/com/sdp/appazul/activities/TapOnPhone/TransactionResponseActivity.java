package com.sdp.appazul.activities.TapOnPhone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdp.appazul.R;
import com.sdp.appazul.activities.home.MainMenuActivity;
import com.sdp.appazul.classes.TapOnPhone;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import digital.paynetics.phos.sdk.entities.Transaction;

public class TransactionResponseActivity extends AppCompatActivity {

    Transaction tap;
    TextView tvTotalAmount;
    TextView tvCardNumber;
    TextView tvApprovalNo;
    TextView tvDateAndTime;
    TextView tvTrnType;
    ImageView imgCardLogo;
    ImageView btnToPClose;

    Date trnDate;
    Dialog cancelAlertDialog;

    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
    DecimalFormat currFormat = new DecimalFormat("#,##0.00", symbols);
    SimpleDateFormat olderFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy - hh:mm aaa");
    DateFormatSymbols dateSymbols = new DateFormatSymbols(Locale.getDefault());
    AppCompatButton btnToPNextSale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_response_screen);

        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvCardNumber = findViewById(R.id.tvCardNumber);
        tvApprovalNo = findViewById(R.id.tvApprovalNo);
        tvDateAndTime = findViewById(R.id.tvDateAndTime);
        tvTrnType = findViewById(R.id.tvTrnType);
        btnToPNextSale = findViewById(R.id.btnToPNextSale);
        btnToPClose = findViewById(R.id.btnToPClose);
        imgCardLogo = findViewById(R.id.imgCardLogo);

        tap = ((AzulApplication) this.getApplicationContext()).getTransaction();
        btnToPNextSale.setOnClickListener(btnToPNextSaleView -> {
            callNewSale();
        });
        btnToPClose.setOnClickListener(btnToPCloseView -> {
            cancelAlert(this);
        });
        tvTotalAmount.setText(Constants.CURRENCY_FORMAT + currFormat.format(tap.getAmount()));
        tvCardNumber.setText(tap.getCard());
        tvApprovalNo.setText("Aprobada " + tap.getAuthCode());
        setType(tap);
        if (tap.getCardType().equalsIgnoreCase("MASTERCARD")) {
            imgCardLogo.setImageResource(R.drawable.ic_marca_mastercard);
        } else if (tap.getCardType().equalsIgnoreCase("VISA")) {
            imgCardLogo.setImageResource(R.drawable.ic_marca_visa);
        }

        if (tap != null) {

            trnDate = tap.getDate();
            dateSymbols.setAmPmStrings(new String[]{"a.m.", "p.m."});
            format.setDateFormatSymbols(dateSymbols);
            String toDate = format.format(trnDate);
            tvDateAndTime.setText(toDate.toLowerCase());
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
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
    private void callNewSale() {
        Intent setIntent;
        setIntent = new Intent(this, PhosCalculatorScreen.class);
        startActivity(setIntent);
        this.overridePendingTransition(R.anim.animation_leave,
                R.anim.slide_nothing);
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
        Intent setIntent;
        setIntent = new Intent(this, MainMenuActivity.class);
        startActivity(setIntent);
        this.overridePendingTransition(R.anim.animation_leave,
                R.anim.slide_nothing);
    }

}