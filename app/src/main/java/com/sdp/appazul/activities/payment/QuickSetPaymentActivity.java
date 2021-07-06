package com.sdp.appazul.activities.payment;

import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sdp.appazul.R;
import com.sdp.appazul.activities.dashboard.LocationFilterBottomSheet;
import com.sdp.appazul.activities.home.BaseLoggedInActivity;
import com.sdp.appazul.activities.home.MainMenuActivity;
import com.sdp.appazul.classes.LocationFilter;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;

import java.text.DecimalFormat;

public class QuickSetPaymentActivity extends BaseLoggedInActivity {
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
    RelativeLayout layoutDeletCancel;
    RelativeLayout layoutFinalAmtInfo;
    boolean sum = false;
    StringBuilder num = new StringBuilder();
    double praviousAmount = 0;
    float amt;
    StringBuilder resultData;
    LocationFilter locationFilter = new LocationFilter();
    String lastLocationName;
    String lastLocationMid;
    String mID;
    ImageView paymentLocationSelector;
    ImageView btnBackScreen;
    LocationFilterBottomSheet bottomsheet;
    String totalAmountEntered;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_set_payment);
        intent = getIntent();
        locationJson = intent.getStringExtra(Constants.LOCATION_RESPONSE);

        initControls();
    }

    private void initControls() {
        tvAmount = findViewById(R.id.tvAmount);
        btnBackScreen = findViewById(R.id.btnBackScreen);
        layoutFinalAmtInfo = findViewById(R.id.layoutFinalAmtInfo);
        tvFinalAmount = findViewById(R.id.tvFinalAmount);
        nextIcon = findViewById(R.id.nextIcon);
        deleteFromInside = findViewById(R.id.deleteFromInside);
        cancelFromInside = findViewById(R.id.cancelFromInside);
        layoutDeletCancel = findViewById(R.id.layoutDeletCancel);
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

        locationFilter = ((AzulApplication) this.getApplication()).getLocationFilter();

        if (locationFilter != null) {
            lastLocationName = locationFilter.getLocationNameAndId();
            lastLocationMid = locationFilter.getmId();
            mID = lastLocationMid;
        }


        tvDelete.setOnClickListener(tvDeleteView -> {
            tvSeventhNo.setVisibility(View.GONE);
            tvDelete.setVisibility(View.GONE);
            layoutDeletCancel.setVisibility(View.VISIBLE);
        });
        btnBackScreen.setOnClickListener(btnBackScreenView -> {
            Intent intent = new Intent(QuickSetPaymentActivity.this, MainMenuActivity.class);
            intent.putExtra(Constants.LOCATION_RESPONSE, locationJson);
            startActivity(intent);
        });
        closeDeleteLayout.setOnClickListener(closeDeleteLayoutView -> {
            tvSeventhNo.setVisibility(View.VISIBLE);
            tvDelete.setVisibility(View.VISIBLE);
            layoutDeletCancel.setVisibility(View.GONE);
        });

        cancelFromInside.setOnClickListener(cancelFromInsideView ->

                callAllDelete());
        deleteFromInside.setOnClickListener(deleteFromInsideView ->

                callSingleDelete());

        tvAddAndEquals.setOnClickListener(deleteFromInsideView -> {
            sum = true;
            if (num != null && num.length() > 0) {
                praviousAmount = praviousAmount + Double.parseDouble(num.toString());
                num.setLength(0);
                tvAmount.setText(Constants.CURRANCY);
                tvFinalAmount.setText(Constants.COBRAR);
                layoutFinalAmtInfo.setBackgroundResource(R.drawable.blue_background_textview);
                resultData = new StringBuilder();
                resultData.append(praviousAmount);
                addNumbers(resultData);
            }
        });

        tvFinalAmount.setOnClickListener(tvFinalAmountView -> {
            Intent intent = new Intent(QuickSetPaymentActivity.this, QuickPayConfirmActivity.class);
            if (Boolean.FALSE.equals(sum)) {
                intent.putExtra(Constants.TOTAL_AMOUNT, num.toString());
            } else {
                intent.putExtra(Constants.TOTAL_AMOUNT, resultData.toString());
            }
            intent.putExtra(Constants.LOCATION_RESPONSE, locationJson);
            startActivity(intent);
        });
        callNumPadListeners();
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

    private void callAllDelete() {

        if (num.length() <= 0) {
            tvAmount.setText(Constants.CURRANCY);
            tvFinalAmount.setText(Constants.COBRAR);
            layoutFinalAmtInfo.setBackgroundResource(R.drawable.blue_background_textview);
            tvSeventhNo.setVisibility(View.VISIBLE);
            tvDelete.setVisibility(View.VISIBLE);
            layoutDeletCancel.setVisibility(View.GONE);
            resultData = new StringBuilder();
            praviousAmount = 0;
        } else {
            resultData = new StringBuilder();
            praviousAmount = 0;
            num.setLength(0);
            tvFinalAmount.setText(Constants.COBRAR);
            tvAmount.setText(Constants.CURRANCY);
            layoutFinalAmtInfo.setBackgroundResource(R.drawable.blue_background_textview);
        }
    }

    private void callSingleDelete() {
        if (num.length() > 0) {
            num.deleteCharAt(num.length() - 1);
            addNumbers(num);
        } else {
            tvAmount.setText(Constants.CURRANCY);
            tvFinalAmount.setText(Constants.COBRAR);
            layoutFinalAmtInfo.setBackgroundResource(R.drawable.blue_background_textview);
            tvSeventhNo.setVisibility(View.VISIBLE);
            tvDelete.setVisibility(View.VISIBLE);
            layoutDeletCancel.setVisibility(View.GONE);
        }
    }

    private void callNumPadListeners() {

        tvFirstNo.setOnClickListener(tv1View -> {
            num.append(1);
            addNumbers(num);
        });
        tvSecondNo.setOnClickListener(tv1View -> {
            num.append(2);
            addNumbers(num);
        });
        tvThirdNo.setOnClickListener(tv1View -> {
            num.append(3);
            addNumbers(num);
        });
        tvFourthNo.setOnClickListener(tv1View -> {
            num.append(4);
            addNumbers(num);
        });
        tvFifthNo.setOnClickListener(tv1View -> {
            num.append(5);
            addNumbers(num);
        });
        tvSixthNo.setOnClickListener(tv1View -> {
            num.append(6);
            addNumbers(num);
        });
        tvSeventhNo.setOnClickListener(tv1View -> {
            num.append(7);
            addNumbers(num);
        });
        tvEightNo.setOnClickListener(tv1View -> {
            num.append(8);
            addNumbers(num);
        });
        tvNine.setOnClickListener(tv1View -> {
            num.append(9);
            addNumbers(num);
        });
        tvZero.setOnClickListener(tv1View -> {
            num.append(0);
            addNumbers(num);
        });
    }

    DecimalFormat currFormat = new DecimalFormat("#,##0.00");

    @SuppressLint("ResourceType")
    private void addNumbers(StringBuilder num) {
        if (num != null && num.length() > 0) {
            amt = Float.parseFloat(num.toString());
            float newAmt = amt / 100;
            tvAmount.setText("RD$ " + currFormat.format(newAmt));
            tvFinalAmount.setText("Cobrar RD$ " + currFormat.format(newAmt));

            if (tvFinalAmount.getText().toString().length() > 0) {
                layoutFinalAmtInfo.setBackgroundResource(R.drawable.amount_text_box);
                nextIcon.setVisibility(View.VISIBLE);
                tvFinalAmount.setTextColor(ContextCompat.getColor(QuickSetPaymentActivity.this, R.color.white));
            } else {
                layoutFinalAmtInfo.setBackgroundResource(R.drawable.blue_background_textview);
                nextIcon.setVisibility(View.GONE);
                tvFinalAmount.setText(Constants.COBRAR);
            }
        }

    }



    public void setContent(String content, String merchantId, int dimissFlag) {
        mID = merchantId;
        if (dimissFlag == 1) {
            bottomsheet.dismiss();
        }

        if (!TextUtils.isEmpty(content)){
            Log.d("Data","");
        }
    }


}