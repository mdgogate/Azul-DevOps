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

import com.sdp.appazul.activities.home.BaseLoggedInActivity;
import com.sdp.appazul.activities.home.MenuLocationFilter;
import com.sdp.appazul.api.ApiManager;
import com.sdp.appazul.classes.LocationFilter;
import com.sdp.appazul.classes.LocationFilterThirdGroup;
import com.sdp.appazul.globals.AppAlters;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SetPaymentInfoActivity extends BaseLoggedInActivity {
    TextView tvAmount;
    TextView tvFinalAmount;
    String locationJson;
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
    TextView tvSelectedPaymentLocation;
    RelativeLayout layoutDeleteCancel;
    RelativeLayout layoutFinalAmtInfo;
    RelativeLayout locationFilterForTrans;
    boolean sum = false;
    StringBuilder num = new StringBuilder();
    int previousAmount = 0;
    StringBuilder resultData;
    LocationFilter locationFilter = new LocationFilter();
    String lastLocationName;
    String lastLocationMid;
    String mID;
    ImageView paymentLocationSelector;
    ImageView btnBackScreen;
    MenuLocationFilter locationBottomSheet;
    String totalAmountEntered;
    Intent intent;
    ApiManager apiManager = new ApiManager(this);
    int tempAmount = 0;
    Map<String, String> defaultLocation;
    int amountCheck = 0;
    String responseCode;
    Context context;
    Map<String, String> defaultLocations = new HashMap<>();
    int resultDataCheck;
    String selectedCurrency;
    RelativeLayout act_set_payment;
    static int SNACK_LENGTH = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_payment_info);
        context = this;
        intent = getIntent();
        locationJson = ((AzulApplication) context.getApplicationContext()).getLocationDataShare();

        initControls();

        locationFilter = ((AzulApplication) this.getApplication()).getLocationFilter();

        if (locationFilter != null) {
            lastLocationName = locationFilter.getLocationNameAndId();
            lastLocationMid = locationFilter.getmId();
            mID = lastLocationMid;
            responseCode = locationFilter.getPaymentCode();
            selectedCurrency = locationFilter.getCurrency();
            tvSelectedPaymentLocation.setText(lastLocationName.toLowerCase());
        } else {
            getDefaultLocation(locationJson);
            defaultLocations = ((AzulApplication) this.getApplication()).getDefaultLocation();
            if (defaultLocations != null) {
                String defaultLocationName = defaultLocations.get("CHILD_LOC_NAME");
                String defaultLocationId = defaultLocations.get("CHILD_LOC_ID");
                String locationToDisplay = defaultLocationName + " - " + defaultLocationId;
                tvSelectedPaymentLocation.setText(locationToDisplay);
                selectedCurrency = defaultLocations.get("CURR");
            }
        }
        checkCurrency();
        getPraviousAmountEntered();
    }

    private void initControls() {
        act_set_payment = findViewById(R.id.act_set_payment);
        tvAmount = findViewById(R.id.tvAmount);
        btnBackScreen = findViewById(R.id.btnBackScreen);
        locationFilterForTrans = findViewById(R.id.locaFilterForTran);
        paymentLocationSelector = findViewById(R.id.paymentLocationSelector);
        tvSelectedPaymentLocation = findViewById(R.id.tvSelectedPaymentLocation);
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

        locationFilterForTrans.setOnClickListener(paymentLocationSelectorView -> {
            locationBottomSheet = new MenuLocationFilter(locationJson, "SET_PAYMENT", 4);
            locationBottomSheet.show(getSupportFragmentManager(), "bottomSheet");

        });
        tvDelete.setOnClickListener(tvDeleteView -> {
            tvSeventhNo.setVisibility(View.GONE);
            tvDelete.setVisibility(View.GONE);
            layoutDeleteCancel.setVisibility(View.VISIBLE);
        });
        btnBackScreen.setOnClickListener(btnBackScreenView -> {


            Intent intent = new Intent(SetPaymentInfoActivity.this, DashBoardActivity.class);
            intent.putExtra(Constants.LOCATION_RESPONSE, locationJson);
            overridePendingTransition(0, 1);
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
                callingConfirmationActivity());

        callNumPadListeners();

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
        resultData.append(previousAmount);
        resultDataCheck = Integer.parseInt(resultData.toString());

        if (resultDataCheck < 999999999) {
            checkNewExtraAmount(resultData);
        } else {
            resultData = new StringBuilder();
            resultData.append(tempAmount);
            amountCheck = 0;
            previousAmount = 0;
            resultDataCheck = 0;
            num.setLength(0);
            addNumbers(resultData);
            showToast();
        }
    }

    private void checkNewExtraAmount(StringBuilder previousResultData) {
        sum = true;
        if (num != null && num.length() > 0) {
            num.setLength(0);
            layoutFinalAmtInfo.setBackgroundResource(R.drawable.blue_background_textview);
            addNumbers(previousResultData);
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
            addNumbers(resultData);
        }
    }

    private void showToast() {
        Snackbar snackbar = Snackbar.make(act_set_payment, this.getResources().getString(R.string.calculator_limit_label), SNACK_LENGTH)
                .setBackgroundTint(Color.parseColor(Constants.COLOR_SKY));
        snackbar.show();
    }

    private void getPraviousAmountEntered() {
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

            Intent confirmIntent = new Intent(SetPaymentInfoActivity.this, PaymentConfirmActivity.class);
            String totalAmount = tvFinalAmount.getText().toString().replace(",", "");
            String totalAmountNew;
            if (selectedCurrency.equalsIgnoreCase("USD")) {
                totalAmountNew = totalAmount.replace("Cobrar US$ ", "");
            } else {
                totalAmountNew = totalAmount.replace("Cobrar RD$ ", "");
            }
            String totalLastAmount = totalAmountNew.replace(".", "");

            if (Boolean.FALSE.equals(sum)) {
                confirmIntent.putExtra(Constants.TOTAL_AMOUNT, totalLastAmount);
            } else {
                Log.d("TAG", totalLastAmount + "   : 1010 ResultData 1010 : " + resultData.toString());
                confirmIntent.putExtra(Constants.TOTAL_AMOUNT, totalLastAmount);
            }
            confirmIntent.putExtra("CODE", responseCode);
            confirmIntent.putExtra("CURRENCY", selectedCurrency);

            ((AzulApplication) ((SetPaymentInfoActivity) this).getApplication()).setLocationDataShare(locationJson);

            startActivity(confirmIntent);
            this.overridePendingTransition(R.anim.animation_enter,
                    R.anim.slide_nothing);

        } else {
            Snackbar snackbar = Snackbar.make(act_set_payment, this.getResources().getString(R.string.empty_amount_error), SNACK_LENGTH)
                    .setBackgroundTint(Color.parseColor(Constants.COLOR_SKY));
            snackbar.show();
        }
    }

    private void callAllDelete() {

        if (num.length() <= 0) {
            if (selectedCurrency.equalsIgnoreCase("USD")) {
                tvAmount.setText(Constants.CURRENCY_USD);
            } else {
                tvAmount.setText(Constants.CURRENCY);
            }
            tvFinalAmount.setText(Constants.COBRAR);
            layoutFinalAmtInfo.setBackgroundResource(R.drawable.textview_show_amount_bg);
            tvSeventhNo.setVisibility(View.VISIBLE);
            tvDelete.setVisibility(View.VISIBLE);
            layoutDeleteCancel.setVisibility(View.GONE);
            resultData = new StringBuilder();
            previousAmount = 0;
            tempAmount = 0;
            num.setLength(0);
        } else {
            if (selectedCurrency.equalsIgnoreCase("USD")) {
                tvAmount.setText(Constants.CURRENCY_USD);
            } else {
                tvAmount.setText(Constants.CURRENCY);
            }
            tvFinalAmount.setText(Constants.COBRAR);
            layoutFinalAmtInfo.setBackgroundResource(R.drawable.textview_show_amount_bg);
            previousAmount = 0;
            tempAmount = 0;
            num.setLength(0);
            tvSeventhNo.setVisibility(View.VISIBLE);
            tvDelete.setVisibility(View.VISIBLE);
            layoutDeleteCancel.setVisibility(View.GONE);
            resultData = new StringBuilder();
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
        layoutFinalAmtInfo.setBackgroundResource(R.drawable.textview_show_amount_bg);
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
        sixToTenListeners();
    }

    private void sixToTenListeners() {
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

    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
    DecimalFormat currFormat = new DecimalFormat("#,##0.00",symbols);
    double lastAmount = 0;

    @SuppressLint("ResourceType")
    private void addNumbers(StringBuilder num) {
        if (num != null && num.length() > 0) {

            int parsedAmount = Integer.parseInt(num.toString());
            if (parsedAmount > 999999999) {
                previousAmount = 0;

                Snackbar snackbar = Snackbar.make(act_set_payment, this.getResources().getString(R.string.calculator_limit_label), SNACK_LENGTH)
                        .setBackgroundTint(Color.parseColor(Constants.COLOR_SKY));
                snackbar.show();
            } else {

                lastAmount = Double.parseDouble(new DecimalFormat("#").format(Double.parseDouble(num.toString())));
                double totalEnteredAmount = lastAmount / 100;

                setAmount(totalEnteredAmount);
                String finalAmountToShow;
                if (selectedCurrency.equalsIgnoreCase("USD")) {
                    finalAmountToShow = Constants.COBRAR_CURRENCY_USD + currFormat.format(totalEnteredAmount);
                } else {
                    finalAmountToShow = Constants.COBRAR_CURRANCY + currFormat.format(totalEnteredAmount);
                }
                tvFinalAmount.setText(finalAmountToShow);
                if (tvFinalAmount.getText().toString().length() > 0) {
                    layoutFinalAmtInfo.setBackgroundResource(R.drawable.amount_text_box);
                    checkEmptyAmount();
                    tvFinalAmount.setTextColor(ContextCompat.getColor(SetPaymentInfoActivity.this, R.color.white));

                } else {
                    layoutFinalAmtInfo.setBackgroundResource(R.drawable.blue_background_textview);
                    nextIcon.setVisibility(View.GONE);
                    tvFinalAmount.setText(Constants.COBRAR);
                }

            }
        }

    }

    private void setAmount(double totalEnteredAmount) {
        String amountToDisplay;
        if (selectedCurrency.equalsIgnoreCase("USD")) {
            amountToDisplay = Constants.CURRENCY_FORMAT_USD + currFormat.format(totalEnteredAmount);
        } else {
            amountToDisplay = Constants.CURRENCY_FORMAT + currFormat.format(totalEnteredAmount);
        }
        tvAmount.setText(amountToDisplay);
    }

    private void checkEmptyAmount() {
        if (tvFinalAmount.getText().toString().equalsIgnoreCase(Constants.COBRAR_CURRANCY) &&
                tvFinalAmount.getText().toString().equalsIgnoreCase(Constants.COBRAR)) {
            nextIcon.clearAnimation();
            nextIcon.setVisibility(View.GONE);
        } else {
            nextIcon.setVisibility(View.VISIBLE);
            makeMeShake(nextIcon, 8);
        }
    }

    public static void makeMeShake(View view, int offset) {
        Animation anim = new TranslateAnimation(-offset, offset, 0, 0);
        anim.setDuration(600);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        view.startAnimation(anim);
    }

    public void setContent(String content, String merchantId, int dismissFlag, String code, LocationFilterThirdGroup locationFilterThirdGroup) {
        tvSelectedPaymentLocation.setText(content.toLowerCase());
        mID = merchantId;
        if (dismissFlag == 1) {
            locationBottomSheet.dismiss();
        }
        responseCode = code;
        selectedCurrency = locationFilterThirdGroup.getCurrency();
        setTaxCurrencyHint();
    }

    public void paymentLnkMerchantResponse(String responseData) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(responseData);
            Log.d("DATA", "Link Merchant Data ==>  " + jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setTaxCurrencyHint() {
        if (num != null && !TextUtils.isEmpty(num.toString())) {
            addNumbers(num);
        } else if (resultData != null && !TextUtils.isEmpty(resultData.toString())) {
            addNumbers(resultData);
        } else {
            checkCurrency();
        }
    }

    private void getDefaultLocation(String locationResponse) {

        try {
            JSONObject jsonResponse = new JSONObject(locationResponse);
            JSONArray parentLevelLocations = jsonResponse.getJSONArray("CallCenters");
            for (int i = 0; i < parentLevelLocations.length(); i++) {
                JSONObject parentData = parentLevelLocations.getJSONObject(i);
                responseCode = parentData.getString("Code");

                String parentLocationName = parentData.getString("Name");
                JSONArray assignedLocationsObject = parentData.getJSONArray("Merchants");
                for (int j = 0; j < assignedLocationsObject.length(); j++) {
                    JSONObject secondPositionLocation = assignedLocationsObject.getJSONObject(0);
                    String secondPositionLocationId = secondPositionLocation.getString(Constants.MERCHANT_ID);
                    String secondPositionLocationName = secondPositionLocation.getString("Name");
                    String secondPositionTax = secondPositionLocation.getString("ReportsTax");
                    String secondPositionCurrency = secondPositionLocation.getString("Currency");
                    defaultLocation = new HashMap<>();
                    defaultLocation.put("PARENT_LOC_NAME", parentLocationName);
                    defaultLocation.put("CHILD_LOC_ID", secondPositionLocationId);
                    defaultLocation.put("CHILD_LOC_NAME", secondPositionLocationName);
                    defaultLocation.put("TAX_STATUS", secondPositionTax);
                    defaultLocation.put("CODE", responseCode);
                    defaultLocation.put("CURR", secondPositionCurrency);
                    ((AzulApplication) (this).getApplication()).setDefaultLocation(defaultLocation);
                    long mId = Long.parseLong(secondPositionLocationId);


                    LocationFilter locationFilterObj = new LocationFilter();
                    locationFilterObj.setChildPosition((Integer) 0);
                    locationFilterObj.setParentPosition(0);
                    locationFilterObj.setLocationNameAndId(secondPositionLocationName + " - " + mId);
                    locationFilterObj.setmId(secondPositionLocationId);
                    locationFilterObj.setLocationName(secondPositionLocationName);
                    locationFilterObj.setTaxExempt(secondPositionTax);
                    locationFilterObj.setParentName(parentLocationName);
                    locationFilterObj.setPaymentCode(responseCode);
                    locationFilterObj.setCurrency(secondPositionCurrency);
                    ((AzulApplication) ((SetPaymentInfoActivity) context).getApplication()).setLocationFilter(locationFilterObj);

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void manageDeleteMenu() {
        if (layoutDeleteCancel.getVisibility() == View.VISIBLE) {
            layoutDeleteCancel.setVisibility(View.GONE);
            tvSeventhNo.setVisibility(View.VISIBLE);
            tvDelete.setVisibility(View.VISIBLE);
        }
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
}