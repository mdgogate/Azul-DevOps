package com.azul.app.android.activities.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.azul.app.android.R;
import com.azul.app.android.activities.home.BaseLoggedInActivity;
import com.azul.app.android.activities.transactions.QrTransactions;
import com.azul.app.android.adapters.TransactionHistoryAdapter;
import com.azul.app.android.adapters.WidgetTransactionHAdapter;
import com.azul.app.android.api.ApiManager;
import com.azul.app.android.api.ServiceUrls;
import com.azul.app.android.classes.TransactionHistory;
import com.azul.app.android.globals.AzulApplication;
import com.azul.app.android.globals.Constants;
import com.azul.app.android.globals.KeyConstants;
import com.azul.app.android.security.RSAHelper;
import com.azul.app.android.utils.DateUtils;
import com.azul.app.android.utils.DeviceUtils;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;

public class QrCode extends BaseLoggedInActivity {
    ViewPager viewPagerTwo;
    TabLayout circleIndicatorTwo;
    TextView showQr;
    TextView toolbarTextTitleTwo;
    TextView swipeLeft;
    GregorianCalendar gregorianCalendar;
    RelativeLayout layoutTextSearch;
    TextView clearTextView;
    ImageView searchinActive;
    EditText etSearchBy;
    WidgetTransactionHAdapter historyAdapter;
    List<TransactionHistory> list;
    List<TransactionHistory> transactionFilteredList;
    ListView listTransactions;
    ArrayList<TransactionHistory> filterarrayList = new ArrayList<>();
    List<String> monthList = new ArrayList<>();
    GregorianCalendar month;
    DateUtils utils = new DateUtils();
    GregorianCalendar selectedFinalDate;
    CardView cardDatePicker;
    LinearLayout layoutDateRangeFilter;
    TextView tvFromActivity;
    TextView tvToActivity;
    TextView tvDateFilter;
    TextView tvAmount;
    TextView tvReferenceNo;
    ImageView imgClearRangeFilter;
    Boolean tvAmountClick = false;
    Boolean tvRefernceClick = false;
    TextView tvNoRecordFound;
    ImageView btnTransactionBack;
    ApiManager apiManager = new ApiManager(QrCode.this);
    ImageView imgViewQrCode;
    TextView qrTitle;
    TextView qrMerchantId;
    TextView qrTId;
    List<TransactionHistory> temp;
    RelativeLayout layoutAmountandRefFilter;
    ImageView locationSelector;
    String locationJson;
    TextView selectedLocationName;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initControls();
        pageListner();

    }

    private void initControls() {
        selectedLocationName = findViewById(R.id.selectedLocationName);
        viewPagerTwo = findViewById(R.id.viewPagerTwo);
        showQr = findViewById(R.id.showQr);
        toolbarTextTitleTwo = findViewById(R.id.toolbarTextTitleTwo);
        swipeLeft = findViewById(R.id.swipeLeft);
        btnTransactionBack = findViewById(R.id.btnTransactionBack);
        sharedPreferences = ((AzulApplication) this.getApplication()).getPrefs();
        String lastLocationNamewithId = sharedPreferences.getString(Constants.SELECTED_LOCATION_QRCODE, "");
        String lastLocationName = sharedPreferences.getString(Constants.SELECTED_LOCATION_NAME_QRCODE, "");
        String lastLocationMid = sharedPreferences.getString(Constants.SELECTED_LOCATION_MID_QRCODE, "");
        selectedLocationName.setText(lastLocationNamewithId);
        mID = lastLocationMid;
        locName = lastLocationName;
        callGetQrApi("24000000007");

        Intent intent = getIntent();
        locationJson = intent.getStringExtra("LOCATION_RESPONSE");
        burgerMenu();

        btnTransactionBack.setOnClickListener(view -> {
            finish();

            Intent intent1 = new Intent(QrCode.this, DashBoardActivity.class);
            intent1.putExtra("LOCATION_RESPONSE", locationJson);
            startActivityForResult(intent1, 1);
        });

    }


    private void pageListner() {
        viewPagerTwo.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("TAG", "scroll");
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    showQr.setVisibility(View.VISIBLE);
                    toolbarTextTitleTwo.setText(R.string.qr_code);
                    swipeLeft.setText(R.string.swipe_left);
                } else {
                    showQr.setVisibility(View.GONE);
                    toolbarTextTitleTwo.setText(R.string.sales_of_day);
                    swipeLeft.setText(R.string.swipe_right);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("TAG", "scroll");
            }
        });
    }

    public class ViewPagerAdapterTwo extends PagerAdapter {
        int[] layouts = {R.layout.qrcode_widget_layout, R.layout.transaction_qrcode_widget_layout};
        LayoutInflater layoutInflater;
        Context context;
        String imgData;
        String mID;
        String selectedlocName;

        public ViewPagerAdapterTwo(QrCode qrCode, String responseData, String merchID, String locName) {
            context = qrCode;
            imgData = responseData;
            mID = merchID;
            selectedlocName = locName;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, @NotNull Object object) {
            return view.equals(object);
        }

        @NotNull
        @Override
        public Object instantiateItem(@NotNull ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View qrView = layoutInflater.inflate(R.layout.qrcode_widget_layout, container, false);
            View qrtranView = layoutInflater.inflate(R.layout.transaction_qrcode_widget_layout, container, false);
            View[] viewArr = {qrView, qrtranView};
            container.addView(viewArr[position]);
            qrSetView(qrView);
            secondView(qrtranView);
            return viewArr[position];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, @NotNull Object object) {
            container.removeView((RelativeLayout) object);
        }

        private void qrSetView(View qrView) {
            imgViewQrCode = qrView.findViewById(R.id.imgViewQrCode);
            qrTitle = qrView.findViewById(R.id.qrTitle);
            qrMerchantId = qrView.findViewById(R.id.qrMerchantId);
            qrTId = qrView.findViewById(R.id.qrTId);

            if (!TextUtils.isEmpty(selectedlocName)) {
                qrTitle.setText(selectedlocName);
            }
            qrMerchantId.setText("MID: " + mID);

            SVG svg = null;
            try {
                if (!TextUtils.isEmpty(imgData)) {
                    svg = SVG.getFromString(imgData);
                    PictureDrawable pd = new PictureDrawable(svg.renderToPicture());
                    imgViewQrCode.setImageDrawable(pd);
                }

            } catch (
                    SVGParseException e) {
                e.printStackTrace();
            }
        }


        private void secondView(View view) {
            etSearchBy = view.findViewById(R.id.etSearchBy);
            layoutTextSearch = view.findViewById(R.id.layoutTextSearch);
            clearTextView = view.findViewById(R.id.clearTextView);
            searchinActive = view.findViewById(R.id.searchinActive);
            listTransactions = view.findViewById(R.id.listTransactions);
            cardDatePicker = view.findViewById(R.id.cardDatePicker);
            layoutDateRangeFilter = view.findViewById(R.id.layoutDateRangeFilter);
            tvFromActivity = view.findViewById(R.id.tvFromActivity);
            tvToActivity = view.findViewById(R.id.tvToActivity);
            imgClearRangeFilter = view.findViewById(R.id.imgClearRangeFilter);
            tvDateFilter = view.findViewById(R.id.tvDateFilter);
            layoutAmountandRefFilter = view.findViewById(R.id.layoutAmountandRefFilter);
            tvAmount = view.findViewById(R.id.tvAmount);
            tvReferenceNo = view.findViewById(R.id.tvReferenceNo);
            tvNoRecordFound = view.findViewById(R.id.tvNoRecordFound);
            getTransactionListData();
            etSearchBy.setOnClickListener(etSearchByview ->
                    hidShow());

            etSearchBy.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    Log.d("TAG", "");
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (Boolean.TRUE.equals(tvAmountClick)) {
                        filterListByAmt(charSequence, true);
                    } else {
                        filterList(charSequence, false);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    Log.d("TAG", "");
                }
            });
            clearTextView.setOnClickListener(clearTextView -> {
                searchinActive.setTag("bg");
                searchinActive.setImageResource(R.drawable.search_in_active);
                etSearchBy.setText("");
                if (clearTextView.getVisibility() == View.VISIBLE) {
                    clearTextView.setVisibility(View.GONE);
                }
                if (layoutAmountandRefFilter.getVisibility() == View.VISIBLE) {
                    layoutAmountandRefFilter.setVisibility(View.GONE);
                }
            });


            tvReferenceNo.setOnClickListener(tvReferenceNoview -> {

                tvReferenceNo.setBackgroundResource(R.drawable.middle_navigation_bg);
                tvAmount.setBackgroundResource(0);
                tvAmount.setTextColor(ContextCompat.getColor(context, R.color.slider_heading));
                tvReferenceNo.setTextColor(ContextCompat.getColor(context, R.color.blue_3));
                tvAmountClick = false;
                tvRefernceClick = true;
                etSearchBy.setText("");
            });

            tvAmount.setOnClickListener(tvReferenceNoview -> {
                tvAmount.setBackgroundResource(R.drawable.middle_navigation_bg);
                tvReferenceNo.setBackgroundResource(0);
                tvReferenceNo.setTextColor(ContextCompat.getColor(context, R.color.slider_heading));
                tvAmount.setTextColor(ContextCompat.getColor(context, R.color.blue_3));
                tvAmountClick = true;
                tvRefernceClick = false;
                etSearchBy.setText("");
            });
        }

        private void getTransactionListData() {
            gregorianCalendar = (GregorianCalendar) getInstance();
            monthList = utils.getmonthList();
            month = (GregorianCalendar) gregorianCalendar.clone();
            String spanishmonthname = monthList
                    .get(Integer.parseInt((String) DateFormat.format(Constants.MM, month)) - 1);
            String date = "" + gregorianCalendar.get(DAY_OF_MONTH);
            String initialDate = "" + gregorianCalendar.get(DAY_OF_MONTH);
            String initialMonth = "" + (gregorianCalendar.get(MONTH) + 1);
            String currDate = gregorianCalendar.get(YEAR) + "/" +
                    initialMonth + "/"
                    + initialDate;

            callQRTransaction(currDate, currDate);
            tvDateFilter.setText(date + " de " + spanishmonthname);
        }

        private void callQRTransaction(String fromDateString, String toDateString) {
            JSONObject json = new JSONObject();
            try {
                String tcpKey = ((AzulApplication) QrCode.this.getApplication()).getTcpKey();
                json.put("tcp", RSAHelper.ecryptRSA(QrCode.this, tcpKey));
                JSONObject payload = new JSONObject();
                payload.put("device", DeviceUtils.getDeviceId(QrCode.this));
                JSONObject pagosrequest = new JSONObject();
                pagosrequest.put("dateFrom", fromDateString);
                pagosrequest.put("dateTo", toDateString);

                payload.put("obtenerrequest", pagosrequest);
                json.put("payload", RSAHelper.encryptAES(payload.toString(), Base64.decode(tcpKey, 0)));
            } catch (Exception e) {
                Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
            }
            apiManager.callAPI(ServiceUrls.QR_TRANSACTION, json);
        }

        private void hidShow() {
            String backgroundImageName = String.valueOf(searchinActive.getTag());

            if (backgroundImageName.equalsIgnoreCase("bg")) {
                searchinActive.setTag(R.drawable.ic_search_active);
                searchinActive.setImageResource(R.drawable.ic_search_active);
            }

            if (clearTextView.getVisibility() == View.GONE) {
                clearTextView.setVisibility(View.VISIBLE);
            }

            if (layoutAmountandRefFilter.getVisibility() == View.GONE) {
                layoutAmountandRefFilter.setVisibility(View.VISIBLE);
            }
        }

    }

    List<TransactionHistory> loList;

    void filterList(CharSequence text, boolean type) {
        if (!filterarrayList.isEmpty()) {
            serachInList(filterarrayList, text, type);
        } else if (loList != null && loList.size() > 0) {
            serachInList(loList, text, type);
        } else {
            serachInList(list, text, type);
        }
    }

    void filterListByAmt(CharSequence text, boolean type) {
        if (!filterarrayList.isEmpty()) {
            serachInList(filterarrayList, text, type);
        } else if (loList != null && loList.size() > 0) {
            serachInList(loList, text, type);
        } else {
            serachInList(list, text, type);
        }
    }

    public void serachInList(List<TransactionHistory> mainlist, CharSequence text, Boolean type) {
        temp = new ArrayList<>();
        for (TransactionHistory tr : mainlist) {
            if (Boolean.TRUE.equals(type)) {
                if (tr.getAmount().trim().contains(text)) {
                    temp.add(tr);
                }
            } else {
                if (tr.getReferenceNo().trim().contains(text)) {
                    temp.add(tr);
                }
            }
        }
        if (!temp.isEmpty()) {

            listTransactions.setVisibility(View.VISIBLE);
            tvNoRecordFound.setVisibility(View.GONE);
            historyAdapter.updateList(temp);
        } else {
            listTransactions.setVisibility(View.GONE);
            tvNoRecordFound.setVisibility(View.VISIBLE);
        }

    }


    public void qrTransactionResponse(String responseData) {
        try {
            JSONObject jsonObject = new JSONObject(responseData);
            parseTransactionData(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseTransactionData(JSONObject jsonObject) {
        loList = new ArrayList<>();
        list = new ArrayList<>();
        transactionFilteredList = new ArrayList<>();
        try {
            JSONObject jsonTransactionOb = jsonObject.getJSONObject("qrtransactions");
            JSONArray jsonArray = jsonTransactionOb.getJSONArray("Transactions");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonTrObject = jsonArray.getJSONObject(i);
                String amount = jsonTrObject.getString("Amount");
                String authCode = jsonTrObject.getString("AuthorizationCode");
                String referenceNo = jsonTrObject.getString("TransactionNumber");
                String trTime = jsonTrObject.getString("TransactionTime");
                String merchantId = jsonTrObject.getString("MerchantId");
                String location = jsonTrObject.getString("MerchantName");
                list.add(new TransactionHistory(authCode, referenceNo, location, trTime, amount, merchantId, tvDateFilter.getText().toString()));
            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }

        if (!TextUtils.isEmpty(mID)) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getMerchantId().equalsIgnoreCase(mID)) {
                    loList.add(list.get(i));
                }
            }
            if (loList.size() > 0) {
                historyAdapter = new WidgetTransactionHAdapter(QrCode.this, loList);
                listTransactions.setAdapter(historyAdapter);
                listTransactions.setVisibility(View.VISIBLE);
                tvNoRecordFound.setVisibility(View.GONE);
            }else {
                listTransactions.setVisibility(View.GONE);
                tvNoRecordFound.setVisibility(View.VISIBLE);
            }
        } else {

            historyAdapter = new WidgetTransactionHAdapter(QrCode.this, list);
            listTransactions.setAdapter(historyAdapter);
        }
    }

    boolean submenuVisibility;
    LocationFilterBottomSheet bottomsheet;

    private void burgerMenu() {
        locationSelector = findViewById(R.id.locationSelector);

        locationSelector.setOnClickListener(btnBurgerMenuView -> {
            locationSelector.setRotation(submenuVisibility ? 180 : 0);
            bottomsheet = new LocationFilterBottomSheet(locationJson, "QrCode");
            bottomsheet.show(getSupportFragmentManager(), "bottomSheet");
        });
    }

    String mID = "";
    String locName = "";

    public void setContent(String content, String locNameselected, String lastChildMidQr, int dismissFlag) {
        mID = lastChildMidQr;
        locName = locNameselected;

        if (dismissFlag == 1) {
            callGetQrApi(lastChildMidQr);
            selectedLocationName.setText(content);
            bottomsheet.dismiss();
        }

        filterDataByMid(lastChildMidQr);
    }

    public void filterDataByMid(String lastChildMidQr){
        if (!TextUtils.isEmpty(lastChildMidQr)) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getMerchantId().equalsIgnoreCase(lastChildMidQr)) {
                    loList.add(list.get(i));
                }
            }
            if (!loList.isEmpty()) {
                listTransactions.setVisibility(View.VISIBLE);
                tvNoRecordFound.setVisibility(View.GONE);
                if (historyAdapter != null) {
                    historyAdapter.updateList(loList);
                    historyAdapter.notifyDataSetChanged();
                } else {
                    historyAdapter = new WidgetTransactionHAdapter(QrCode.this, loList);
                    historyAdapter.notifyDataSetChanged();
                }
                listTransactions.setAdapter(historyAdapter);


            } else {
                listTransactions.setVisibility(View.GONE);
                tvNoRecordFound.setVisibility(View.VISIBLE);
            }

        }
    }

    private void callGetQrApi(String lastLocationMid) {
        JSONObject json = new JSONObject();
        try {
            String tcpKey = ((AzulApplication) this.getApplication()).getTcpKey();
            json.put("tcp", RSAHelper.ecryptRSA(QrCode.this, tcpKey));
            JSONObject payload = new JSONObject();
            payload.put("device", DeviceUtils.getDeviceId(this));
            JSONObject pagosrequest = new JSONObject();
            pagosrequest.put("merchantId", lastLocationMid);

            payload.put("obtenerrequest", pagosrequest);
            json.put("payload", RSAHelper.encryptAES(payload.toString(), Base64.decode(tcpKey, 0)));
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.GET_QR, json);
    }

    public void getResponseForQr(String responseData) {
        try {
            viewPagerTwo.setAdapter(new ViewPagerAdapterTwo(this, responseData, mID, locName));
            circleIndicatorTwo = findViewById(R.id.circleIndicatorTwo);
            circleIndicatorTwo.setupWithViewPager(viewPagerTwo, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}