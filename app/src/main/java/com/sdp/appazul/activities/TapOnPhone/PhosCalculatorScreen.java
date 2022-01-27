package com.sdp.appazul.activities.TapOnPhone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.sdp.appazul.R;
import com.sdp.appazul.activities.home.BaseLoggedInActivity;
import com.sdp.appazul.activities.home.MainMenuActivity;
import com.sdp.appazul.activities.home.MenuLocationFilter;
import com.sdp.appazul.activities.payment.PaymentConfirmActivity;
import com.sdp.appazul.classes.CardLessPayment;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import digital.paynetics.phos.PhosSdk;
import digital.paynetics.phos.exceptions.PhosException;
import digital.paynetics.phos.sdk.callback.AuthCallback;
import digital.paynetics.phos.sdk.callback.InitCallback;
import digital.paynetics.phos.sdk.callback.TransactionCallback;
import digital.paynetics.phos.sdk.callback.TransactionListCallback;
import digital.paynetics.phos.sdk.entities.Transaction;
import digital.paynetics.phos.sdk.entities.Transactions;


public class PhosCalculatorScreen extends BaseLoggedInActivity {

    CardView sdkLocationFilterLayout;
    TextView tvVisibleAmount;
    TextView tvTotalAmountSdkTitle;
    AppCompatButton btnConfirmAmount;
    TextView tvFirstNo;
    TextView tvSecondNo;
    TextView tvThirdNo;
    TextView tvFourthNo;
    ImageView btnBackScreen;
    TextView tvFifthNo;
    TextView tvSixthNo;
    TextView tvSeventhNo;
    TextView tvEightNo;
    TextView tvNine;
    TextView tvZero;
    TextView tvFinalAmount;
    ImageView tvDelete;
    ImageView tvAddAndEquals;
    RelativeLayout layoutFinalAmtInfo;
    RelativeLayout layoutDeleteCancel;
    RelativeLayout AmountTab;
    static int SNACK_LENGTH = 0;
    int amountCheck = 0;
    ImageView closeDeleteLayout;
    int previousAmount = 0;
    RelativeLayout activity_phos_calc;
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
    DecimalFormat currFormat = new DecimalFormat("#,##0.00", symbols);
    double lastAmount = 0;
    StringBuilder num = new StringBuilder();
    ImageView deleteFromInside;
    ImageView cancelFromInside;
    StringBuilder resultData;
    int tempAmount = 0;
    boolean sum = false;
    LinearLayout numPad;
    ImageView nextIcon;
    boolean opened = false;
    String locationJson;
    Context context;
    MenuLocationFilter menuLocationBottomSheet;
    TextView tvSelectedPaymentLocation;
    LocationFilter locationFilter = new LocationFilter();
    int resultDataCheck;

    Map<String, String> defaultLocation;
    String responseCode;
    Map<String, String> defaultLocations = new HashMap<>();
    String TAG = "PHOS_SDK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phos_calculator_screen);
        context = this;
        initControls();
        locationJson = ((AzulApplication) context.getApplicationContext()).getLocationDataShare();
        locationFilter = ((AzulApplication) this.getApplication()).getLocationFilter();

        CardLessPayment cardLessPaymentDetails = ((AzulApplication) this.getApplication()).getCardLessPayment();

