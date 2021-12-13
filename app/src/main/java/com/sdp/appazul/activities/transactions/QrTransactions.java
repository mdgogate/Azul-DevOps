package com.sdp.appazul.activities.transactions;

import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.sdp.appazul.R;
import com.sdp.appazul.activities.dashboard.DashBoardActivity;
import com.sdp.appazul.activities.home.BaseLoggedInActivity;
import com.sdp.appazul.activities.home.HomeLocationFilter;
import com.sdp.appazul.activities.payment.PaymentLocationFilter;
import com.sdp.appazul.adapters.DialogCalendarAdapter;
import com.sdp.appazul.adapters.TransactionHistoryAdapter;
import com.sdp.appazul.api.ApiManager;
import com.sdp.appazul.api.ServiceUrls;
import com.sdp.appazul.classes.SettleTransaction;
import com.sdp.appazul.classes.TransactionHistory;
import com.sdp.appazul.globals.AppAlters;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.KeyConstants;
import com.sdp.appazul.security.RSAHelper;
import com.sdp.appazul.utils.DateUtils;
import com.sdp.appazul.utils.DeviceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;

public class QrTransactions extends BaseLoggedInActivity {

    ImageView clearTextView;
    ImageView searchInActive;

    EditText etSearchBy;
    TransactionHistoryAdapter historyAdapter;
    List<TransactionHistory> list;
    List<TransactionHistory> transactionFilteredList;
    ListView lvTransaction;
    boolean editText1Selected = false;
    boolean editText2Selected = false;
    ArrayList<TransactionHistory> filterArrayList = new ArrayList<>();
    TextView title;
    TextView calendarTitle;
    GregorianCalendar month;
    DialogCalendarAdapter calendarAdapter;
    ImageView next;
    ImageView previous;
    GridView gridview;
    ArrayList<String> desc;
    RelativeLayout calendarLayout;
    RelativeLayout locationFilter;
    LinearLayout fromDateLayout;
    LinearLayout toDateLayout;
    ArrayList<String> startDates = new ArrayList<>();
    ArrayList<String> nameOfEvent = new ArrayList<>();
    List<TransactionHistory> temp;
    Dialog calendarDialog;
    Button btnConfirm;
    LinearLayout layoutDateFilter;
    LinearLayout linearLayout;
    List<String> daysList = new ArrayList<>();
    List<String> monthList = new ArrayList<>();
    List<String> monthListCap = new ArrayList<>();
    GregorianCalendar selectedInitialDate;
    GregorianCalendar selectedFinalDate;
    String currentDateTime = "";
    String selectedInitialDateString = "";
    String selectedFinalDateString = "";
    DateUtils utils = new DateUtils();
    LinearLayout layoutDateRangeFilter;
    TextView tvFromActivity;
    TextView tvToActivity;
    TextView tvAmount;
    TextView tvReferenceNo;
    Boolean tvAmountClick = true;
    Boolean tvReferenceClick = false;
    TextView tvNoRecordFound;
    ImageView btnTransactionBack;
    long newDiff;
    LinearLayout layoutSinceDatePicker;
    LinearLayout layoutUntilDatePicker;
    ImageView ivFromIcon;
    ImageView ivToIcon;
    ApiManager apiManager = new ApiManager(QrTransactions.this);
    String selectedFromDateForApi = "";
    String selectedToDateForApi = "";
    LinearLayout layoutSearch;
    RelativeLayout listFilterLayout;
    String locationJson;
    SharedPreferences sharedPreferences;
    ImageView tranLocationSelector;
    boolean submenuVisibility;
    TextView tvSelectedLocation;
    String mID = "";
    HomeLocationFilter homeLocationBottomSheet;
    Map<String, String> defaultLocation;
    Map<String, String> defaultLocations = new HashMap<>();
    String fetchFinal = "";
    String formattedFromDate;
    String formattedTODate;
    String defaultLocationName;
    SimpleDateFormat olderFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    RelativeLayout act_qr_transaction;

