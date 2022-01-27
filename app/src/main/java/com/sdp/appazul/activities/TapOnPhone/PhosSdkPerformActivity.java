package com.sdp.appazul.activities.TapOnPhone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.sdp.appazul.R;
import com.sdp.appazul.activities.dashboard.DashBoardActivity;
import com.sdp.appazul.activities.home.MainMenuActivity;
import com.sdp.appazul.activities.payment.LinkSharingActivity;
import com.sdp.appazul.classes.CardLessPayment;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.KeyConstants;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import digital.paynetics.phos.PhosSdk;
import digital.paynetics.phos.screens.views.IndicatorView;

public class PhosSdkPerformActivity extends AppCompatActivity {

    TextView tv_amount_value;
    TextView tvToPay;
    TextView tvLocationSelected;
    ImageView btnClose;
    String locationJson;
    Context context;
    String totalAmountEntered;
    String selectedLocation;
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
    DecimalFormat currFormat = new DecimalFormat("#,##0.00", symbols);
    ImageButton btnBack;
    RelativeLayout includeLayout;
    FrameLayout nfc_marker;
    Dialog cancelAlertDialog;
    ConstraintLayout vg__present_card;
    ImageView imgView;
    static int SNACK_LENGTH_LONG = -2;
    static int SNACK_LENGTH = 0;
    String option;
    IndicatorView indicatorView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.test_draw);
        setContentView(R.layout.phos_sdk__activity_perform_sale_v2);
        context = this;
        Intent intent = getIntent();
        totalAmountEntered = intent.getStringExtra(Constants.TOTAL_AMOUNT);
        selectedLocation = intent.getStringExtra(Constants.LOCATION_NAME_SELECTED);
        option = intent.getStringExtra("SCREEN_TYPE");


        indicatorView = findViewById(R.id.labelAmount);

        tv_amount_value = findViewById(R.id.tv_amount_value);
        tvToPay = findViewById(R.id.tvToPay);
        tvLocationSelected = findViewById(R.id.tvLocationSelected);
        btnClose = findViewById(R.id.btnClose);
        btnBack = findViewById(R.id.include2);
        vg__present_card = findViewById(R.id.vg__present_card);
        imgView = findViewById(R.id.imgView);

        Glide.with(this).asGif().load(R.raw.contactless_card_animation).into(imgView);
        if (option.equalsIgnoreCase("REFUND")){
            tvToPay.setText("Total a devolver:");
        }else {
            tvToPay.setText("Total a pagar:");
        }

        locationJson = ((AzulApplication) context.getApplicationContext()).getLocationDataShare();
        btnBack.setOnClickListener(btnBackView -> {
            Intent setIntent = new Intent(this, PhosCalculatorScreen.class);
            startActivity(setIntent);
            this.overridePendingTransition(R.anim.animation_leave,
                    R.anim.slide_nothing);
            ((AzulApplication) ((PhosSdkPerformActivity) this).getApplication()).setLocationDataShare(locationJson);

        });

        if (totalAmountEntered != null && !TextUtils.isEmpty(totalAmountEntered)) {
            try {
                double newAmount = Double.parseDouble(totalAmountEntered);
                double totalEnteredAmount = newAmount / 100;
                tv_amount_value.setText(Constants.CURRENCY_FORMAT + currFormat.format(totalEnteredAmount));
            } catch (NumberFormatException numberFormatException) {
                Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(numberFormatException));
            }
        }
        if (selectedLocation != null && !TextUtils.isEmpty(selectedLocation)) {
            tvLocationSelected.setText(selectedLocation);
        }
        btnClose.setOnClickListener(btnCloseView -> {
            cancelAlert(context);
        });

        imgView.setOnClickListener(imgViewView -> {
//            Snackbar snackbar = Snackbar.make(vg__present_card,
//                    "Procesando…", Snackbar.LENGTH_INDEFINITE)
//                    .setBackgroundTint(Color.parseColor("#0057B8"));
//            snackbar.show();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    customSnackBar("Proceso completado, retira tu tarjeta");
                }
            }, 5000);


        });
    }
    public void customSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(vg__present_card, message,
                SNACK_LENGTH).setBackgroundTint(Color.parseColor(Constants.COLOR_SKY));
        View snackBarLayout = snackbar.getView();
        TextView textView = (TextView) snackBarLayout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.snackbar_check_circle_outline_white, 0, 0, 0);
        textView.setCompoundDrawablePadding(35);
        snackbar.show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callNextScreen();
            }
        }, 2000);
    }

    private void callNextScreen() {
        Intent intent = new Intent(this, CardlessAnimationScreen.class);
        intent.putExtra(Constants.TOTAL_AMOUNT, totalAmountEntered);
        intent.putExtra(Constants.LOCATION_NAME_SELECTED, selectedLocation);
        intent.putExtra("SCREEN_TYPE", option);

        startActivity(intent);
        CardLessPayment payment = new CardLessPayment();
        ((AzulApplication) ((PhosSdkPerformActivity) context).getApplication()).setCardLessPayment(payment);
        this.overridePendingTransition(R.anim.animation_enter,
                R.anim.slide_nothing);
        ((AzulApplication) ((PhosSdkPerformActivity) this).getApplication()).setLocationDataShare(locationJson);

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

                Intent setIntent = new Intent(this, MainMenuActivity.class);
                startActivity(setIntent);
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
}