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
import android.widget.TextView;

import com.ncorti.slidetoact.SlideToActView;
import com.sdp.appazul.R;
import com.sdp.appazul.activities.dashboard.DashBoardActivity;
import com.sdp.appazul.activities.dashboard.LocationFilterBottomSheet;
import com.sdp.appazul.activities.home.BaseLoggedInActivity;
import com.sdp.appazul.activities.home.MainMenuActivity;
import com.sdp.appazul.api.ApiManager;
import com.sdp.appazul.api.ServiceUrls;
import com.sdp.appazul.classes.LocationFilter;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.KeyConstants;
import com.sdp.appazul.security.RSAHelper;
import com.sdp.appazul.utils.DeviceUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class QuickPayValidationActivity extends BaseLoggedInActivity {
    TextView tvEmail;
    TextView tvCustomerName;
    TextView tvValidateCommerce;
    TextView tvTaxInclude;
    TextView tvValidLocation;
    TextView tvValidTrnType;
    TextView tvValidOrderNo;
    TextView tvValidAmount;
    TextView tvValidTaxAmount;
    ImageView switchStatus;
    ImageView btnBackScreen;
    TextView cancelPayment;
    String locationJson;
    String paymentLocations;
    double modifiedAmount;
    String responseCode;
    String taxStatus;
    String selectedCurrency;
    String previousParentLocationName;
    String selectedLocation;
    String selectedLocationID;
    String selectedTrnType;
    String selectedOrderNumber;
    String emailAddress;
    String clientName;
    String enteredTaxAmount;
    String enteredTotalAmount;
    SlideToActView btnConfirmPayment;
    LocationFilterBottomSheet locationBottomSheet;
    LocationFilter locationFilter = new LocationFilter();
    String lastLocationName;
    String merchantId;
    String lastLocationMid;
    ApiManager apiManager = new ApiManager(this);
    LinearLayout layoutITBIS;
    Dialog cancelerDialog;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_pay_validation);
        context = this;
        getDataFromBackScreen();

        initControls();
    }

    private void getDataFromBackScreen() {
        Intent intent = getIntent();
        selectedLocationID = intent.getStringExtra("SELECTED_LOCATION_ID");
        selectedLocation = intent.getStringExtra(Constants.SELECTED_LOCATION);
        selectedTrnType = intent.getStringExtra("SELECTED_TRN_TYPE");
        selectedOrderNumber = intent.getStringExtra("ORDER_NO");
        clientName = intent.getStringExtra("CLIENT_NAME");
        emailAddress = intent.getStringExtra("EMAIL");
        enteredTaxAmount = intent.getStringExtra(Constants.TAX_LABEL);
        enteredTotalAmount = intent.getStringExtra("TOTAL_AMOUNT");
        responseCode = intent.getStringExtra("CODE");
        previousParentLocationName = intent.getStringExtra("SELECTED_PARENT_LOCATION");
        taxStatus = intent.getStringExtra("TAX_STATUS");
        modifiedAmount = intent.getDoubleExtra("AMOUNT", 0);
        selectedCurrency = intent.getStringExtra("CURRENCY");

        locationJson = ((AzulApplication) context.getApplicationContext()).getLocationDataShare();

    }

    private void initControls() {
        tvTaxInclude = findViewById(R.id.tvTaxInclude);
        tvEmail = findViewById(R.id.tvEmail);
        layoutITBIS = findViewById(R.id.layoutITBIS);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        btnBackScreen = findViewById(R.id.btnBackScreen);

        locationFilter = ((AzulApplication) this.getApplication()).getLocationFilter();
        if (locationFilter != null) {
            lastLocationName = locationFilter.getLocationNameAndId();
            lastLocationMid = locationFilter.getmId();
            merchantId = lastLocationMid;

        }

        btnBackScreen.setOnClickListener(btnBackScreenView -> {
            Intent intent = new Intent(QuickPayValidationActivity.this, QuickPayConfirmActivity.class);
            ((AzulApplication) ((QuickPayValidationActivity) this).getApplication()).setLocationDataShare(locationJson);
            intent.putExtra("CODE", responseCode);
            intent.putExtra("SELECTED_LOCATION_ID", selectedLocationID);
            intent.putExtra(Constants.SELECTED_LOCATION, selectedLocation);
            intent.putExtra("SELECTED_TRN_TYPE", selectedTrnType);
            intent.putExtra("ORDER_NO", selectedOrderNumber);
            intent.putExtra("CLIENT_NAME", clientName);
            intent.putExtra("EMAIL", emailAddress);
            intent.putExtra(Constants.TAX_LABEL, enteredTaxAmount);

            startActivity(intent);
            this.overridePendingTransition(R.anim.animation_leave,
                    R.anim.slide_nothing);
        });

        tvValidateCommerce = findViewById(R.id.tvValidaComercio);
        tvValidLocation = findViewById(R.id.tvValidLocation);
        tvValidTrnType = findViewById(R.id.tvValidTrnType);
        tvValidOrderNo = findViewById(R.id.tvValidOrderNo);
        tvValidAmount = findViewById(R.id.tvValidAmount);
        tvValidTaxAmount = findViewById(R.id.tvValidTaxAmount);
        switchStatus = findViewById(R.id.switchStatus);
        cancelPayment = findViewById(R.id.cancelPayment);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);

        setData();
        setListeners();
    }

    private void setListeners() {
        btnConfirmPayment.setOnSlideCompleteListener(slideToActView -> {
            cancelPayment.setEnabled(false);
            cancelPayment.setTextColor(ContextCompat.getColor(QuickPayValidationActivity.this, R.color.light_gray));
            callLinkCreateApi(enteredTotalAmount,
                    enteredTaxAmount, selectedOrderNumber,
                    selectedTrnType, selectedLocationID,
                    clientName, emailAddress);
        });

        cancelPayment.setOnClickListener(view -> cancelAlert(0, this));
    }

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
                textTitle.setText(getResources().getString(R.string.alert_sorry_title));
                btnNo.setVisibility(View.GONE);
                btnYes.setText("Aceptar");
                textMsg.setText(getResources().getString(R.string.alert_sorry_message));
            } else {
                btnYes.setText("Sí");
                btnNo.setVisibility(View.VISIBLE);
                textTitle.setText(getResources().getString(R.string.Cancel));
                textMsg.setText(getResources().getString(R.string.cancel_button_message));
            }

            btnYes.setOnClickListener(view -> yesButtonListener(dialogType));
            btnNo.setOnClickListener(view -> {
                if (cancelerDialog != null && cancelerDialog.isShowing()) {
                    cancelerDialog.dismiss();
                }
            });
            cancelerDialog.show();

        } catch (
                Exception e) {
            Log.e("Exception : ", Log.getStackTraceString(e));
        }
    }

    private void yesButtonListener(int dialogType) {
        if (dialogType == 1) {

            Intent intent = new Intent(QuickPayValidationActivity.this, MainMenuActivity.class);
            ((AzulApplication) ((QuickPayValidationActivity) this).getApplication()).setLocationDataShare(locationJson);
            startActivity(intent);
            this.overridePendingTransition(R.anim.animation_leave,
                    R.anim.slide_nothing);
            if (cancelerDialog != null && cancelerDialog.isShowing()) {
                cancelerDialog.dismiss();
            }
        } else {
            Intent intent = new Intent(QuickPayValidationActivity.this, QuickSetPaymentActivity.class);
            ((AzulApplication) ((QuickPayValidationActivity) this).getApplication()).setLocationDataShare(locationJson);
            startActivity(intent);
            this.overridePendingTransition(R.anim.animation_leave,
                    R.anim.slide_nothing);
            if (cancelerDialog != null && cancelerDialog.isShowing()) {
                cancelerDialog.dismiss();
            }
        }
    }

    private void callLinkCreateApi(
            String enteredTotalAmount,
            String enteredTaxAmount,
            String selectedOrderNumber,
            String selectedTrnType,
            String selectedLocationID,
            String clientName,
            String customerEmail) {

        JSONObject json = new JSONObject();
        JSONObject decJson = new JSONObject();
        try {
            String tcpKey = ((AzulApplication) QuickPayValidationActivity.this.getApplication()).getTcpKey();
            String vcr = ((AzulApplication) QuickPayValidationActivity.this.getApplication()).getVcr();
            json.put("tcp", RSAHelper.ecryptRSA(QuickPayValidationActivity.this, tcpKey));
            JSONObject payload = new JSONObject();
            payload.put("device", DeviceUtils.getDeviceId(QuickPayValidationActivity.this));

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
            payload.put("NotifySpanish", false);
            payload.put("NotifyEnglish", false);

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
            if (selectedOrderNumber != null && selectedOrderNumber.length() > 0) {
                payload.put("OrderNumber", selectedOrderNumber);
            } else {
                payload.put("OrderNumber", "");
            }

            double finalAmount = Double.parseDouble(enteredTotalAmount.replace(",", ""));
            payload.put("Amount", finalAmount);

            payload.put("MerchantId", selectedLocationID);
            payload.put("ClientName", clientName);
            payload.put("ClientEmail", customerEmail);

            Log.d("DATA", "Request Data ==>  " + payload.toString());
            json.put("payload", RSAHelper.encryptAES(payload.toString(), Base64.decode(tcpKey, 0), Base64.decode(vcr, 0)));


        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.CREATE_PAYMENT_LINK, json);
    }

    private void setData() {

        if (!TextUtils.isEmpty(selectedLocation) && !TextUtils.isEmpty(selectedTrnType)
                && !TextUtils.isEmpty(enteredTotalAmount)) {

            tvValidLocation.setText(selectedLocation.toLowerCase());
            tvValidateCommerce.setText(previousParentLocationName.toLowerCase());
            String amountText;
            if (selectedCurrency.equalsIgnoreCase("USD")) {
                amountText = Constants.CURRENCY_FORMAT_USD + enteredTotalAmount;
            } else {
                amountText = Constants.CURRENCY_FORMAT + enteredTotalAmount;
            }
            tvValidAmount.setText(amountText);

            if (selectedOrderNumber == null || selectedOrderNumber.length() <= 0) {
                tvValidOrderNo.setText("N/A");
            } else {
                tvValidOrderNo.setText(selectedOrderNumber);
            }
            if (taxStatus.equalsIgnoreCase(Constants.BOOLEAN_FALSE)) {
                tvTaxInclude.setVisibility(View.GONE);
                layoutITBIS.setVisibility(View.GONE);
            } else if (enteredTaxAmount == null || enteredTaxAmount.length() <= 0 || enteredTaxAmount.equalsIgnoreCase("null")) {
                tvTaxInclude.setVisibility(View.VISIBLE);
                layoutITBIS.setVisibility(View.VISIBLE);
                setRemainingTax();


            } else {
                tvTaxInclude.setVisibility(View.VISIBLE);
                layoutITBIS.setVisibility(View.VISIBLE);
                setTax();
            }
            customerValidation();

            tvValidTrnType.setText(selectedTrnType);
        }

    }

    private void setTax() {
        String taxAmount;
        if (selectedCurrency.equalsIgnoreCase("USD")) {
            taxAmount = Constants.CURRENCY_FORMAT_USD + enteredTaxAmount;
        } else {
            taxAmount = Constants.CURRENCY_FORMAT + enteredTaxAmount;
        }
        tvValidTaxAmount.setText(taxAmount);
    }

    private void setRemainingTax() {
        String validTaxAmount;
        if (selectedCurrency.equalsIgnoreCase("USD")) {
            validTaxAmount = Constants.CURRENCY_FORMAT_USD + getResources().getString(R.string.zero_amount);
        } else {
            validTaxAmount = Constants.CURRENCY_FORMAT + getResources().getString(R.string.zero_amount);
        }
        tvValidTaxAmount.setText(validTaxAmount);
    }

    private void customerValidation() {
        if (clientName == null || clientName.length() <= 0) {
            tvCustomerName.setText("N/A");
        } else {
            tvCustomerName.setText(clientName);
        }
        if (emailAddress == null || emailAddress.length() <= 0) {
            tvEmail.setText("N/A");
        } else {
            tvEmail.setText(emailAddress);
        }
        Log.d("QuickPayValidationAct", "customerValidation: " + selectedCurrency);
    }


    public void setContent(String name, String content, String merchantId, int dismissFlag, String taxExempt) {
        this.merchantId = merchantId;
        if (dismissFlag == 1) {
            locationBottomSheet.dismiss();
        }

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(content) && !TextUtils.isEmpty(taxExempt)) {
            Log.d("Data", "");
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
                parseDataObject(jsonTransactionOb);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseDataObject(JSONObject jsonTransactionOb) {
        try {
            if (jsonTransactionOb != null &&
                    !jsonTransactionOb.toString().equalsIgnoreCase("null") &&
                    !jsonTransactionOb.toString().equalsIgnoreCase("")) {

                responseLink = jsonTransactionOb.getString("LinkFullUrl");
                JSONObject jsonLinkInfoObject = jsonTransactionOb.getJSONObject("LinkInfo");

                displayAmount = jsonLinkInfoObject.getString("Amount");
                currency = jsonLinkInfoObject.getString("Currency");
                openNextScreen(responseLink);
            } else {
                cancelAlert(1, this);
            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void openNextScreen(String responseLink) {
        if (responseLink != null) {
            Intent intent = new Intent(QuickPayValidationActivity.this, QuickLinkShareActivity.class);
            ((AzulApplication) ((QuickPayValidationActivity) this).getApplication()).setLocationDataShare(locationJson);
            intent.putExtra("RESPONSE_LINK", responseLink);
            intent.putExtra("DISPLAY_AMOUNT", enteredTotalAmount);
            intent.putExtra("CURRENCY", selectedCurrency);
            intent.putExtra(Constants.SELECTED_LOCATION, selectedLocation);
            startActivity(intent);
            this.overridePendingTransition(R.anim.animation_enter,
                    R.anim.slide_nothing);

        }
    }
}