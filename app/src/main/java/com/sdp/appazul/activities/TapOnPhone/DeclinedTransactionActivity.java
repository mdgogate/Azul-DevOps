package com.sdp.appazul.activities.TapOnPhone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.sdp.appazul.R;
import com.sdp.appazul.activities.home.MainMenuActivity;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;

import java.util.Map;

import digital.paynetics.phos.PhosSdk;
import digital.paynetics.phos.exceptions.PhosException;
import digital.paynetics.phos.sdk.callback.TransactionCallback;
import digital.paynetics.phos.sdk.entities.Transaction;

public class DeclinedTransactionActivity extends AppCompatActivity {
    ImageView btnCloseDeclined;
    TextView tvPayment_declined_lbl_one;
    TextView tvPayment_declined_lbl_two;
    RelativeLayout btnTryAgain;
    TextView btn_new_sale;
    Transaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_declined_transaction);
        transaction = ((AzulApplication) this.getApplicationContext()).getDeclinedTransaction();

        initControls();

    }

    private void initControls() {
        btnCloseDeclined = findViewById(R.id.btnCloseDeclined);
        tvPayment_declined_lbl_one = findViewById(R.id.tvPayment_declined_lbl_one);
        tvPayment_declined_lbl_two = findViewById(R.id.tvPayment_declined_lbl_two);
        btnTryAgain = findViewById(R.id.btnTryAgain);
        btn_new_sale = findViewById(R.id.btn_new_sale);

        btnCloseDeclined.setOnClickListener(btnCloseDeclinedView -> {
            cancelAlert(this);
        });
        btn_new_sale.setOnClickListener(btn_new_saleView -> {
            finish();
            Intent intent = new Intent(this, PhosCalculatorScreen.class);
            startActivity(intent);
            this.overridePendingTransition(R.anim.animation_leave,
                    R.anim.slide_nothing);
        });
        checkStatus();
        btnTryAgain.setOnClickListener(btnTryAgainView -> {
            CallMakeSaleWithAMount(transaction.getAmount());
        });

    }

    private void checkStatus() {
        if (transaction.getActionCode() == 12 || transaction.getActionCode() == 13
                || transaction.getActionCode() == 14) {
            disableLabelOne();
        } else if (transaction.getActionCode() == 25 || transaction.getActionCode() == 28 ||
                transaction.getActionCode() == 55) {
            disableLabelOne();
        } else if (transaction.getActionCode() == 57 || transaction.getActionCode() == 63
                || transaction.getActionCode() == 85 || transaction.getActionCode() == 94) {
            disableLabelOne();
        } else if (transaction.getActionCode() == 91) {
            disableLabelTwo();
        } else if (transaction.getActionCode() == 3) {
            tvPayment_declined_lbl_one.setVisibility(View.VISIBLE);
            tvPayment_declined_lbl_one.setText(R.string.declined_2);
        } else if (transaction.getActionCode() == 5 || transaction.getActionCode() == 51) {
            tvPayment_declined_lbl_one.setVisibility(View.VISIBLE);
            tvPayment_declined_lbl_one.setText(R.string.declined_4);
        } else if (transaction.getActionCode() == 54) {
            tvPayment_declined_lbl_one.setVisibility(View.VISIBLE);
            tvPayment_declined_lbl_one.setText(R.string.declined_3);
        }

    }

    private void disableLabelOne() {
        tvPayment_declined_lbl_two.setVisibility(View.VISIBLE);
        tvPayment_declined_lbl_one.setVisibility(View.GONE);
    }

    private void disableLabelTwo() {
        tvPayment_declined_lbl_one.setVisibility(View.GONE);
        tvPayment_declined_lbl_two.setVisibility(View.GONE);
    }

    Dialog cancelAlertDialog;

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
                finish();
                Intent intent = new Intent(this, MainMenuActivity.class);
                startActivity(intent);
                this.overridePendingTransition(R.anim.animation_leave,
                        R.anim.slide_nothing);
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


    private void CallMakeSaleWithAMount(double amountToShare) {
        PhosSdk.getInstance().makeSaleWithAmount(DeclinedTransactionActivity.this, amountToShare, false, new TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction, @Nullable @org.jetbrains.annotations.Nullable Map<String, String> map) {
                callTransactionReceiptScreen(transaction);
            }

            @Override
            public void onFailure(@Nullable @org.jetbrains.annotations.Nullable Transaction transaction, PhosException e, @Nullable @org.jetbrains.annotations.Nullable Map<String, String> map) {
                callDeclinedScreen(transaction);
            }
        });
    }

    private void callTransactionReceiptScreen(Transaction transaction) {
        ((AzulApplication) ((DeclinedTransactionActivity) this).getApplication()).setTransaction(transaction);
        Intent intent = new Intent(DeclinedTransactionActivity.this, TransactionResponseActivity.class);
        startActivity(intent);
        this.overridePendingTransition(R.anim.animation_enter,
                R.anim.slide_nothing);
    }

    private void callDeclinedScreen(Transaction transaction) {
        ((AzulApplication) ((DeclinedTransactionActivity) this).getApplication()).setDeclinedTransaction(transaction);
        Intent intent = new Intent(DeclinedTransactionActivity.this, DeclinedTransactionActivity.class);
        startActivity(intent);
        this.overridePendingTransition(R.anim.animation_enter,
                R.anim.slide_nothing);
    }
}