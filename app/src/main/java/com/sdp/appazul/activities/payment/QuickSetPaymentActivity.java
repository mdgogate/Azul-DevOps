package com.sdp.appazul.activities.payment;

import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.sdp.appazul.R;
import com.sdp.appazul.activities.dashboard.DashBoardActivity;
import com.sdp.appazul.activities.dashboard.LocationFilterBottomSheet;
import com.sdp.appazul.activities.home.BaseLoggedInActivity;
import com.sdp.appazul.activities.home.MainMenuActivity;
import com.sdp.appazul.classes.LocationFilter;
import com.sdp.appazul.globals.AppAlters;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class QuickSetPaymentActivity extends BaseLoggedInActivity {
    TextView tvAmount;
    TextView tvFinalAmount;
    String locationJson;
    String taxStatus;
    String selectedCurrency;
    ImageView tvDelete;
    ImageView tvAddAndEquals;
    ImageView deleteFromInside;
    ImageView cancelFromInside;
    ImageView closeDeleteLayout;
    ImageView nextIcon;
    TextView tvFirstNo;
    TextView tvSecondNo;
    TextView tvThirdNo;
    TextView tvFourthNo;
    TextView tvFifthNo;
    TextView tvSixthNo;
    TextView tvSeventhNo;
    TextView tvEightNo;
    TextView tvNine;
    TextView tvZero;
    RelativeLayout layoutDeleteCancel;
    RelativeLayout layoutFinalAmtInfo;
    boolean sum = false;
    StringBuilder num = new StringBuilder();
    int previousAmount = 0;
    int tempAmount = 0;
    StringBuilder resultData;
    LocationFilter locationFilter = new LocationFilter();
    String lastLocationName;
    String lastLocationMid;
    String mID;
    ImageView btnBackScreen;
    LocationFilterBottomSheet locationBottomSheet;
    String totalAmountEntered;
    Intent intent;
    String previousLocationName;
    String selectedCode;
    String previousLocationId;
    String previousParentLocationName;
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);

    DecimalFormat currFormat = new DecimalFormat("#,##0.00",symbols);
    double lastAmount = 0;
    int sumFlag = 0;
    int amountCheck = 0;
    Context context;
    int resultDataCheck;
    RelativeLayout act_quick_set_payment;
    static int SNACK_LENGTH = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_set_payment);
        context = this;
        intent = getIntent();
        getIntentData();
        initControls();
        checkCurrency();
    }

    private void checkCurrency() {
        if (selectedCurrency != null && !TextUtils.isEmpty(selectedCurrency)) {
            if (selectedCurrency.equalsIgnoreCase("USD")) {
                tvAmount.setText(Constants.CURRENCY_USD);
            } else {
                tvAmount.setText(Constants.CURRENCY);
            }
        }
    }

    private void getIntentData() {
        taxStatus = intent.getStringExtra("TAX_STATUS");
        selectedCurrency = intent.getStringExtra("CURRENCY");
        locationJson = ((AzulApplication) context.getApplicationContext()).getLocationDataShare();

        selectedCode = intent.getStringExtra("CODE");
        previousLocationName = intent.getStringExtra(Constants.LOCATION_NAME_SELECTED);
        previousParentLocationName = intent.getStringExtra(Constants.LOCATION_PARENT_NAME_SELECTED);
        previousLocationId = intent.getStringExtra(Constants.LOCATION_ID_SELECTED);
    }

    private void initControls() {
        act_quick_set_payment = findViewById(R.id.act_quick_set_payment);
        tvAmount = findViewById(R.id.tvAmount);
        btnBackScreen = findViewById(R.id.btnBackScreen);
        layoutFinalAmtInfo = findViewById(R.id.layoutFinalAmtInfo);
        tvFinalAmount = findViewById(R.id.tvFinalAmount);
        nextIcon = findViewById(R.id.nextIcon);
        deleteFromInside = findViewById(R.id.deleteFromInside);
        cancelFromInside = findViewById(R.id.cancelFromInside);
        layoutDeleteCancel = findViewById(R.id.layoutDeletCancel);
        closeDeleteLayout = findViewById(R.id.closeDeleteLayout);
        tvZero = findViewById(R.id.tvZero);
        tvFirstNo = findViewById(R.id.tvFirstNo);
        tvSecondNo = findViewById(R.id.tvSecondNo);
        tvThirdNo = findViewById(R.id.tvThirdNo);
        tvFourthNo = findViewById(R.id.tvFourthNo);
        tvFifthNo = findViewById(R.id.tvFifthNo);
        tvSixthNo = findViewById(R.id.tvSixthNo);
        tvSeventhNo = findViewById(R.id.tvSeventhNo);
        tvEightNo = findViewById(R.id.tvEightNo);
        tvNine = findViewById(R.id.tvNine);
        tvDelete = findViewById(R.id.tvDelete);
        tvAddAndEquals = findViewById(R.id.tvAddAndEquals);

        locationFilter = ((AzulApplication) this.getApplication()).getLocationFilter();

        if (locationFilter != null) {
            lastLocationName = locationFilter.getLocationNameAndId();
            lastLocationMid = locationFilter.getmId();
            mID = lastLocationMid;
            selectedCode = locationFilter.getPaymentCode();
            selectedCurrency = locationFilter.getCurrency();
        }


        tvDelete.setOnClickListener(tvDeleteView -> {
            tvSeventhNo.setVisibility(View.GONE);
            tvDelete.setVisibility(View.GONE);
            layoutDeleteCancel.setVisibility(View.VISIBLE);
        });
        btnBackScreen.setOnClickListener(btnBackScreenView -> {
            Intent intent = new Intent(QuickSetPaymentActivity.this, MainMenuActivity.class);
            ((AzulApplication) ((QuickSetPaymentActivity) this).getApplication()).setLocationDataShare(locationJson);

            startActivity(intent);
            this.overridePendingTransition(R.anim.animation_leave,
                    R.anim.slide_nothing);
        });
        closeDeleteLayout.setOnClickListener(closeDeleteLayoutView -> {
            tvSeventhNo.setVisibility(View.VISIBLE);
            tvDelete.setVisibility(View.VISIBLE);
            layoutDeleteCancel.setVisibility(View.GONE);
        });

        cancelFromInside.setOnClickListener(cancelFromInsideView ->

                callAllDelete());
        deleteFromInside.setOnClickListener(deleteFromInsideView ->

                callSingleDelete());

        tvAddAndEquals.setOnClickListener(tvAddAndEqualsView -> {
            manageDeleteMenu();
            if (!num.toString().isEmpty()) {
                amountCheck = Integer.parseInt(num.toString());
                if (tempAmount != 0) {
                    Log.d("TAG", "initControls: ");
                } else {
                    tempAmount = previousAmount;
                }
                checkMaxLogic(num, resultData);

            }
        });

        tvFinalAmount.setOnClickListener(tvFinalAmountView ->
                callingConfirmationActivity()
        );
        callNumPadListeners();
        previousAmountOperation();
    }

    private void checkMaxLogic(StringBuilder num, StringBuilder resultData) {
        if (num != null && num.length() <= 9 && amountCheck < 999999999 && previousAmount < 999999999) {
            if (resultData != null && !resultData.toString().isEmpty()) {
                finalAmountValidation();
            } else {
                checkExtraAmount();
            }
        } else {
            resultData = new StringBuilder();
            Log.d("TAG", "resultData 333 : " + resultData.toString());
            resultData.append(tempAmount);
            Log.d("TAG", "resultData 444 : " + resultData.toString());
            amountCheck = 0;
            previousAmount = 0;
            if (num != null) {
                num.setLength(0);
            }
            addNumbers(resultData);
            showToast();
        }
    }

    private void finalAmountValidation() {
        previousAmount = previousAmount + Integer.parseInt(num.toString());
        resultData = new StringBuilder();
        Log.d("TAG", "resultData 1111 : " + resultData.toString());
        resultData.append(previousAmount);
        Log.d("TAG", "resultData 2222 : " + resultData.toString());
        resultDataCheck = Integer.parseInt(resultData.toString());
        Log.d("TAG", "resultData 5555 : " + resultData.toString());

        if (resultDataCheck < 999999999) {
            checkNewExtraAmount(resultData);
        } else {
            resultData = new StringBuilder();
            Log.d("TAG", "resultData 6666 : " + resultData.toString());
            resultData.append(tempAmount);
            amountCheck = 0;
            previousAmount = 0;
            resultDataCheck = 0;
            num.setLength(0);
            Log.d("TAG", "resultData 7777 : " + resultData.toString());
            addNumbers(resultData);
            showToast();
        }
    }

    private void checkExtraAmount() {
        sum = true;
        if (num != null && num.length() > 0) {
            previousAmount = previousAmount + Integer.parseInt(num.toString());
            num.setLength(0);
            layoutFinalAmtInfo.setBackgroundResource(R.drawable.blue_background_textview);
            resultData = new StringBuilder();
            resultData.append(previousAmount);
            Log.d("TAG", "resultData 9191 : " + resultData.toString());
            addNumbers(resultData);
        }
    }

    private void checkNewExtraAmount(StringBuilder previousResultData) {
        sum = true;
        if (num != null && num.length() > 0) {
            num.setLength(0);
            layoutFinalAmtInfo.setBackgroundResource(R.drawable.blue_background_textview);
            Log.d("TAG", "resultData 9999 : " + previousResultData.toString());
            addNumbers(previousResultData);
        }
    }

    private void showToast() {
        Snackbar snackbar = Snackbar.make(act_quick_set_payment, this.getResources().getString(R.string.calculator_limit_label), SNACK_LENGTH)
                .setBackgroundTint(Color.parseColor("#0091DF"));
        snackbar.show();
    }

    private void previousAmountOperation() {
        if (intent.getStringExtra(Constants.TOTAL_AMOUNT) != null) {
            totalAmountEntered = intent.getStringExtra(Constants.TOTAL_AMOUNT);

            if (Boolean.FALSE.equals(sum)) {
                num.append(totalAmountEntered);
                addNumbers(num);

            } else {
                resultData = new StringBuilder();
                resultData.append(totalAmountEntered);
                addNumbers(resultData);
            }
        }
    }

    private void callingConfirmationActivity() {

        if (!TextUtils.isEmpty(tvFinalAmount.getText().toString()) &&
                !tvFinalAmount.getText().toString().equalsIgnoreCase(Constants.COBRAR) &&
                !tvFinalAmount.getText().toString().equalsIgnoreCase(Constants.CHARGE_EMPTY_TITLE)) {

            Intent confirmIntent = new Intent(QuickSetPaymentActivity.this, QuickPayConfirmActivity.class);


            String totalAmount = tvFinalAmount.getText().toString().replace(",", "");
            String totalAmountNew;
            if (selectedCurrency.equalsIgnoreCase("USD")) {
                totalAmountNew = totalAmount.replace("Cobrar US$ ", "");
            } else {
                totalAmountNew = totalAmount.replace("Cobrar RD$ ", "");
            }
            String totalLastAmount = totalAmountNew.replace(".", "");

            Log.d("TAG", "callingConfirmationActivity: " + totalLastAmount);
            if (Boolean.FALSE.equals(sum)) {
                confirmIntent.putExtra(Constants.TOTAL_AMOUNT, totalLastAmount);
            } else {
                Log.d("TAG", totalLastAmount + "   : 1010 ResultData 1010 : " + resultData.toString());
                confirmIntent.putExtra(Constants.TOTAL_AMOUNT, totalLastAmount);
            }

            confirmIntent.putExtra("TAX_STATUS", taxStatus);
            confirmIntent.putExtra("CURRENCY", selectedCurrency);
            ((AzulApplication) ((QuickSetPaymentActivity) this).getApplication()).setLocationDataShare(locationJson);
            confirmIntent.putExtra("CODE", selectedCode);
            confirmIntent.putExtra(Constants.LOCATION_NAME_SELECTED, previousLocationName);
            confirmIntent.putExtra(Constants.LOCATION_ID_SELECTED, previousLocationId);
            confirmIntent.putExtra(Constants.LOCATION_PARENT_NAME_SELECTED, previousParentLocationName);
            startActivity(confirmIntent);

            this.overridePendingTransition(R.anim.animation_enter,
                    R.anim.slide_nothing);

        } else {
            Snackbar snackbar = Snackbar.make(act_quick_set_payment, this.getResources().getString(R.string.empty_amount_error), SNACK_LENGTH)
                    .setBackgroundTint(Color.parseColor("#0091DF"));
            snackbar.show();
        }
    }

    public static void makeMeShake(View view, int offset) {
        Animation anim = new TranslateAnimation(-offset, offset, 0, 0);
        anim.setDuration(600);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        view.startAnimation(anim);
    }

    private void callAllDelete() {

        if (num.length() <= 0) {
            if (selectedCurrency.equalsIgnoreCase("USD")) {
                tvAmount.setText(Constants.CURRENCY_USD);
            } else {
                tvAmount.setText(Constants.CURRENCY);
            }
            tvFinalAmount.setText(Constants.COBRAR);
            layoutFinalAmtInfo.setBackgroundResource(R.drawable.blue_background_textview);
            tvSeventhNo.setVisibility(View.VISIBLE);
            tvDelete.setVisibility(View.VISIBLE);
            layoutDeleteCancel.setVisibility(View.GONE);
            resultData = new StringBuilder();
            previousAmount = 0;
            tempAmount = 0;
            num.setLength(0);
        } else {
            resultData = new StringBuilder();
            previousAmount = 0;
            tempAmount = 0;
            num.setLength(0);
            tvFinalAmount.setText(Constants.COBRAR);
            tvSeventhNo.setVisibility(View.VISIBLE);
            tvDelete.setVisibility(View.VISIBLE);
            layoutDeleteCancel.setVisibility(View.GONE);
            if (selectedCurrency.equalsIgnoreCase("USD")) {
                tvAmount.setText(Constants.CURRENCY_USD);
            } else {
                tvAmount.setText(Constants.CURRENCY);
            }
            layoutFinalAmtInfo.setBackgroundResource(R.drawable.blue_background_textview);

        }
        nextIcon.clearAnimation();
        nextIcon.setVisibility(View.GONE);
    }

    private void callSingleDelete() {
        if (num.length() > 0) {
            num.deleteCharAt(num.length() - 1);
            addNumbers(num);
            if (num.length() == 0) {
                clearingData();
            }
        } else {
            clearingData();
        }
    }

    private void clearingData() {
        if (selectedCurrency.equalsIgnoreCase("USD")) {
            tvAmount.setText(Constants.CURRENCY_USD);
        } else {
            tvAmount.setText(Constants.CURRENCY);
        }
        nextIcon.clearAnimation();
        nextIcon.setVisibility(View.GONE);
        tvFinalAmount.setText(Constants.COBRAR);
        layoutFinalAmtInfo.setBackgroundResource(R.drawable.blue_background_textview);
        tvSeventhNo.setVisibility(View.VISIBLE);
        tvDelete.setVisibility(View.VISIBLE);
        layoutDeleteCancel.setVisibility(View.GONE);
    }

    private void callNumPadListeners() {

        tvFirstNo.setOnClickListener(tv1View -> {
            if (num.length() <= 8) {
                num.append(1);
                addNumbers(num);
                manageDeleteMenu();
            }
        });
        tvSecondNo.setOnClickListener(tv1View -> {
            if (num.length() <= 8) {
                num.append(2);
                addNumbers(num);
                manageDeleteMenu();
            }
        });
        tvThirdNo.setOnClickListener(tv1View -> {
            if (num.length() <= 8) {
                num.append(3);
                addNumbers(num);
                manageDeleteMenu();
            }
        });
        tvFourthNo.setOnClickListener(tv1View -> {
            if (num.length() <= 8) {
                num.append(4);
                addNumbers(num);
                manageDeleteMenu();
            }
        });
        tvFifthNo.setOnClickListener(tv1View -> {
            if (num.length() <= 8) {
                num.append(5);
                addNumbers(num);
                manageDeleteMenu();
            }
        });

        nextPadListeners();
    }

    private void nextPadListeners() {
        tvSixthNo.setOnClickListener(tv1View -> {
            if (num.length() <= 8) {
                num.append(6);
                addNumbers(num);
                manageDeleteMenu();
            }
        });
        tvSeventhNo.setOnClickListener(tv1View -> {
            if (num.length() <= 8) {
                num.append(7);
                addNumbers(num);
            }
        });
        tvEightNo.setOnClickListener(tv1View -> {
            if (num.length() <= 8) {
                num.append(8);
                addNumbers(num);
                manageDeleteMenu();
            }
        });
        tvNine.setOnClickListener(tv1View -> {
            if (num.length() <= 8) {
                num.append(9);
                addNumbers(num);
                manageDeleteMenu();
            }
        });
        tvZero.setOnClickListener(tv1View -> {
            if (num.length() <= 8) {
                num.append(0);
                addNumbers(num);
                manageDeleteMenu();
            }
        });
    }

    @SuppressLint("ResourceType")
    private void addNumbers(StringBuilder num) {
        if (num != null && num.length() > 0) {

            lastAmount = Double.parseDouble(new DecimalFormat("#").format(Double.parseDouble(num.toString())));
            double totalEnteredAmount = lastAmount / 100;
            String amountToDisplay;
            if (selectedCurrency.equalsIgnoreCase("USD")) {
                amountToDisplay = Constants.CURRENCY_FORMAT_USD + currFormat.format(totalEnteredAmount);
            } else {
                amountToDisplay = Constants.CURRENCY_FORMAT + currFormat.format(totalEnteredAmount);
            }
            tvAmount.setText(amountToDisplay);

            setAmountToTextView(totalEnteredAmount);
            if (tvFinalAmount.getText().toString().length() > 0) {
                layoutFinalAmtInfo.setBackgroundResource(R.drawable.amount_text_box);
                if (tvFinalAmount.getText().toString().equalsIgnoreCase(Constants.COBRAR_CURRANCY) &&
                        tvFinalAmount.getText().toString().equalsIgnoreCase(Constants.COBRAR)) {
                    nextIcon.clearAnimation();
                    nextIcon.setVisibility(View.GONE);
                } else {
                    nextIcon.setVisibility(View.VISIBLE);
                    makeMeShake(nextIcon, 8);
                }
                tvFinalAmount.setTextColor(ContextCompat.getColor(QuickSetPaymentActivity.this, R.color.white));

            } else {
                layoutFinalAmtInfo.setBackgroundResource(R.drawable.blue_background_textview);
                nextIcon.setVisibility(View.GONE);
                tvFinalAmount.setText(Constants.COBRAR);
            }
        }

    }

    private void setAmountToTextView(double totalEnteredAmount){
        String finalAmountToShow;
        if (selectedCurrency.equalsIgnoreCase("USD")) {
            finalAmountToShow = Constants.COBRAR_CURRENCY_USD + currFormat.format(totalEnteredAmount);
        } else {
            finalAmountToShow = Constants.COBRAR_CURRANCY + currFormat.format(totalEnteredAmount);
        }
        tvFinalAmount.setText(finalAmountToShow);
    }

    public void setContent(String content, String merchantId, int dismissFlag, String code) {
        mID = merchantId;
        if (dismissFlag == 1) {
            locationBottomSheet.dismiss();
        }

        if (!TextUtils.isEmpty(content)) {
            Log.d("Data", "");
        }
        selectedCode = code;
    }

    public void manageDeleteMenu() {
        if (layoutDeleteCancel.getVisibility() == View.VISIBLE) {
            layoutDeleteCancel.setVisibility(View.GONE);
            tvSeventhNo.setVisibility(View.VISIBLE);
            tvDelete.setVisibility(View.VISIBLE);
        }
    }

}