    static int SNACK_LENGTH = 0;    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_transactions);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initControls();
    }

    @SuppressLint({"WrongConstant", "ClickableViewAccessibility", "SetTextI18n"})
    private void initControls() {
        act_qr_transaction = findViewById(R.id.act_qr_transaction);
        etSearchBy = findViewById(R.id.etSearchBy);
        tranLocationSelector = findViewById(R.id.tran_locationSelector);
        locationFilter = findViewById(R.id.locationFilter);
        tvSelectedLocation = findViewById(R.id.tvSelectedLocation);
        layoutSinceDatePicker = findViewById(R.id.layoutSinceDatePicker);
        layoutUntilDatePicker = findViewById(R.id.layoutUntilDatePicker);
        clearTextView = findViewById(R.id.clearTextView);
        searchInActive = findViewById(R.id.searchinActive);
        lvTransaction = findViewById(R.id.listTransactions);
        layoutDateRangeFilter = findViewById(R.id.layoutDateRangeFilter);
        tvFromActivity = findViewById(R.id.tvFromActivity);
        tvToActivity = findViewById(R.id.tvToActivity);
        tvAmount = findViewById(R.id.tvAmount);
        tvReferenceNo = findViewById(R.id.tvReferenceNo);
        tvNoRecordFound = findViewById(R.id.tvNoRecordFound);
        btnTransactionBack = findViewById(R.id.btnTransactionBack);
        layoutSearch = findViewById(R.id.layoutSearch);
        listFilterLayout = findViewById(R.id.listFilterLayout);

        Intent intent = getIntent();
        locationJson = intent.getStringExtra("LOCATION_RESPONSE");

        sharedPreferences = ((AzulApplication) this.getApplication()).getPrefs();

        String lastLocationNameWithId = sharedPreferences.getString(Constants.SELECTED_LOCATION_QRCODE, "");
        String lastLocationMid = sharedPreferences.getString(Constants.SELECTED_LOCATION_MID_QRCODE, "");

        if (lastLocationNameWithId != null && !TextUtils.isEmpty(lastLocationNameWithId)) {
            tvSelectedLocation.setText(lastLocationNameWithId);
            mID = lastLocationMid;
        } else {

            getDefaultLocation(locationJson);

            defaultLocations = ((AzulApplication) this.getApplication()).getDefaultLocationQr();
            if (defaultLocations != null) {
                defaultLocationName = defaultLocations.get("CHILD_LOC_NAME");
                String defaultLocationId = defaultLocations.get("CHILD_LOC_ID");
                tvSelectedLocation.setText(defaultLocationName + " - " + defaultLocationId);
                mID = defaultLocationId;
            }
        }


        btnTransactionBack.setOnClickListener(view -> {
            finish();
            Intent intent1 = new Intent(QrTransactions.this, DashBoardActivity.class);
            intent1.putExtra("LOCATION_RESPONSE", locationJson);
            startActivityForResult(intent1, 1);
            this.overridePendingTransition(R.anim.animation_leave,
                    R.anim.slide_nothing);
        });

        layoutSearch.setOnClickListener(view -> hidShow());

        etSearchBy.setOnTouchListener((view, motionEvent) -> {
            if (MotionEvent.ACTION_UP == motionEvent.getAction())
                hidShow();
            return false;
        });

        locationFilter.setOnClickListener(btnBurgerMenuView -> {
            tranLocationSelector.setRotation(submenuVisibility ? 180 : 0);
            homeLocationBottomSheet = new HomeLocationFilter(locationJson, "QrTransactions", 0);
            homeLocationBottomSheet.show(getSupportFragmentManager(), "bottomSheet");
        });

        etSearchBy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("TAG", "");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (Boolean.TRUE.equals(tvAmountClick)) {
                    filterList(charSequence, true);
                } else {
                    filterForReferenceList(charSequence, false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("TAG", "");
            }
        });

        clearTextData();
        layoutSinceDatePicker.setOnClickListener(view -> {
            editText1Selected = true;
            editText2Selected = false;
            openCalendarDialog();
        });


        layoutUntilDatePicker.setOnClickListener(view -> {
            editText2Selected = true;
            editText1Selected = false;
            openCalendarDialog();
        });

        tvReferenceNo.setOnClickListener(view -> {

            tvReferenceNo.setBackgroundResource(R.drawable.middle_navigation_bg);
            tvAmount.setBackgroundResource(0);
            tvAmount.setTextColor(ContextCompat.getColor(this, R.color.slider_heading));
            tvReferenceNo.setTextColor(ContextCompat.getColor(this, R.color.blue_3));
            tvAmountClick = false;
            tvReferenceClick = true;
            etSearchBy.setText("");
        });

        tvAmount.setOnClickListener(view -> {
            tvAmount.setBackgroundResource(R.drawable.middle_navigation_bg);
            tvReferenceNo.setBackgroundResource(0);
            tvReferenceNo.setTextColor(ContextCompat.getColor(this, R.color.slider_heading));
            tvAmount.setTextColor(ContextCompat.getColor(this, R.color.blue_3));
            tvAmountClick = true;
            tvReferenceClick = false;
            etSearchBy.setText("");
        });
        initDate();
        setInitialAndFinalDate();
        selectedFromDateForApi = formattedFromDate;
        selectedToDateForApi = formattedTODate;
        callQRTransaction(mID, formattedFromDate, formattedTODate);
        monthList = utils.getmonthList();
        monthListCap = utils.getmonthListWithCaps();
        daysList = utils.getDaysList();
    }

    private void clearTextData() {
        clearTextView.setOnClickListener(view -> {
            searchInActive.setTag("bg");
            searchInActive.setImageResource(R.drawable.search_in_active);
            etSearchBy.setText("");
            if (clearTextView.getVisibility() == View.VISIBLE) {
                clearTextView.setVisibility(View.GONE);
            }
            if (listFilterLayout.getVisibility() == View.VISIBLE) {
                listFilterLayout.setVisibility(View.GONE);
            }
            if (getCurrentFocus() != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
    }


    public void setInitialAndFinalDate() {
        selectedFinalDate = (GregorianCalendar) getInstance();

        String selectedMonth = "" + (selectedFinalDate.get(MONTH) + 1);
        String date = "" + selectedFinalDate.get(DAY_OF_MONTH);

        currentDateTime = (date.length() == 1 ? ("0" + date) : date) + "/"
                + (selectedMonth.length() == 1 ? ("0" + selectedMonth) : selectedMonth) + "/"
                + selectedFinalDate.get(YEAR);

        selectedFinalDateString = (date.length() == 1 ? ("0" + date) : date) + "/"
                + (selectedMonth.length() == 1 ? ("0" + selectedMonth) : selectedMonth) + "/"
                + selectedFinalDate.get(YEAR);

        selectedInitialDate = getDateOneMonthsBeforeDate(selectedFinalDate);
        selectedMonth = "" + (selectedInitialDate.get(MONTH) + 1);
        date = "" + selectedInitialDate.get(DAY_OF_MONTH);

        selectedInitialDateString = (date.length() == 1 ? ("0" + date) : date) + "/"
                + (selectedMonth.length() == 1 ? ("0" + selectedMonth) : selectedMonth) + "/"
                + selectedInitialDate.get(YEAR);

        formattedFromDate = selectedInitialDate.get(YEAR) + "/"
                + (selectedMonth.length() == 1 ? ("0" + selectedMonth) : selectedMonth) + "/" +
                (date.length() == 1 ? ("0" + date) : date);
        formattedFromDate = selectedInitialDate.get(YEAR) + "/"
                + (selectedMonth.length() == 1 ? ("0" + selectedMonth) : selectedMonth) + "/" +
                (date.length() == 1 ? ("0" + date) : date);

        formattedTODate = selectedFinalDate.get(YEAR) + "/"
                + (selectedMonth.length() == 1 ? ("0" + selectedMonth) : selectedMonth) + "/" +
                (date.length() == 1 ? ("0" + date) : date);

        tvFromActivity.setText(selectedInitialDateString);

        if (!TextUtils.isEmpty(fetchFinal)) {
            String finalDate = utils.getStringFormatted(Constants.DD_MM_YYYY, utils.getDateFormatted(Constants.YYYY_MM_DD, fetchFinal));
            tvToActivity.setText(finalDate);
            selectedFinalDateString = finalDate;
            Date d = utils.getDateFormatted(Constants.YYYY_MM_DD, fetchFinal);
            selectedFinalDate = new GregorianCalendar();
            selectedFinalDate.setTimeInMillis(d.getTime());
        } else {
            tvToActivity.setText(selectedFinalDateString);
        }
    }

    private void getDefaultLocation(String locationResponse) {


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
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    void filterList(CharSequence text, boolean type) {
        if (filterArrayList != null && !filterArrayList.isEmpty()) {
            searchInList(filterArrayList, text, type);
        } else if (loList != null && !loList.isEmpty()) {
            searchInList(loList, text, type);
        } else {
            searchInList(list, text, type);
        }
    }

    void filterForReferenceList(CharSequence text, boolean type) {
        if (filterArrayList != null && !filterArrayList.isEmpty()) {
            searchInList(filterArrayList, text, type);
        } else if (loList != null && !loList.isEmpty()) {
            searchInList(loList, text, type);
        } else {
            searchInList(list, text, type);
        }
    }

    public void searchInList(List<TransactionHistory> mainTransactionList, CharSequence text, Boolean type) {
        temp = new ArrayList<>();
        for (TransactionHistory tr : mainTransactionList) {
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

            lvTransaction.setVisibility(View.VISIBLE);
            tvNoRecordFound.setVisibility(View.GONE);
            if (historyAdapter != null) {
                historyAdapter.updateList(temp);
            } else {
                historyAdapter = new TransactionHistoryAdapter(QrTransactions.this, temp);
            }
            historyAdapter.notifyDataSetChanged();
        } else {
            lvTransaction.setVisibility(View.GONE);
            tvNoRecordFound.setVisibility(View.VISIBLE);
        }

    }

    List<TransactionHistory> loList;

    public void setContent(String content, String lastChildMidQr, int dismissFlag) {
        loList = new ArrayList<>();

        tvSelectedLocation.setText(content);
        mID = lastChildMidQr;
        if (!TextUtils.isEmpty(lastChildMidQr)) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getMerchantId().equalsIgnoreCase(lastChildMidQr)) {
                    loList.add(list.get(i));
                }
            }
            if (!loList.isEmpty()) {
                lvTransaction.setVisibility(View.VISIBLE);
                tvNoRecordFound.setVisibility(View.GONE);
                if (historyAdapter != null) {
                    historyAdapter.updateList(loList);
                } else {
                    Collections.sort(loList, byDate);
                    historyAdapter = new TransactionHistoryAdapter(QrTransactions.this, loList);
                }

                historyAdapter.notifyDataSetChanged();
                lvTransaction.setAdapter(historyAdapter);


            } else {
                lvTransaction.setVisibility(View.GONE);
                tvNoRecordFound.setVisibility(View.VISIBLE);
            }

        }

        if (dismissFlag == 1) {
            homeLocationBottomSheet.dismiss();
        }
    }


    private void openCalendarDialog() {

        calendarDialog = new Dialog(this);
        calendarDialog.setContentView(R.layout.settle_calendar_dialog);
        calendarDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        calendarDialog.setCanceledOnTouchOutside(true);

        calendarTitle = calendarDialog.findViewById(R.id.cal_title);
        btnConfirm = calendarDialog.findViewById(R.id.btnConfirm);
        layoutDateFilter = calendarDialog.findViewById(R.id.layoutDateFilter);
        linearLayout = calendarDialog.findViewById(R.id.rel_days);
        title = calendarDialog.findViewById(R.id.title);
        next = calendarDialog.findViewById(R.id.right);
        previous = calendarDialog.findViewById(R.id.left);
        gridview = calendarDialog.findViewById(R.id.gridview);
        calendarLayout = calendarDialog.findViewById(R.id.calendar_layout);
        fromDateLayout = calendarDialog.findViewById(R.id.fromDateLayout);
        toDateLayout = calendarDialog.findViewById(R.id.toDateLayoutw);
        ivFromIcon = calendarDialog.findViewById(R.id.iv_from_icon);
        ivToIcon = calendarDialog.findViewById(R.id.iv_to_icon);


        calendarDialog.setOnShowListener(dialogInterface -> showCalendarView());

        setDays();
        calendarDialog.show();
    }

    private void initDate() {
        selectedFinalDate = (GregorianCalendar) getInstance();
        selectedInitialDate = getDateOneMonthsBeforeDate(selectedFinalDate);

        String selectedMonthRange = "" + (selectedFinalDate.get(MONTH) + 1);
        String dateRange = "" + selectedFinalDate.get(DAY_OF_MONTH);

        currentDateTime = (dateRange.length() == 1 ? ("0" + dateRange) : dateRange) + "/"
                + (selectedMonthRange.length() == 1 ? ("0" + selectedMonthRange) : selectedMonthRange) + "/"
                + selectedFinalDate.get(YEAR);
        selectedMonthRange = "" + (selectedInitialDate.get(MONTH) + 1);
        dateRange = "" + selectedInitialDate.get(DAY_OF_MONTH);

        selectedInitialDateString = (dateRange.length() == 1 ? ("0" + dateRange) : dateRange) + "/"
                + (selectedMonthRange.length() == 1 ? ("0" + selectedMonthRange) : selectedMonthRange) + "/"
                + selectedInitialDate.get(YEAR);

    }

    public long printDifference(String fromSDate, String toSDate) {
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DD_MM_YYYY, Locale.ENGLISH);
        Date startDate;
        Date endDate;


        try {
            startDate = formatter.parse(fromSDate);
            endDate = formatter.parse(toSDate);

            long different = 0;
            if (startDate != null && endDate != null) {
                different = endDate.getTime() - startDate.getTime();
            }

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;


            newDiff = Math.abs(elapsedDays);

            return newDiff;
        } catch (ParseException e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        return 0;
    }


    public void setDays() {
        final TextView[] txtView = new TextView[7];
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/Gotham-Medium.ttf");
        for (int i = 0; i < 7; i++) {

            txtView[i] = new TextView(this);
            txtView[i].setText(daysList.get(i));
            txtView[i].setTextSize(13);
            txtView[i].setTextColor(ContextCompat.getColor(QrTransactions.this, R.color.time_frame));
            txtView[i].setId(i);
            txtView[i].setPadding(8, 8, 0, 0);
            txtView[i].setTypeface(typeface);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0, MATCH_PARENT,
                    1);
            linearLayout.addView(txtView[i], param);
        }
    }


    public void showCalendarView() {

        Log.d("TAG", "showCalendarView: 111");
        if (editText1Selected) {
            Log.d("TAG", "showCalendarView: 222");
            fromDateBox();
        } else if (editText2Selected) {
            Log.d("TAG", "showCalendarView: 333");
            toDateBox();
        }

        next.setEnabled(true);
        if (editText1Selected) {
            previous.setVisibility(View.VISIBLE);
        }

        calendarAdapter.fechaFinal = selectedFinalDate;
        calendarAdapter.fechaInitial = selectedInitialDate;

        gridview.setAdapter(calendarAdapter);

        calendarOnClickListener();


        previous.setOnClickListener(previousView -> {
            setPreviousMonth();
            next.setVisibility(View.VISIBLE);
            refreshCalendar();
            next.setEnabled(true);
        });

        next.setOnClickListener(nextView -> {
            setNextMonth();
            previous.setVisibility(View.VISIBLE);
            refreshCalendar();
            next.setEnabled(true);

        });
    }

    private void calendarOnClickListener() {
        gridview.setOnItemClickListener((parent, v, position, id) -> {
            desc = new ArrayList<>();
            String selectedGridDate = DialogCalendarAdapter.dayString.get(position);
            String[] separatedTime = selectedGridDate.split("-");
            String gridValueString = separatedTime[2].replaceFirst("^0*", "");
            ((DialogCalendarAdapter) parent.getAdapter()).setSelected(v);
            int gridValue = Integer.parseInt(gridValueString);

            calendarClickOperations(gridValue, position);
            ((DialogCalendarAdapter) parent.getAdapter()).setSelected(v);

            for (int i = 0; i < startDates.size(); i++) {
                if (startDates.get(i).equals(selectedGridDate)) {
                    desc.add(nameOfEvent.get(i));
                }
            }
            desc = null;
        });
    }

    private void calendarClickOperations(int gridValue, int position) {
        if ((gridValue > 10) && (position < 8)) {
            setPreviousMonth();
            refreshCalendar();
        } else if ((gridValue < 7) && (position > 28)) {
            setNextMonth();
            refreshCalendar();
        } else {
            String selectedMonth = "" + (month.get(MONTH) + 1);
            GregorianCalendar selectedDate = (GregorianCalendar) month.clone();
            selectedDate.set(DATE, gridValue);

            String selectedDateString = (gridValue < 10 ? ("0" + gridValue) : gridValue) + "/"
                    + (selectedMonth.length() == 1 ? ("0" + selectedMonth) : selectedMonth) + "/"
                    + month.get(YEAR);
            String datesForApi = month.get(YEAR) + "/" +
                    selectedMonth + "/"
                    + gridValue;

            dateFilterSelector(selectedDateString, selectedDate, datesForApi);
        }
    }

    private void dateFilterSelector(String selectedDateString, GregorianCalendar selectedDate, String datesForApi) {
        if (editText1Selected) {
            fromDateSelectedBox(selectedDateString, selectedDate, datesForApi);

        } else if (editText2Selected) {
            toDateSelectedBox(selectedDateString, selectedDate, datesForApi);
        }
    }

    private void toDateSelectedBox(String selectedDateString, GregorianCalendar selectedDate, String datesForApi) {

        if (utils.isCurrentDayCheck(currentDateTime, selectedDateString)) {
            if (!TextUtils.isEmpty(tvFromActivity.getText().toString())) {
                toDateDifferenceCheck(selectedDateString, selectedDate, datesForApi);
            } else {
                tvToActivity.setText(selectedDateString);
                selectedFinalDate = selectedDate;
            }
            refreshCalendar();
            showCalendarView();
        } else {
            Snackbar snackbar = Snackbar.make(act_qr_transaction,
                    this.getResources().getString(R.string.current_date_compare), SNACK_LENGTH)
                    .setBackgroundTint(Color.parseColor("#0091DF"));
            snackbar.show();
        }
    }

    private void toDateDifferenceCheck(String selectedDateString, GregorianCalendar selectedDate, String datesForApi) {

        String tempDate = tvFromActivity.getText().toString();
        if (utils.isToDateSmaller(tvFromActivity.getText().toString(), selectedDateString)) {
            tvFromActivity.setText(selectedDateString);
            tvToActivity.setText(tempDate);
            selectedFinalDate = selectedInitialDate;
            selectedInitialDate = selectedDate;
            selectedToDateForApi = selectedFromDateForApi;
            selectedFromDateForApi = datesForApi;
        } else {
            tvFromActivity.setText(tempDate);
            tvToActivity.setText(selectedDateString);
            selectedFinalDate = selectedDate;
            selectedToDateForApi = datesForApi;
        }
        callQRTransaction(mID, selectedFromDateForApi, selectedToDateForApi);
        showConfirmBtn();
        editText2Selected = false;

    }

    private void fromDateSelectedBox(String selectedDateString, GregorianCalendar selectedDate, String datesForApi) {
        if (utils.isCurrentDayCheck(currentDateTime, selectedDateString)) {

            if (!TextUtils.isEmpty(tvToActivity.getText().toString())) {

                String tempDate = tvToActivity.getText().toString();
                if (utils.isFromDateSmaller(selectedDateString, tvToActivity.getText().toString())) {
                    tvFromActivity.setText(selectedDateString);
                    tvToActivity.setText(tempDate);
                    selectedInitialDate = selectedDate;

                    selectedFromDateForApi = datesForApi;

                    Log.d("API", "FROM SMALL " + selectedFromDateForApi + " == " + selectedToDateForApi);
                } else {
                    tvFromActivity.setText(tempDate);
                    tvToActivity.setText(selectedDateString);
                    selectedInitialDate = selectedFinalDate;
                    selectedFinalDate = selectedDate;

                    selectedFromDateForApi = selectedToDateForApi;
                    selectedToDateForApi = datesForApi;

                    Log.d("API", "FROM BIG " + selectedFromDateForApi + " == " + selectedToDateForApi);

                }
                callQRTransaction(mID, selectedFromDateForApi, selectedToDateForApi);
                showConfirmBtn();
                editText1Selected = false;

            } else {
                selectedInitialDate = selectedDate;
                tvFromActivity.setText(selectedDateString);
            }


            refreshCalendar();
            showCalendarView();


        } else {
            Snackbar snackbar = Snackbar.make(act_qr_transaction,
                    this.getResources().getString(R.string.current_date_compare), SNACK_LENGTH)
                    .setBackgroundTint(Color.parseColor("#0091DF"));
            snackbar.show();
        }
    }


    private void toDateBox() {
        String year = (String) DateFormat.format(Constants.YYYY, selectedFinalDate);
        month = (GregorianCalendar) selectedFinalDate.clone();
        String spanishMonthName = monthListCap
                .get(Integer.parseInt((String) DateFormat.format(Constants.MM, month)) - 1);
        title.setText(spanishMonthName + " " + year);

        String initialMonth = "" + (selectedInitialDate.get(MONTH) + 1);
        String initialDate = "" + selectedInitialDate.get(DAY_OF_MONTH);

        selectedInitialDateString = (initialDate.length() == 1 ? ("0" + initialDate) : initialDate) + "/"
                + (initialMonth.length() == 1 ? ("0" + initialMonth) : initialMonth) + "/"
                + selectedInitialDate.get(YEAR);
        String selectedMonth = "" + (selectedFinalDate.get(MONTH) + 1);
        String date = "" + selectedFinalDate.get(DAY_OF_MONTH);
        selectedFinalDateString = (date.length() == 1 ? ("0" + date) : date) + "/"
                + (selectedMonth.length() == 1 ? ("0" + selectedMonth) : selectedMonth) + "/"
                + selectedFinalDate.get(YEAR);

        calendarAdapter = new DialogCalendarAdapter(this, month, false, selectedInitialDateString,
                selectedFinalDateString);
        calendarTitle.setText(getResources().getString(R.string.claendar_end_date_title));
    }

    private void fromDateBox() {
        String year = (String) DateFormat.format(Constants.YYYY, selectedInitialDate);
        month = (GregorianCalendar) selectedInitialDate.clone();
        String spanishMonthName = monthListCap
                .get(Integer.parseInt((String) DateFormat.format(Constants.MM, month)) - 1);
        title.setText(spanishMonthName + " " + year);

        String initialMonth = "" + (selectedInitialDate.get(MONTH) + 1);
        String initialDate = "" + selectedInitialDate.get(DAY_OF_MONTH);

        selectedInitialDateString = (initialDate.length() == 1 ? ("0" + initialDate) : initialDate) + "/"
                + (initialMonth.length() == 1 ? ("0" + initialMonth) : initialMonth) + "/"
                + selectedInitialDate.get(YEAR);

        String selectedMonth = "" + (selectedFinalDate.get(MONTH) + 1);
        String date = "" + selectedFinalDate.get(DAY_OF_MONTH);

        selectedFinalDateString = (date.length() == 1 ? ("0" + date) : date) + "/"
                + (selectedMonth.length() == 1 ? ("0" + selectedMonth) : selectedMonth) + "/"
                + selectedFinalDate.get(YEAR);

        calendarAdapter = new DialogCalendarAdapter(this, month, false, selectedInitialDateString,
                selectedFinalDateString);
        calendarTitle.setText(getResources().getString(R.string.claendar_start_date_title));
    }


    private void showConfirmBtn() {
        if (calendarDialog != null && calendarDialog.isShowing()) {
            calendarDialog.dismiss();
        }
    }


    public void refreshCalendar() {
        calendarAdapter.refreshDays();
        calendarAdapter.notifyDataSetChanged();
        String spanishMonthName = monthListCap
                .get(Integer.parseInt((String) DateFormat.format(Constants.MM, month)) - 1);
        String year = (String) DateFormat.format(Constants.YYYY, month);
        String fullDate = spanishMonthName + " " + year;
        title.setText(fullDate);
    }

    protected void setNextMonth() {
        calendarAdapter.refreshDays();
        calendarAdapter.notifyDataSetChanged();
        if (month.get(MONTH) == month.getActualMaximum(MONTH)) {
            month.set((month.get(YEAR) + 1), month.getActualMinimum(MONTH), 1);
        } else {
            month.set(MONTH, month.get(MONTH) + 1);
        }
    }

    protected void setPreviousMonth() {
        calendarAdapter.refreshDays();
        calendarAdapter.notifyDataSetChanged();
        if (month.get(MONTH) == month.getActualMinimum(MONTH)) {
            month.set((month.get(YEAR) - 1), month.getActualMaximum(MONTH), 1);
        } else {
            month.set(MONTH, month.get(MONTH) - 1);
        }
    }

    public GregorianCalendar getDateOneMonthsBeforeDate(GregorianCalendar date) {
        return new GregorianCalendar(date.get(YEAR), (date.get(MONTH)),
                date.get(DAY_OF_MONTH));
    }

    String showDate;


    private void hidShow() {
        String backgroundImageName = String.valueOf(searchInActive.getTag());

        if (backgroundImageName.equalsIgnoreCase("bg")) {
            searchInActive.setTag(R.drawable.ic_search_active);
            searchInActive.setImageResource(R.drawable.ic_search_active);
        }

        if (listFilterLayout.getVisibility() == View.GONE) {
            listFilterLayout.setVisibility(View.VISIBLE);
        }

        if (clearTextView.getVisibility() == View.GONE) {
            clearTextView.setVisibility(View.VISIBLE);
        }
    }

    private void callQRTransaction(String locationId, String fromDateString, String toDateString) {

        JSONObject json = new JSONObject();
        try {
            String tcpKey = ((AzulApplication) this.getApplication()).getTcpKey();
            String vcr = ((AzulApplication) this.getApplication()).getVcr();
            json.put("tcp", RSAHelper.ecryptRSA(QrTransactions.this, tcpKey));
            JSONObject payload = new JSONObject();
            payload.put("device", DeviceUtils.getDeviceId(this));
            payload.put("dateFrom", fromDateString);
            payload.put("dateTo", toDateString);
            payload.put("merchantIdList", locationId);
            Log.d("TAG", "callQRTransaction: " + payload.toString());

            json.put("payload", RSAHelper.encryptAES(payload.toString(), Base64.decode(tcpKey, 0), Base64.decode(vcr, 0)));

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.QR_TRANSACTION, json);
    }

    public void qrTransactionResponse(String responseData) {
        try {
            JSONObject jsonObject = new JSONObject(responseData);
            Log.d("QR DATA", "DATA ==>  " + jsonObject.toString());
            parseTransactionData(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseTransactionData(JSONObject jsonObject) {
        list = new ArrayList<>();
        transactionFilteredList = new ArrayList<>();

        try {
            JSONObject jsonTransactionOb = jsonObject.getJSONObject(Constants.QR_TRANSACTION_JSON_LBL);
            JSONArray jsonArray = jsonTransactionOb.getJSONArray("Transactions");
            if (jsonArray.toString().equalsIgnoreCase("[]")) {
                lvTransaction.setVisibility(View.GONE);
                tvNoRecordFound.setVisibility(View.VISIBLE);
            } else {
                transactionParse(jsonArray);
            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }

    }

    private void transactionParse(JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonTrObject = jsonArray.getJSONObject(i);

                String amount = jsonTrObject.getString("Amount");
                String referenceNo = jsonTrObject.getString("TransactionNumber");
                String trTime = jsonTrObject.getString("TransactionTime");
                String trDate = jsonTrObject.getString("TransactionDate");
                String merchantId = jsonTrObject.getString("MerchantId");
                String location = jsonTrObject.getString("MerchantName");

                list.add(new TransactionHistory(referenceNo, location, trDate, trTime, amount, merchantId, showDate));

            }


            if (!TextUtils.isEmpty(mID)) {
                setAdapter();
            } else {
                Collections.sort(list, byDate);
                historyAdapter = new TransactionHistoryAdapter(QrTransactions.this, list);
                lvTransaction.setAdapter(historyAdapter);
            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void setAdapter() {
        for (int i = 0; i < list.size(); i++) {
            setArrayListWithMid(i, mID, list);
        }

        if (!transactionFilteredList.isEmpty()) {
            Collections.sort(transactionFilteredList, byDate);

            historyAdapter = new TransactionHistoryAdapter(QrTransactions.this, transactionFilteredList);
            lvTransaction.setVisibility(View.VISIBLE);
            tvNoRecordFound.setVisibility(View.GONE);
            lvTransaction.setAdapter(historyAdapter);

        } else {
            lvTransaction.setVisibility(View.GONE);
            tvNoRecordFound.setVisibility(View.VISIBLE);
        }
    }

    private void setArrayListWithMid(int i, String mID, List<TransactionHistory> list) {
        if (list.get(i).getMerchantId().equalsIgnoreCase(mID)) {
            transactionFilteredList.add(list.get(i));
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
            } catch (ParseException e) {
                Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
            }

            if (inputDateT11Older != null && inputDateT2Older != null) {
                return (inputDateT11Older.getTime() > inputDateT2Older.getTime() ? -1 : 1);
            }

            return 0;

        }
    };
}