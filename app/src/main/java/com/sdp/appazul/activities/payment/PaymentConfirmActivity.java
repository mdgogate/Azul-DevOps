package com.sdp.appazul.activities.payment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.sdp.appazul.R;
import com.sdp.appazul.activities.home.BaseLoggedInActivity;
import com.sdp.appazul.activities.home.MenuLocationFilter;
import com.sdp.appazul.classes.LocationFilter;
import com.sdp.appazul.classes.LocationFilterThirdGroup;
import com.sdp.appazul.classes.PaymentDetails;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.KeyConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class PaymentConfirmActivity extends BaseLoggedInActivity {
    String locationJson;
    EditText etOrderNo;
    EditText etITBISAmount;
    EditText etTotalAmount;
    TextView cancelPayment;
    TextView tvTrnType;
    TextView tvLocalityName;
    TextView searchInActive;
    TextView searchInAmount;
    TextView tvSelectedLocation;
    TextView tvCommerceName;
    TextView tvITBISError;
    TextView tvAmountError;
    TextView tvITBISInclude;
    Button btnConfirmPayment;
    ImageView switchITBIS;
    ImageView btnBackScreen;
    RelativeLayout layoutSearchByEdittext;
    RelativeLayout layoutAmountAllComp;
    RelativeLayout locationFilterForPaymentConfirm;
    String totalAmountEntered;
    double lastAmount = 0;
    ImageView paymentConfirmLocFilter;
    MenuLocationFilter locationsBottomSheet;
    String lastLocationName;
    String lastLocationMid;
    String mID;
    String taxStatus;
    LinearLayout layoutITBIS;
    Context context;
    TextView tvAmountTitle;
    boolean buttonOn = false;
    double modifiedAmount;
    double modifiedTax;
    String newAmt;
    Dialog dialog;
    String paymentLocation;
    Map<String, String> defaultLocations = new HashMap<>();
    double bankTaxAmount;
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
    DecimalFormat currFormat = new DecimalFormat("#,##0.00",symbols);
    LocationFilter locationFilter = new LocationFilter();

    String selectedCurrency;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirma);
        context = this;
        Intent intent = getIntent();
        locationJson = ((AzulApplication) context.getApplicationContext()).getLocationDataShare();
        paymentLocation = ((AzulApplication) context.getApplicationContext()).getLocationDataShare();

        totalAmountEntered = intent.getStringExtra(Constants.TOTAL_AMOUNT);
        selectedCurrency = intent.getStringExtra("CURRENCY");
        responseCode = intent.getStringExtra("CODE");
        initControls();

        locationFilter = ((AzulApplication) this.getApplication()).getLocationFilter();
        if (locationFilter != null) {
            setPreviousLocationData(locationFilter);
        } else {
            defaultLocations = ((AzulApplication) this.getApplication()).getDefaultLocation();

            if (defaultLocations != null) {
                String defaultParentName = defaultLocations.get("PARENT_LOC_NAME");
                String defaultLocationName = defaultLocations.get("CHILD_LOC_NAME");
                String defaultLocationId = defaultLocations.get("CHILD_LOC_ID");
                String tacStatusValue = defaultLocations.get("TAX_STATUS");
                selectedCurrency = defaultLocations.get("CURR");
                String locationToDisplay = defaultLocationName + " - " + defaultLocationId;
                tvSelectedLocation.setText(locationToDisplay);
                setLocationData(defaultParentName, defaultLocationName, defaultLocationId, tacStatusValue);
            }
        }


        if (totalAmountEntered != null && !TextUtils.isEmpty(totalAmountEntered)) {
            try {
                double newAmount = Double.parseDouble(totalAmountEntered);
                double totalEnteredAmount = newAmount / 100;
                lastAmount = totalEnteredAmount;
            } catch (NumberFormatException numberFormatException) {
                Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(numberFormatException));
            }
        } else {
            PaymentDetails paymentDetails = ((AzulApplication) this.getApplication()).getDetails();
            if (paymentDetails != null) {
                tvLocalityName.setText(paymentDetails.getSelectedLocation().toLowerCase());
                tvTrnType.setText(paymentDetails.getSelectedTrnType());

                validateOrderNo(paymentDetails);
                lastAmount = paymentDetails.getEnteredTotalAmount();
                validateTaxAndSwitchStatus(paymentDetails);
            }
        }
        lastAmountValidate();
        setTaxCurrencyHint();
    }

    private void validateOrderNo(PaymentDetails paymentDetails) {
        if (paymentDetails.getSelectedOrderNumber() != null && paymentDetails.getSelectedOrderNumber().length() > 0) {
            etOrderNo.setText(paymentDetails.getSelectedOrderNumber());
        } else {
            etOrderNo.setText("");
        }
    }

    private void setPreviousLocationData(LocationFilter locationFilter) {
        if (locationFilter != null) {
            lastLocationName = locationFilter.getLocationNameAndId();
            lastLocationMid = locationFilter.getmId();
            mID = lastLocationMid;
            tvSelectedLocation.setText(lastLocationName);
            tvLocalityName.setText(locationFilter.getLocationName().toLowerCase());
            if (locationFilter.getParentName() != null) {
                tvCommerceName.setText(locationFilter.getParentName().toLowerCase());
            }
            taxStatus = locationFilter.getTaxExempt();
            selectedCurrency = locationFilter.getCurrency();
        }

        if (locationFilter != null &&
                !TextUtils.isEmpty(locationFilter.getTaxExempt()) &&
                locationFilter.getTaxExempt().equalsIgnoreCase(Constants.BOOLEAN_TRUE)) {
            layoutITBIS.setVisibility(View.VISIBLE);
            tvITBISInclude.setVisibility(View.VISIBLE);

            taxStatus = locationFilter.getTaxExempt();
            selectedCurrency = locationFilter.getCurrency();

            dynamicMargin(0);
        } else {
            layoutITBIS.setVisibility(View.GONE);
            tvITBISInclude.setVisibility(View.GONE);
            dynamicMargin(1);
        }
    }

    private void setLocationData(String parentName, String defaultLocationName, String defaultLocationId, String tacStatusValue) {
        lastLocationMid = defaultLocationId;
        mID = lastLocationMid;
        tvSelectedLocation.setText(defaultLocationName + " - " + defaultLocationId);
        tvLocalityName.setText(defaultLocationName.toLowerCase());
        tvCommerceName.setText(parentName.toLowerCase());
        taxStatus = tacStatusValue;

        if (!TextUtils.isEmpty(tacStatusValue) &&
                tacStatusValue.equalsIgnoreCase(Constants.BOOLEAN_TRUE)) {
            layoutITBIS.setVisibility(View.VISIBLE);
            tvITBISInclude.setVisibility(View.VISIBLE);
            dynamicMargin(0);
        } else {
            layoutITBIS.setVisibility(View.GONE);
            tvITBISInclude.setVisibility(View.GONE);
            dynamicMargin(1);
        }
    }

    private void validateTaxAndSwitchStatus(PaymentDetails paymentDetails) {
        if (paymentDetails.getSwitchFlag() == 1) {
            buttonOn = true;
            switchITBIS.setImageResource(R.drawable.switch_on_state);
            String taxWithoutComma = paymentDetails.getEnteredTaxAmount().replace(",", "");
            String taxWithoutDecimal = taxWithoutComma.replace("\\.", "");
            modifiedTax = Double.parseDouble(taxWithoutDecimal);
            etITBISAmount.setText(currFormat.format(modifiedTax));
            etITBISAmount.setTextColor(ContextCompat.getColor(PaymentConfirmActivity.this, R.color.font_hint));
            searchInActive.setTextColor(ContextCompat.getColor(PaymentConfirmActivity.this, R.color.font_hint));
            currencyCheckForTax();
        } else {
            buttonOn = false;
            switchITBIS.setImageResource(R.drawable.switch_off_state);
            String emptyAmount = "0.00";
            etITBISAmount.setText(emptyAmount);
            etITBISAmount.setTextColor(Color.parseColor(Constants.LIGHT_BLUE));
            searchInActive.setTextColor(Color.parseColor(Constants.LIGHT_BLUE));
            layoutSearchByEdittext.setBackgroundResource(R.drawable.border_background);
            currencyCheckForTax();
        }
    }

    private void lastAmountValidate() {
        if (lastAmount > 0) {
            currencyCheck();
            etTotalAmount.setText(currFormat.format(lastAmount));
            searchInAmount.setTextColor(ContextCompat.getColor(PaymentConfirmActivity.this, R.color.font_hint));
            etTotalAmount.setTextColor(ContextCompat.getColor(PaymentConfirmActivity.this, R.color.font_hint));
        }
    }

    private double calculateTax(double totalAmountEntered) {
        double subTotal = (totalAmountEntered / 1.18);
        double minusAmount = subTotal - totalAmountEntered;
        return minusAmount * (-1);
    }

    private void initControls() {
        btnBackScreen = findViewById(R.id.btnBackScreen);
        tvAmountTitle = findViewById(R.id.tvAmountTitle);
        locationFilterForPaymentConfirm = findViewById(R.id.locaFilterForpaymentConfirm);
        paymentConfirmLocFilter = findViewById(R.id.paymentConfirmLocFilter);
        layoutSearchByEdittext = findViewById(R.id.layoutSearchByEdittext);
        layoutAmountAllComp = findViewById(R.id.layoutAmountAllComp);
        layoutITBIS = findViewById(R.id.layoutITBIS);
        tvLocalityName = findViewById(R.id.tvLocalidadName);
        tvITBISError = findViewById(R.id.tvITBISError);
        tvAmountError = findViewById(R.id.tvAmountError);
        etOrderNo = findViewById(R.id.etOrderNo);
        searchInActive = findViewById(R.id.searchInActive);
        searchInAmount = findViewById(R.id.searchinAmount);
        etITBISAmount = findViewById(R.id.etITBISAmount);
        etTotalAmount = findViewById(R.id.etTotalAmount);
        tvTrnType = findViewById(R.id.tvTrnType);
        tvSelectedLocation = findViewById(R.id.tvSelectedLocation);
        cancelPayment = findViewById(R.id.cancelPayment);
        tvCommerceName = findViewById(R.id.tvComercio);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
        switchITBIS = findViewById(R.id.switchITBIS);
        tvITBISInclude = findViewById(R.id.tvITBISInclude);

        btnBackScreen.setOnClickListener(btnBackScreenView -> {
            Intent intent = new Intent(PaymentConfirmActivity.this, SetPaymentInfoActivity.class);
            ((AzulApplication) ((PaymentConfirmActivity) this).getApplication()).setLocationDataShare(locationJson);
            intent.putExtra(Constants.TOTAL_AMOUNT, totalAmountEntered);
            startActivity(intent);
            this.overridePendingTransition(R.anim.animation_leave,
                    R.anim.slide_nothing);
        });
        locationFilterForPaymentConfirm.setOnClickListener(paymentLocationSelectorView -> {
            locationsBottomSheet = new MenuLocationFilter(paymentLocation, "PAYMENT_CONFIRM", 4);
            locationsBottomSheet.show(getSupportFragmentManager(), "bottomSheet");
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
                etITBISAmount.setTextColor(ContextCompat.getColor(PaymentConfirmActivity.this, R.color.font_hint));
                searchInActive.setTextColor(ContextCompat.getColor(PaymentConfirmActivity.this, R.color.font_hint));
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (etITBISAmount.getText().toString().length() <= 0) {
                    Log.d("", "");
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
                etTotalAmount.setTextColor(ContextCompat.getColor(PaymentConfirmActivity.this, R.color.font_hint));
                searchInAmount.setTextColor(ContextCompat.getColor(PaymentConfirmActivity.this, R.color.font_hint));

                currencyCheck();
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (etTotalAmount.getText().toString().length() <= 0
                        || etTotalAmount.getText().toString().equalsIgnoreCase("0")
                        || etTotalAmount.getText().toString().equalsIgnoreCase("0.")) {
                    errorFunc();
                } else if (etTotalAmount.getText().toString().equalsIgnoreCase("0.0")
                        || etTotalAmount.getText().toString().equalsIgnoreCase("0.00")) {
                    errorFunc();
                } else {
                    tvAmountError.setVisibility(View.GONE);
                    btnConfirmPayment.setEnabled(true);
                    btnConfirmPayment.setBackgroundResource(R.drawable.link_button_background);
                    if (!etITBISAmount.getText().toString().isEmpty() && !etTotalAmount.getText().toString().isEmpty()) {
                        taxCalculations();
                    }
                }
            }
        });

        etITBISAmount.setOnFocusChangeListener((etITBISAmountView, b) -> {
            layoutSearchByEdittext.setBackgroundResource(R.drawable.active_border_background);
            layoutAmountAllComp.setBackgroundResource(R.drawable.border_background);
            etOrderNo.setBackgroundResource(R.drawable.border_background);
            searchInActive.setTextColor(ContextCompat.getColor(PaymentConfirmActivity.this, R.color.font_hint));
            currencyCheckForTax();
            tvITBISError.setVisibility(View.GONE);
        });
        etTotalAmount.setOnFocusChangeListener((etTotalAmountView, b) -> {
            layoutAmountAllComp.setBackgroundResource(R.drawable.active_border_background);
            layoutSearchByEdittext.setBackgroundResource(R.drawable.border_background);
            searchInAmount.setTextColor(ContextCompat.getColor(PaymentConfirmActivity.this, R.color.font_hint));
            etOrderNo.setBackgroundResource(R.drawable.border_background);
            currencyCheckForTax();
            tvAmountError.setVisibility(View.GONE);
        });
        etOrderNo.setOnFocusChangeListener((etOrderNoView, b) -> {
            etOrderNo.setBackgroundResource(R.drawable.active_border_background);
            layoutAmountAllComp.setBackgroundResource(R.drawable.border_background);
            layoutSearchByEdittext.setBackgroundResource(R.drawable.border_background);
        });


        switchListener();
        btnConfirmPayment.setOnClickListener(btnConfirmPaymentView -> callPaymentValidationScreen());

        cancelPayment.setOnClickListener(cancelPaymentView -> cancelAlert(context));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            etOrderNo.setCustomInsertionActionModeCallback(callback);
            etITBISAmount.setCustomInsertionActionModeCallback(callback);
            etTotalAmount.setCustomInsertionActionModeCallback(callback);
        }

        etOrderNo.setCustomSelectionActionModeCallback(callback);
        etITBISAmount.setCustomSelectionActionModeCallback(callback);
        etTotalAmount.setCustomSelectionActionModeCallback(callback);

    }

    ActionMode.Callback callback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            if (menu != null) {
                menu.removeItem(android.R.id.paste);
                menu.removeItem(android.R.id.copy);
                menu.removeItem(android.R.id.copy);
                menu.removeItem(android.R.id.selectAll);
            }
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            Log.d(TAG, "onDestroyActionMode: ");
        }
    };

    private void errorFunc() {
        layoutAmountAllComp.setBackgroundResource(R.drawable.error_background_shadow);
        tvAmountError.setText(context.getString(R.string.less_amount_error));
        btnConfirmPayment.setBackgroundResource(R.drawable.continue_btn_error_bg);
        btnConfirmPayment.setEnabled(false);
        tvAmountError.setVisibility(View.VISIBLE);
    }

    private void callPaymentValidationScreen() {
        Intent intent = new Intent(PaymentConfirmActivity.this, PaymentDataValidateActivity.class);
        PaymentDetails details = new PaymentDetails();
        if (responseCode != null) {
            intent.putExtra("CODE", responseCode);
        }
        intent.putExtra("SELECTED_LOCATION_ID", mID);

        intent.putExtra("SELECTED_PARENT_LOCATION", tvCommerceName.getText().toString());
        intent.putExtra("SELECTED_LOCATION", tvLocalityName.getText().toString());
        intent.putExtra("SELECTED_TRN_TYPE", tvTrnType.getText().toString());
        intent.putExtra("ORDER_NO", etOrderNo.getText().toString());
        intent.putExtra("TAX_STATUS", taxStatus);
        intent.putExtra("CURRENCY", selectedCurrency);
        details.setCurrency(selectedCurrency);
        if (taxStatus != null && taxStatus.equalsIgnoreCase(Constants.BOOLEAN_TRUE)) {
            intent.putExtra(Constants.TAX_LABEL, etITBISAmount.getText().toString());
            details.setEnteredTaxAmount(etITBISAmount.getText().toString());
        } else {
            details.setEnteredTaxAmount("0");
        }
        if (Boolean.TRUE.equals(buttonOn)) {
            details.setSwitchFlag(1);
        } else {
            details.setSwitchFlag(0);
        }
        intent.putExtra(Constants.TOTAL_AMOUNT, etTotalAmount.getText().toString());
        ((AzulApplication) ((PaymentConfirmActivity) this).getApplication()).setLocationDataShare(locationJson);

        if (modifiedAmount > 0) {
            intent.putExtra(Constants.AMOUNT_LABEL, modifiedAmount);
            details.setEnteredTotalAmount(modifiedAmount);
        } else {


            if (etTotalAmount.getText().toString().contains(",")) {
                double editedAmount = Double.parseDouble(etTotalAmount.getText().toString().replace(",", ""));
                intent.putExtra(Constants.AMOUNT_LABEL, editedAmount);
                details.setEnteredTotalAmount(editedAmount);
            } else {
                double editedAmount = Double.parseDouble(etTotalAmount.getText().toString());
                intent.putExtra(Constants.AMOUNT_LABEL, editedAmount);
                details.setEnteredTotalAmount(editedAmount);
            }
        }
        details.setSelectedLocationID(mID);
        details.setSelectedLocation(tvLocalityName.getText().toString());
        details.setSelectedTrnType(tvTrnType.getText().toString());
        details.setSelectedOrderNumber(etOrderNo.getText().toString());


        ((AzulApplication) ((PaymentConfirmActivity) context).getApplication()).setDetails(details);
        startActivity(intent);
        this.overridePendingTransition(R.anim.animation_enter,
                R.anim.slide_nothing);

    }

    public void cancelAlert(Context activity) {
        try {

            dialog = new Dialog(activity);
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

                Intent intent = new Intent(PaymentConfirmActivity.this, SetPaymentInfoActivity.class);
                ((AzulApplication) ((PaymentConfirmActivity) this).getApplication()).setLocationDataShare(locationJson);

                startActivity(intent);
                this.overridePendingTransition(R.anim.animation_leave,
                        R.anim.slide_nothing);
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
        Log.d(TAG, "taxCalculations: " + taxAmountCheck + "  =  " + totalAmountCheck);
        if (taxAmountCheck > totalAmountCheck) {
            layoutSearchByEdittext.setBackgroundResource(R.drawable.error_background_shadow);
            tvITBISError.setText(getResources().getString(R.string.itbis_error));
            tvITBISError.setVisibility(View.VISIBLE);
            btnConfirmPayment.setBackgroundResource(R.drawable.continue_btn_error_bg);
            btnConfirmPayment.setEnabled(false);
        } else {
            Log.d(TAG, "taxCalculations: link_button_background");
            btnConfirmPayment.setEnabled(true);
            btnConfirmPayment.setBackgroundResource(R.drawable.link_button_background);
            layoutSearchByEdittext.setBackgroundResource(R.drawable.active_border_background);
            tvITBISError.setVisibility(View.GONE);
        }
    }

    public void dynamicMargin(int flag) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tvAmountTitle.getLayoutParams();
        if (flag == 0) {
            layoutParams.setMargins(0, 0, 0, 0);
        } else {
            layoutParams.setMargins(0, 28, 0, 0);
        }
        tvAmountTitle.setLayoutParams(layoutParams);
    }


    private void switchListener() {
        switchITBIS.setOnClickListener(switchITBISView -> {

            if (!TextUtils.isEmpty(etTotalAmount.getText().toString())) {
                String amountGiven = etTotalAmount.getText().toString();
                String amountWithoutComma = amountGiven.replace(",", "");
                String amountWithoutDecimal = amountWithoutComma.replace("\\.", "");
                modifiedAmount = Double.parseDouble(amountWithoutDecimal);

                if (!buttonOn) {
                    buttonOn = true;
                    switchITBIS.setImageResource(R.drawable.switch_on_state);
                    bankTaxAmount = calculateTax(modifiedAmount);
                    etITBISAmount.setText(currFormat.format(bankTaxAmount));
                    etITBISAmount.setTextColor(ContextCompat.getColor(PaymentConfirmActivity.this, R.color.font_hint));
                    searchInActive.setTextColor(ContextCompat.getColor(PaymentConfirmActivity.this, R.color.font_hint));
                    currencyCheckForTax();
                } else {
                    buttonOn = false;
                    switchITBIS.setImageResource(R.drawable.switch_off_state);
                    String emptyAmount = "0.00";
                    etITBISAmount.setText(emptyAmount);
                    etITBISAmount.setTextColor(Color.parseColor(Constants.LIGHT_BLUE));
                    searchInActive.setTextColor(Color.parseColor(Constants.LIGHT_BLUE));
                    layoutSearchByEdittext.setBackgroundResource(R.drawable.border_background);
                    currencyCheckForTax();
                }
            } else {
                tvAmountError.setVisibility(View.VISIBLE);
                tvAmountError.setText(context.getString(R.string.less_amount_error));
            }
        });

    }

    public void setContent(String parentObjectDataName, String name,
                           String content, String merchantId, int dismissFlag, LocationFilterThirdGroup locationFilterThirdGroup, String Code) {
        tvSelectedLocation.setText(content);
        tvLocalityName.setText(name.toLowerCase());
        tvCommerceName.setText(parentObjectDataName.toLowerCase());
        mID = merchantId;
        taxStatus = locationFilterThirdGroup.getTaxExempt();
        if (dismissFlag == 1) {
            locationsBottomSheet.dismiss();
        }
        if (!TextUtils.isEmpty(locationFilterThirdGroup.getTaxExempt()) && locationFilterThirdGroup.getTaxExempt().equalsIgnoreCase(Constants.BOOLEAN_TRUE)) {
            layoutITBIS.setVisibility(View.VISIBLE);
            tvITBISInclude.setVisibility(View.VISIBLE);
            dynamicMargin(0);
        } else {
            layoutITBIS.setVisibility(View.GONE);
            tvITBISInclude.setVisibility(View.GONE);
            dynamicMargin(1);
        }
        responseCode = Code;
        selectedCurrency = locationFilterThirdGroup.getCurrency();
        setTaxCurrencyHint();
    }

    String responseCode;


    public void paymentLnkMerchantResponse(String responseData) {
        JSONObject jsonObject;
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

    String TAG = "TAG";

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        if (!etTotalAmount.getText().toString().trim().equalsIgnoreCase("") && !etTotalAmount.getText().toString().trim().contains(",")) {
            etTotalAmount.setText(getUpdatedCurrency(etTotalAmount.getText().toString()));
            etTotalAmount.setCursorVisible(false);
        }
        if (!etITBISAmount.getText().toString().trim().equalsIgnoreCase("") && !etITBISAmount.getText().toString().trim().contains(",")) {
            etITBISAmount.setText(getUpdatedCurrency(etITBISAmount.getText().toString()));
            etITBISAmount.setCursorVisible(false);
            taxCalculations();
        }
        return super.dispatchTouchEvent(ev);
    }

    public String getUpdatedCurrency(String amount) {

        double aDouble;
        try {
            aDouble = Double.parseDouble(amount.replace(",", "").replace("-", ""));
        } catch (NumberFormatException e) {
            aDouble = 0.00;
        }
        Locale locale = new Locale("en", "US");
        String pattern = "#,##0.00";

        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        decimalFormat.applyPattern(pattern);
        String aString = "0.00";
        if (aDouble <= 0) {
            return aString;
        } else {
            aString = decimalFormat.format(aDouble);
        }
        return aString;
    }

    public void setTaxCurrencyHint() {
        if (selectedCurrency.equalsIgnoreCase("USD")) {
            searchInActive.setText(Constants.CURRENCY_FORMAT_USD);
            searchInAmount.setText(Constants.CURRENCY_FORMAT_USD);
        } else {
            searchInActive.setText(Constants.CURRENCY_FORMAT);
            searchInAmount.setText(Constants.CURRENCY_FORMAT);
        }
    }
    public void currencyCheck(){
        if (selectedCurrency.equalsIgnoreCase("USD")) {
            searchInAmount.setText(Constants.CURRENCY_FORMAT_USD);
        } else {
            searchInAmount.setText(Constants.CURRENCY_FORMAT);
        }
    }public void currencyCheckForTax(){
        if (selectedCurrency.equalsIgnoreCase("USD")) {
            searchInActive.setText(Constants.CURRENCY_FORMAT_USD);
        } else {
            searchInActive.setText(Constants.CURRENCY_FORMAT);
        }
    }
}