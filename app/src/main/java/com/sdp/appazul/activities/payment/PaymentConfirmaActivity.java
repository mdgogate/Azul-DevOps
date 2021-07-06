package com.sdp.appazul.activities.payment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.sdp.appazul.R;
import com.sdp.appazul.activities.dashboard.LocationFilterBottomSheet;
import com.sdp.appazul.activities.home.BaseLoggedInActivity;
import com.sdp.appazul.api.ApiManager;
import com.sdp.appazul.api.ServiceUrls;
import com.sdp.appazul.classes.LocationFilter;
import com.sdp.appazul.classes.PaymentDetails;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.KeyConstants;
import com.sdp.appazul.security.RSAHelper;
import com.sdp.appazul.utils.DeviceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;


public class PaymentConfirmaActivity extends BaseLoggedInActivity {
    String locationJson;
    EditText etOrderNo;
    EditText etITBISAmount;
    EditText etTotalAmount;
    TextView cancelPayment;
    TextView tvTrnType;
    TextView tvLocalidadName;
    TextView searchinActive;
    TextView searchinAmount;
    TextView tvSelectedLocation;
    TextView tvComercio;
    TextView tvITBISError;
    TextView tvAmountError;
    TextView tvITBISInclude;
    Button btnConfirmPayment;
    ImageView switchITBIS;
    ImageView btnBackScreen;
    RelativeLayout layoutSearchByEdittext;
    RelativeLayout layoutAmountAllComp;
    String previousCleanString;
    String totalAmountEntered;
    double lastAmount = 0;
    ImageView paymentConfirmLocFilter;
    LocationFilterBottomSheet bottomsheet;
    LocationFilter locationFilter = new LocationFilter();
    String lastLocationName;
    String lastLocationMid;
    String mID;
    String taxStatus;
    RelativeLayout layoutITBIS;
    Context context;
    TextView tvAmountTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirma);
        context = this;
        Intent intent = getIntent();
        locationJson = intent.getStringExtra(Constants.LOCATION_RESPONSE);
        totalAmountEntered = intent.getStringExtra(Constants.TOTAL_AMOUNT);

        locationFilter = ((AzulApplication) this.getApplication()).getLocationFilter();
        initControls();

        if (totalAmountEntered != null) {
            lastAmount = Double.parseDouble(new DecimalFormat("#").format(Double.parseDouble(totalAmountEntered)));
        } else {

            PaymentDetails paymentDetails = ((AzulApplication) this.getApplication()).getDetails();
            if (paymentDetails != null) {
                tvLocalidadName.setText(paymentDetails.getSelectedLocation());
                tvTrnType.setText(paymentDetails.getSelectedTrnType());
                if (paymentDetails.getSelectedOrderNumber() != null && paymentDetails.getSelectedOrderNumber().length() > 0) {
                    etOrderNo.setText(paymentDetails.getSelectedOrderNumber());
                } else {
                    etOrderNo.setText("");
                }
            }
            lastAmount = paymentDetails.getEnteredTotalAmount();
        }

        getMerchantApiCall();

        if (lastAmount != 0) {
            double totalEnteredAmount = lastAmount / 100;
            etTotalAmount.setText("" + currFormat.format(totalEnteredAmount));
            searchinAmount.setTextColor(ContextCompat.getColor(PaymentConfirmaActivity.this, R.color.font_hint));
            etTotalAmount.setTextColor(ContextCompat.getColor(PaymentConfirmaActivity.this, R.color.font_hint));
        }
    }

    private double calculateTax(double totalAmountEntered) {
        double subTotal =  (totalAmountEntered / 1.18) ;
        return (subTotal / 100.0f) * 18;
    }

    private void initControls() {
        btnBackScreen = findViewById(R.id.btnBackScreen);
        tvAmountTitle = findViewById(R.id.tvAmountTitle);
        paymentConfirmLocFilter = findViewById(R.id.paymentConfirmLocFilter);
        layoutSearchByEdittext = findViewById(R.id.layoutSearchByEdittext);
        layoutAmountAllComp = findViewById(R.id.layoutAmountAllComp);
        layoutITBIS = findViewById(R.id.layoutITBIS);
        tvLocalidadName = findViewById(R.id.tvLocalidadName);
        tvITBISError = findViewById(R.id.tvITBISError);
        tvAmountError = findViewById(R.id.tvAmountError);
        etOrderNo = findViewById(R.id.etOrderNo);
        searchinActive = findViewById(R.id.searchinActive);
        searchinAmount = findViewById(R.id.searchinAmount);
        etITBISAmount = findViewById(R.id.etITBISAmount);
        etTotalAmount = findViewById(R.id.etTotalAmount);
        tvTrnType = findViewById(R.id.tvTrnType);
        tvSelectedLocation = findViewById(R.id.tvSelectedLocation);
        cancelPayment = findViewById(R.id.cancelPayment);
        tvComercio = findViewById(R.id.tvComercio);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
        switchITBIS = findViewById(R.id.switchITBIS);
        tvITBISInclude = findViewById(R.id.tvITBISInclude);

        setLocationData(locationFilter);
        btnBackScreen.setOnClickListener(btnBackScreenView -> {
            Intent intent = new Intent(PaymentConfirmaActivity.this, SetPaymentInfoActivity.class);
            intent.putExtra(Constants.LOCATION_RESPONSE, locationJson);
            intent.putExtra(Constants.TOTAL_AMOUNT, totalAmountEntered);
            startActivity(intent);
        });
        paymentConfirmLocFilter.setOnClickListener(paymentLocationSelectorView -> {
            bottomsheet = new LocationFilterBottomSheet(locationJson, "PAYMENT_CONFIRM");
            bottomsheet.show(getSupportFragmentManager(), "bottomSheet");
        });

        etITBISAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("", "");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                layoutSearchByEdittext.setBackgroundResource(R.drawable.active_border_background);
                layoutAmountAllComp.setBackgroundResource(R.drawable.border_background);
                etOrderNo.setBackgroundResource(R.drawable.border_background);
                etITBISAmount.setTextColor(ContextCompat.getColor(PaymentConfirmaActivity.this, R.color.font_hint));
                searchinActive.setTextColor(ContextCompat.getColor(PaymentConfirmaActivity.this, R.color.font_hint));
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (etITBISAmount.getText().toString().length() <= 0) {
                    layoutSearchByEdittext.setBackgroundResource(R.drawable.error_background_shadow);
                    tvITBISError.setText(context.getString(R.string.less_tax_error));
                    tvITBISError.setVisibility(View.VISIBLE);
                } else {
                    taxCalculations();
                }
            }
        });
        etTotalAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("", "");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                layoutAmountAllComp.setBackgroundResource(R.drawable.active_border_background);
                layoutSearchByEdittext.setBackgroundResource(R.drawable.border_background);
                etTotalAmount.setTextColor(ContextCompat.getColor(PaymentConfirmaActivity.this, R.color.font_hint));
                searchinAmount.setTextColor(ContextCompat.getColor(PaymentConfirmaActivity.this, R.color.font_hint));
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (etTotalAmount.getText().toString().length() <= 0) {
                    layoutAmountAllComp.setBackgroundResource(R.drawable.error_background_shadow);
                    tvAmountError.setText(context.getString(R.string.less_amount_error));
                    tvAmountError.setVisibility(View.VISIBLE);
                }
            }
        });

        etITBISAmount.setOnClickListener(view -> {
            layoutSearchByEdittext.setBackgroundResource(R.drawable.active_border_background);
            layoutAmountAllComp.setBackgroundResource(R.drawable.border_background);
            etOrderNo.setBackgroundResource(R.drawable.border_background);
            searchinActive.setTextColor(ContextCompat.getColor(PaymentConfirmaActivity.this, R.color.font_hint));
            tvITBISError.setVisibility(View.GONE);
        });
        etTotalAmount.setOnClickListener(view -> {
            layoutAmountAllComp.setBackgroundResource(R.drawable.active_border_background);
            layoutSearchByEdittext.setBackgroundResource(R.drawable.border_background);
            searchinAmount.setTextColor(ContextCompat.getColor(PaymentConfirmaActivity.this, R.color.font_hint));
            etOrderNo.setBackgroundResource(R.drawable.border_background);
            tvAmountError.setVisibility(View.GONE);
        });
        etOrderNo.setOnClickListener(view -> {
            etOrderNo.setBackgroundResource(R.drawable.active_border_background);
            layoutAmountAllComp.setBackgroundResource(R.drawable.border_background);
            layoutSearchByEdittext.setBackgroundResource(R.drawable.border_background);
        });


        switchListener();
        btnConfirmPayment.setOnClickListener(btnConfirmPaymentView -> callPaymentValidationScreen());

        cancelPayment.setOnClickListener(cancelPaymentView -> cancelAlert(context));
    }

    private void callPaymentValidationScreen() {
        Intent intent = new Intent(PaymentConfirmaActivity.this, PaymentDataValidateActivity.class);
        PaymentDetails details = new PaymentDetails();
        if (responseCode != null) {
            intent.putExtra("CODE", responseCode);
        }
        intent.putExtra("SLECTED_LOCATION_ID", mID);

        intent.putExtra("SLECTED_PARENT_LOCATION", tvComercio.getText().toString());
        intent.putExtra("SLECTED_LOCATION", tvLocalidadName.getText().toString());
        intent.putExtra("SLECTED_TRN_TYPE", tvTrnType.getText().toString());
        intent.putExtra("ORDER_NO", etOrderNo.getText().toString());
        if (taxStatus != null && taxStatus.equalsIgnoreCase(Constants.BOOLEAN_FALSE)) {
            intent.putExtra("ITBIS_TAX", etITBISAmount.getText().toString());
        }
        if (Boolean.TRUE.equals(buttonOn)) {
            details.setEnteredTaxAmount(newAmt);
        }
        intent.putExtra(Constants.TOTAL_AMOUNT, etTotalAmount.getText().toString());
        intent.putExtra(Constants.LOCATION_RESPONSE, locationJson);

        if (modifiedAmount != 0) {
            intent.putExtra("AMOUNT", modifiedAmount);
            details.setEnteredTotalAmount(modifiedAmount);
        } else {
            intent.putExtra("AMOUNT", lastAmount);
            details.setEnteredTotalAmount(lastAmount);
        }
        details.setSelectedLocationID(mID);
        details.setSelectedLocation(tvLocalidadName.getText().toString());
        details.setSelectedTrnType(tvTrnType.getText().toString());
        details.setSelectedOrderNumber(etOrderNo.getText().toString());


        ((AzulApplication) ((PaymentConfirmaActivity) context).getApplication()).setDetails(details);
        startActivity(intent);
    }

    public void cancelAlert(Context activity) {
        try {

            Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setContentView(R.layout.cancel_dialog_layout);
            TextView textTitle = dialog.findViewById(R.id.textTitle);
            TextView textMsg = dialog.findViewById(R.id.textMsg);
            Typeface typeface;
            typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Medium.ttf");
            textTitle.setTypeface(typeface);
            textMsg.setTypeface(typeface);
            TextView btnNo = dialog.findViewById(R.id.btnNo);
            TextView btnYes = dialog.findViewById(R.id.btnYes);
            btnYes.setTypeface(typeface);

            btnYes.setOnClickListener(view -> {

                Intent intent = new Intent(PaymentConfirmaActivity.this, SetPaymentInfoActivity.class);
                intent.putExtra(Constants.LOCATION_RESPONSE, locationJson);
                startActivity(intent);
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            });
            btnNo.setOnClickListener(view -> {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            });
            dialog.show();

        } catch (
                Exception e) {
            Log.e("Exception : ", Log.getStackTraceString(e));
        }
    }

    public void taxCalculations() {
        String taxAmount = etITBISAmount.getText().toString().replace(",", "");
        String totalAmount = etTotalAmount.getText().toString().replace(",", "");
        String totalAmountNew = totalAmount.replace("RD$", "");

        double taxAmountCheck = Double.parseDouble(taxAmount);
        double totalAmountCheck = Double.parseDouble(totalAmountNew);

        if (taxAmountCheck > totalAmountCheck) {
            layoutSearchByEdittext.setBackgroundResource(R.drawable.error_background_shadow);
            tvITBISError.setText("El ITBIS digitado excede el monto total");
            tvITBISError.setVisibility(View.VISIBLE);
        } else {
            layoutSearchByEdittext.setBackgroundResource(R.drawable.active_border_background);
            tvITBISError.setVisibility(View.GONE);
        }
    }

    public void setLocationData(LocationFilter locationFilter) {
        if (locationFilter != null) {
            lastLocationName = locationFilter.getLocationNameAndId();
            lastLocationMid = locationFilter.getmId();
            mID = lastLocationMid;
            tvSelectedLocation.setText(lastLocationName);
            tvLocalidadName.setText(locationFilter.getLocationName());
            if (locationFilter.getParentName() != null) {
                tvComercio.setText(locationFilter.getParentName());
            }
            taxStatus = locationFilter.getTaxExempt();
        }

        if (locationFilter != null &&
                !TextUtils.isEmpty(locationFilter.getTaxExempt()) &&
                locationFilter.getTaxExempt().equalsIgnoreCase(Constants.BOOLEAN_FALSE)) {
            layoutITBIS.setVisibility(View.VISIBLE);
            tvITBISInclude.setVisibility(View.VISIBLE);

            taxStatus = locationFilter.getTaxExempt();

            dynamicMargin(0);
        } else {
            layoutITBIS.setVisibility(View.GONE);
            tvITBISInclude.setVisibility(View.GONE);
            dynamicMargin(1);

        }
    }

    public void dynamicMargin(int flag) {
        if (flag == 0) {
            LinearLayout.LayoutParams layoutparams = (LinearLayout.LayoutParams) tvAmountTitle.getLayoutParams();

            layoutparams.setMargins(0, 0, 0, 0);
            tvAmountTitle.setLayoutParams(layoutparams);
        }else {
            LinearLayout.LayoutParams layoutparams = (LinearLayout.LayoutParams) tvAmountTitle.getLayoutParams();

            layoutparams.setMargins(0, 28, 0, 0);
            tvAmountTitle.setLayoutParams(layoutparams);
        }
    }

    boolean buttonOn = false;
    double modifiedAmount;
    double newAmt;

    private void switchListener() {
        switchITBIS.setOnClickListener(switchITBISView -> {


            String amountGiven = etTotalAmount.getText().toString();

            String amountwithoutComma = amountGiven.replaceAll(",", "");
            String amountWithoutDecimal = amountwithoutComma.replaceAll("\\.", "");
            modifiedAmount = Double.parseDouble(amountWithoutDecimal);

            if (!buttonOn) {
                buttonOn = true;

                switchITBIS.setImageResource(R.drawable.switch_on_state);

                etITBISAmount.setEnabled(true);
                itbisAmount = calculateTax(modifiedAmount);
                newAmt = itbisAmount / 100;
                etITBISAmount.setText("" + currFormat.format(newAmt));
                etITBISAmount.setTextColor(ContextCompat.getColor(PaymentConfirmaActivity.this, R.color.font_hint));
                searchinActive.setTextColor(ContextCompat.getColor(PaymentConfirmaActivity.this, R.color.font_hint));
            } else {
                buttonOn = false;
                switchITBIS.setImageResource(R.drawable.switch_off_state);
                etITBISAmount.setText("0.00");
                etITBISAmount.setEnabled(false);
                etITBISAmount.setTextColor(Color.parseColor("#C4D0DC"));
                searchinActive.setTextColor(Color.parseColor("#C4D0DC"));
                layoutSearchByEdittext.setBackgroundResource(R.drawable.border_background);
            }
        });

    }


    double itbisAmount;
    DecimalFormat currFormat = new DecimalFormat("#,##0.00");

    String prefix = "RD$ ";


    public void setContent(String parentObjectDataName, String name, String content, String merchantId, int dimissFlag, String tax) {
        tvSelectedLocation.setText(content);
        tvLocalidadName.setText(name);
        tvComercio.setText(parentObjectDataName);
        mID = merchantId;
        taxStatus = tax;
        if (dimissFlag == 1) {
            bottomsheet.dismiss();
        }
        if (!TextUtils.isEmpty(tax) && tax.equalsIgnoreCase(Constants.BOOLEAN_FALSE)) {
            layoutITBIS.setVisibility(View.VISIBLE);
            tvITBISInclude.setVisibility(View.VISIBLE);
            dynamicMargin(0);
        } else {
            layoutITBIS.setVisibility(View.GONE);
            tvITBISInclude.setVisibility(View.GONE);
            dynamicMargin(1);
        }
    }

    ApiManager apiManager = new ApiManager(this);
    String responseCode;

    private void getMerchantApiCall() {
        JSONObject json = new JSONObject();
        try {
            String tcpKey = ((AzulApplication) PaymentConfirmaActivity.this.getApplication()).getTcpKey();
            json.put("tcp", RSAHelper.ecryptRSA(PaymentConfirmaActivity.this, tcpKey));
            JSONObject payload = new JSONObject();
            payload.put("device", DeviceUtils.getDeviceId(PaymentConfirmaActivity.this));

            json.put("payload", RSAHelper.encryptAES(payload.toString(), Base64.decode(tcpKey, 0)));
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.GET_MERCHANTS_FOR_PAYMENT_LINK, json);
    }

    public void paymentLnkMerchantResponse(String responseData) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(responseData);
            Log.d("DATA", "Link Merchant Data ==>  " + jsonObject.toString());
            parseLinkData(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void parseLinkData(JSONObject responseData) {

        try {
            JSONObject jsonTransactionOb = responseData.getJSONObject("data");
            JSONArray jsonArray = jsonTransactionOb.getJSONArray("CallCenters");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject linkObject = jsonArray.getJSONObject(i);
                responseCode = linkObject.getString("Code");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}