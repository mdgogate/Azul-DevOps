package com.sdp.appazul.activities.transactions;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sdp.appazul.R;
import com.sdp.appazul.activities.dashboard.DashBoardActivity;
import com.sdp.appazul.activities.home.BaseLoggedInActivity;
import com.sdp.appazul.api.ApiManager;
import com.sdp.appazul.api.ServiceUrls;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.KeyConstants;
import com.sdp.appazul.security.RSAHelper;
import com.sdp.appazul.utils.DeviceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class PaymentLinkDetails extends BaseLoggedInActivity {

    String selectedLinkId;
    String paymentCode;
    ApiManager apiManager = new ApiManager(this);

    RelativeLayout paymentMainLayout;
    RelativeLayout itbisLayout;
    TextView tvLinkId;
    TextView tvTransactionsType;
    TextView tvStatus;
    TextView tvTrnResponse;
    TextView tvCreatedDateTIme;
    TextView tvCardNumber;
    TextView tvApprovalNo;
    TextView tvDateAndTime;
    TextView tvAmount;
    TextView tvIncludeTaxTitle;
    TextView tvItbisAmountValue;
    TextView tvOrderNoAndTypeValue;
    TextView tvComercioValue;
    TextView tvLocationNameValue;
    TextView tvUserValue;
    TextView tvDirectionIpValue;
    TextView tvCustomerNameValue;
    TextView tvEmailValue;
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
    RelativeLayout hiddenLayout;
    TextView tvHiddenAmount;
    TextView tvHiddenTax;
    ImageView btnBackToPrevious;
    SimpleDateFormat olderDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    SimpleDateFormat olderDateFormatWithMilliseconds = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    SimpleDateFormat newDateFormat = new SimpleDateFormat(Constants.DD_MM_YYYY);
    DecimalFormat currencyFormat = new DecimalFormat("#,##0.00", symbols);
    String locationJson;
    String paymentLocation;
    String taxExemptFlag;
    ImageView cardImgView;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_link_details);
        Intent intent = getIntent();

        context = this;
        selectedLinkId = intent.getStringExtra("LINK_ID");
        paymentCode = intent.getStringExtra("PAYMENT_CODE");
        taxExemptFlag = intent.getStringExtra("PAYMENT_TAX");
        initControls();
        locationJson = ((AzulApplication) context.getApplicationContext()).getLocationDataShare();
        paymentLocation = ((AzulApplication) context.getApplicationContext()).getLocationDataShare();

        if (!TextUtils.isEmpty(selectedLinkId) && !TextUtils.isEmpty(paymentCode)) {
            getPaymentDetailsFromApi(selectedLinkId, paymentCode);
        } else {
            Log.d("PayDetails", "onCreate: ");
        }
    }

    private void initControls() {
        btnBackToPrevious = findViewById(R.id.btnBackToPrevious);
        paymentMainLayout = findViewById(R.id.paymentMainLayout);
        itbisLayout = findViewById(R.id.itbisLayout);
        tvLinkId = findViewById(R.id.tvLinkId);
        tvTransactionsType = findViewById(R.id.tvTransactionsType);
        tvStatus = findViewById(R.id.tvStatus);
        tvTrnResponse = findViewById(R.id.tvTrnResponse);
        tvCreatedDateTIme = findViewById(R.id.tvCreatedDateTIme);
        tvCardNumber = findViewById(R.id.tvCardNumber);
        tvApprovalNo = findViewById(R.id.tvApprovalNo);
        tvDateAndTime = findViewById(R.id.tvDateAndTime);
        tvAmount = findViewById(R.id.tvAmount);
        tvIncludeTaxTitle = findViewById(R.id.tvIncludeTaxTitle);
        tvItbisAmountValue = findViewById(R.id.tvItbisAmountValue);
        tvOrderNoAndTypeValue = findViewById(R.id.tvOrderNoAndTypeValue);
        tvComercioValue = findViewById(R.id.tvComercioValue);
        tvLocationNameValue = findViewById(R.id.tvLocationNameValue);
        tvUserValue = findViewById(R.id.tvUserValue);
        tvDirectionIpValue = findViewById(R.id.tvDirectionIpValue);
        tvCustomerNameValue = findViewById(R.id.tvCustomerNameValue);
        tvEmailValue = findViewById(R.id.tvEmailValue);

        hiddenLayout = findViewById(R.id.hiddenLayout);
        tvHiddenAmount = findViewById(R.id.tvHiddenAmount);
        tvHiddenTax = findViewById(R.id.tvHiddenTax);
        cardImgView = findViewById(R.id.cardImgView);
        btnBackToPrevious.setOnClickListener(btnBackToPreviousView -> {
            Intent intent1 = new Intent(PaymentLinkDetails.this, PaymentLinkTransactions.class);
            ((AzulApplication) ((PaymentLinkDetails) this).getApplication()).setLocationDataShare(locationJson);
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent1);
            this.overridePendingTransition(R.anim.animation_leave,
                    R.anim.slide_nothing);
        });
    }

    private void getPaymentDetailsFromApi(String LinkId, String paymentCode) {
        JSONObject json = new JSONObject();
        try {
            String tcpKey = ((AzulApplication) PaymentLinkDetails.this.getApplication()).getTcpKey();
            String vcr = ((AzulApplication) PaymentLinkDetails.this.getApplication()).getVcr();
            json.put("tcp", RSAHelper.ecryptRSA(PaymentLinkDetails.this, tcpKey));
            JSONObject payload = new JSONObject();
            payload.put("device", DeviceUtils.getDeviceId(PaymentLinkDetails.this));
            payload.put("Code", paymentCode);
            payload.put("LinkId", LinkId);

            json.put("payload", RSAHelper.encryptAES(payload.toString(), Base64.decode(tcpKey, 0), Base64.decode(vcr, 0)));
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.PAYMENT_LINK_INFO, json);
    }

    public void getPaymentLinkInfoResponse(String responseData) {

        Log.d("TAG", "getPaymentLinkInfoResponse: " + responseData);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(responseData);

            JSONObject jsonTransactionOb = jsonObject.getJSONObject("data");

            parseDetails(jsonTransactionOb);
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }

    }

    private void parseDetails(JSONObject responseJsonObject) {
        JSONObject jsonObject;
        try {

            jsonObject = responseJsonObject.getJSONObject("LinkInfo");
            setPaymentData(jsonObject);
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void setPaymentData(JSONObject jsonObject) {

        try {
            String linkId = jsonObject.getString("LinkId");
            String transactionType = jsonObject.getString("TransactionStatus");
            String status = jsonObject.getString("Status");
            String createdOn = jsonObject.getString("AddDate");
//            String cardType = jsonObject.getString("");                                          // Missing parameter
            String cardNumber = jsonObject.getString("PayedCardNumber");
            String transactionResponse = jsonObject.getString("TransactionResponse");
            String authorizationNumber = jsonObject.getString("PayedAuthorizationNumber");
            String paymentDate = jsonObject.getString("PayedDate");
            String amount = jsonObject.getString("Amount");
            String taxAmount = jsonObject.getString("Itbis");                                 // Need to Verify
            String orderNumber = jsonObject.getString("OrderNumber");
            String locationName = jsonObject.getString("MerchantName");                       // Need to Verify
            String locationId = jsonObject.getString("MerchantId");
            // Missing merchant name
            String createdBy = jsonObject.getString("AddUser");
            String createdIpAddress = jsonObject.getString("AddIpAddress");
            String clientName = jsonObject.getString("ClientName");
            String clientEmail = jsonObject.getString("ClientEmail");
            String amountCurrency = jsonObject.getString("Currency");

            tvLinkId.setText(linkId);

            checkTransactionType(transactionType, cardNumber);

            setStatusAndResponse(status, transactionResponse, authorizationNumber);
            setCreatedOnDate(createdOn);

            setPaymentDate(paymentDate);
            if (transactionResponse.equalsIgnoreCase(Constants.STATUS_APPROVED)
                    || transactionResponse.equalsIgnoreCase(Constants.STATUS_APPROVED_SPANISH)) {
                paymentMainLayout.setVisibility(View.VISIBLE);
                hiddenLayout.setVisibility(View.GONE);
                double paymentAmount = Double.parseDouble(amount);
                if (amountCurrency.equalsIgnoreCase("USD")) {
                    tvAmount.setText(Constants.CURRENCY_FORMAT_USD + currencyFormat.format(paymentAmount));
                } else {
                    tvAmount.setText(Constants.CURRENCY_FORMAT + currencyFormat.format(paymentAmount));
                }

                taxChecker(taxExemptFlag);
            } else {
                paymentMainLayout.setVisibility(View.GONE);
                hiddenLayout.setVisibility(View.VISIBLE);
                double paymentAmount = Double.parseDouble(amount);
                if (amountCurrency.equalsIgnoreCase("USD")) {
                    tvHiddenAmount.setText(Constants.CURRENCY_FORMAT_USD + currencyFormat.format(paymentAmount));
                } else {
                    tvHiddenAmount.setText(Constants.CURRENCY_FORMAT + currencyFormat.format(paymentAmount));
                }
                if (taxExemptFlag.equalsIgnoreCase("true")) {
                    tvHiddenTax.setVisibility(View.VISIBLE);
                    tvIncludeTaxTitle.setVisibility(View.VISIBLE);
                } else {
                    tvIncludeTaxTitle.setVisibility(View.GONE);
                    tvHiddenTax.setVisibility(View.GONE);
                }

            }

            setTaxData(amountCurrency, taxAmount, taxExemptFlag);

            setOrderNo(orderNumber);

            tvLocationNameValue.setText(locationName);
            tvUserValue.setText(createdBy);
            setAddressAndName(createdIpAddress, clientName);
            setEmail(clientEmail);
            setParentName(locationName, locationId);
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }

    }

    private void taxChecker(String taxExempt) {
        if (taxExempt.equalsIgnoreCase("true")) {
            tvIncludeTaxTitle.setVisibility(View.VISIBLE);
        } else {
            tvIncludeTaxTitle.setVisibility(View.GONE);
        }
    }

    private void setParentName(String locationName, String locationId) {
        if (paymentLocation != null && !TextUtils.isEmpty(paymentLocation)) {
            Log.d("PaymentLinkDetails", locationName + "  == setParentName: == " + locationId);
            String parentLocationName = getGroupName(paymentLocation, locationName, locationId);
            if (parentLocationName != null && !TextUtils.isEmpty(parentLocationName)) {
                tvComercioValue.setText(parentLocationName);
            } else {
                tvComercioValue.setText("");
            }
        }
    }

    private void setAddressAndName(String createdIpAddress, String clientName) {
        if (createdIpAddress.equalsIgnoreCase("")) {
            tvDirectionIpValue.setText("N/A");
        } else {
            tvDirectionIpValue.setText(createdIpAddress);
        }
        if (clientName.equalsIgnoreCase("")) {
            tvCustomerNameValue.setText("N/A");
        } else {
            tvCustomerNameValue.setText(clientName);
        }
    }

    private void setEmail(String clientEmail) {
        if (clientEmail.equalsIgnoreCase("")) {
            tvEmailValue.setText("N/A");
        } else {
            tvEmailValue.setText(clientEmail);
        }
    }

    private void setOrderNo(String orderNumber) {

        if (orderNumber != null && orderNumber.equalsIgnoreCase("")) {
            tvOrderNoAndTypeValue.setText("N/A");
        } else {
            tvOrderNoAndTypeValue.setText(orderNumber);
        }
    }

    private void checkTransactionType(String transactionType, String cardNumber) {
        if (transactionType.equalsIgnoreCase("Sale")) {
            tvTransactionsType.setText("Venta");
        } else if (transactionType.equalsIgnoreCase("HoldOnly")) {
            tvTransactionsType.setText("Sólo autorización");
        } else if (transactionType.equalsIgnoreCase("HoldPosted")) {
            tvTransactionsType.setText("Autorización completada");
        } else {
            tvTransactionsType.setText(transactionType);
        }

        if (cardNumber != null && cardNumber.length() > 0) {
            checkCardBrand(cardNumber);
        }
        if (cardNumber != null && cardNumber.equalsIgnoreCase("")) {
            tvCardNumber.setText("-");
        } else {
            tvCardNumber.setText(cardNumber);
        }
    }

    private void checkCardBrand(String cardNumber) {
        String cardBrand = GetCreditCardType(cardNumber);
        if (cardBrand.equalsIgnoreCase("American Express")) {
            cardImgView.setImageResource(R.drawable.ic_marca_tarjeta___amex);
        } else if (cardBrand.equalsIgnoreCase("Visa")) {
            cardImgView.setImageResource(R.drawable.ic_marca_visa);
        } else if (cardBrand.equalsIgnoreCase("Discover")) {
            cardImgView.setImageResource(R.drawable.ic_marca_discover);
        } else if (cardBrand.equalsIgnoreCase("Master Card")) {
            cardImgView.setImageResource(R.drawable.ic_marca_mastercard);
        } else if (cardBrand.equalsIgnoreCase("Diners")) {
            cardImgView.setImageResource(R.drawable.ic_logo_card____diners);
        } else if (cardBrand.equalsIgnoreCase("JCB")) {
            cardImgView.setImageResource(R.drawable.ic_marca_jcb);
        } else {
            cardImgView.setImageResource(R.drawable.ic_marca_visa);
        }
    }

    private void setTaxData(String amountCurrency, String taxAmount, String taxExempt) {
        if (taxExempt.equalsIgnoreCase("true")) {
            itbisLayout.setVisibility(View.VISIBLE);
        } else {
            itbisLayout.setVisibility(View.GONE);
            if (taxAmount.equalsIgnoreCase("0.0")) {
                if (amountCurrency.equalsIgnoreCase("USD")) {
                    tvItbisAmountValue.setText(Constants.CURRENCY_USD);
                } else {
                    tvItbisAmountValue.setText(Constants.CURRENCY);
                }
            } else {
                double payedTax = Double.parseDouble(taxAmount);

                if (amountCurrency.equalsIgnoreCase("USD")) {
                    tvItbisAmountValue.setText(Constants.CURRENCY_FORMAT_USD + currencyFormat.format(payedTax));
                } else {
                    tvItbisAmountValue.setText(Constants.CURRENCY_FORMAT + currencyFormat.format(payedTax));
                }
            }
        }
    }

    private void setStatusAndResponse(String status, String transactionResponse, String authorizationNumber) {

        if (status.equalsIgnoreCase(Constants.STATUS_PROCEED) &&
                transactionResponse.equalsIgnoreCase(Constants.STATUS_APPROVED)) {
            tvTrnResponse.setText(Constants.STATUS_APPROVED_SPANISH);
            tvApprovalNo.setText(Constants.STATUS_APPROVED_SPANISH + " " + authorizationNumber);
            tvApprovalNo.setTextColor(Color.parseColor(Constants.LIGHT_SKY));
            tvTrnResponse.setTextColor(Color.parseColor(Constants.LIGHT_SKY));
            tvStatus.setTextColor(Color.parseColor(Constants.COLOR_SKY));
            tvStatus.setText("Procesado" + " - ");

        } else if (status.equalsIgnoreCase(Constants.STATUS_PROCEED)
                && transactionResponse.equalsIgnoreCase(Constants.STATUS_DECLINED)
                || transactionResponse.equalsIgnoreCase(Constants.STATUS_DECLINED_SPANISH)) {  //Declinada
            tvStatus.setTextColor(Color.parseColor(Constants.COLOR_SKY));
            tvStatus.setText("Procesado" + " - ");
            tvTrnResponse.setText(Constants.STATUS_DECLINED_SPANISH);
            tvTrnResponse.setTextColor(Color.parseColor(Constants.ORANGE));
            tvApprovalNo.setText(Constants.STATUS_DECLINED_SPANISH);
            tvApprovalNo.setTextColor(Color.parseColor(Constants.ORANGE));

        } else if (status.equalsIgnoreCase(Constants.STATUS_CANCELLED_SPANISH)
                || status.equalsIgnoreCase("Cancelled")
                && transactionResponse.equalsIgnoreCase(Constants.STATUS_DECLINED)
                || transactionResponse.equalsIgnoreCase(Constants.STATUS_DECLINED_SPANISH)) {  //Declinada
            tvStatus.setTextColor(Color.parseColor("#FF3A3A"));
            tvStatus.setText(Constants.STATUS_CANCELLED_SPANISH + " - ");
            tvTrnResponse.setText(Constants.STATUS_DECLINED_SPANISH);
            tvTrnResponse.setTextColor(Color.parseColor(Constants.ORANGE));
            tvApprovalNo.setText(Constants.STATUS_DECLINED_SPANISH);
            tvApprovalNo.setTextColor(Color.parseColor(Constants.ORANGE));

        } else if (status.equalsIgnoreCase("Voided")
                && transactionResponse.equalsIgnoreCase(Constants.STATUS_DECLINED)
                || transactionResponse.equalsIgnoreCase(Constants.STATUS_DECLINED_SPANISH)) {
            tvStatus.setTextColor(Color.parseColor(Constants.GRAY_BLUE));
            tvStatus.setText("Anulada" + " - ");
            tvTrnResponse.setText(Constants.STATUS_DECLINED_SPANISH);
            tvTrnResponse.setTextColor(Color.parseColor(Constants.ORANGE));
            tvApprovalNo.setText(Constants.STATUS_DECLINED_SPANISH);
            tvApprovalNo.setTextColor(Color.parseColor(Constants.ORANGE));

        } else if (status.equalsIgnoreCase("Voided") &&
                transactionResponse.equalsIgnoreCase(Constants.STATUS_APPROVED)
                || transactionResponse.equalsIgnoreCase(Constants.STATUS_APPROVED_SPANISH)) {
            tvTrnResponse.setText(Constants.STATUS_APPROVED_SPANISH);
            tvTrnResponse.setTextColor(Color.parseColor(Constants.LIGHT_SKY));
            tvStatus.setText("Anulada" + " - ");
            tvStatus.setTextColor(Color.parseColor(Constants.GRAY_BLUE));
            tvApprovalNo.setText(Constants.STATUS_APPROVED_SPANISH + " " + authorizationNumber);
            tvApprovalNo.setTextColor(Color.parseColor(Constants.LIGHT_SKY));
        }
        checkRemainingStatusData(status, transactionResponse);
    }

    private void checkRemainingStatusData(String status, String transactionResponse) {
        if (status.equalsIgnoreCase("Cancelled")
                && transactionResponse.equalsIgnoreCase("None")) {
            tvApprovalNo.setVisibility(View.GONE);
            tvTrnResponse.setVisibility(View.GONE);
            tvStatus.setTextColor(Color.parseColor("#FF3A3A"));
            tvStatus.setText(Constants.STATUS_CANCELLED_SPANISH);
        } else if (status.equalsIgnoreCase("Expired")
                || status.equalsIgnoreCase("Expirado")) {
            tvApprovalNo.setVisibility(View.GONE);
            tvTrnResponse.setVisibility(View.GONE);
            tvStatus.setText("Expirado");
            tvStatus.setTextColor(Color.parseColor(Constants.GRAY_BLUE));
        } else if (status.equalsIgnoreCase("Generated")
                || status.equalsIgnoreCase("Generado")) { //Generated == Generado
            tvApprovalNo.setVisibility(View.GONE);
            tvTrnResponse.setVisibility(View.GONE);
            tvStatus.setText("Generado");
            tvStatus.setTextColor(Color.parseColor(Constants.COLOR_SKY));
        } else if (status.equalsIgnoreCase("Open")
                && transactionResponse.equalsIgnoreCase("None")) {
            tvApprovalNo.setVisibility(View.GONE);
            tvTrnResponse.setVisibility(View.GONE);
            tvStatus.setText("Abierto");
            tvStatus.setTextColor(Color.parseColor(Constants.COLOR_SKY));
        }
    }

    private void setPaymentDate(String paymentDate) {
        Log.d("TAG", "setPaymentDate: " + paymentDate.length());
        try {
            Date givenDate;
            if (paymentDate.length() > 19) {
                givenDate = olderDateFormatWithMilliseconds.parse(paymentDate);
            } else {
                givenDate = olderDateFormat.parse(paymentDate);
            }
            String paymentDoneDate = newDateFormat.format(givenDate);
            String trnTime = paymentDate.substring(11, 17);   //0001-01-01T00:00:00
            String finalTimeString = timeFormatConvertor(trnTime);
            tvDateAndTime.setText(paymentDoneDate + " " + finalTimeString);
        } catch (ParseException e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void setCreatedOnDate(String createdOn) {
        Log.d("TAG", "setCreatedOnDate: " + createdOn.length());
        try {
            Date givenDate;
            if (createdOn.length() > 19) {
                givenDate = olderDateFormatWithMilliseconds.parse(createdOn);
            } else {
                givenDate = olderDateFormat.parse(createdOn);
            }
            String paymentDate = newDateFormat.format(givenDate);
            String trnTime = createdOn.substring(11, 17);
            String finalTimeString = timeFormatConvertor(trnTime);
            tvCreatedDateTIme.setText("Creado: " + paymentDate + " " + finalTimeString);
        } catch (ParseException e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private String timeFormatConvertor(String inputTime) {
        try {
            DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
            symbols.setAmPmStrings(new String[]{"a.m.", "p.m."});
            SimpleDateFormat sdfFormat24 = new SimpleDateFormat("HH:mm");
            SimpleDateFormat sdfFormat12 = new SimpleDateFormat("hh:mm a");
            sdfFormat12.setDateFormatSymbols(symbols);
            Date datefor24hr = sdfFormat24.parse(inputTime);
            return sdfFormat12.format(datefor24hr);
        } catch (final ParseException e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        return null;
    }

    public String GetCreditCardType(String CreditCardNumber) {

        if (CreditCardNumber.length() == 15 &&
                CreditCardNumber.substring(0, 2).matches("34") ||
                CreditCardNumber.substring(0, 2).matches("37")) {
            return "American Express";
        } else if (CreditCardNumber.length() == 13 &&
                CreditCardNumber.substring(0, 1).matches("4")) {
            return "Visa";
        } else if (CreditCardNumber.length() == 16 &&
                CreditCardNumber.substring(0, 1).matches("6011")) {
            return "Discover";
        } else if (Integer.parseInt(CreditCardNumber.substring(0, 2)) >= 51
                && Integer.parseInt(CreditCardNumber.substring(0, 2)) <= 55) {
            return "Master Card";
        } else {
            return callOtherLoops(CreditCardNumber);
        }
    }

    private String callOtherLoops(String creditCardNumber) {
        if (creditCardNumber.length() == 14 &&
                creditCardNumber.substring(0, 2).matches("30") ||
                creditCardNumber.substring(0, 2).matches("36") ||
                creditCardNumber.substring(0, 2).matches("38")) {
            return "Diners";
        } else if (creditCardNumber.length() == 16 &&
                creditCardNumber.substring(0, 2).matches("35")) {
            return "JCB";
        } else if (creditCardNumber.length() == 15 &&
                creditCardNumber.substring(0, 4).matches("2131") ||
                creditCardNumber.substring(0, 4).matches("1800")) {
            return "JCB";
        } else {
            return "invalid";
        }
    }

    private String getGroupName(String locationResponse, String locationName, String locationId) {

        try {
            JSONObject jsonResponse = new JSONObject(locationResponse);
            JSONArray parentLevelLocations = jsonResponse.getJSONArray("CallCenters");
            for (int i = 0; i < parentLevelLocations.length(); i++) {

                JSONObject parentData = parentLevelLocations.getJSONObject(i);

                String parentLocationName = parentData.getString("Name");
                JSONArray assignedLocationsObject = parentData.getJSONArray("Merchants");
                for (int j = 0; j < assignedLocationsObject.length(); j++) {
                    JSONObject secondPositionLocation = assignedLocationsObject.getJSONObject(j);
                    String secondPositionLocationId = secondPositionLocation.getString(Constants.MERCHANT_ID);
                    String secondPositionLocationName = secondPositionLocation.getString("Name");
                    return verifyData(secondPositionLocationId, secondPositionLocationName, locationName, locationId, parentLocationName);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    private String verifyData(String secondPositionLocationId, String secondPositionLocationName, String locationName, String locationId, String parentLocationName) {

        if (secondPositionLocationName.equalsIgnoreCase(locationName)
                && secondPositionLocationId.equalsIgnoreCase(locationId)) {
            return parentLocationName;
        }
        return "";
    }
}