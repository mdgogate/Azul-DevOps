package com.sdp.appazul.activities.payment;

import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.ncorti.slidetoact.SlideToActView;
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

import org.json.JSONObject;

public class QuickPayValidationActivity extends BaseLoggedInActivity {
    TextView tvEmail;
    TextView tvCustomerName;
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
    String selectedTrnType;
    String selectedOrderNumber;
    String emailAddress;
    String custName;
    String enteredTaxAmount;
    String enteredTotalAmount;
    SlideToActView btnConfirmPayment;
    LocationFilterBottomSheet bottomsheet;
    LocationFilter locationFilter = new LocationFilter();
    String lastLocationName;
    String mID;
    String lastLocationMid;
    ApiManager apiManager = new ApiManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_pay_validation);
        getDataFromBackScreen();

        initControls();
    }

    private void getDataFromBackScreen() {
        Intent intent = getIntent();
        selectedLocationID = intent.getStringExtra("SLECTED_LOCATION_ID");
        selectedLocation = intent.getStringExtra("SLECTED_LOCATION");
        selectedTrnType = intent.getStringExtra("SLECTED_TRN_TYPE");
        selectedOrderNumber = intent.getStringExtra("ORDER_NO");
        custName = intent.getStringExtra("CUST_NAME");
        emailAddress = intent.getStringExtra("EMAIL");
        enteredTaxAmount = intent.getStringExtra("ITBIS_TAX");
        enteredTotalAmount = intent.getStringExtra("TOTAL_AMOUNT");
        responseCode = intent.getStringExtra("CODE");
        modifiedAmount = intent.getDoubleExtra("AMOUNT", 0);

        locationJson = intent.getStringExtra(Constants.LOCATION_RESPONSE);
    }

    private void initControls() {
        tvEmail = findViewById(R.id.tvEmail);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        btnBackScreen = findViewById(R.id.btnBackScreen);

        locationFilter = ((AzulApplication) this.getApplication()).getLocationFilter();
        if (locationFilter != null) {
            lastLocationName = locationFilter.getLocationNameAndId();
            lastLocationMid = locationFilter.getmId();
            mID = lastLocationMid;

        }

        btnBackScreen.setOnClickListener(btnBackScreenView -> {
            Intent intent = new Intent(QuickPayValidationActivity.this, QuickPayConfirmActivity.class);
            intent.putExtra(Constants.LOCATION_RESPONSE, locationJson);

            intent.putExtra("CODE", responseCode);

            intent.putExtra("SLECTED_LOCATION_ID", selectedLocationID);
            intent.putExtra("SLECTED_LOCATION", selectedLocation);
            intent.putExtra("SLECTED_TRN_TYPE", selectedTrnType);
            intent.putExtra("ORDER_NO", selectedOrderNumber);
            intent.putExtra("CUST_NAME", custName);
            intent.putExtra("EMAIL", emailAddress);
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

        setData();
        setListeners();
    }

    private void setListeners() {
        btnConfirmPayment.setOnSlideCompleteListener(slideToActView -> {
            cancelPayment.setEnabled(false);
            cancelPayment.setTextColor(ContextCompat.getColor(QuickPayValidationActivity.this, R.color.light_gray));

            paymentLnkCreateResponse();
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
            String tcpKey = ((AzulApplication) QuickPayValidationActivity.this.getApplication()).getTcpKey();
            json.put("tcp", RSAHelper.ecryptRSA(QuickPayValidationActivity.this, tcpKey));
            JSONObject payload = new JSONObject();
            payload.put("device", DeviceUtils.getDeviceId(QuickPayValidationActivity.this));
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
            if (custName == null || custName.length() <= 0) {
                tvCustomerName.setText("N/A");
            } else {
                tvCustomerName.setText(custName);
            }
            if (emailAddress == null || emailAddress.length() <= 0) {
                tvEmail.setText("N/A");
            } else {
                tvEmail.setText(emailAddress);
            }
            if (enteredTaxAmount == null || enteredTaxAmount.length() <= 0 || enteredTaxAmount.equalsIgnoreCase("null")) {
                tvValidTaxAmount.setText(Constants.CURRANCY_FORMAT + "0.00");
            } else {
                tvValidTaxAmount.setText(Constants.CURRANCY_FORMAT + enteredTaxAmount);
            }
            tvValidTrnType.setText(selectedTrnType);
        }

    }


    public void setContent(String name, String content, String merchantId, int dimissFlag, String taxExempt) {
        mID = merchantId;
        if (dimissFlag == 1) {
            bottomsheet.dismiss();
        }

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(content) && !TextUtils.isEmpty(taxExempt)){
            Log.d("Data","");
        }
    }

    String responseLink;

    public void paymentLnkCreateResponse() {
        Intent intent = new Intent(QuickPayValidationActivity.this, QuickLinkShareActivity.class);
        intent.putExtra(Constants.LOCATION_RESPONSE, locationJson);
        startActivity(intent);
    }
}