package com.sdp.appazul.activities.payment;

import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.util.Base64;
import android.util.Log;

import android.view.ActionMode;
import android.view.ContextMenu;
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

import com.sdp.appazul.R;
import com.sdp.appazul.activities.home.BaseLoggedInActivity;
import com.sdp.appazul.activities.home.MenuLocationFilter;
import com.sdp.appazul.api.ApiManager;
import com.sdp.appazul.api.ServiceUrls;
import com.sdp.appazul.classes.LocationFilter;
import com.sdp.appazul.classes.LocationFilterThirdGroup;
import com.sdp.appazul.classes.PaymentDetails;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.GlobalFunctions;
import com.sdp.appazul.globals.KeyConstants;
import com.sdp.appazul.security.RSAHelper;
import com.sdp.appazul.utils.DeviceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuickPayConfirmActivity extends BaseLoggedInActivity {
    String locationJson;
    EditText etOrderNo;
    EditText etEmail;
    EditText etCustomerName;
    EditText etITBISAmount;
    EditText etTotalAmount;
    TextView tvEmailError;
    TextView cancelPayment;
    TextView tvTrnType;
    TextView tvLocationName;
    TextView searchInActive;
    TextView searchInAmount;
    TextView tvCommerceName;
    TextView tvITBISError;
    TextView tvAmountError;
    TextView tvITBISInclude;
    Button btnConfirmPayment;
    ImageView switchITBIS;
    ImageView btnBackScreen;
    RelativeLayout layoutSearchByEdittext;
    RelativeLayout layoutAmountAllComp;
    String totalAmountEntered;
    double lastAmount = 0;
    ImageView openLocationBar;
    MenuLocationFilter menuLocationBottomSheet;
    LocationFilter locationFilter = new LocationFilter();
    String lastLocationName;
    String lastLocationMid;
    String mID;
    String taxStatus;
    String selectedCurrency;
    String previousTaxStatus;
    LinearLayout layoutITBIS;
    LinearLayout layoutOrderInfo;
    RelativeLayout layoutEmailInfo;
    Context context;
    TextView tvAmountTitle;
    String previousLocationName;
    String previousParentLocationName;
    String previousLocationId;
    ApiManager apiManager = new ApiManager(this);
    String responseCode;
    double bankTaxAmount;
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
    DecimalFormat currFormat = new DecimalFormat("#,##0.00",symbols);
    boolean buttonOn = false;
    double modifiedAmount;
    double modifiedTax;
    double newAmt;
    private static final double UNINITIALIZED = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_pay_confirm);
        context = this;
        getIntentData();
        initControls();
        amountCheck(totalAmountEntered);

        if (lastAmount > UNINITIALIZED) {
            setCurrencyForAmount();
            etTotalAmount.setText(currFormat.format(lastAmount));
            searchInAmount.setTextColor(ContextCompat.getColor(QuickPayConfirmActivity.this, R.color.font_hint));
            etTotalAmount.setTextColor(ContextCompat.getColor(QuickPayConfirmActivity.this, R.color.font_hint));
        }
        setTaxCurrencyHint();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        locationFilter = ((AzulApplication) this.getApplication()).getLocationFilter();
        responseCode = intent.getStringExtra("CODE");
        totalAmountEntered = intent.getStringExtra(Constants.TOTAL_AMOUNT);
        locationJson = ((AzulApplication) context.getApplicationContext()).getLocationDataShare();

        previousLocationName = intent.getStringExtra(Constants.LOCATION_NAME_SELECTED);
        previousTaxStatus = intent.getStringExtra("TAX_STATUS");
        selectedCurrency = intent.getStringExtra("CURRENCY");
        previousParentLocationName = intent.getStringExtra(Constants.LOCATION_PARENT_NAME_SELECTED);
        previousLocationId = intent.getStringExtra(Constants.LOCATION_ID_SELECTED);
    }

    private void amountCheck(String totalAmountGiven) {
        try {
            if (totalAmountGiven != null && !TextUtils.isEmpty(totalAmountGiven)) {
                double newAmount = Double.parseDouble(totalAmountGiven);
                double totalEnteredAmount = newAmount / 100;
                lastAmount = totalEnteredAmount;
            } else {
                getPreviousEnteredAmount();
            }
        } catch (NumberFormatException numberFormatException) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(numberFormatException));
        }
    }

    private void getPreviousEnteredAmount() {
        PaymentDetails paymentDetails = ((AzulApplication) this.getApplication()).getDetails();
        if (paymentDetails != null) {
            tvLocationName.setText(paymentDetails.getSelectedLocation().toLowerCase());
            tvTrnType.setText(paymentDetails.getSelectedTrnType());
            if (paymentDetails.getSelectedOrderNumber() != null && paymentDetails.getSelectedOrderNumber().length() > 0) {
                etOrderNo.setText(paymentDetails.getSelectedOrderNumber());
            } else {
                etOrderNo.setText("");
            }
            if (paymentDetails.getSelectedEmail() != null && paymentDetails.getSelectedEmail().length() > 0) {
                etEmail.setText(paymentDetails.getSelectedEmail());
            } else {
                etEmail.setText("");
            }
            if (paymentDetails.getSelectedName() != null && paymentDetails.getSelectedName().length() > 0) {
                etCustomerName.setText(paymentDetails.getSelectedName());
            } else {
                etCustomerName.setText("");
            }

            validateTaxAndStatus(paymentDetails);
            lastAmount = paymentDetails.getEnteredTotalAmount();
        }

    }

    private void validateTaxAndStatus(PaymentDetails paymentDetails) {
        if (paymentDetails.getSwitchFlag() == 1) {
            buttonOn = true;
            switchITBIS.setImageResource(R.drawable.switch_on_state);
            String taxWithoutComma = paymentDetails.getEnteredTaxAmount().replace(",", "");
            String taxWithoutDecimal = taxWithoutComma.replace("\\.", "");
            modifiedTax = Double.parseDouble(taxWithoutDecimal);
            etITBISAmount.setText(currFormat.format(modifiedTax));
            etITBISAmount.setTextColor(ContextCompat.getColor(QuickPayConfirmActivity.this, R.color.font_hint));
            searchInActive.setTextColor(ContextCompat.getColor(QuickPayConfirmActivity.this, R.color.font_hint));
            checkCurrency();
        } else {
            buttonOn = false;
            switchITBIS.setImageResource(R.drawable.switch_off_state);
            String emptyAmount = "0.00";
            etITBISAmount.setText(emptyAmount);
            etITBISAmount.setTextColor(Color.parseColor(Constants.LIGHT_BLUE));
            searchInActive.setTextColor(Color.parseColor(Constants.LIGHT_BLUE));
            layoutSearchByEdittext.setBackgroundResource(R.drawable.border_background);
            if (selectedCurrency.equalsIgnoreCase("USD")) {
                searchInActive.setText(Constants.CURRENCY_FORMAT_USD);
            } else {
                searchInActive.setText(Constants.CURRENCY_FORMAT);
            }
        }
    }

    private double calculateTax(double totalAmountEntered) {
        double subTotal = (totalAmountEntered / 1.18);
        double minusAmount = subTotal - totalAmountEntered;
        return minusAmount * (-1);
    }

    private void initControls() {
        layoutOrderInfo = findViewById(R.id.layoutOrderInfo);
        layoutEmailInfo = findViewById(R.id.layoutemailInfo);
        etEmail = findViewById(R.id.etEmail);
        etCustomerName = findViewById(R.id.etCustomerName);
        openLocationBar = findViewById(R.id.openLocationBar);
        btnBackScreen = findViewById(R.id.btnBackScreen);
        tvAmountTitle = findViewById(R.id.tvAmountTitle);
        layoutSearchByEdittext = findViewById(R.id.layoutSearchByEdittext);
        layoutAmountAllComp = findViewById(R.id.layoutAmountAllComp);
        layoutITBIS = findViewById(R.id.layoutITBIS);
        tvLocationName = findViewById(R.id.tvLocalidadName);
        tvITBISError = findViewById(R.id.tvITBISError);
        tvAmountError = findViewById(R.id.tvAmountError);
        etOrderNo = findViewById(R.id.etOrderNo);
        searchInActive = findViewById(R.id.searchinActive);
        searchInAmount = findViewById(R.id.searchinAmount);
        etITBISAmount = findViewById(R.id.etITBISAmount);
        etTotalAmount = findViewById(R.id.etTotalAmount);
        tvTrnType = findViewById(R.id.tvTrnType);
        tvEmailError = findViewById(R.id.tvEmailError);
        cancelPayment = findViewById(R.id.cancelPayment);
        tvCommerceName = findViewById(R.id.tvComercio);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
        switchITBIS = findViewById(R.id.switchITBIS);
        tvITBISInclude = findViewById(R.id.tvITBISInclude);

        setLocationData(locationFilter);
        btnBackScreen.setOnClickListener(btnBackScreenView -> {
            Intent intent = new Intent(QuickPayConfirmActivity.this, QuickSetPaymentActivity.class);
            ((AzulApplication) ((QuickPayConfirmActivity) this).getApplication()).setLocationDataShare(locationJson);
            if (responseCode != null) {
                intent.putExtra("CODE", responseCode);
            }
            intent.putExtra(Constants.TOTAL_AMOUNT, totalAmountEntered);
            startActivity(intent);
            this.overridePendingTransition(R.anim.animation_leave,
                    R.anim.slide_nothing);
        });
        openLocationBar.setOnClickListener(paymentLocationSelectorView -> {
            menuLocationBottomSheet = new MenuLocationFilter(locationJson, "QUICK_PAYMENT_CONFIRM", 1);
            menuLocationBottomSheet.show(getSupportFragmentManager(), "bottomSheet");
        });


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
                Log.d("TAG", "onDestroyActionMode: ");
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            etOrderNo.setCustomInsertionActionModeCallback(callback);
            etCustomerName.setCustomInsertionActionModeCallback(callback);
            etITBISAmount.setCustomInsertionActionModeCallback(callback);
            etTotalAmount.setCustomInsertionActionModeCallback(callback);
            etEmail.setCustomInsertionActionModeCallback(callback);
        }

        etOrderNo.setCustomSelectionActionModeCallback(callback);
        etCustomerName.setCustomSelectionActionModeCallback(callback);
        etITBISAmount.setCustomSelectionActionModeCallback(callback);
        etTotalAmount.setCustomSelectionActionModeCallback(callback);
        etEmail.setCustomSelectionActionModeCallback(callback);

        editTextListeners();


        switchListener();
        btnConfirmPayment.setOnClickListener(btnConfirmPaymentView ->

                uiValidation());

        cancelPayment.setOnClickListener(cancelPaymentView ->

                cancelAlert(context));
    }

    private void editTextListeners() {

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
                etCustomerName.setBackgroundResource(R.drawable.border_background);
                layoutEmailInfo.setBackgroundResource(R.drawable.border_background);
                etITBISAmount.setTextColor(ContextCompat.getColor(QuickPayConfirmActivity.this, R.color.font_hint));
                searchInActive.setTextColor(ContextCompat.getColor(QuickPayConfirmActivity.this, R.color.font_hint));
                checkCurrency();
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
                etTotalAmount.setTextColor(ContextCompat.getColor(QuickPayConfirmActivity.this, R.color.font_hint));
                searchInAmount.setTextColor(ContextCompat.getColor(QuickPayConfirmActivity.this, R.color.font_hint));
                setCurrencyForAmount();
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

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("", "");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvEmailError.setVisibility(View.GONE);
                btnConfirmPayment.setBackgroundResource(R.drawable.link_button_background);
                btnConfirmPayment.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("", "");
                tvEmailError.setVisibility(View.GONE);
                btnConfirmPayment.setBackgroundResource(R.drawable.link_button_background);
                btnConfirmPayment.setEnabled(true);
            }
        });

        etITBISAmount.setOnFocusChangeListener((view, b) ->

        {
            layoutSearchByEdittext.setBackgroundResource(R.drawable.active_border_background);
            layoutAmountAllComp.setBackgroundResource(R.drawable.border_background);
            etOrderNo.setBackgroundResource(R.drawable.border_background);
            etCustomerName.setBackgroundResource(R.drawable.border_background);
            layoutEmailInfo.setBackgroundResource(R.drawable.border_background);
            searchInActive.setTextColor(ContextCompat.getColor(QuickPayConfirmActivity.this, R.color.font_hint));
            if (selectedCurrency.equalsIgnoreCase("USD")) {
                searchInActive.setText(Constants.CURRENCY_FORMAT_USD);
            } else {
                searchInActive.setText(Constants.CURRENCY_FORMAT);
            }
            tvITBISError.setVisibility(View.GONE);
        });
        etTotalAmount.setOnFocusChangeListener((view, b) -> {
            layoutAmountAllComp.setBackgroundResource(R.drawable.active_border_background);
            layoutSearchByEdittext.setBackgroundResource(R.drawable.border_background);
            setCurrencyForAmount();
            searchInAmount.setTextColor(ContextCompat.getColor(QuickPayConfirmActivity.this, R.color.font_hint));
            etOrderNo.setBackgroundResource(R.drawable.border_background);
            etCustomerName.setBackgroundResource(R.drawable.border_background);
            layoutEmailInfo.setBackgroundResource(R.drawable.border_background);
            tvAmountError.setVisibility(View.GONE);
        });


        etOrderNo.setOnFocusChangeListener((view, b) ->

        {
            etOrderNo.setBackgroundResource(R.drawable.active_border_background);
            layoutAmountAllComp.setBackgroundResource(R.drawable.border_background);
            etCustomerName.setBackgroundResource(R.drawable.border_background);
            layoutEmailInfo.setBackgroundResource(R.drawable.border_background);
            layoutSearchByEdittext.setBackgroundResource(R.drawable.border_background);
        });

        etCustomerName.setOnFocusChangeListener((view, b) ->

        {
            etCustomerName.setBackgroundResource(R.drawable.active_border_background);
            etOrderNo.setBackgroundResource(R.drawable.border_background);
            layoutEmailInfo.setBackgroundResource(R.drawable.border_background);
            layoutAmountAllComp.setBackgroundResource(R.drawable.border_background);
            layoutSearchByEdittext.setBackgroundResource(R.drawable.border_background);
        });

        etEmail.setOnFocusChangeListener((view, b) ->

        {
            layoutEmailInfo.setBackgroundResource(R.drawable.active_border_background);
            etOrderNo.setBackgroundResource(R.drawable.border_background);
            etCustomerName.setBackgroundResource(R.drawable.border_background);
            layoutAmountAllComp.setBackgroundResource(R.drawable.border_background);
            layoutSearchByEdittext.setBackgroundResource(R.drawable.border_background);
            tvEmailError.setVisibility(View.GONE);
            btnConfirmPayment.setBackgroundResource(R.drawable.link_button_background);
            btnConfirmPayment.setEnabled(true);
        });
    }
    private void setCurrencyForAmount(){
        if (selectedCurrency.equalsIgnoreCase("USD")) {
            searchInAmount.setText(Constants.CURRENCY_FORMAT_USD);
        } else {
            searchInAmount.setText(Constants.CURRENCY_FORMAT);
        }
    }

    private void uiValidation() {
        String taxAmount = etITBISAmount.getText().toString().replace(",", "");
        String totalAmount = etTotalAmount.getText().toString().replace(",", "");
        String totalAmountNew = totalAmount.replace("RD$", "");
        double taxAmountCheck = 0;
        double totalAmountCheck = 0;
        if (taxAmount != null && !taxAmount.isEmpty() && totalAmountNew != null && !totalAmountNew.isEmpty()) {
            taxAmountCheck = Double.parseDouble(taxAmount);
            totalAmountCheck = Double.parseDouble(totalAmountNew);
            if (!TextUtils.isEmpty(etEmail.getText().toString())) {
                if (Boolean.TRUE.equals(GlobalFunctions.isValidEmail(etEmail.getText().toString()))) {
                    callPaymentValidationScreen();
                    tvEmailError.setVisibility(View.GONE);
                    btnConfirmPayment.setBackgroundResource(R.drawable.link_button_background);
                    btnConfirmPayment.setEnabled(true);
                } else {
                    tvEmailError.setVisibility(View.VISIBLE);
                    tvEmailError.setText(context.getString(R.string.email_error_msg));
                    btnConfirmPayment.setBackgroundResource(R.drawable.continue_btn_error_bg);
                    btnConfirmPayment.setEnabled(false);
                }
            } else if (taxAmountCheck > totalAmountCheck) {
                layoutSearchByEdittext.setBackgroundResource(R.drawable.error_background_shadow);
                tvITBISError.setVisibility(View.VISIBLE);
                tvITBISError.setText(getResources().getString(R.string.itbis_error));
                btnConfirmPayment.setBackgroundResource(R.drawable.continue_btn_error_bg);
                btnConfirmPayment.setEnabled(false);
            } else if (TextUtils.isEmpty(etTotalAmount.getText().toString())) {
                tvAmountError.setVisibility(View.VISIBLE);
                tvAmountError.setText(context.getString(R.string.less_amount_error));
            } else {
                callPaymentValidationScreen();
            }

        } else {
            validateData();
        }

    }


    private void validateData() {

        if (!TextUtils.isEmpty(etEmail.getText().toString())) {
            if (Boolean.TRUE.equals(GlobalFunctions.isValidEmail(etEmail.getText().toString()))) {
                callPaymentValidationScreen();
                tvEmailError.setVisibility(View.GONE);
                btnConfirmPayment.setBackgroundResource(R.drawable.link_button_background);
                btnConfirmPayment.setEnabled(true);
            } else {
                tvEmailError.setVisibility(View.VISIBLE);
                tvEmailError.setText(context.getString(R.string.email_error_msg));
                btnConfirmPayment.setBackgroundResource(R.drawable.continue_btn_error_bg);
                btnConfirmPayment.setEnabled(false);
            }
        } else if (TextUtils.isEmpty(etTotalAmount.getText().toString())) {
            tvAmountError.setVisibility(View.VISIBLE);
            tvAmountError.setText(context.getString(R.string.less_amount_error));
        } else {
            callPaymentValidationScreen();
        }
    }

    private void callPaymentValidationScreen() {
        Intent intent = new Intent(QuickPayConfirmActivity.this, QuickPayValidationActivity.class);
        PaymentDetails details = new PaymentDetails();
        if (responseCode != null) {
            intent.putExtra("CODE", responseCode);
        }
        intent.putExtra("SELECTED_LOCATION_ID", mID);
        intent.putExtra("SELECTED_PARENT_LOCATION", tvCommerceName.getText().toString());
        intent.putExtra("TAX_STATUS", taxStatus);
        intent.putExtra("CURRENCY", selectedCurrency);
        Log.d("QuickPayConfirmActivity", "callPaymentValidationScreen: " + selectedCurrency);
        details.setCurrency(selectedCurrency);
        intent.putExtra("SELECTED_LOCATION", tvLocationName.getText().toString());
        intent.putExtra("SELECTED_TRN_TYPE", tvTrnType.getText().toString());
        intent.putExtra("ORDER_NO", etOrderNo.getText().toString());
        intent.putExtra("CLIENT_NAME", etCustomerName.getText().toString());
        intent.putExtra("EMAIL", etEmail.getText().toString());

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
        ((AzulApplication) ((QuickPayConfirmActivity) this).getApplication()).setLocationDataShare(locationJson);
        if (modifiedAmount > UNINITIALIZED) {
            intent.putExtra("AMOUNT", modifiedAmount);
            details.setEnteredTotalAmount(modifiedAmount);
        } else {
            intent.putExtra("AMOUNT", lastAmount);
            details.setEnteredTotalAmount(lastAmount);
        }
        details.setSelectedLocationID(mID);
        details.setSelectedLocation(tvLocationName.getText().toString());
        details.setSelectedTrnType(tvTrnType.getText().toString());
        details.setSelectedOrderNumber(etOrderNo.getText().toString());
        details.setSelectedName(etCustomerName.getText().toString());
        details.setSelectedEmail(etEmail.getText().toString());


        ((AzulApplication) ((QuickPayConfirmActivity) context).getApplication()).setDetails(details);
        startActivity(intent);
        this.overridePendingTransition(R.anim.animation_enter,
                R.anim.slide_nothing);
    }

    Dialog cancelAlertDialog;

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
            textMsg.setTypeface(typeface);
            TextView btnNo = cancelAlertDialog.findViewById(R.id.btnNo);
            TextView btnYes = cancelAlertDialog.findViewById(R.id.btnYes);
            btnYes.setTypeface(typeface);

            btnYes.setOnClickListener(view -> {

                Intent intent = new Intent(QuickPayConfirmActivity.this, QuickSetPaymentActivity.class);
                ((AzulApplication) ((QuickPayConfirmActivity) this).getApplication()).setLocationDataShare(locationJson);
                if (responseCode != null) {
                    intent.putExtra("CODE", responseCode);
                }
                startActivity(intent);
                this.overridePendingTransition(R.anim.animation_leave,
                        R.anim.slide_nothing);
                if (cancelAlertDialog != null && cancelAlertDialog.isShowing()) {
                    cancelAlertDialog.dismiss();
                }
            });
            btnNo.setOnClickListener(view -> {
                if (cancelAlertDialog != null && cancelAlertDialog.isShowing()) {
                    cancelAlertDialog.dismiss();
                }
            });
            cancelAlertDialog.show();

        } catch (
                Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    public void taxCalculations() {
        if (!etITBISAmount.getText().toString().isEmpty() && !etTotalAmount.getText().toString().isEmpty()) {
            String taxAmount = etITBISAmount.getText().toString().replace(",", "");
            String totalAmount = etTotalAmount.getText().toString().replace(",", "");
            String totalAmountNew = totalAmount.replace("RD$", "");

            double taxAmountCheck = Double.parseDouble(taxAmount);
            double totalAmountCheck = Double.parseDouble(totalAmountNew);

            if (taxAmountCheck > totalAmountCheck) {
                layoutSearchByEdittext.setBackgroundResource(R.drawable.error_background_shadow);
                tvITBISError.setVisibility(View.VISIBLE);
                tvITBISError.setText(getResources().getString(R.string.itbis_error));
                btnConfirmPayment.setBackgroundResource(R.drawable.continue_btn_error_bg);
                btnConfirmPayment.setEnabled(false);
            } else {
                btnConfirmPayment.setEnabled(true);
                btnConfirmPayment.setBackgroundResource(R.drawable.link_button_background);
                layoutSearchByEdittext.setBackgroundResource(R.drawable.active_border_background);
                tvITBISError.setVisibility(View.GONE);
            }
        }
    }

    public void setLocationData(LocationFilter locationFilter) {

        if (locationFilter != null) {
            lastLocationName = locationFilter.getLocationNameAndId();
            lastLocationMid = locationFilter.getmId();
            mID = lastLocationMid;
            tvLocationName.setText(locationFilter.getLocationName().toLowerCase());
            if (locationFilter.getParentName() != null) {
                tvCommerceName.setText(locationFilter.getParentName().toLowerCase());
            }
            taxStatus = locationFilter.getTaxExempt();
            Log.d("QuickPayConfirmActivity", "setLocationData: 111  " + selectedCurrency);
            selectedCurrency = locationFilter.getCurrency();
        } else {
            if (!TextUtils.isEmpty(previousLocationName) && !TextUtils.isEmpty(previousParentLocationName)) {
                tvLocationName.setText(previousLocationName.toLowerCase());
                tvCommerceName.setText(previousParentLocationName.toLowerCase());
            }
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
            if (!TextUtils.isEmpty(previousTaxStatus) &&
                    previousTaxStatus.equalsIgnoreCase(Constants.BOOLEAN_TRUE)) {
                layoutITBIS.setVisibility(View.VISIBLE);
                tvITBISInclude.setVisibility(View.VISIBLE);
                dynamicMargin(0);
                taxStatus = previousTaxStatus;
            } else {
                layoutITBIS.setVisibility(View.GONE);
                tvITBISInclude.setVisibility(View.GONE);
                dynamicMargin(1);
            }


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
                    etITBISAmount.setTextColor(ContextCompat.getColor(QuickPayConfirmActivity.this, R.color.font_hint));
                    searchInActive.setTextColor(ContextCompat.getColor(QuickPayConfirmActivity.this, R.color.font_hint));
                    checkCurrency();
                } else {
                    buttonOn = false;
                    switchITBIS.setImageResource(R.drawable.switch_off_state);
                    String emptyAmount = "0.00";
                    etITBISAmount.setText(emptyAmount);
                    etITBISAmount.setTextColor(Color.parseColor(Constants.LIGHT_BLUE));
                    searchInActive.setTextColor(Color.parseColor(Constants.LIGHT_BLUE));
                    layoutSearchByEdittext.setBackgroundResource(R.drawable.border_background);
                    checkCurrency();
                }
            } else {
                tvAmountError.setVisibility(View.VISIBLE);
                tvAmountError.setText(context.getString(R.string.less_amount_error));
            }
        });

    }

    public void checkCurrency() {
        if (selectedCurrency.equalsIgnoreCase("USD")) {
            searchInActive.setText(Constants.CURRENCY_FORMAT_USD);
        } else {
            searchInActive.setText(Constants.CURRENCY_FORMAT);
        }
    }

    public void setContent(String parentObjectDataName, String name, String content, String
            merchantId, int dismissFlag, LocationFilterThirdGroup locationFilterThirdGroup, String code) {
        tvLocationName.setText(name.toLowerCase());
        tvCommerceName.setText(parentObjectDataName.toLowerCase());
        mID = merchantId;
        taxStatus = locationFilterThirdGroup.getTaxExempt();
        selectedCurrency = locationFilterThirdGroup.getCurrency();
        if (dismissFlag == 1) {
            menuLocationBottomSheet.dismiss();
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
        responseCode = code;
        setTaxCurrencyHint();
    }

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
        if (aDouble <= UNINITIALIZED) {
            return aString;
        } else {
            aString = decimalFormat.format(aDouble);
        }
        return aString;
    }

    private void errorFunc() {
        layoutAmountAllComp.setBackgroundResource(R.drawable.error_background_shadow);
        tvAmountError.setText(context.getString(R.string.less_amount_error));
        btnConfirmPayment.setBackgroundResource(R.drawable.continue_btn_error_bg);
        btnConfirmPayment.setEnabled(false);
        tvAmountError.setVisibility(View.VISIBLE);
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
}