package com.sdp.appazul.activities.transactions;

import androidx.appcompat.app.AppCompatActivity;
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
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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
import com.sdp.appazul.activities.payment.PaymentLocationFilter;
import com.sdp.appazul.adapters.DialogCalendarAdapter;
import com.sdp.appazul.adapters.SettleTransactionAdapter;
import com.sdp.appazul.api.ApiManager;
import com.sdp.appazul.api.ServiceUrls;
import com.sdp.appazul.classes.SettleTransaction;
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

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
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

public class SettledTransactionsQuery extends AppCompatActivity {
    ListView listTransactions;
    TextView tvNoRecordFound;

    SettleTransactionAdapter adapter;
    List<SettleTransaction> list;
    RelativeLayout topBarLayout;
    RelativeLayout layoutSearchByEdittext;
    RelativeLayout locationFilterForTran;
    EditText etSearchBy;
    ImageView clearEnteredText;
    TextView tvLotNo;
    TextView tvApprovalNo;
    TextView tvTerminalNo;
    TextView tvCardNo;
    boolean lotNoClick = true;
    boolean approvalNoClick = false;
    boolean cardNoClick = false;
    boolean terminalNoClick = false;
    List<SettleTransaction> tempList;
    Dialog calendarDialog;
    TextView tvFromDate;
    TextView tvToDate;
    TextView calendarTitle;
    Button btnConfirm;
    LinearLayout layoutDateFilter;
    LinearLayout linearLayout;
    TextView title;
    DialogCalendarAdapter calendarAdapter;
    ImageView next;
    ImageView previous;
    GridView gridview;
    RelativeLayout calendarLayout;
    LinearLayout fromDateLayout;
    LinearLayout toDateLayout;
    ImageView ivFromIcon;
    ImageView ivToIcon;
    boolean editText1Selected = false;
    boolean editText2Selected = false;
    TextView tvFromActivity;
    TextView tvToActivity;
    GregorianCalendar selectedInitialDate;
    long newDiff;
    List<String> daysList = new ArrayList<>();
    List<String> monthList = new ArrayList<>();
    List<String> monthListCap = new ArrayList<>();
    GregorianCalendar month;
    String selectedInitialDateString = "";
    String selectedFinalDateString = "";
    GregorianCalendar selectedFinalDate;
    DateUtils utils = new DateUtils();
    String currentDateTime = "";
    String selectedFromDateForApi = "";
    String selectedToDateForApi = "";
    ArrayList<String> desc;
    ArrayList<String> startDates = new ArrayList<>();
    ArrayList<String> nameOfEvent = new ArrayList<>();
    ArrayList<SettleTransaction> filteredArrayList = new ArrayList<>();
    String fetchFinal = "";
    String locationJson;
    ImageView btnSettleTrBack;
    ImageView settleTranLocationSelector;
    boolean submenuVisibility;
    PaymentLocationFilter locationsBottomSheet;
    SharedPreferences sharedPreferences;
    String mID = "";
    TextView tvSelectedSettleLocation;
    ImageView activeSearchImage;
    Map<String, String> defaultLocation;
    Map<String, String> defaultLocations = new HashMap<>();
    LinearLayout layoutSearch;
    ApiManager apiManager = new ApiManager(SettledTransactionsQuery.this);
    RelativeLayout act_settle_transaction;
    static int SNACK_LENGTH = 0;
    List<SettleTransaction> transactionFilteredList;
    SimpleDateFormat olderFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settled_transactions_query);
        initControls();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initControls() {
        act_settle_transaction = findViewById(R.id.act_settle_transaction);
        activeSearchImage = findViewById(R.id.activeSearchImage);
        tvSelectedSettleLocation = findViewById(R.id.tvSelectedSettleLocation);
        Intent intent = getIntent();

        locationJson = intent.getStringExtra(Constants.LOCATION_RESPONSE);
        sharedPreferences = ((AzulApplication) this.getApplication()).getPrefs();

        String lastLocationName = sharedPreferences.getString(Constants.SELECTED_LOCATION_DASHBOARD, "");
        mID = sharedPreferences.getString(Constants.SELECTED_LOCATION_MID_DASHBOARD, "");


        if (lastLocationName != null && !TextUtils.isEmpty(lastLocationName)) {
            tvSelectedSettleLocation.setText(lastLocationName);
        } else {
            getDefaultLocation(locationJson);

            defaultLocations = ((AzulApplication) this.getApplication()).getDefaultLocation();

            if (defaultLocations != null) {
                String defaultLocationName = defaultLocations.get("CHILD_LOC_NAME");
                String defaultLocationId = defaultLocations.get("CHILD_LOC_ID");
                String locationToDisplay = defaultLocationName + " - " + defaultLocationId;
                tvSelectedSettleLocation.setText(locationToDisplay);
                mID = defaultLocationId;
            }
        }

        btnSettleTrBack = findViewById(R.id.btnSettleTrBack);
        settleTranLocationSelector = findViewById(R.id.SettleTranLocationSelector);
        locationFilterForTran = findViewById(R.id.locaFilterForTran);
        tvLotNo = findViewById(R.id.tvLotNo);
        tvApprovalNo = findViewById(R.id.tvApprovalNo);
        tvCardNo = findViewById(R.id.tvCardNo);
        tvTerminalNo = findViewById(R.id.tvTerminalNo);
        tvLotNo.setOnClickListener(tvLotNoView -> filterWithLotNo());
        tvApprovalNo.setOnClickListener(tvLotNoView -> filterWithApprovalNo());
        tvCardNo.setOnClickListener(tvLotNoView -> filterWithCardNo());
        tvTerminalNo.setOnClickListener(tvLotNoView -> filterWithTerminalNo());
        clearEnteredText = findViewById(R.id.clearEnteredText);
        etSearchBy = findViewById(R.id.etSearchBy);
        layoutSearchByEdittext = findViewById(R.id.layoutSearchByEdittext);
        topBarLayout = findViewById(R.id.topBarLayout);
        tvFromActivity = findViewById(R.id.tvFromActivity);
        tvToActivity = findViewById(R.id.tvToActivity);
        layoutSearch = findViewById(R.id.layoutSearch);
        layoutSearch.setOnClickListener(view -> showSearchBar());

        btnSettleTrBack.setOnClickListener(view -> {
            finish();
            Intent intent1 = new Intent(SettledTransactionsQuery.this, DashBoardActivity.class);
            intent1.putExtra(Constants.LOCATION_RESPONSE, locationJson);
            startActivityForResult(intent1, 1);
            this.overridePendingTransition(R.anim.animation_leave,
                    R.anim.slide_nothing);
        });

        locationFilterForTran.setOnClickListener(btnBurgerMenuView -> {
            settleTranLocationSelector.setRotation(submenuVisibility ? 180 : 0);
//            locationsBottomSheet = new LocationFilterBottomSheet(locationJson, "SettledTransactionsQuery");
            locationsBottomSheet = new PaymentLocationFilter(locationJson, "SettledTransactionsQuery", 0);
            locationsBottomSheet.show(getSupportFragmentManager(), "bottomSheet");
        });

        etSearchBy.setOnTouchListener((view, motionEvent) -> {
            if (MotionEvent.ACTION_UP == motionEvent.getAction())
                showSearchBar();
            return false;
        });

        etSearchBy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("TAG", "");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (Boolean.TRUE.equals(lotNoClick)) {
                    filterList(charSequence, 0);
                } else if (Boolean.TRUE.equals(approvalNoClick)) {
                    filterList(charSequence, 1);
                } else if (Boolean.TRUE.equals(cardNoClick)) {
                    filterList(charSequence, 2);
                } else if (Boolean.TRUE.equals(terminalNoClick)) {
                    filterList(charSequence, 3);
                } else {
                    Log.d("TAG", "");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("TAG", "");

            }
        });

        clearEnteredText.setOnClickListener(clearEnteredTextView ->
                hideSearchBar());


        listTransactions = findViewById(R.id.listTransactions);
        tvNoRecordFound = findViewById(R.id.tvNoRecordFound);
        tvFromActivity.setOnClickListener(tvFromActivityView -> {
                    editText1Selected = true;
                    editText2Selected = false;
                    openCalendarDialog();
                }
        );
        tvToActivity.setOnClickListener(tvToActivityView -> {
                    editText2Selected = true;
                    editText1Selected = false;
                    openCalendarDialog();
                }
        );
        initDate();
        setInitialAndFinalDate();
        selectedFromDateForApi = formattedFromDate;
        selectedToDateForApi = formattedTODate;

        callQRTransaction(formattedFromDate, formattedTODate, mID);
        monthList = utils.getmonthList();
        monthListCap = utils.getmonthListWithCaps();
        daysList = utils.getDaysList();

    }


    private void getDefaultLocation(String locationResponse) {

        try {
            JSONObject jsonResponse = new JSONObject(locationResponse);
            JSONArray parentLevelLocations = jsonResponse.getJSONArray("AssignedUnits");
            for (int i = 0; i < parentLevelLocations.length(); i++) {
                JSONObject parentData = parentLevelLocations.getJSONObject(i);
                String parentLocationName = parentData.getString("Name");
                JSONArray assignedLocationsObject = parentData.getJSONArray("AssignedLocations");
                for (int j = 0; j < assignedLocationsObject.length(); j++) {

                    JSONObject secondPositionLocation = assignedLocationsObject.getJSONObject(0);
                    String secondPositionLocationId = secondPositionLocation.getString(Constants.MERCHANT_ID);
                    String secondPositionLocationName = secondPositionLocation.getString("Name");
                    defaultLocation = new HashMap<>();
                    defaultLocation.put("PARENT_LOC_NAME", parentLocationName);
                    defaultLocation.put("CHILD_LOC_ID", secondPositionLocationId);
                    defaultLocation.put("CHILD_LOC_NAME", secondPositionLocationName);
                    ((AzulApplication) (this).getApplication()).setDefaultLocation(defaultLocation);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void filterList(CharSequence charSequence, int type) {

        if (loList != null && !loList.isEmpty()) {
            searchingInList(loList, charSequence, type);
        } else if (filteredArrayList != null && !filteredArrayList.isEmpty()) {
            searchingInList(filteredArrayList, charSequence, type);
        } else {
            searchingInList(list, charSequence, type);
        }
    }

    private void openCalendarDialog() {

        calendarDialog = new Dialog(this);
        calendarDialog.setContentView(R.layout.settle_calendar_dialog);
        calendarDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        calendarDialog.setCanceledOnTouchOutside(true);

        tvFromDate = calendarDialog.findViewById(R.id.tvFromDate);
        tvToDate = calendarDialog.findViewById(R.id.tvToDate);
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

        if (!TextUtils.isEmpty(tvFromActivity.getText().toString()) && !TextUtils.isEmpty(tvToActivity.getText().toString())) {
            tvFromDate.setText(tvFromActivity.getText().toString());
            tvToDate.setText(tvToActivity.getText().toString());
            btnConfirm.setEnabled(true);
            btnConfirm.setBackgroundResource(R.drawable.btn_confirm_gradient);
            btnConfirm.setTextColor(ContextCompat.getColor(SettledTransactionsQuery.this, R.color.white_bg));
        }

        calendarDialog.setOnShowListener(dialogInterface -> showCalendar());

        btnConfirm.setOnClickListener(view -> {

            tvFromActivity.setText(tvFromDate.getText().toString());
            tvToActivity.setText(tvToDate.getText().toString());

//            if (!TextUtils.isEmpty(selectedFromDateForApi) && !TextUtils.isEmpty(selectedToDateForApi)) {
            callQRTransaction(selectedFromDateForApi, selectedToDateForApi, mID);
//            }
            if (calendarDialog != null && calendarDialog.isShowing()) {
                calendarDialog.dismiss();
            }
        });

        setDays();
        calendarDialog.show();
    }


    private void callQRTransaction(String fromDateString, String toDateString, String mID) {
        Log.d("API CALL", "SET TRANS == " + fromDateString + " === " + toDateString + " === " + mID);
        JSONObject json = new JSONObject();
        try {
            String tcpKey = ((AzulApplication) this.getApplication()).getTcpKey();
            String vcr = ((AzulApplication) this.getApplicationContext()).getVcr();
            json.put("tcp", RSAHelper.ecryptRSA(SettledTransactionsQuery.this, tcpKey));
            JSONObject payload = new JSONObject();
            payload.put("device", DeviceUtils.getDeviceId(this));
            payload.put("dateFrom", fromDateString);
//            payload.put("dateFrom", "2020-04-01");
            payload.put("dateTo", toDateString);
//            payload.put("dateTo", "2021-08-08");
            payload.put("type", "liquidated");
            payload.put("merchantIdList", mID);
//            payload.put("merchantIdList", "67703161554");

            json.put("payload", RSAHelper.encryptAES(payload.toString(), Base64.decode(tcpKey, 0), Base64.decode(vcr, 0)));


        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.SETTLEMENT_TRANSACTION, json);
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

        formattedFromDate = selectedInitialDate.get(YEAR) + "-"
                + (selectedMonth.length() == 1 ? ("0" + selectedMonth) : selectedMonth) + "-" +
                (date.length() == 1 ? ("0" + date) : date);

        formattedTODate = selectedFinalDate.get(YEAR) + "-"
                + (selectedMonth.length() == 1 ? ("0" + selectedMonth) : selectedMonth) + "-" +
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

    String formattedFromDate;
    String formattedTODate;

    public void setDays() {
        final TextView[] txtView = new TextView[7];
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/Gotham-Medium.ttf");
        for (int i = 0; i < 7; i++) {

            txtView[i] = new TextView(this);
            txtView[i].setText(daysList.get(i));
            txtView[i].setTextSize(13);
            txtView[i].setTextColor(ContextCompat.getColor(SettledTransactionsQuery.this, R.color.time_frame));
            txtView[i].setId(i);
            txtView[i].setPadding(8, 8, 0, 0);
            txtView[i].setTypeface(typeface);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0, MATCH_PARENT,
                    1);
            linearLayout.addView(txtView[i], param);
        }
    }

    public void showCalendar() {

        if (editText1Selected) {
            fromDateOperations(selectedInitialDate, selectedFinalDate);

        } else if (editText2Selected) {
            toDateOperations(selectedInitialDate, selectedFinalDate);

        }

        next.setEnabled(true);
        if (editText1Selected) {
            previous.setVisibility(View.VISIBLE);
        }

        calendarAdapter.fechaFinal = selectedFinalDate;
        calendarAdapter.fechaInitial = selectedInitialDate;

        gridview.setAdapter(calendarAdapter);

        gridview.setOnItemClickListener((parent, v, position, id) ->
                onCalendarClickListener(parent, v, position)
        );


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

    private void onCalendarClickListener(AdapterView<?> parent, View v, int position) {
        desc = new ArrayList<>();
        String selectedGridDate = DialogCalendarAdapter.dayString.get(position);
        String[] separatedTime = selectedGridDate.split("-");
        String gridValueString = separatedTime[2].replaceFirst("^0*", "");
        ((DialogCalendarAdapter) parent.getAdapter()).setSelected(v);
        int gridValue = Integer.parseInt(gridValueString);
        if ((gridValue > 10) && (position < 8)) {
            setPreviousMonth();
            refreshCalendar();
        } else if ((gridValue < 7) && (position > 28)) {
            setNextMonth();
            refreshCalendar();
        } else {
            onDaySelector(gridValue);

        }
        ((DialogCalendarAdapter) parent.getAdapter()).setSelected(v);

        for (int i = 0; i < startDates.size(); i++) {
            if (startDates.get(i).equals(selectedGridDate)) {
                desc.add(nameOfEvent.get(i));
            }
        }
        desc = null;
    }

    private void onDaySelector(int gridValue) {
        String selectedMonth = "" + (month.get(MONTH) + 1);
        GregorianCalendar calendarInstance = (GregorianCalendar) month.clone();
        calendarInstance.set(DATE, gridValue);

        String selectedDateString = (gridValue < 10 ? ("0" + gridValue) : gridValue) + "/"
                + (selectedMonth.length() == 1 ? ("0" + selectedMonth) : selectedMonth) + "/"
                + month.get(YEAR);
        String datesForApi = month.get(YEAR) + "-" +
                (selectedMonth.length() == 1 ? ("0" + selectedMonth) : selectedMonth) + "-"
                + (gridValue < 10 ? ("0" + gridValue) : gridValue);


        if (editText1Selected) {
            fromDateSelector(selectedDateString, calendarInstance, datesForApi);

        } else if (editText2Selected) {
            toDateSelector(selectedDateString, calendarInstance, datesForApi);

        }
    }

    private void toDateSelector(String selectedDateString, GregorianCalendar gregorianCalendar, String datesForApi) {
        if (utils.isCurrentDayCheck(currentDateTime, selectedDateString)) {
            if (!TextUtils.isEmpty(tvFromActivity.getText().toString())) {

                String tempDate = tvFromActivity.getText().toString();
                if (utils.isToDateSmaller(tvFromActivity.getText().toString(), selectedDateString)) {
                    tvFromActivity.setText(selectedDateString);
                    tvToActivity.setText(tempDate);
                    selectedFinalDate = selectedInitialDate;
                    selectedInitialDate = gregorianCalendar;

                    selectedToDateForApi = selectedFromDateForApi;
                    selectedFromDateForApi = datesForApi;
                    Log.d("API", "TO SMALL " + selectedFromDateForApi + " == " + selectedToDateForApi);

                } else {
                    tvFromActivity.setText(tempDate);
                    tvToActivity.setText(selectedDateString);
                    selectedFinalDate = gregorianCalendar;
                    selectedToDateForApi = datesForApi;
                    Log.d("API", "TO BIG " + selectedFromDateForApi + " == " + selectedToDateForApi);

                }
                callQRTransaction(selectedFromDateForApi, selectedToDateForApi, mID);
                showConfirmBtn();
                editText2Selected = false;
            } else {
                tvToActivity.setText(selectedDateString);
                selectedFinalDate = gregorianCalendar;
            }


            refreshCalendar();
            showCalendar();


        } else {
            Snackbar snackbar = Snackbar.make(act_settle_transaction, this.getResources().getString(R.string.current_date_compare), SNACK_LENGTH)
                    .setBackgroundTint(Color.parseColor("#0091DF"));
            snackbar.show();
        }
    }

    private void fromDateSelector(String selectedDateString, GregorianCalendar selectedDate, String datesForApi) {
        if (utils.isCurrentDayCheck(currentDateTime, selectedDateString)) {

            if (!TextUtils.isEmpty(tvToActivity.getText().toString())) {

                String tempDate = tvToActivity.getText().toString();
                if (utils.isFromDateSmaller(selectedDateString, tvToActivity.getText().toString())) {
                    tvFromActivity.setText(selectedDateString);
                    tvToActivity.setText(tempDate);
                    selectedInitialDate = selectedDate;

                    selectedFromDateForApi = datesForApi;

                } else {
                    tvFromActivity.setText(tempDate);
                    tvToActivity.setText(selectedDateString);
                    selectedInitialDate = selectedFinalDate;
                    selectedFinalDate = selectedDate;

                    selectedFromDateForApi = selectedToDateForApi;
                    selectedToDateForApi = datesForApi;


                }
                callQRTransaction(selectedFromDateForApi, selectedToDateForApi, mID);
                showConfirmBtn();
                editText1Selected = false;
            } else {
                selectedInitialDate = selectedDate;
                tvFromActivity.setText(selectedDateString);
            }

            refreshCalendar();
            showCalendar();

        } else {

            Snackbar snackbar = Snackbar.make(act_settle_transaction, this.getResources().getString(R.string.current_date_compare), SNACK_LENGTH)
                    .setBackgroundTint(Color.parseColor("#0091DF"));
            snackbar.show();
        }
    }

    private void toDateOperations(GregorianCalendar insideSelectedInitialDate, GregorianCalendar insideSelectedFinalDate) {
        String year = (String) DateFormat.format(Constants.YYYY, this.selectedFinalDate);
        month = (GregorianCalendar) insideSelectedFinalDate.clone();
        String spanishMonthName = monthListCap
                .get(Integer.parseInt((String) DateFormat.format(Constants.MM, month)) - 1);
        String toDateTitle = spanishMonthName + " " + year;
        title.setText(toDateTitle);

        String initialMonth = "" + (insideSelectedInitialDate.get(MONTH) + 1);
        String initialDate = "" + insideSelectedInitialDate.get(DAY_OF_MONTH);

        selectedInitialDateString = (initialDate.length() == 1 ? ("0" + initialDate) : initialDate) + "/"
                + (initialMonth.length() == 1 ? ("0" + initialMonth) : initialMonth) + "/"
                + insideSelectedInitialDate.get(YEAR);
        String selectedMonthAsStr = "" + (insideSelectedFinalDate.get(MONTH) + 1);
        String date = "" + insideSelectedFinalDate.get(DAY_OF_MONTH);
        selectedFinalDateString = (date.length() == 1 ? ("0" + date) : date) + "/"
                + (selectedMonthAsStr.length() == 1 ? ("0" + selectedMonthAsStr) : selectedMonthAsStr) + "/"
                + insideSelectedFinalDate.get(YEAR);

        calendarAdapter = new DialogCalendarAdapter(this, month, false, selectedInitialDateString,
                selectedFinalDateString);
    }

    private void fromDateOperations(GregorianCalendar insideSelectedInitialDate, GregorianCalendar insideSelectedFinalDate) {
        String year = (String) DateFormat.format(Constants.YYYY, insideSelectedInitialDate);
        month = (GregorianCalendar) insideSelectedInitialDate.clone();
        String spanishMonthName = monthListCap
                .get(Integer.parseInt((String) DateFormat.format(Constants.MM, month)) - 1);
        String fromDateTitle = spanishMonthName + " " + year;
        title.setText(fromDateTitle);

        String initialMonth = "" + (insideSelectedInitialDate.get(MONTH) + 1);
        String initialDate = "" + insideSelectedInitialDate.get(DAY_OF_MONTH);

        selectedInitialDateString = (initialDate.length() == 1 ? ("0" + initialDate) : initialDate) + "/"
                + (initialMonth.length() == 1 ? ("0" + initialMonth) : initialMonth) + "/"
                + insideSelectedInitialDate.get(YEAR);

        String selectedMonth = "" + (insideSelectedFinalDate.get(MONTH) + 1);
        String date = "" + insideSelectedFinalDate.get(DAY_OF_MONTH);

        selectedFinalDateString = (date.length() == 1 ? ("0" + date) : date) + "/"
                + (selectedMonth.length() == 1 ? ("0" + selectedMonth) : selectedMonth) + "/"
                + insideSelectedFinalDate.get(YEAR);
        calendarAdapter = new DialogCalendarAdapter(this, month, false, selectedInitialDateString,
                selectedFinalDateString);
    }

    private void showConfirmBtn() {
        if (calendarDialog != null && calendarDialog.isShowing()) {
            calendarDialog.dismiss();
        }
    }

    public long printDifference(String fromSDate, String toSDate) {
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DD_MM_YYYY, Locale.ENGLISH);
        Date startDate;
        Date endDate;
        try {
            startDate = formatter.parse(fromSDate);
            endDate = formatter.parse(toSDate);

            long different = endDate.getTime() - startDate.getTime();
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


    @SuppressLint("NewApi")
    private void searchingInList(List<SettleTransaction> mainList, CharSequence charSequence,
                                 int sType) {
        tempList = new ArrayList<>();
        for (SettleTransaction transaction : mainList) {
            if (sType == 0 && transaction.getLotNo().trim().contains(charSequence)) {
                tempList.add(transaction);
            } else if (sType == 1 && transaction.getApprovalNo().trim().contains(charSequence)) {
                tempList.add(transaction);
            } else if (sType == 2 && transaction.getCardNumber().trim().contains(charSequence)) {
                tempList.add(transaction);
            } else if (sType == 3 && transaction.getTerminalId().trim().contains(charSequence)) {
                tempList.add(transaction);
            } else {
                Log.d("TAG", "");
            }
        }
        if (tempList != null && !tempList.isEmpty()) {
            sortTempList();
        } else {
            listTransactions.setVisibility(View.GONE);
            tvNoRecordFound.setVisibility(View.VISIBLE);
        }
    }

    private void sortTempList() {

        listTransactions.setVisibility(View.VISIBLE);
        tvNoRecordFound.setVisibility(View.GONE);

        if (adapter != null) {
            Collections.sort(tempList, byDate);
            adapter.updateList(tempList);
            adapter.notifyDataSetChanged();
        }
    }

    private void filterWithLotNo() {
        tvLotNo.setBackgroundResource(R.drawable.middle_navigation_bg);
        tvLotNo.setTextColor(ContextCompat.getColor(this, R.color.blue_3));
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        tvApprovalNo.setBackgroundResource(0);
        tvCardNo.setBackgroundResource(0);
        tvTerminalNo.setBackgroundResource(0);
        tvApprovalNo.setTextColor(ContextCompat.getColor(this, R.color.slider_heading));
        tvCardNo.setTextColor(ContextCompat.getColor(this, R.color.slider_heading));
        tvTerminalNo.setTextColor(ContextCompat.getColor(this, R.color.slider_heading));

        lotNoClick = true;
        approvalNoClick = false;
        cardNoClick = false;
        terminalNoClick = false;
        etSearchBy.setInputType(InputType.TYPE_CLASS_NUMBER);
        etSearchBy.setText("");
    }

    private void filterWithApprovalNo() {
        tvApprovalNo.setBackgroundResource(R.drawable.middle_navigation_bg);
        tvApprovalNo.setTextColor(ContextCompat.getColor(this, R.color.blue_3));
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        tvLotNo.setBackgroundResource(0);
        tvCardNo.setBackgroundResource(0);
        tvTerminalNo.setBackgroundResource(0);

        tvLotNo.setTextColor(ContextCompat.getColor(this, R.color.slider_heading));
        tvCardNo.setTextColor(ContextCompat.getColor(this, R.color.slider_heading));
        tvTerminalNo.setTextColor(ContextCompat.getColor(this, R.color.slider_heading));

        lotNoClick = false;
        approvalNoClick = true;
        cardNoClick = false;
        terminalNoClick = false;
        etSearchBy.setInputType(InputType.TYPE_CLASS_TEXT);
        etSearchBy.setEnabled(true);
        etSearchBy.setText("");
    }

    private void filterWithCardNo() {
        tvCardNo.setBackgroundResource(R.drawable.middle_navigation_bg);
        tvCardNo.setTextColor(ContextCompat.getColor(this, R.color.blue_3));
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        tvLotNo.setBackgroundResource(0);
        tvApprovalNo.setBackgroundResource(0);
        tvTerminalNo.setBackgroundResource(0);

        tvLotNo.setTextColor(ContextCompat.getColor(this, R.color.slider_heading));
        tvApprovalNo.setTextColor(ContextCompat.getColor(this, R.color.slider_heading));
        tvTerminalNo.setTextColor(ContextCompat.getColor(this, R.color.slider_heading));

        lotNoClick = false;
        approvalNoClick = false;
        cardNoClick = true;
        terminalNoClick = false;
        etSearchBy.setInputType(InputType.TYPE_CLASS_NUMBER);
        etSearchBy.setEnabled(true);
        etSearchBy.setText("");
    }

    private void filterWithTerminalNo() {
        tvTerminalNo.setBackgroundResource(R.drawable.middle_navigation_bg);
        tvTerminalNo.setTextColor(ContextCompat.getColor(this, R.color.blue_3));

        tvLotNo.setBackgroundResource(0);
        tvApprovalNo.setBackgroundResource(0);
        tvCardNo.setBackgroundResource(0);

        tvLotNo.setTextColor(ContextCompat.getColor(this, R.color.slider_heading));
        tvApprovalNo.setTextColor(ContextCompat.getColor(this, R.color.slider_heading));
        tvCardNo.setTextColor(ContextCompat.getColor(this, R.color.slider_heading));
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        lotNoClick = false;
        approvalNoClick = false;
        cardNoClick = false;
        terminalNoClick = true;
        etSearchBy.setInputType(InputType.TYPE_CLASS_NUMBER);
        etSearchBy.setText("");
        etSearchBy.setEnabled(true);
    }

    private void showSearchBar() {

        if (topBarLayout.getVisibility() == View.GONE) {
            topBarLayout.setVisibility(View.VISIBLE);
        }
        if (clearEnteredText.getVisibility() == View.GONE) {
            clearEnteredText.setVisibility(View.VISIBLE);
        }
        String backgroundImageName = String.valueOf(activeSearchImage.getTag());
        if (backgroundImageName.equalsIgnoreCase("bg")) {
            activeSearchImage.setTag(R.drawable.ic_search_active);
            activeSearchImage.setImageResource(R.drawable.ic_search_active);
        }
    }

    private void hideSearchBar() {
        activeSearchImage.setTag("bg");
        activeSearchImage.setImageResource(R.drawable.search_in_active);
        if (topBarLayout.getVisibility() == View.VISIBLE) {
            topBarLayout.setVisibility(View.GONE);
        }
        if (clearEnteredText.getVisibility() == View.VISIBLE) {
            clearEnteredText.setVisibility(View.GONE);
        }
        etSearchBy.setText("");
    }


    List<SettleTransaction> loList;

    @SuppressLint("NewApi")
    public void setContent(String content, String lastChildMidQr, int dismissFlag) {
        loList = new ArrayList<>();
        tvSelectedSettleLocation.setText(content);
        if (dismissFlag == 1) {
            locationsBottomSheet.dismiss();
            callQRTransaction(selectedFromDateForApi, selectedToDateForApi, lastChildMidQr);
        }

        mID = lastChildMidQr;
        if (!TextUtils.isEmpty(lastChildMidQr)) {
            locationSelector(lastChildMidQr);
        }


    }

    private void locationSelector(String lastChildMidQr) {
        if (filteredArrayList != null && !filteredArrayList.isEmpty()) {
            for (int i = 0; i < filteredArrayList.size(); i++) {
                if (filteredArrayList.get(i).getMerchantId().equalsIgnoreCase(lastChildMidQr)) {
                    loList.add(filteredArrayList.get(i));
                }
            }

            if (loList != null && loList.isEmpty()) {
                sortArraylist();
            } else {
                listTransactions.setVisibility(View.GONE);
                tvNoRecordFound.setVisibility(View.VISIBLE);
            }
        } else {
            transactionsWithoutFilter(lastChildMidQr);
        }
    }

    private void transactionsWithoutFilter(String lastChildMidQr) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMerchantId().equalsIgnoreCase(lastChildMidQr)) {
                loList.add(list.get(i));
            }
        }
        if (loList != null && !loList.isEmpty()) {
            listTransactions.setVisibility(View.VISIBLE);
            tvNoRecordFound.setVisibility(View.GONE);
            if (adapter != null) {
                Collections.sort(loList, byDate);
                adapter.updateList(loList);
            } else {
                Collections.sort(list, byDate);
                adapter = new SettleTransactionAdapter(SettledTransactionsQuery.this, list);
            }
            adapter.notifyDataSetChanged();
            listTransactions.setAdapter(adapter);


        } else {
            listTransactions.setVisibility(View.GONE);
            tvNoRecordFound.setVisibility(View.VISIBLE);
        }
    }

    private void sortArraylist() {
        listTransactions.setVisibility(View.VISIBLE);
        tvNoRecordFound.setVisibility(View.GONE);
        if (adapter != null) {
            Collections.sort(loList, byDate);
            adapter.updateList(loList);
        } else {
            Collections.sort(filteredArrayList, byDate);
            adapter = new SettleTransactionAdapter(SettledTransactionsQuery.this, filteredArrayList);
        }
        adapter.notifyDataSetChanged();
        listTransactions.setAdapter(adapter);

    }

    public void getSettleTrnResponse(String responseData) {
        try {
            JSONObject jsonObject = new JSONObject(responseData);
            Log.d("DATA", "SETTLE TRN DATA ==>  " + jsonObject.toString());
            parseLocalJson(jsonObject, mID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("NewApi")
    private void parseLocalJson(JSONObject jsonResponse, String locationId) {
        transactionFilteredList = new ArrayList<>();
        list = new ArrayList<>();
        try {
            JSONObject jsonTransactionOb = jsonResponse.getJSONObject("data");
            JSONArray jsonArray = jsonTransactionOb.getJSONArray("Transactions");
            if (jsonArray.toString().equalsIgnoreCase("[]")) {
                listTransactions.setVisibility(View.GONE);
                tvNoRecordFound.setVisibility(View.VISIBLE);
            } else {
                locationParseOperation(jsonArray, locationId);
            }

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }


    private void locationParseOperation(JSONArray jsonArray, String locationId) {
        try {

            DecimalFormat df = new DecimalFormat("0.##");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonTrObject = jsonArray.getJSONObject(i);

                String cardNumber = jsonTrObject.getString("CreditCard");
                String cardName = jsonTrObject.getString("CardType");
                String approvalNo = jsonTrObject.getString("Response");
                String transactionDate = jsonTrObject.getString("Date");
                String transactionTime = jsonTrObject.getString("Time");
                String amount = jsonTrObject.getString("Amount");
                String referenceNo = jsonTrObject.getString("TerminalReference");
                String settlementDate = jsonTrObject.getString("DateLiquidated");
                String terminalId = jsonTrObject.getString("TerminalId");
                String transactionType = jsonTrObject.getString("TransactionType");
                double lotNumber = jsonTrObject.getDouble("BatchHeader");
                String lotNo = df.format(lotNumber);
                String cardType = jsonTrObject.getString("CardBrand");
                String currency = jsonTrObject.getString("Currency");
                String merchantId = jsonTrObject.getString("LocationId");

                list.add(new SettleTransaction(merchantId, currency, cardNumber, cardType,
                        transactionDate, transactionTime, cardName, approvalNo, amount,
                        transactionType, terminalId, lotNo, referenceNo, settlementDate));

            }
            Log.d("TAG", "locationParseOperation: " + list.size());
            if (!TextUtils.isEmpty(locationId)) {
                Log.d("TAG", "locationParseOperation: 2222" + list.size());
                if (!list.isEmpty()) {
                    setTransactionAdapter(list, locationId);
                }

            } else {
                Log.d("TAG", "locationParseOperation: 3333" + list.size());
                Collections.sort(list, byDate);
                adapter = new SettleTransactionAdapter(SettledTransactionsQuery.this, list);
                listTransactions.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void setTransactionAdapter(List<SettleTransaction> givenList, String locationId) {

        for (SettleTransaction transaction : givenList) {
            if (transaction.getMerchantId().equalsIgnoreCase(locationId)) {
                transactionFilteredList.add(transaction);
            }
        }
        if (!transactionFilteredList.isEmpty()) {
            Collections.sort(transactionFilteredList, byDate);
            adapter = new SettleTransactionAdapter(SettledTransactionsQuery.this, transactionFilteredList);
            listTransactions.setVisibility(View.VISIBLE);
            tvNoRecordFound.setVisibility(View.GONE);
            listTransactions.setAdapter(adapter);
        } else {
            listTransactions.setVisibility(View.GONE);
            tvNoRecordFound.setVisibility(View.VISIBLE);
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

    private Comparator<SettleTransaction> byDate = new Comparator<SettleTransaction>() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

        public int compare(SettleTransaction ord1, SettleTransaction ord2) {

            Date inputDateT11Older = null;
            Date inputDateT2Older = null;

            String inputDateT1 = ord1.getTrnDateTime().substring(0, 10);
            String inputTimeT1 = ord1.getTime().substring(11, 19);

            String inputDateT2 = ord2.getTrnDateTime().substring(0, 10);
            String inputTimeT2 = ord2.getTime().substring(11, 19);


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