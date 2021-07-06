package com.sdp.appazul.activities.payment;

import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdp.appazul.R;
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


public class PaymentDataValidateActivity extends BaseLoggedInActivity {
    TextView tvValidaComercio;
    TextView tvValidLocation;
    TextView tvValidTrnType;
    TextView tvValidOrderNo;
    TextView tvValidAmount;
    TextView tvValidTaxAmount;
    ImageView switchStatus;
    ImageView btnBackScreen;
    TextView cancelPayment;
    String locationJson;
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
    LocationFilterBottomSheet bottomsheet;
    LocationFilter locationFilter = new LocationFilter();
    String lastLocationName;
    String mID;
    String lastLocationMid;
    ApiManager apiManager = new ApiManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_data_validate);
        getDataFromBackScreen();

        initControls();
    }

    private void getDataFromBackScreen() {
        Intent intent = getIntent();
        selectedParentLocationID = intent.getStringExtra("SLECTED_PARENT_LOCATION");
        selectedLocationID = intent.getStringExtra("SLECTED_LOCATION_ID");
        selectedLocation = intent.getStringExtra("SLECTED_LOCATION");
        selectedTrnType = intent.getStringExtra("SLECTED_TRN_TYPE");
        selectedOrderNumber = intent.getStringExtra("ORDER_NO");
        enteredTaxAmount = intent.getStringExtra("ITBIS_TAX");
        enteredTotalAmount = intent.getStringExtra("TOTAL_AMOUNT");
        responseCode = intent.getStringExtra("CODE");
        modifiedAmount = intent.getDoubleExtra("AMOUNT", 0);

        locationJson = intent.getStringExtra(Constants.LOCATION_RESPONSE);
    }

    private void initControls() {
        paymentConfirmLocFilter = findViewById(R.id.paymentConfirmLocFilter);
        tvSelectedLocation = findViewById(R.id.tvSelectedLocation);
        btnBackScreen = findViewById(R.id.btnBackScreen);

        locationFilter = ((AzulApplication) this.getApplication()).getLocationFilter();
        if (locationFilter != null) {
            lastLocationName = locationFilter.getLocationNameAndId();
            tvSelectedLocation.setText(lastLocationName);
            lastLocationMid = locationFilter.getmId();
            mID = lastLocationMid;

        }

        btnBackScreen.setOnClickListener(btnBackScreenView -> {
            Intent intent = new Intent(PaymentDataValidateActivity.this, PaymentConfirmaActivity.class);
            intent.putExtra(Constants.LOCATION_RESPONSE, locationJson);

            intent.putExtra("CODE", responseCode);

            intent.putExtra("SLECTED_PARENT_LOCATION", selectedParentLocationID);
            intent.putExtra("SLECTED_LOCATION_ID", selectedLocationID);
            intent.putExtra("SLECTED_LOCATION", selectedLocation);
            intent.putExtra("SLECTED_TRN_TYPE", selectedTrnType);
            intent.putExtra("ORDER_NO", selectedOrderNumber);
            intent.putExtra("ITBIS_TAX", enteredTaxAmount);

            startActivity(intent);
        });

        tvValidaComercio = findViewById(R.id.tvValidaComercio);
        tvValidLocation = findViewById(R.id.tvValidLocation);
        tvValidTrnType = findViewById(R.id.tvValidTrnType);
        tvValidOrderNo = findViewById(R.id.tvValidOrderNo);
        tvValidAmount = findViewById(R.id.tvValidAmount);
        tvValidTaxAmount = findViewById(R.id.tvValidTaxAmount);
        switchStatus = findViewById(R.id.switchStatus);
        cancelPayment = findViewById(R.id.cancelPayment);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);

        paymentConfirmLocFilter.setOnClickListener(paymentLocationSelectorView -> {
            bottomsheet = new LocationFilterBottomSheet(locationJson, Constants.PAYMENT_VALIDATE);
            bottomsheet.show(getSupportFragmentManager(), "bottomSheet");

        });
        setData();
        setListeners();
    }

    private void setListeners() {
        btnConfirmPayment.setOnSlideCompleteListener(slideToActView -> {
            cancelPayment.setEnabled(false);
            cancelPayment.setTextColor(ContextCompat.getColor(PaymentDataValidateActivity.this, R.color.light_gray));

            callLinkCreateApi(responseCode, enteredTotalAmount, enteredTaxAmount, selectedOrderNumber, selectedTrnType, selectedLocationID);

        });
    }

    private void callLinkCreateApi(String code,
                                   String enteredTotalAmount,
                                   String enteredTaxAmount,
                                   String selectedOrderNumber,
                                   String selectedTrnType,
                                   String selectedLocationID) {

        JSONObject json = new JSONObject();
        JSONObject decJson = new JSONObject();
        try {
            String tcpKey = ((AzulApplication) PaymentDataValidateActivity.this.getApplication()).getTcpKey();
            json.put("tcp", RSAHelper.ecryptRSA(PaymentDataValidateActivity.this, tcpKey));
            JSONObject payload = new JSONObject();
            payload.put("device", DeviceUtils.getDeviceId(PaymentDataValidateActivity.this));
            JSONObject pagosrequest = new JSONObject();
            pagosrequest.put("Code", code);
            pagosrequest.put("TransactionType", selectedTrnType);
            pagosrequest.put("Itbis", enteredTaxAmount);

            if (selectedOrderNumber != null && selectedOrderNumber.length() > 0) {
                pagosrequest.put("OrderNumber", selectedOrderNumber);
            } else {
                pagosrequest.put("OrderNumber", "");
            }
            pagosrequest.put("Amount", enteredTotalAmount);
            pagosrequest.put("MerchantId", selectedLocationID);

            json.put("payload", RSAHelper.encryptAES(payload.toString(), Base64.decode(tcpKey, 0)));


            Log.d("DATA", "Request Data ==>  " + decJson.toString());

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.CREATE_PAYMENT_LINK, json);
    }

    private void setData() {

        if (!TextUtils.isEmpty(selectedLocation) && !TextUtils.isEmpty(selectedTrnType)
                && !TextUtils.isEmpty(enteredTotalAmount)) {
            tvValidLocation.setText(selectedLocation);
            tvValidAmount.setText(Constants.CURRANCY_FORMAT + enteredTotalAmount);
            if (selectedOrderNumber == null || selectedOrderNumber.length() <= 0) {
                tvValidOrderNo.setText("N/A");
            } else {
                tvValidOrderNo.setText(selectedOrderNumber);
            }
            if (enteredTaxAmount == null || enteredTaxAmount.length() <= 0 || enteredTaxAmount.equalsIgnoreCase("null")) {
                tvValidTaxAmount.setText(Constants.CURRANCY_FORMAT + "0.00");
            } else {
                tvValidTaxAmount.setText(Constants.CURRANCY_FORMAT + enteredTaxAmount);
            }
            tvValidTrnType.setText(selectedTrnType);
            if (selectedParentLocationID != null) {
                tvValidaComercio.setText("" + selectedParentLocationID);
            } else {
                tvValidaComercio.setText("Importadora Wu SRL");
            }
        }

    }


    public void setContent(String name, String content, String merchantId, int dimissFlag, String taxExempt) {
        tvSelectedLocation.setText(content);
        mID = merchantId;
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(taxExempt)) {
            Log.d("Data", "" + name + taxExempt);
        }
        if (dimissFlag == 1) {
            bottomsheet.dismiss();
        }
    }

    String responseLink;

    public void paymentLnkCreateResponse(String responseData) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(responseData);
            JSONObject jsonTransactionOb = jsonObject.getJSONObject("data");
            responseLink = jsonTransactionOb.getString("LinkFullUrl");

            Log.d("DATA", "Link Data ==>  " + responseLink);

            if (responseLink != null) {
                Intent intent = new Intent(PaymentDataValidateActivity.this, LinkSharingActivity.class);
                intent.putExtra(Constants.LOCATION_RESPONSE, locationJson);
                intent.putExtra("RESPONSE_LINK", responseLink);
                startActivity(intent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}