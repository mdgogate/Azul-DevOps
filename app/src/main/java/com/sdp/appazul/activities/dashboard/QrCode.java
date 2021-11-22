package com.sdp.appazul.activities.dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.sdp.appazul.R;
import com.sdp.appazul.activities.home.BaseLoggedInActivity;
import com.sdp.appazul.activities.home.HomeLocationFilter;
import com.sdp.appazul.adapters.WidgetTransactionHAdapter;
import com.sdp.appazul.api.ApiManager;
import com.sdp.appazul.api.ServiceUrls;
import com.sdp.appazul.classes.TransactionHistory;
import com.sdp.appazul.globals.AppAlters;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.KeyConstants;
import com.sdp.appazul.security.RSAHelper;
import com.sdp.appazul.utils.DateUtils;
import com.sdp.appazul.utils.DeviceUtils;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.google.android.material.tabs.TabLayout;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.utils.DownloadHandlerObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    ImageView clearTextView;
    ImageView searchInActive;
    EditText etSearchBy;
    WidgetTransactionHAdapter historyAdapter;
    List<TransactionHistory> list;
    List<TransactionHistory> transactionFilteredList;
    ListView listTransactions;
    ArrayList<TransactionHistory> filteredArrayList = new ArrayList<>();
    List<String> monthList = new ArrayList<>();
    GregorianCalendar month;
    DateUtils utils = new DateUtils();
    CardView cardDatePicker;
    LinearLayout layoutDateRangeFilter;
    TextView tvFromActivity;
    TextView tvToActivity;
    TextView tvDateFilter;
    TextView tvAmount;
    TextView tvReferenceNo;
    ImageView imgClearRangeFilter;
    Boolean tvAmountClick = true;
    Boolean tvReferenceClick = false;
    TextView tvNoRecordFound;
    ImageView btnTransactionBack;
    ApiManager apiManager = new ApiManager(QrCode.this);
    ImageView imgViewQrCode;
    ImageView imgViewQrCodeHidden;
    TextView qrTitle;
    TextView qrTitleHidden;
    TextView qrMerchantId;
    TextView qrMerchantIdHidden;
    TextView qrTId;
    TextView qrTIdHidden;
    List<TransactionHistory> temp;
    RelativeLayout layoutAmountAndRefFilter;
    ImageView locationSelector;
    RelativeLayout locationFilter;
    String locationJson;
    TextView selectedLocationName;
    SharedPreferences sharedPreferences;
    ImageView imgDownloadQrCode;
    ImageView imgDownloadQrCodeHidden;
    ImageView imgShareQrCode;
    ImageView imgShareQrCodeHidden;
    CardView qrViewPager;
    CardView qrViewPagerHidden;
    int pagerPosition;
    RelativeLayout layoutDownloadShare;
    ImageView replaceDownloadableImage;
    DownloadHandlerObject downloadHandlerObject = new DownloadHandlerObject(this);
    RelativeLayout downloadableLayout;
    ProgressDialog progressDialog;
    int shareFlag = 0;
    int qrChange = 0;
    boolean submenuVisibility;
    HomeLocationFilter homeLocationBottomSheet;
    Context context;
    RelativeLayout downloadLayoutHidden;
    RelativeLayout shareLayoutHidden;
    RelativeLayout bottom_sheet;
    static int SNACK_LENGTH = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = this;
        initControls();
        pageListener();
        verifyStoragePermissions(this);
    }

    private void initControls() {
        bottom_sheet = findViewById(R.id.bottom_sheet);
        selectedLocationName = findViewById(R.id.selectedLocationName);
        viewPagerTwo = findViewById(R.id.viewPagerTwo);
        showQr = findViewById(R.id.showQr);
        toolbarTextTitleTwo = findViewById(R.id.toolbarTextTitleTwo);
        swipeLeft = findViewById(R.id.swipeLeft);
        btnTransactionBack = findViewById(R.id.btnTransactionBack);
        replaceDownloadableImage = findViewById(R.id.replaceDownloadableImage);
        downloadableLayout = findViewById(R.id.downloadableLayout);


        locationJson = ((AzulApplication) context.getApplicationContext()).getLocationDataShare();
        burgerMenu();
        checkLocation();

        btnTransactionBack.setOnClickListener(view -> {
            finish();

            Intent intent1 = new Intent(QrCode.this, DashBoardActivity.class);
            ((AzulApplication) ((QrCode) this).getApplication()).setLocationDataShare(locationJson);

            startActivityForResult(intent1, 1);
            this.overridePendingTransition(R.anim.animation_leave,
                    R.anim.slide_nothing);
        });

    }

    Map<String, String> defaultLocations = new HashMap<>();

    private void checkLocation() {
        sharedPreferences = ((AzulApplication) this.getApplication()).getPrefs();
        String lastLocationNameWithId = sharedPreferences.getString(Constants.SELECTED_LOCATION_QRCODE, "");
        String lastLocationName = sharedPreferences.getString(Constants.SELECTED_LOCATION_NAME_QRCODE, "");
        String lastLocationMid = sharedPreferences.getString(Constants.SELECTED_LOCATION_MID_QRCODE, "");
        mID = lastLocationMid;
        locName = lastLocationName;
        if (lastLocationNameWithId != null && !TextUtils.isEmpty(lastLocationNameWithId)) {
            selectedLocationName.setText(lastLocationNameWithId);

            callGetQrApi(lastLocationMid);
        } else {
            getDefaultLocationForQr(locationJson);

            defaultLocations = ((AzulApplication) this.getApplication()).getDefaultLocationQr();

            if (defaultLocations != null) {

                defaultLocationName = defaultLocations.get("CHILD_LOC_NAME");
                defaultLocationId = defaultLocations.get("CHILD_LOC_ID");
                mID = defaultLocationId;
                locName = defaultLocationName;
                selectedLocationName.setText(defaultLocationName + " - " + defaultLocationId);
                callGetQrApi(defaultLocationId);
            }
        }


    }

    String defaultLocationName;
    String defaultLocationId;
    Map<String, String> defaultLocation;

    private void getDefaultLocationForQr(String locationResponse) {

        try {
            JSONObject jsonResponse = new JSONObject(locationResponse);
            JSONArray parentLevelLocations = jsonResponse.getJSONArray("AssignedUnits");
            for (int i = 0; i < parentLevelLocations.length(); i++) {
                JSONObject parentData = parentLevelLocations.getJSONObject(i);
                String parentLocationName = parentData.getString("Name");

                JSONArray assignedLocationsObject = parentData.getJSONArray(Constants.ASSIGNED_LOCATION_QR);

                for (int j = 0; j < assignedLocationsObject.length(); j++) {
                    JSONObject secondPositionLocation = assignedLocationsObject.getJSONObject(0);
                    String secondPositionLocationId = secondPositionLocation.getString(Constants.MERCHANT_ID);
                    String secondPositionLocationName = secondPositionLocation.getString("Name");
                    defaultLocation = new HashMap<>();
                    defaultLocation.put("PARENT_LOC_NAME", parentLocationName);
                    defaultLocation.put("CHILD_LOC_ID", secondPositionLocationId);
                    defaultLocation.put("CHILD_LOC_NAME", secondPositionLocationName);
                    ((AzulApplication) (this).getApplication()).setDefaultLocationQr(defaultLocation);
                    sharedPreferences.edit().putInt(Constants.SELECTED_PARENT_QRCODE, 0).apply();
                    sharedPreferences.edit().putInt(Constants.SELECTED_CHILD_QRCODE, (Integer) 0).apply();
                    sharedPreferences.edit().putString(Constants.SELECTED_LOCATION_QRCODE, secondPositionLocationName + " - " + secondPositionLocationId).apply();
                    sharedPreferences.edit().putString(Constants.SELECTED_LOCATION_MID_QRCODE, secondPositionLocationId).apply();

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void pageListener() {
        viewPagerTwo.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("TAG", "scroll");
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    pagerPosition = position;
                    showQr.setVisibility(View.VISIBLE);
                    toolbarTextTitleTwo.setText(R.string.qr_code);
                    swipeLeft.setText(R.string.swipe_left);

                } else {
                    pagerPosition = position;
                    showQr.setVisibility(View.GONE);
                    toolbarTextTitleTwo.setText(R.string.sales_of_day);
                    swipeLeft.setText(R.string.swipe_right);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("", "");
            }
        });
    }

    public class ViewPagerAdapterTwo extends PagerAdapter {
        int[] layouts = {R.layout.qrcode_widget_layout, R.layout.transaction_qrcode_widget_layout};
        LayoutInflater layoutInflater;
        Context context;
        String imgData;
        String mID;
        String selectedLoName;
        SVG svg = null;
        SVG svgForMarket = null;


        public ViewPagerAdapterTwo(QrCode qrCode, String responseData, String merchID, String locName) {
            context = qrCode;
            imgData = responseData;
            mID = merchID;
            selectedLoName = locName;
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
            View getQrView = layoutInflater.inflate(R.layout.transaction_qrcode_widget_layout, container, false);
            View[] viewArr = {qrView, getQrView};
            container.addView(viewArr[position]);
            qrSetView(qrView);
            secondView(getQrView);
            return viewArr[position];
        }


        @Override
        public void destroyItem(ViewGroup container, int position, @NotNull Object object) {
            container.removeView((RelativeLayout) object);
        }

        private void qrSetView(View qrView) {
            downloadLayoutHidden = qrView.findViewById(R.id.downloadLayoutHidden);
            shareLayoutHidden = qrView.findViewById(R.id.shareLayoutHidden);
            imgViewQrCode = qrView.findViewById(R.id.imgViewQrCode);
            imgViewQrCode = qrView.findViewById(R.id.imgViewQrCode);
            imgViewQrCodeHidden = qrView.findViewById(R.id.imgViewQrCodeHidden);
            imgDownloadQrCode = qrView.findViewById(R.id.imgDownloadQrCode);
            imgDownloadQrCodeHidden = qrView.findViewById(R.id.imgDownloadQrCodeHidden);
            qrViewPager = qrView.findViewById(R.id.qrViewPager);
            qrViewPagerHidden = qrView.findViewById(R.id.qrViewPagerHidden);
            imgShareQrCode = qrView.findViewById(R.id.imgShareQrCode);
            imgShareQrCodeHidden = qrView.findViewById(R.id.imgShareQrCodeHidden);
            qrTitle = qrView.findViewById(R.id.qrTitle);
            qrTitleHidden = qrView.findViewById(R.id.qrTitleHidden);
            qrMerchantId = qrView.findViewById(R.id.qrMerchantId);
            qrMerchantIdHidden = qrView.findViewById(R.id.qrMerchantIdHidden);
            qrTId = qrView.findViewById(R.id.qrTId);
            qrTIdHidden = qrView.findViewById(R.id.qrTIdHidden);
            layoutDownloadShare = qrView.findViewById(R.id.layoutDownloadShare);
            String merchantToDisplay = getResources().getString(R.string.merchant_id_title) + mID;
            String transToDisplay = "TID: " + mID;
            String merchantNameToDisplay = selectedLoName;
            if (!TextUtils.isEmpty(merchantToDisplay)) {
                qrMerchantId.setText(merchantToDisplay);
                qrMerchantIdHidden.setText(merchantToDisplay);
                qrTIdHidden.setText(transToDisplay);
                qrTId.setText(transToDisplay);
            } else {
                qrMerchantIdHidden.setText("-");
            }
            if (!TextUtils.isEmpty(merchantNameToDisplay)) {
                qrTitle.setText(merchantNameToDisplay);
                qrTitleHidden.setText(merchantNameToDisplay);
            } else {
                qrTitle.setText("-");
                qrTitleHidden.setText("-");
            }
            try {
                if (imgData != null && !TextUtils.isEmpty(imgData)) {
                    svg = SVG.getFromString(imgData);
                    svgForMarket = SVG.getFromString(imgData);

                    PictureDrawable pd = new PictureDrawable(svg.renderToPicture());
                    PictureDrawable pdForMarket = new PictureDrawable(svgForMarket.renderToPicture());
                    imgViewQrCode.setImageDrawable(pd);
                    imgViewQrCodeHidden.setImageDrawable(pd);
                    replaceDownloadableImage.setImageDrawable(pdForMarket);
                } else {
                    Toast.makeText(getApplicationContext(), "No se recibieron los datos del código QR", Toast.LENGTH_SHORT).show();
                }
            } catch (
                    SVGParseException e) {
                e.printStackTrace();
            }
            buttonListeners();
        }

        private void buttonListeners() {

            copyButtonListener();

            shareLayoutHidden.setOnClickListener(imgShareQrCodeHiddenView -> {
                if (imgData != null && !TextUtils.isEmpty(imgData)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // do your stuff
                            runOnUiThread(new Runnable() {
                                public void run() {

                                    try {
                                        saveImage(getBitmapFromView(downloadableLayout), 1);
                                    } catch (IOException e) {
                                        Log.e("Codi go Download", "codi go DownloadShare: " + e.getLocalizedMessage());
                                    }
                                }
                            });
                        }
                    }).start();
                } else {
                    Toast.makeText(getApplicationContext(), "El código QR no está disponible para descargar o compartir.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void copyButtonListener() {
            downloadLayoutHidden.setOnClickListener(imgDownloadQrCodeHiddenView -> {
                if (imgData != null && !TextUtils.isEmpty(imgData)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // do your stuff
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    try {
                                        saveImage(getBitmapFromView(downloadableLayout), 0);
                                    } catch (IOException e) {
                                        Log.e("Codi go Download", "codi go DownloadShare: " + e.getLocalizedMessage());
                                    }
                                }
                            });
                        }
                    }).start();
                } else {
                    Toast.makeText(getApplicationContext(), "El código QR no está disponible para descargar o compartir.", Toast.LENGTH_SHORT).show();
                }

            });
        }


        @SuppressLint("ClickableViewAccessibility")
        private void secondView(View view) {
            etSearchBy = view.findViewById(R.id.etSearchBy);
            layoutTextSearch = view.findViewById(R.id.layoutTextSearch);
            clearTextView = view.findViewById(R.id.clearTextView);
            searchInActive = view.findViewById(R.id.searchinActive);
            listTransactions = view.findViewById(R.id.listTransactions);
            cardDatePicker = view.findViewById(R.id.cardDatePicker);
            layoutDateRangeFilter = view.findViewById(R.id.layoutDateRangeFilter);
            tvFromActivity = view.findViewById(R.id.tvFromActivity);
            tvToActivity = view.findViewById(R.id.tvToActivity);
            imgClearRangeFilter = view.findViewById(R.id.imgClearRangeFilter);
            tvDateFilter = view.findViewById(R.id.tvDateFilter);
            layoutAmountAndRefFilter = view.findViewById(R.id.layoutAmountandRefFilter);
            tvAmount = view.findViewById(R.id.tvAmount);
            tvReferenceNo = view.findViewById(R.id.tvReferenceNo);
            tvNoRecordFound = view.findViewById(R.id.tvNoRecordFound);
            getTransactionListData();

            etSearchBy.setOnTouchListener((view1, motionEvent) -> {
                if (MotionEvent.ACTION_UP == motionEvent.getAction())
                    hidShow();
                return false;
            });

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
                searchInActive.setTag("bg");
                searchInActive.setImageResource(R.drawable.transaction_search_icon_inactive);
                etSearchBy.setText("");
                if (clearTextView.getVisibility() == View.VISIBLE) {
                    clearTextView.setVisibility(View.GONE);
                }
                etSearchBy.setHint(getResources().getString(R.string.search_hint));
                if (layoutAmountAndRefFilter.getVisibility() == View.VISIBLE) {
                    layoutAmountAndRefFilter.setVisibility(View.GONE);
                }

                if (getCurrentFocus() != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            });


            tvReferenceNo.setOnClickListener(tvReferenceNoView -> {

                tvReferenceNo.setBackgroundResource(R.drawable.middle_navigation_bg);
                tvAmount.setBackgroundResource(0);
                tvAmount.setTextColor(ContextCompat.getColor(context, R.color.slider_heading));
                tvReferenceNo.setTextColor(ContextCompat.getColor(context, R.color.blue_3));
                tvAmountClick = false;
                tvReferenceClick = true;
                etSearchBy.setText("");

                etSearchBy.setHint(getResources().getString(R.string.reference_no_hint));

            });

            tvAmount.setOnClickListener(tvReferenceNoView -> {
                tvAmount.setBackgroundResource(R.drawable.middle_navigation_bg);
                tvReferenceNo.setBackgroundResource(0);
                tvReferenceNo.setTextColor(ContextCompat.getColor(context, R.color.slider_heading));
                tvAmount.setTextColor(ContextCompat.getColor(context, R.color.blue_3));
                etSearchBy.setHint(getResources().getString(R.string.amount_hint));
                tvAmountClick = true;
                tvReferenceClick = false;
                etSearchBy.setText("");
            });
        }

        private void getTransactionListData() {
            gregorianCalendar = (GregorianCalendar) getInstance();
            monthList = utils.getmonthList();
            month = (GregorianCalendar) gregorianCalendar.clone();
            String spanishMonthName = monthList
                    .get(Integer.parseInt((String) DateFormat.format(Constants.MM, month)) - 1);
            String date = "" + gregorianCalendar.get(DAY_OF_MONTH);
            String initialDate = "" + gregorianCalendar.get(DAY_OF_MONTH);
            String initialMonth = "" + (gregorianCalendar.get(MONTH) + 1);
            String currDate = gregorianCalendar.get(YEAR) + "/" +
                    initialMonth + "/"
                    + initialDate;

            callQRTransaction(mID, currDate, currDate);
            String dateToDisplay = date + " de " + spanishMonthName;
            tvDateFilter.setText(dateToDisplay);
        }

        private void callQRTransaction(String locationId, String fromDateString, String toDateString) {
            JSONObject json = new JSONObject();
            try {

                String tcpKey = ((AzulApplication) QrCode.this.getApplication()).getTcpKey();
                String vcr = ((AzulApplication) QrCode.this.getApplication()).getVcr();
                json.put("tcp", RSAHelper.ecryptRSA(QrCode.this, tcpKey));
                JSONObject payload = new JSONObject();
                payload.put("device", DeviceUtils.getDeviceId(QrCode.this));
                payload.put("dateFrom", fromDateString);
                payload.put("dateTo", toDateString);
                payload.put("merchantIdList", locationId);
                json.put("payload", RSAHelper.encryptAES(payload.toString(), Base64.decode(tcpKey, 0), Base64.decode(vcr, 0)));
            } catch (Exception e) {
                Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
            }
            apiManager.callAPI(ServiceUrls.QR_TRANSACTION, json);
        }

        private void hidShow() {
            String backgroundImageName = String.valueOf(searchInActive.getTag());

            if (backgroundImageName.equalsIgnoreCase("bg")) {
                searchInActive.setTag(R.drawable.transaction_search_icon_active);
                searchInActive.setImageResource(R.drawable.transaction_search_icon_active);
            }

            if (clearTextView.getVisibility() == View.GONE) {
                clearTextView.setVisibility(View.VISIBLE);
            }
            if (Boolean.TRUE.equals(tvAmountClick)) {
                etSearchBy.setHint(getResources().getString(R.string.amount_hint));
            } else {
                etSearchBy.setHint(getResources().getString(R.string.reference_no_hint));
            }
            if (layoutAmountAndRefFilter.getVisibility() == View.GONE) {
                layoutAmountAndRefFilter.setVisibility(View.VISIBLE);
            }
        }

    }

    SimpleDateFormat olderFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private Comparator<TransactionHistory> byDate = new Comparator<TransactionHistory>() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

        public int compare(TransactionHistory ord1, TransactionHistory ord2) {

            Date inputDateT11Older = null;
            Date inputDateT2Older = null;


            String inputDateT1 = ord1.getTrDate().substring(0, 10);
            String inputTimeT1 = ord1.getTime().substring(0, 8);

            String inputDateT2 = ord2.getTrDate().substring(0, 10);
            String inputTimeT2 = ord2.getTime().substring(0, 8);


            String settle1 = inputDateT1 + "T" + inputTimeT1;
            String settle2 = inputDateT2 + "T" + inputTimeT2;
            Log.d("TAG", settle1 + "  == compare  == " + settle2);

            try {
                inputDateT11Older = olderFormat.parse(settle1);
                inputDateT2Older = olderFormat.parse(settle2);

                Log.d("TAG", inputDateT11Older.getTime() + "  == getTime  == " + inputDateT2Older.getTime());
            } catch (ParseException e) {
                Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
            }

            if (inputDateT11Older != null && inputDateT2Older != null) {
                return (inputDateT11Older.getTime() > inputDateT2Older.getTime() ? -1 : 1);
            }

            return 0;
        }
    };
    List<TransactionHistory> loList;

    void filterList(CharSequence text, boolean type) {
        if (!filteredArrayList.isEmpty()) {
            searchInList(filteredArrayList, text, type);
        } else if (loList != null && !loList.isEmpty()) {
            searchInList(loList, text, type);
        } else {
            searchInList(list, text, type);
        }
    }

    void filterListByAmt(CharSequence text, boolean type) {
        if (!filteredArrayList.isEmpty()) {
            searchInList(filteredArrayList, text, type);
        } else if (loList != null && !loList.isEmpty()) {
            searchInList(loList, text, type);
        } else {
            searchInList(list, text, type);
        }
    }

    public void searchInList(List<TransactionHistory> transactionMainList, CharSequence text, Boolean type) {
        temp = new ArrayList<>();
        for (TransactionHistory tr : transactionMainList) {
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
            Collections.sort(temp, byDate);

            if (historyAdapter != null) {
                historyAdapter.updateList(temp);
            } else {
                historyAdapter = new WidgetTransactionHAdapter(QrCode.this, temp);
                historyAdapter.notifyDataSetChanged();
            }
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
            JSONObject jsonTransactionOb = jsonObject.getJSONObject(Constants.QR_TRANSACTION_JSON_LBL);
            JSONArray jsonArray = jsonTransactionOb.getJSONArray("Transactions");
            if (jsonArray.toString().equalsIgnoreCase("[]")) {
                listTransactions.setVisibility(View.GONE);
                tvNoRecordFound.setVisibility(View.VISIBLE);
            } else {
                transactionParsing(jsonArray);
            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void transactionParsing(JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonTrObject = jsonArray.getJSONObject(i);
                String amount = jsonTrObject.getString("Amount");
                String referenceNo = jsonTrObject.getString("TransactionNumber");
                String trTime = jsonTrObject.getString("TransactionTime");
                String trDate = jsonTrObject.getString("TransactionDate");
                String merchantId = jsonTrObject.getString("MerchantId");
                String location = jsonTrObject.getString("MerchantName");
                list.add(new TransactionHistory(referenceNo, location, trDate, trTime, amount, merchantId, tvDateFilter.getText().toString()));
            }

            settingAdapter(mID, list, loList);
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void settingAdapter(String mID, List<TransactionHistory> list, List<TransactionHistory> loList) {

        if (!TextUtils.isEmpty(mID)) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getMerchantId().equalsIgnoreCase(mID)) {
                    loList.add(list.get(i));
                }
            }

            if (!loList.isEmpty()) {

                Collections.sort(loList, byDate);
                historyAdapter = new WidgetTransactionHAdapter(QrCode.this, loList);
                listTransactions.setAdapter(historyAdapter);
                listTransactions.setVisibility(View.VISIBLE);
                tvNoRecordFound.setVisibility(View.GONE);
            } else {
                listTransactions.setVisibility(View.GONE);
                tvNoRecordFound.setVisibility(View.VISIBLE);
            }
        } else {

            Collections.sort(list, byDate);
            historyAdapter = new WidgetTransactionHAdapter(QrCode.this, list);
            listTransactions.setAdapter(historyAdapter);
        }
    }


    private void burgerMenu() {
        locationSelector = findViewById(R.id.locationSelector);
        locationFilter = findViewById(R.id.locationFilter);

        locationSelector.setOnClickListener(btnBurgerMenuView -> {
            locationSelector.setRotation(submenuVisibility ? 180 : 0);
            homeLocationBottomSheet = new HomeLocationFilter(locationJson, "QrCode", 0);
            homeLocationBottomSheet.show(getSupportFragmentManager(), "bottomSheet");
        });
        locationFilter.setOnClickListener(locationFilterView -> {
            locationSelector.setRotation(submenuVisibility ? 180 : 0);
            homeLocationBottomSheet = new HomeLocationFilter(locationJson, "QrCode", 0);
            homeLocationBottomSheet.show(getSupportFragmentManager(), "bottomSheet");
        });
    }

    String mID = "";
    String locName = "";

    public void setContent(String content, String locationNameSelected, String lastChildMidQr, int dismissFlag) {
        mID = lastChildMidQr;
        locName = locationNameSelected;

        if (dismissFlag == 1) {
            callGetQrApi(lastChildMidQr);
            selectedLocationName.setText(content);
            homeLocationBottomSheet.dismiss();
        }


        filterDataByMid(lastChildMidQr);
    }

    public void filterDataByMid(String lastChildMidQr) {
        if (loList != null && !loList.isEmpty()) {
            loList.clear();
        }
        if (list != null && !list.isEmpty()) {
            checkMid(list, lastChildMidQr);
        }
        if (loList != null && !loList.isEmpty()) {
            setTransactionAdapter();
        }


    }

    private void checkMid(List<TransactionHistory> list, String lastChildMidQr) {
        for (int i = 0; i < list.size(); i++) {
            if (!TextUtils.isEmpty(lastChildMidQr) && list.get(i).getMerchantId().equalsIgnoreCase(lastChildMidQr)) {
                loList.add(list.get(i));
            }
        }
    }

    private void setTransactionAdapter() {
        listTransactions.setVisibility(View.VISIBLE);
        tvNoRecordFound.setVisibility(View.GONE);
        Collections.sort(loList, byDate);
        if (historyAdapter != null) {
            historyAdapter.updateList(loList);
        } else {
            historyAdapter = new WidgetTransactionHAdapter(QrCode.this, loList);
        }
        historyAdapter.notifyDataSetChanged();
        listTransactions.setAdapter(historyAdapter);
    }

    private void callGetQrApi(String lastLocationMid) {
        JSONObject json = new JSONObject();
        try {
            String tcpKey = ((AzulApplication) this.getApplication()).getTcpKey();
            String vcr = ((AzulApplication) this.getApplication()).getVcr();
            json.put("tcp", RSAHelper.ecryptRSA(QrCode.this, tcpKey));
            JSONObject payload = new JSONObject();
            payload.put("device", DeviceUtils.getDeviceId(this));
            payload.put("merchantId", lastLocationMid);

            json.put("payload", RSAHelper.encryptAES(payload.toString(), Base64.decode(tcpKey, 0), Base64.decode(vcr, 0)));
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.GET_QR, json);
        Log.d("TAG", "callGetQrApi: " + qrChange);

    }

    public void getResponseForQr(String responseData) {
        try {
            if (responseData != null && !responseData.equalsIgnoreCase("")) {
                viewPagerTwo.setAdapter(new ViewPagerAdapterTwo(this, responseData, mID, locName));
                circleIndicatorTwo = findViewById(R.id.circleIndicatorTwo);
                circleIndicatorTwo.setupWithViewPager(viewPagerTwo, true);
                if (pagerPosition == 0) {
                    viewPagerTwo.setCurrentItem(0);
                } else {
                    viewPagerTwo.setCurrentItem(1);
                }
            } else {
                viewPagerTwo.setAdapter(new ViewPagerAdapterTwo(this, responseData, mID, locName));
                circleIndicatorTwo = findViewById(R.id.circleIndicatorTwo);
                circleIndicatorTwo.setupWithViewPager(viewPagerTwo, true);
                if (pagerPosition == 0) {
                    viewPagerTwo.setCurrentItem(0);
                } else {
                    viewPagerTwo.setCurrentItem(1);
                }
                Toast.makeText(getApplicationContext(), "No se recibieron los datos del código QR", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * convert the view into the bitmap so we can download bitmap
     * Creates the bitmap form the specific view to function.
     *
     * @param view - get the view to generate the bitmap
     * @see android.graphics.drawable.BitmapDrawable
     */
    private static Bitmap getBitmapFromView(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) bgDrawable.draw(canvas);
        else canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }


    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity - Get the current context to check for permissions.
     */
    public static void verifyStoragePermissions(Activity activity) {
        String[] permissionArray = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        // Check if we have write permission
        int permission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, permissionArray, 1);
        }
    }

    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param finalBitmap - gets the current bitmap from the generated layout.
     * @throws IOException - Throws the IOException in case file not found or permission issue
     * @see IOException
     */

    private void saveImage(Bitmap finalBitmap, int downloadAndShare) throws IOException {
        handler.sendEmptyMessage(1);

        if (downloadAndShare == 0) {
            downloadHandlerObject.saveImage(finalBitmap, String.valueOf(System.currentTimeMillis()), 0);
            shareFlag = 0;
        } else {
            downloadHandlerObject.saveImage(finalBitmap, String.valueOf(System.currentTimeMillis()), 1);
            shareFlag = 1;
        }
        handler.sendEmptyMessage(2);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {


            if (message.what == 1) {
                progressDialog = ProgressDialog.show(QrCode.this, null,
                        getResources().getString(R.string.loading_lbl), false, false);
                progressDialog.setCancelable(false);

            } else if (message.what == 2) {
                if ((progressDialog != null) && progressDialog.isShowing()) {
                    progressDialog.dismiss();

                    if (shareFlag == 0) {
                        Snackbar snackbar = Snackbar.make(bottom_sheet, context.getResources().getString(R.string.save_qrcode), SNACK_LENGTH)
                                .setBackgroundTint(Color.parseColor("#0091DF"));
                        snackbar.show();
                    }
                }

            }
            return false;
        }
    });
}