        if (locationFilter != null) {
            setPreviousLocationData(locationFilter);
        } else {
            getDefaultLocation(locationJson);
            defaultLocations = ((AzulApplication) this.getApplication()).getDefaultLocation();
            if (defaultLocations != null) {
                String defaultLocationName = defaultLocations.get("CHILD_LOC_NAME");
                String defaultLocationId = defaultLocations.get("CHILD_LOC_ID");
                String locationToDisplay = defaultLocationName + " - " + defaultLocationId;
                tvSelectedPaymentLocation.setText(locationToDisplay);
            }
        }
        locationFilterImpl();
        getLastEnteredAmount(cardLessPaymentDetails);
    }

    private void getLastEnteredAmount(CardLessPayment cardLessPaymentDetails) {

        if (cardLessPaymentDetails != null && cardLessPaymentDetails.getAmountEntered() != null) {
            num.append(cardLessPaymentDetails.getAmountEntered());
            addNumbers(num);
        }
    }

    private void setPreviousLocationData(LocationFilter locationFilter) {
        if (locationFilter.getParentName() != null) {
            tvSelectedPaymentLocation.setText(locationFilter.getLocationNameAndId());
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
                    ((AzulApplication) ((PhosCalculatorScreen) context).getApplication()).setLocationFilter(locationFilterObj);

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void locationFilterImpl() {
        sdkLocationFilterLayout.setOnClickListener(sdkLocationFilterLayoutView -> {
            menuLocationBottomSheet = new MenuLocationFilter(locationJson, "PHOS_CALCULATOR", 3);
            menuLocationBottomSheet.show(getSupportFragmentManager(), "bottomSheet");
        });
    }

    private void initControls() {
        sdkLocationFilterLayout = findViewById(R.id.sdkLocationFilterLayout);
        tvVisibleAmount = findViewById(R.id.tvVisibleAmount);
        tvTotalAmountSdkTitle = findViewById(R.id.tvTotalAmountSdkTitle);
        btnBackScreen = findViewById(R.id.btnBackScreen);
        btnConfirmAmount = findViewById(R.id.btnConfirmAmount);
        AmountTab = findViewById(R.id.AmountTab);
        nextIcon = findViewById(R.id.nextIcon);
        layoutFinalAmtInfo = findViewById(R.id.layoutFinalAmtInfo);
        numPad = findViewById(R.id.numPad);
        layoutDeleteCancel = findViewById(R.id.layoutDeletCancel);
        activity_phos_calc = findViewById(R.id.activity_phos_calc);
        tvSelectedPaymentLocation = findViewById(R.id.tvSelectedPaymentLocation);
        tvZero = findViewById(R.id.tvZero);
        tvFirstNo = findViewById(R.id.tvFirstNo);
        tvSecondNo = findViewById(R.id.tvSecondNo);
        closeDeleteLayout = findViewById(R.id.closeDeleteLayout);
        tvFinalAmount = findViewById(R.id.tvFinalAmount);
        tvThirdNo = findViewById(R.id.tvThirdNo);
        tvFourthNo = findViewById(R.id.tvFourthNo);
        tvFifthNo = findViewById(R.id.tvFifthNo);
        tvSixthNo = findViewById(R.id.tvSixthNo);
        tvSeventhNo = findViewById(R.id.tvSeventhNo);
        tvEightNo = findViewById(R.id.tvEightNo);
        tvNine = findViewById(R.id.tvNine);
        tvDelete = findViewById(R.id.tvDelete);
        tvAddAndEquals = findViewById(R.id.tvAddAndEquals);
        deleteFromInside = findViewById(R.id.deleteFromInside);
        cancelFromInside = findViewById(R.id.cancelFromInside);

        callNumPadListeners();
        tvDelete.setOnClickListener(tvDeleteView -> {
            tvFourthNo.setVisibility(View.GONE);
            tvSeventhNo.setVisibility(View.GONE);
            tvDelete.setVisibility(View.GONE);
            layoutDeleteCancel.setVisibility(View.VISIBLE);
        });
        closeDeleteLayout.setOnClickListener(closeDeleteLayoutView -> {
            tvFourthNo.setVisibility(View.VISIBLE);
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
                    Log.d("TAG", "Check ");
                } else {
                    tempAmount = previousAmount;
                }
                checkMaxLogic(num, resultData);
            }
        });
        AmountTab.setOnClickListener(AmountTabView -> {
            if (Boolean.FALSE.equals(opened)) {
                numPad.setVisibility(View.VISIBLE);
                TranslateAnimation animate = new TranslateAnimation(
                        0,
                        0,
                        numPad.getHeight(),
                        0);
                animate.setDuration(700);
                animate.setFillAfter(true);
                numPad.startAnimation(animate);
                opened = true;
                nextIcon.clearAnimation();
                nextIcon.setVisibility(View.GONE);
                layoutFinalAmtInfo.setVisibility(View.GONE);
            }
        });

        btnBackScreen.setOnClickListener(btnBackScreenView -> {


            Intent intent = new Intent(PhosCalculatorScreen.this, MainMenuActivity.class);
            overridePendingTransition(0, 1);
            startActivity(intent);
            CardLessPayment payment = new CardLessPayment();
            ((AzulApplication) ((PhosCalculatorScreen) context).getApplication()).setCardLessPayment(payment);

            this.overridePendingTransition(R.anim.animation_enter,
                    R.anim.slide_nothing);
        });

        btnConfirmAmount.setOnClickListener(btnConfirmAmountView -> {
            if (!TextUtils.isEmpty(tvVisibleAmount.getText().toString()) &&
                    !tvVisibleAmount.getText().toString().equalsIgnoreCase(Constants.CURRENCY)) {


                numPad.setVisibility(View.INVISIBLE);
                TranslateAnimation animate = new TranslateAnimation(
                        0,
                        0,
                        0,
                        numPad.getHeight());
                animate.setDuration(700);
                animate.setFillAfter(true);
                numPad.startAnimation(animate);
                tvFinalAmount.setText(Constants.COBRAR + " " + tvVisibleAmount.getText().toString());
                nextIcon.setVisibility(View.VISIBLE);
                makeMeShake(nextIcon, 8);
                opened = false;
                layoutFinalAmtInfo.setVisibility(View.VISIBLE);
            } else {
                customSnackBar(this.getResources().getString(R.string.enter_amount_message));
            }
        });

        layoutFinalAmtInfo.setOnClickListener(layoutFinalAmtInfoView -> {

            String totalAmount = tvFinalAmount.getText().toString().replace(",", "");
            String totalAmountNew = totalAmount.replace("Cobrar RD$ ", "");

            double amountToShare = Double.parseDouble(totalAmountNew);
            CallMakeSaleWithAMount(amountToShare);
        });
    }

    private void CallMakeSaleWithAMount(double amountToShare) {
        PhosSdk.getInstance().makeSaleWithAmount(PhosCalculatorScreen.this, amountToShare, false, new TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction, @Nullable @org.jetbrains.annotations.Nullable Map<String, String> map) {
                callTransactionReceiptScreen(transaction);
            }

            @Override
            public void onFailure(@Nullable @org.jetbrains.annotations.Nullable Transaction transaction, PhosException e, @Nullable @org.jetbrains.annotations.Nullable Map<String, String> map) {
                callDeclinedScreen(transaction);
            }
        });
    }

    private void callDeclinedScreen(Transaction transaction) {
        ((AzulApplication) ((PhosCalculatorScreen) this).getApplication()).setDeclinedTransaction(transaction);
        Intent intent = new Intent(PhosCalculatorScreen.this, DeclinedTransactionActivity.class);
        startActivity(intent);
        this.overridePendingTransition(R.anim.animation_enter,
                R.anim.slide_nothing);
    }

    private void callTransactionReceiptScreen(Transaction transaction) {
        ((AzulApplication) ((PhosCalculatorScreen) this).getApplication()).setTransaction(transaction);
        Intent intent = new Intent(PhosCalculatorScreen.this, TransactionResponseActivity.class);
        startActivity(intent);
        this.overridePendingTransition(R.anim.animation_enter,
                R.anim.slide_nothing);
    }

    public void customSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(activity_phos_calc, message,
                SNACK_LENGTH).setBackgroundTint(Color.parseColor(Constants.ORANGE));
        View snackbarLayout = snackbar.getView();
        TextView textView = (TextView) snackbarLayout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phos_alert_outline_white, 0, 0, 0);
        textView.setCompoundDrawablePadding(35);
        snackbar.show();
    }

    public static void makeMeShake(View view, int offset) {
        Animation anim = new TranslateAnimation(-offset, offset, 0, 0);
        anim.setDuration(600);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        view.startAnimation(anim);
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
            customSnackBar(this.getResources().getString(R.string.calculator_limit_label));
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
            customSnackBar(this.getResources().getString(R.string.calculator_limit_label));

        }
    }
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    private void checkNewExtraAmount(StringBuilder previousResultData) {
        sum = true;
        if (num != null && num.length() > 0) {
            num.setLength(0);
            btnConfirmAmount.setBackgroundResource(R.drawable.link_button_background);
            addNumbers(previousResultData);
        }
    }

    private void checkExtraAmount() {
        sum = true;
        if (num != null && num.length() > 0) {
            previousAmount = previousAmount + Integer.parseInt(num.toString());
            num.setLength(0);
            btnConfirmAmount.setBackgroundResource(R.drawable.link_button_background);
            resultData = new StringBuilder();
            resultData.append(previousAmount);
            addNumbers(resultData);
        }
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
        tvVisibleAmount.setText(Constants.CURRENCY);
        tvFourthNo.setVisibility(View.VISIBLE);
        btnConfirmAmount.setBackgroundResource(R.drawable.textview_show_amount_bg);
        tvSeventhNo.setVisibility(View.VISIBLE);
        tvDelete.setVisibility(View.VISIBLE);
        layoutDeleteCancel.setVisibility(View.GONE);
    }

    private void callAllDelete() {
        if (num.length() <= 0) {

            btnConfirmAmount.setBackgroundResource(R.drawable.textview_show_amount_bg);
            tvVisibleAmount.setText(Constants.CURRENCY);
            tvFourthNo.setVisibility(View.VISIBLE);
            tvSeventhNo.setVisibility(View.VISIBLE);
            tvDelete.setVisibility(View.VISIBLE);
            layoutDeleteCancel.setVisibility(View.GONE);
            resultData = new StringBuilder();
            previousAmount = 0;
            tempAmount = 0;
            num.setLength(0);
        } else {
            tvVisibleAmount.setText(Constants.CURRENCY);
            previousAmount = 0;
            btnConfirmAmount.setBackgroundResource(R.drawable.textview_show_amount_bg);
            tempAmount = 0;
            num.setLength(0);
            tvSeventhNo.setVisibility(View.VISIBLE);
            tvFourthNo.setVisibility(View.VISIBLE);
            tvDelete.setVisibility(View.VISIBLE);
            layoutDeleteCancel.setVisibility(View.GONE);
            resultData = new StringBuilder();
        }
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


    @SuppressLint("ResourceType")
    private void addNumbers(StringBuilder num) {
        if (num != null && num.length() > 0) {

            int parsedAmount = Integer.parseInt(num.toString());
            if (parsedAmount > 999999999) {
                previousAmount = 0;

                Snackbar snackbar = Snackbar.make(activity_phos_calc, this.getResources().getString(R.string.calculator_limit_label), SNACK_LENGTH)
                        .setBackgroundTint(Color.parseColor(Constants.COLOR_SKY));
                snackbar.show();
            } else {

                lastAmount = Double.parseDouble(new DecimalFormat("#").format(Double.parseDouble(num.toString())));
                double totalEnteredAmount = lastAmount / 100;

                setAmount(totalEnteredAmount);
                String finalAmountToShow;
                finalAmountToShow = Constants.CURRENCY_FORMAT + currFormat.format(totalEnteredAmount);

                tvVisibleAmount.setText(finalAmountToShow);
                if (tvVisibleAmount.getText().toString().length() > 0) {
                    btnConfirmAmount.setBackgroundResource(R.drawable.link_button_background);
                    tvVisibleAmount.setTextColor(ContextCompat.getColor(PhosCalculatorScreen.this, R.color.white));
                } else {
                    checkCurr();
                }

            }
        }
    }

    private void checkCurr() {
        if (!tvVisibleAmount.getText().toString().equalsIgnoreCase(Constants.CURRENCY)) {
            btnConfirmAmount.setBackgroundResource(R.drawable.textview_show_amount_bg);
            tvVisibleAmount.setText(Constants.CURRENCY);
        } else {
            tvVisibleAmount.setText(Constants.CURRENCY);
        }
    }


    private void setAmount(double totalEnteredAmount) {
        String amountToDisplay;
        amountToDisplay = Constants.CURRENCY_FORMAT + currFormat.format(totalEnteredAmount);

        tvVisibleAmount.setText(amountToDisplay);
    }

    public void manageDeleteMenu() {
        if (layoutDeleteCancel.getVisibility() == View.VISIBLE) {
            layoutDeleteCancel.setVisibility(View.GONE);
            tvFourthNo.setVisibility(View.VISIBLE);
            tvSeventhNo.setVisibility(View.VISIBLE);
            tvDelete.setVisibility(View.VISIBLE);
        }
    }

    public void setContent(String name, String name1, String s, String merchantId, int dismissFlag, LocationFilterThirdGroup locationFilterThirdGroup, String code) {
        if (dismissFlag == 1) {
            menuLocationBottomSheet.dismiss();
        }
        tvSelectedPaymentLocation.setText(s);
    }


    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = manager.getActiveNetworkInfo();
            doSomethingOnNetworkChange(ni, intent);
        }
    };

    private void doSomethingOnNetworkChange(NetworkInfo ni, Intent intent) {
        if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
            if (cancelAlertDialog != null && cancelAlertDialog.isShowing()) {
                cancelAlertDialog.dismiss();
            }
        } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
            cancelAlert(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkStateReceiver);
        super.onPause();
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
            LinearLayout buttonsLayout = cancelAlertDialog.findViewById(R.id.buttonsLayout);
            Typeface typeface;
            typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Medium.ttf");
            textTitle.setTypeface(typeface);
            textTitle.setText(getResources().getString(R.string.Network_lbl));
            textMsg.setTypeface(typeface);
            textMsg.setText(getResources().getString(R.string.internet_connection_status));
            TextView btnNo = cancelAlertDialog.findViewById(R.id.btnNo);
            TextView btnYesHide = cancelAlertDialog.findViewById(R.id.btnYesHide);
            btnYesHide.setTypeface(typeface);
            btnNo.setVisibility(View.GONE);
            buttonsLayout.setVisibility(View.GONE);
            btnYesHide.setVisibility(View.VISIBLE);
            btnYesHide.setOnClickListener(btnYesHideView -> {
                callBackScreen();
            });

            cancelAlertDialog.show();

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void callBackScreen() {
        Intent setIntent;
        setIntent = new Intent(this, MainMenuActivity.class);
        startActivity(setIntent);
        this.overridePendingTransition(R.anim.animation_leave,
                R.anim.slide_nothing);
    }
}