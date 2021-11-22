package com.sdp.appazul.activities.payment;

import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sdp.appazul.R;
import com.sdp.appazul.activities.dashboard.DashBoardActivity;
import com.sdp.appazul.activities.dashboard.LocationFilterBottomSheet;
import com.sdp.appazul.activities.home.BaseLoggedInActivity;
import com.sdp.appazul.api.ApiManager;
import com.sdp.appazul.api.ServiceUrls;
import com.sdp.appazul.classes.LocationFilter;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.KeyConstants;
import com.sdp.appazul.security.RSAHelper;
import com.sdp.appazul.utils.DeviceUtils;
import com.ncorti.slidetoact.SlideToActView;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class PaymentDataValidateActivity extends BaseLoggedInActivity {
    TextView tvValidaCommerce;
    TextView tvValidLocation;
    TextView tvValidTrnType;
    TextView tvValidOrderNo;
    TextView tvValidAmount;
    TextView tvValidTaxAmount;
    ImageView switchStatus;
    ImageView btnBackScreen;
    TextView cancelPayment;
    double modifiedAmount;
    String responseCode;
    String selectedLocation;
    String selectedLocationID;
    String selectedParentLocationID;
    String selectedTrnType;
    String selectedOrderNumber;
    String enteredTaxAmount;
    String enteredTotalAmount;
    SlideToActView btnConfirmPayment;
    ImageView paymentConfirmLocFilter;
    TextView tvSelectedLocation;
    LocationFilterBottomSheet locationsBottomSheet;
    String lastLocationName;
    String mID;
    String lastLocationMid;
    ApiManager apiManager = new ApiManager(this);
    String paymentLocation;
    String taxStatus;
    TextView tvTaxInclude;
    LinearLayout layoutITBIS;
    RelativeLayout locationFilterSelector;
    Context context;
    Map<String, String> defaultLocations = new HashMap<>();
    LocationFilter locationFilter = new LocationFilter();
    String selectedCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_data_validate);
        context = this;
        getDataFromBackScreen();
        defaultLocations = ((AzulApplication) this.getApplication()).getDefaultLocation();

        initControls();

    }

    private void getDataFromBackScreen() {
        Intent intent = getIntent();
        selectedParentLocationID = intent.getStringExtra("SELECTED_PARENT_LOCATION");
        selectedLocationID = intent.getStringExtra("SELECTED_LOCATION_ID");
        selectedLocation = intent.getStringExtra(Constants.SELECTED_LOCATION);
        selectedTrnType = intent.getStringExtra("SELECTED_TRN_TYPE");
        selectedOrderNumber = intent.getStringExtra("ORDER_NO");
        enteredTaxAmount = intent.getStringExtra(Constants.TAX_LABEL);
        enteredTotalAmount = intent.getStringExtra("TOTAL_AMOUNT");
        responseCode = intent.getStringExtra("CODE");
        modifiedAmount = intent.getDoubleExtra("AMOUNT", 0);
        taxStatus = intent.getStringExtra("TAX_STATUS");
        selectedCurrency = intent.getStringExtra("CURRENCY");

        paymentLocation = ((AzulApplication) context.getApplicationContext()).getLocationDataShare();

    }

    private void initControls() {
        locationFilterSelector = findViewById(R.id.locationFilterSelector);
        paymentConfirmLocFilter = findViewById(R.id.paymentConfirmLocFilter);
        tvSelectedLocation = findViewById(R.id.tvSelectedLocation);
        btnBackScreen = findViewById(R.id.btnBackScreen);
        locationFilter = ((AzulApplication) this.getApplication()).getLocationFilter();
        if (locationFilter != null) {
            lastLocationName = locationFilter.getLocationNameAndId();
            tvSelectedLocation.setText(lastLocationName);
            lastLocationMid = locationFilter.getmId();
            mID = lastLocationMid;

        } else {
            if (defaultLocations != null) {
                String defaultParentName = defaultLocations.get("PARENT_LOC_NAME");
                String defaultLocationName = defaultLocations.get("CHILD_LOC_NAME");
                String defaultLocationId = defaultLocations.get("CHILD_LOC_ID");
                String tacStatusValue = defaultLocations.get("TAX_STATUS");
                String codeValue = defaultLocations.get("CODE");
                selectedCurrency = defaultLocations.get("CURR");
                String locationToDisplay = defaultLocationName + " - " + defaultLocationId;
                tvSelectedLocation.setText(locationToDisplay);
                lastLocationMid = defaultLocationId;
                mID = lastLocationMid;
                selectedParentLocationID = defaultParentName;
                responseCode = codeValue;
                taxStatus = tacStatusValue;
            }
        }

        btnBackScreen.setOnClickListener(btnBackScreenView -> {
            Intent intent = new Intent(PaymentDataValidateActivity.this, PaymentConfirmActivity.class);
            ((AzulApplication) ((PaymentDataValidateActivity) this).getApplication()).setLocationDataShare(paymentLocation);

            intent.putExtra("CODE", responseCode);
            intent.putExtra("SELECTED_PARENT_LOCATION", selectedParentLocationID);
            intent.putExtra("SELECTED_LOCATION_ID", selectedLocationID);
            intent.putExtra(Constants.SELECTED_LOCATION, selectedLocation);
            intent.putExtra("SELECTED_TRN_TYPE", selectedTrnType);
            intent.putExtra("ORDER_NO", selectedOrderNumber);
            intent.putExtra(Constants.TAX_LABEL, enteredTaxAmount);
            startActivity(intent);
            this.overridePendingTransition(R.anim.animation_leave,
                    R.anim.slide_nothing);

        });

        tvValidaCommerce = findViewById(R.id.tvValidaComercio);
        tvValidLocation = findViewById(R.id.tvValidLocation);
        tvValidTrnType = findViewById(R.id.tvValidTrnType);
        tvValidOrderNo = findViewById(R.id.tvValidOrderNo);
        tvValidAmount = findViewById(R.id.tvValidAmount);
        tvValidTaxAmount = findViewById(R.id.tvValidTaxAmount);
        switchStatus = findViewById(R.id.switchStatus);
        cancelPayment = findViewById(R.id.cancelPayment);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
        layoutITBIS = findViewById(R.id.layoutITBIS);
        tvTaxInclude = findViewById(R.id.tvTaxInclude);
        locationFilterSelector.setEnabled(false);
        locationFilterSelector.setClickable(false);
        locationFilterSelector.setOnClickListener(paymentLocationSelectorView -> {
            locationsBottomSheet = new LocationFilterBottomSheet(paymentLocation, Constants.PAYMENT_VALIDATE);
            locationsBottomSheet.show(getSupportFragmentManager(), "bottomSheet");

        });

        cancelPayment.setOnClickListener(view -> cancelAlert(0, this));
        setData();
        setListeners();
    }

    private void setListeners() {
        btnConfirmPayment.setOnSlideCompleteListener(slideToActView -> {
            cancelPayment.setEnabled(false);
            cancelPayment.setTextColor(ContextCompat.getColor(PaymentDataValidateActivity.this, R.color.light_gray));

            callLinkCreateApi( enteredTotalAmount, enteredTaxAmount, selectedOrderNumber, selectedTrnType, selectedLocationID);

        });
    }

    private void callLinkCreateApi(
                                   String enteredTotalAmount,
                                   String enteredTaxAmount,
                                   String selectedOrderNumber,
                                   String selectedTrnType,
                                   String selectedLocationID) {

        JSONObject json = new JSONObject();
        try {
            String tcpKey = ((AzulApplication) PaymentDataValidateActivity.this.getApplication()).getTcpKey();
            String vcr = ((AzulApplication) PaymentDataValidateActivity.this.getApplication()).getVcr();
            json.put("tcp", RSAHelper.ecryptRSA(PaymentDataValidateActivity.this, tcpKey));
            JSONObject payload = new JSONObject();
            payload.put("device", DeviceUtils.getDeviceId(PaymentDataValidateActivity.this));
            Log.d("PaymentData", "callLinkCreateApi: " + responseCode);
            payload.put("Code", responseCode);

            if (selectedTrnType.equalsIgnoreCase("Venta")) {
                payload.put("TransactionType", "Sale");
            } else {
                payload.put("TransactionType", selectedTrnType);
            }
            payload.put("Language", "ES");
            payload.put("ClientContractNumber", "");
            payload.put("ClientIdentType", "");
            payload.put("ClientIdentNum", "");
            payload.put("ClientEmail", "");
            payload.put("NotifySpanish", false);
            payload.put("NotifyEnglish", false);
            payload.put("ClientName", "");
            if (selectedOrderNumber != null && selectedOrderNumber.length() > 0) {
                payload.put("OrderNumber", selectedOrderNumber);
            } else {
                payload.put("OrderNumber", "");
            }

            if (enteredTaxAmount != null && !TextUtils.isEmpty(enteredTaxAmount)) {
                if (enteredTaxAmount.contains(",")) {
                    double finalItbis = Double.parseDouble(enteredTaxAmount.replace(",", ""));
                    payload.put(Constants.TAX_LABEL, finalItbis);
                } else {
                    double finalItbis = Double.parseDouble(enteredTaxAmount);
                    payload.put(Constants.TAX_LABEL, finalItbis);
                }
            } else {
                payload.put(Constants.TAX_LABEL, 0);
            }

            double finalAmount = Double.parseDouble(enteredTotalAmount.replace(",", ""));
            payload.put("Amount", finalAmount);
            payload.put("MerchantId", selectedLocationID);


            Log.d("TAG", "Decryptrd Req : " + payload.toString());
            json.put("payload", RSAHelper.encryptAES(payload.toString(), Base64.decode(tcpKey, 0), Base64.decode(vcr, 0)));

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.CREATE_PAYMENT_LINK, json);
    }

    public static String getEncodedString(String encodedString) {
        return new String(encodedString.getBytes(StandardCharsets.UTF_8));
    }

    private void setData() {

        if (!TextUtils.isEmpty(selectedLocation) && !TextUtils.isEmpty(selectedTrnType)
                && !TextUtils.isEmpty(enteredTotalAmount)) {
            tvValidLocation.setText(selectedLocation);
            String validAmountToShow;
            if (selectedCurrency.equalsIgnoreCase("USD")) {
                validAmountToShow = Constants.CURRENCY_FORMAT_USD + enteredTotalAmount;
            } else {
                validAmountToShow = Constants.CURRENCY_FORMAT + enteredTotalAmount;
            }
            tvValidAmount.setText(validAmountToShow);
            if (selectedOrderNumber == null || selectedOrderNumber.length() <= 0) {
                tvValidOrderNo.setText("N/A");
            } else {
                tvValidOrderNo.setText(selectedOrderNumber);
            }
            setTax();
            tvValidTrnType.setText(selectedTrnType);
            setParentLocationName();

            taxDataAttach();
        }

    }
    private void setTax(){
        if (enteredTaxAmount == null || enteredTaxAmount.length() <= 0 || enteredTaxAmount.equalsIgnoreCase("null")) {
            String taxToDisplay;
            if (selectedCurrency.equalsIgnoreCase("USD")) {
                taxToDisplay = Constants.CURRENCY_FORMAT_USD + getResources().getString(R.string.zero_amount);
            } else {
                taxToDisplay = Constants.CURRENCY_FORMAT + getResources().getString(R.string.zero_amount);
            }
            tvValidTaxAmount.setText(taxToDisplay);
        } else {
            String taxAmount;
            if (selectedCurrency.equalsIgnoreCase("USD")) {
                taxAmount = Constants.CURRENCY_FORMAT_USD + enteredTaxAmount;
            } else {
                taxAmount = Constants.CURRENCY_FORMAT + enteredTaxAmount;
            }
            tvValidTaxAmount.setText(taxAmount);
        }
    }
    private void setParentLocationName(){
        if (selectedParentLocationID != null) {
            tvValidaCommerce.setText(selectedParentLocationID);
        } else {
            tvValidaCommerce.setText("Importadora Wu SRL");
        }
    }
    private void taxDataAttach() {
        if (taxStatus != null && taxStatus.equalsIgnoreCase(Constants.BOOLEAN_FALSE)) {
            tvTaxInclude.setVisibility(View.GONE);
            layoutITBIS.setVisibility(View.GONE);
        } else if (enteredTaxAmount == null || enteredTaxAmount.length() <= 0 || enteredTaxAmount.equalsIgnoreCase("null")) {
            tvTaxInclude.setVisibility(View.VISIBLE);
            layoutITBIS.setVisibility(View.VISIBLE);
            String validTaxAmount;
            if (selectedCurrency.equalsIgnoreCase("USD")) {
                validTaxAmount = Constants.CURRENCY_FORMAT_USD + getResources().getString(R.string.zero_amount);
            } else {
                validTaxAmount = Constants.CURRENCY_FORMAT + getResources().getString(R.string.zero_amount);
            }
            tvValidTaxAmount.setText(validTaxAmount);
        } else {
            tvTaxInclude.setVisibility(View.VISIBLE);
            layoutITBIS.setVisibility(View.VISIBLE);
            String taxAmount;
            if (selectedCurrency.equalsIgnoreCase("USD")) {
                taxAmount = Constants.CURRENCY_FORMAT_USD + enteredTaxAmount;
            } else {
                taxAmount = Constants.CURRENCY_FORMAT + enteredTaxAmount;
            }
            tvValidTaxAmount.setText(taxAmount);
        }

    }


    public void setContent(String name, String content, String merchantId, int dismissFlag, String taxExempt) {
        tvSelectedLocation.setText(content);
        mID = merchantId;
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(taxExempt)) {
            Log.d("Data", "" + name + taxExempt);
        }
        if (dismissFlag == 1) {
            locationsBottomSheet.dismiss();
        }
    }

    String responseLink;
    String displayAmount;
    String currency;

    public void paymentLnkCreateResponse(String responseData) {
        JSONObject jsonObject;
        try {
            if (responseData.contains("\"data\":null") || responseData.contains("bsStatusCode\":500")) {
                cancelAlert(1, this);
            } else {
                jsonObject = new JSONObject(responseData);
                JSONObject jsonTransactionOb = jsonObject.getJSONObject("data");
                responseLink = jsonTransactionOb.getString("LinkFullUrl");
                JSONObject jsonLinkInfoObject = jsonTransactionOb.getJSONObject("LinkInfo");

                displayAmount = jsonLinkInfoObject.getString("Amount");
                currency = jsonLinkInfoObject.getString("Currency");

                if (responseLink != null) {
                    Intent intent = new Intent(PaymentDataValidateActivity.this, LinkSharingActivity.class);
                    ((AzulApplication) ((PaymentDataValidateActivity) this).getApplication()).setLocationDataShare(paymentLocation);

                    intent.putExtra("RESPONSE_LINK", responseLink);
                    intent.putExtra("DISPLAY_AMOUNT", enteredTotalAmount);
                    intent.putExtra("CURRENCY", selectedCurrency);
                    intent.putExtra(Constants.SELECTED_LOCATION, selectedLocation);
                    startActivity(intent);
                    this.overridePendingTransition(R.anim.animation_enter,
                            R.anim.slide_nothing);

                }
            }
        } catch (JSONException e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }

    }

    Dialog cancelerDialog;

    public void cancelAlert(int dialogType, Context activity) {
        try {
            cancelerDialog = new Dialog(activity);
            cancelerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            cancelerDialog.setCancelable(false);
            cancelerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            cancelerDialog.setContentView(R.layout.cancel_dialog_layout);
            TextView textTitle = cancelerDialog.findViewById(R.id.textTitle);
            TextView textMsg = cancelerDialog.findViewById(R.id.textMsg);
            Typeface typeface;
            typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Medium.ttf");
            textTitle.setTypeface(typeface);
            textMsg.setTypeface(typeface);
            TextView btnNo = cancelerDialog.findViewById(R.id.btnNo);
            TextView btnYes = cancelerDialog.findViewById(R.id.btnYes);
            btnYes.setTypeface(typeface);

            if (dialogType == 1) {
                btnNo.setVisibility(View.GONE);
                btnYes.setText("Aceptar");
                textTitle.setText(getResources().getString(R.string.alert_sorry_title));
                textMsg.setText(getResources().getString(R.string.alert_sorry_message));
            } else {
                btnYes.setText("Sí");
                btnNo.setVisibility(View.VISIBLE);
                textTitle.setText(getResources().getString(R.string.Cancel));
                textMsg.setText(getResources().getString(R.string.cancel_button_message));
            }

            btnYes.setOnClickListener(view ->
                    yesButtonListener(dialogType));
            btnNo.setOnClickListener(view -> {
                if (cancelerDialog != null && cancelerDialog.isShowing()) {
                    cancelerDialog.dismiss();
                }
            });
            cancelerDialog.show();

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void yesButtonListener(int dialogType) {
        if (dialogType == 1) {

            Intent intent = new Intent(PaymentDataValidateActivity.this, DashBoardActivity.class);
            ((AzulApplication) ((PaymentDataValidateActivity) this).getApplication()).setLocationDataShare(paymentLocation);

            startActivity(intent);
            this.overridePendingTransition(R.anim.animation_leave,
                    R.anim.slide_nothing);
            if (cancelerDialog != null && cancelerDialog.isShowing()) {
                cancelerDialog.dismiss();
            }
        } else {
            Intent intent = new Intent(PaymentDataValidateActivity.this, SetPaymentInfoActivity.class);
            ((AzulApplication) ((PaymentDataValidateActivity) this).getApplication()).setLocationDataShare(paymentLocation);
            startActivity(intent);
            this.overridePendingTransition(R.anim.animation_leave,
                    R.anim.slide_nothing);
            if (cancelerDialog != null && cancelerDialog.isShowing()) {
                cancelerDialog.dismiss();
            }
        }
    }
}