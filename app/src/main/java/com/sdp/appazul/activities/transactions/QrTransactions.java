package com.sdp.appazul.activities.transactions;

import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sdp.appazul.R;
import com.sdp.appazul.activities.dashboard.DashBoardActivity;
import com.sdp.appazul.activities.dashboard.LocationFilterBottomSheet;
import com.sdp.appazul.activities.home.BaseLoggedInActivity;
import com.sdp.appazul.adapters.DialogCalendarAdapter;
import com.sdp.appazul.adapters.TransactionHistoryAdapter;
import com.sdp.appazul.api.ApiManager;
import com.sdp.appazul.api.ServiceUrls;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;

public class QrTransactions extends BaseLoggedInActivity {

    TextView clearTextView;
    ImageView searchinActive;
    ImageView imgCalendarIcon;
    EditText etSearchBy;
    TransactionHistoryAdapter historyAdapter;
    List<TransactionHistory> list;
    List<TransactionHistory> transactionFilteredList;
    ListView lvTransaction;
    boolean editText1Selected = false;
    boolean editText2Selected = false;
    ArrayList<TransactionHistory> filterarrayList = new ArrayList<>();
    TextView title;
    TextView calendarTitle;
    GregorianCalendar month;
    DialogCalendarAdapter calendarAdapter;
    ImageView next;
    ImageView previous;
    GridView gridview;
    ArrayList<String> desc;
    RelativeLayout calendarLayout;
    LinearLayout fromDateLayout;
    LinearLayout toDateLayoutw;
    ArrayList<String> startDates = new ArrayList<>();
    ArrayList<String> nameOfEvent = new ArrayList<>();
    List<TransactionHistory> temp;
    Dialog calendarDialog;
    Button btnshowFilter;
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
    TextView tvFromDate;
    TextView tvToDate;
    String fechaFinal = "";
    DateUtils utils = new DateUtils();
    LinearLayout cardDatePicker;
    LinearLayout layoutDateRangeFilter;
    TextView tvFromActivity;
    TextView tvToActivity;
    TextView tvDateFilter;
    TextView tvAmount;
    TextView tvReferenceNo;
    ImageView imgClearRangeFilter;
    String todaysDate = "";
    Boolean tvAmountClick = false;
    Boolean tvRefernceClick = false;
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
    String selectedSingleDateForApi = "";
    LinearLayout layoutSearch;
    RelativeLayout listFilterLayout;
    String locationJson;
    SharedPreferences sharedPreferences;
    ImageView tranLocationSelector;
    boolean submenuVisibility;
    TextView tvSelectedLocation;
    String mID = "";
    LocationFilterBottomSheet bottomsheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_transactions);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initControls();
    }

    @SuppressLint("WrongConstant")
    private void initControls() {
        etSearchBy = findViewById(R.id.etSearchBy);
        tranLocationSelector = findViewById(R.id.tran_locationSelector);
        tvSelectedLocation = findViewById(R.id.tvSelectedLocation);
        layoutSinceDatePicker = findViewById(R.id.layoutSinceDatePicker);
        layoutUntilDatePicker = findViewById(R.id.layoutUntilDatePicker);
        clearTextView = findViewById(R.id.clearTextView);
        searchinActive = findViewById(R.id.searchinActive);
        lvTransaction = findViewById(R.id.listTransactions);
        imgCalendarIcon = findViewById(R.id.imgCalendarIcon);
        cardDatePicker = findViewById(R.id.cardDatePicker);
        layoutDateRangeFilter = findViewById(R.id.layoutDateRangeFilter);
        tvFromActivity = findViewById(R.id.tvFromActivity);
        tvToActivity = findViewById(R.id.tvToActivity);
        imgClearRangeFilter = findViewById(R.id.imgClearRangeFilter);
        tvDateFilter = findViewById(R.id.tvDateFilter);
        tvAmount = findViewById(R.id.tvAmount);
        tvReferenceNo = findViewById(R.id.tvReferenceNo);
        tvNoRecordFound = findViewById(R.id.tvNoRecordFound);
        btnTransactionBack = findViewById(R.id.btnTransactionBack);
        layoutSearch = findViewById(R.id.layoutSearch);
        listFilterLayout = findViewById(R.id.listFilterLayout);

        Intent intent = getIntent();
        locationJson = intent.getStringExtra("LOCATION_RESPONSE");

        sharedPreferences = ((AzulApplication) this.getApplication()).getPrefs();

        String lastLocationNamee = sharedPreferences.getString(Constants.SELECTED_LOCATION_DASHBOARD, "");
        String lastLocationMid = sharedPreferences.getString(Constants.SELECTED_LOCATION_MID_QR_TRANSACTION, "");
        mID = lastLocationMid;


        if (!TextUtils.isEmpty(lastLocationNamee)) {
            tvSelectedLocation.setText(lastLocationNamee);
        } else {
            tvSelectedLocation.setText("Pruebac");
        }


        btnTransactionBack.setOnClickListener(view -> {
            finish();
            Intent intent1 = new Intent(QrTransactions.this, DashBoardActivity.class);
            intent1.putExtra("LOCATION_RESPONSE", locationJson);
            startActivityForResult(intent1, 1);
        });

        getTransactionListData();

        layoutSearch.setOnClickListener(view -> hidShow());
        etSearchBy.setOnClickListener(view ->
                hidShow());

        tranLocationSelector.setOnClickListener(btnBurgerMenuView -> {
            tranLocationSelector.setRotation(submenuVisibility ? 180 : 0);
            bottomsheet = new LocationFilterBottomSheet(locationJson, "QrTransactions");
            bottomsheet.show(getSupportFragmentManager(), "bottomSheet");
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
        clearTextView.setOnClickListener(view -> {
            searchinActive.setTag("bg");
            searchinActive.setImageResource(R.drawable.search_in_active);
            etSearchBy.setText("");
            if (clearTextView.getVisibility() == View.VISIBLE) {
                clearTextView.setVisibility(View.GONE);
            }
            if (listFilterLayout.getVisibility() == View.VISIBLE) {
                listFilterLayout.setVisibility(View.GONE);
            }

        });
        imgCalendarIcon.setOnClickListener(view -> openCalendarDialog());

        imgClearRangeFilter.setOnClickListener(view -> {
            layoutDateRangeFilter.setVisibility(View.GONE);
            cardDatePicker.setVisibility(View.VISIBLE);
            historyAdapter.updateList(list);
            getTransactionListData();
        });

        tvReferenceNo.setOnClickListener(view -> {

            tvReferenceNo.setBackgroundResource(R.drawable.middle_navigation_bg);
            tvAmount.setBackgroundResource(0);
            tvAmount.setTextColor(ContextCompat.getColor(this, R.color.slider_heading));
            tvReferenceNo.setTextColor(ContextCompat.getColor(this, R.color.blue_3));
            tvAmountClick = false;
            tvRefernceClick = true;
            etSearchBy.setText("");
        });

        tvAmount.setOnClickListener(view -> {
            tvAmount.setBackgroundResource(R.drawable.middle_navigation_bg);
            tvReferenceNo.setBackgroundResource(0);
            tvReferenceNo.setTextColor(ContextCompat.getColor(this, R.color.slider_heading));
            tvAmount.setTextColor(ContextCompat.getColor(this, R.color.blue_3));
            tvAmountClick = true;
            tvRefernceClick = false;
            etSearchBy.setText("");
        });
    }


    void filterList(CharSequence text, boolean type) {
        if (!filterarrayList.isEmpty()) {
            serachInList(filterarrayList, text, type);
        } else if (loList != null && loList.size() > 0) {
            serachInList(loList, text, type);
        } else {
            serachInList(list, text, type);
        }
    }

    void filterForReferenceList(CharSequence text, boolean type) {
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

            lvTransaction.setVisibility(View.VISIBLE);
            tvNoRecordFound.setVisibility(View.GONE);
            historyAdapter.updateList(temp);
        } else {
            lvTransaction.setVisibility(View.GONE);
            tvNoRecordFound.setVisibility(View.VISIBLE);
        }

    }

    List<TransactionHistory> loList;

    public void setContent(String content, String lastChildMidQr, int dimissFlag) {
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
                    historyAdapter.notifyDataSetChanged();
                } else {
                    historyAdapter = new TransactionHistoryAdapter(QrTransactions.this, loList);
                    historyAdapter.notifyDataSetChanged();
                }
                lvTransaction.setAdapter(historyAdapter);


            } else {
                lvTransaction.setVisibility(View.GONE);
                tvNoRecordFound.setVisibility(View.VISIBLE);
            }

        }

        if (dimissFlag == 1) {
            bottomsheet.dismiss();
        }
    }

    Boolean filterButtonPressed = false;

    private void openCalendarDialog() {

        calendarDialog = new Dialog(this);
        calendarDialog.setContentView(R.layout.calendar_dialog);
        calendarDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        btnshowFilter = calendarDialog.findViewById(R.id.btnshowFilter);
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
        toDateLayoutw = calendarDialog.findViewById(R.id.toDateLayoutw);
        ivFromIcon = calendarDialog.findViewById(R.id.iv_from_icon);
        ivToIcon = calendarDialog.findViewById(R.id.iv_to_icon);

        if (!TextUtils.isEmpty(tvDateFilter.getText().toString())) {
            calendarTitle.setText(tvDateFilter.getText().toString());
        }

        initDate();

        calendarDialog.setOnShowListener(dialogInterface -> showcalendar());

        btnshowFilter.setOnClickListener(view -> {
            layoutDateFilter.setVisibility(View.VISIBLE);
            btnshowFilter.setVisibility(View.GONE);
            filterButtonPressed = true;
            btnConfirm.setVisibility(View.VISIBLE);
            calendarTitle.setText("Rango de fecha");
        });

        fromDateLayout.setOnClickListener(fromDateView -> {
            fromDateLayout.setBackgroundResource(R.drawable.spinner_background);
            toDateLayoutw.setBackgroundResource(R.drawable.tv_unselect_background);
            editText1Selected = true;
            editText2Selected = false;

            ivToIcon.setImageResource(R.drawable.arr_down);
            ivFromIcon.setImageResource(R.drawable.up_arrow);
            showcalendar();
        });

        toDateLayoutw.setOnClickListener(toDateLayoutwView -> {
            toDateLayoutw.setBackgroundResource(R.drawable.spinner_background);
            fromDateLayout.setBackgroundResource(R.drawable.tv_unselect_background);
            editText1Selected = false;
            editText2Selected = true;

            ivFromIcon.setImageResource(R.drawable.arr_down);
            ivToIcon.setImageResource(R.drawable.up_arrow);

            showcalendar();
        });


        btnConfirm.setOnClickListener(view -> {

            cardDatePicker.setVisibility(View.GONE);
            layoutDateRangeFilter.setVisibility(View.VISIBLE);
            tvFromActivity.setText(tvFromDate.getText().toString());
            tvToActivity.setText(tvToDate.getText().toString());

            if (!TextUtils.isEmpty(selectedFromDateForApi) && !TextUtils.isEmpty(selectedToDateForApi)) {
                callQRTransaction(selectedFromDateForApi, selectedToDateForApi);
                editText1Selected = true;
                editText2Selected = true;
                filterButtonPressed = true;
                showcalendar();
            }

            if (calendarDialog != null && calendarDialog.isShowing()) {
                calendarDialog.dismiss();
            }

        });

        monthList = utils.getmonthList();
        monthListCap = utils.getmonthListWithCaps();
        daysList = utils.getDaysList();
        setDays();


        calendarDialog.show();
    }

    private void initDate() {
        selectedFinalDate = (GregorianCalendar) getInstance();
        selectedInitialDate = getDateOneMonthsBeforeDate(selectedFinalDate);

        String selectedMonthRange = "" + (selectedFinalDate.get(MONTH) + 1);
        String daterange = "" + selectedFinalDate.get(DAY_OF_MONTH);

        currentDateTime = (daterange.length() == 1 ? ("0" + daterange) : daterange) + "/"
                + (selectedMonthRange.length() == 1 ? ("0" + selectedMonthRange) : selectedMonthRange) + "/"
                + selectedFinalDate.get(YEAR);
        selectedMonthRange = "" + (selectedInitialDate.get(MONTH) + 1);
        daterange = "" + selectedInitialDate.get(DAY_OF_MONTH);

        selectedInitialDateString = (daterange.length() == 1 ? ("0" + daterange) : daterange) + "/"
                + (selectedMonthRange.length() == 1 ? ("0" + selectedMonthRange) : selectedMonthRange) + "/"
                + selectedInitialDate.get(YEAR);

    }

    public long printDifference(String fromSDate, String toSDate) {
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DD_MM_YYYY, Locale.ENGLISH);
        Date startDate = null;
        Date endDate = null;


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
            e.printStackTrace();
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


    public void showcalendar() {


        if (editText1Selected) {
            if (Boolean.TRUE.equals(filterButtonPressed)) {
                String year = (String) DateFormat.format(Constants.YYYY, selectedInitialDate);
                month = (GregorianCalendar) selectedInitialDate.clone();
                String spanishmonthname = monthListCap
                        .get(Integer.parseInt((String) DateFormat.format(Constants.MM, month)) - 1);
                title.setText(spanishmonthname + " " + year);

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
            }
        } else if (editText2Selected) {
            if (Boolean.TRUE.equals(filterButtonPressed)) {
                String year = (String) DateFormat.format(Constants.YYYY, selectedFinalDate);
                month = (GregorianCalendar) selectedFinalDate.clone();
                String spanishmonthname = monthListCap
                        .get(Integer.parseInt((String) DateFormat.format(Constants.MM, month)) - 1);
                title.setText(spanishmonthname + " " + year);

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
            }
        } else {

            String year = (String) DateFormat.format(Constants.YYYY, selectedFinalDate);
            month = (GregorianCalendar) selectedFinalDate.clone();
            String spanishmonthname = monthListCap
                    .get(Integer.parseInt((String) DateFormat.format(Constants.MM, month)) - 1);
            title.setText(spanishmonthname + " " + year);

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

        }

        next.setEnabled(true);
        if (editText1Selected) {
            previous.setVisibility(View.VISIBLE);
        }

        calendarAdapter.fechaFinal = selectedFinalDate;
        calendarAdapter.fechaInitial = selectedInitialDate;

        gridview.setAdapter(calendarAdapter);

        gridview.setOnItemClickListener((parent, v, position, id) -> {
            desc = new ArrayList<>();
            String selectedGridDate = DialogCalendarAdapter.dayString.get(position);
            String[] separatedTime = selectedGridDate.split("-");
            String gridvalueString = separatedTime[2].replaceFirst("^0*", "");
            ((DialogCalendarAdapter) parent.getAdapter()).setSelected(v);
            int gridvalue = Integer.parseInt(gridvalueString);
            if ((gridvalue > 10) && (position < 8)) {
                setPreviousMonth();
                refreshCalendar();
            } else if ((gridvalue < 7) && (position > 28)) {
                setNextMonth();
                refreshCalendar();
            } else {
                String selectedMonth = "" + (month.get(MONTH) + 1);
                GregorianCalendar selectedDate = (GregorianCalendar) month.clone();
                selectedDate.set(DATE, gridvalue);

                String selectedDateString = (gridvalue < 10 ? ("0" + gridvalue) : gridvalue) + "/"
                        + (selectedMonth.length() == 1 ? ("0" + selectedMonth) : selectedMonth) + "/"
                        + month.get(YEAR);

                if (editText1Selected) {
                    if (Boolean.TRUE.equals(filterButtonPressed)) {
                        if (utils.isCurrentDayCheck(currentDateTime, selectedDateString)) {

                            if (!TextUtils.isEmpty(tvToDate.getText().toString())) {
                                long dif = 0;
                                dif = printDifference(selectedDateString, tvToDate.getText().toString());
                                if (dif >= 31) {
                                    AppAlters.showCustomAlert(this,
                                            "Utiliza un rango de fecha no \nmayor a 31 días",
                                            "Por favor inténtalo de nuevo.", "Continuar");
                                    btnConfirm.setBackgroundResource(R.drawable.button_light_bg);
                                    btnConfirm.setEnabled(false);

                                } else {


                                    String tempDate = tvToDate.getText().toString();
                                    if (utils.isFromDateSmaller(selectedDateString, tvToDate.getText().toString())) {
                                        tvFromDate.setText(selectedDateString);
                                        tvToDate.setText(tempDate);
                                        selectedInitialDate = selectedDate;
                                        showConfirmBtn();
                                    } else {
                                        tvFromDate.setText(tempDate);
                                        tvToDate.setText(selectedDateString);
                                        selectedInitialDate = selectedFinalDate;
                                        selectedFinalDate = selectedDate;
                                        showConfirmBtn();
                                    }
                                }
                            } else {

                                selectedInitialDate = selectedDate;
                                tvFromDate.setText(selectedDateString);

                            }


                            String chossenFromDate = month.get(YEAR) + "/" +
                                    selectedMonth + "/"
                                    + gridvalue;

                            selectedFromDateForApi = chossenFromDate;

                            refreshCalendar();
                            showcalendar();
                            editText1Selected = false;
                            ivToIcon.setImageResource(R.drawable.arr_down);
                            ivFromIcon.setImageResource(R.drawable.arr_down);

                        } else {
                            toastPopup(this, R.string.current_date_compare);
                        }
                    }
                } else if (editText2Selected) {
                    if (Boolean.TRUE.equals(filterButtonPressed)) {
                        if (utils.isCurrentDayCheck(currentDateTime, selectedDateString)) {
                            if (!TextUtils.isEmpty(tvFromDate.getText().toString())) {
                                long dif = 0;
                                dif = printDifference(tvFromDate.getText().toString(), selectedDateString);

                                if (dif >= 31) {
                                    AppAlters.showCustomAlert(this,
                                            "Utiliza un rango de fecha no \nmayor a 31 días",
                                            "Por favor inténtalo de nuevo.", "Continuar");
                                    btnConfirm.setBackgroundResource(R.drawable.button_light_bg);
                                    btnConfirm.setEnabled(false);
                                } else {


                                    String tempDate = tvFromDate.getText().toString();
                                    if (utils.isToDateSmaller(tvFromDate.getText().toString(), selectedDateString)) {
                                        tvFromDate.setText(selectedDateString);
                                        tvToDate.setText(tempDate);
                                        selectedFinalDate = selectedInitialDate;
                                        selectedInitialDate = selectedDate;
                                        showConfirmBtn();
                                    } else {
                                        tvFromDate.setText(tempDate);
                                        tvToDate.setText(selectedDateString);
                                        selectedFinalDate = selectedDate;
                                        showConfirmBtn();
                                    }
                                }
                            } else {
                                tvToDate.setText(selectedDateString);
                                selectedFinalDate = selectedDate;
                            }

                            String chossenToDate = month.get(YEAR) + "/" +
                                    selectedMonth + "/"
                                    + gridvalue;

                            selectedToDateForApi = chossenToDate;
                            Log.d("SELECTED DATE ", "TO DATE ::: " + selectedToDateForApi);

                            refreshCalendar();
                            showcalendar();
                            editText2Selected = false;

                            ivToIcon.setImageResource(R.drawable.arr_down);
                            ivFromIcon.setImageResource(R.drawable.arr_down);


                        } else {
                            toastPopup(this, R.string.current_date_compare);
                        }
                    }
                } else {

                    if (utils.isCurrentDayCheck(currentDateTime, selectedDateString)) {

                        tvDateFilter.setText(selectedDateString);
                        String chossenFromDate = month.get(YEAR) + "/" +
                                selectedMonth + "/"
                                + gridvalue;

                        selectedSingleDateForApi = chossenFromDate;

                        todaysDate = selectedDateString;

                        if (!TextUtils.isEmpty(selectedSingleDateForApi)) {
                            callQRTransaction(selectedSingleDateForApi, selectedSingleDateForApi);
                        }
                        if (calendarDialog != null && calendarDialog.isShowing()) {
                            calendarDialog.dismiss();
                        }
                    } else {
                        toastPopup(this, R.string.current_date_compare);
                    }
                }

            }
            ((DialogCalendarAdapter) parent.getAdapter()).setSelected(v);

            for (int i = 0; i < startDates.size(); i++) {
                if (startDates.get(i).equals(selectedGridDate)) {
                    desc.add(nameOfEvent.get(i));
                }
            }
            desc = null;
        });


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


    private void showConfirmBtn() {
        if (!TextUtils.isEmpty(tvFromDate.getText().toString()) && !TextUtils.isEmpty(tvToDate.getText().toString())) {
            btnConfirm.setEnabled(true);
            btnConfirm.setBackgroundResource(R.drawable.btn_confirm_gradient);
            btnConfirm.setTextColor(ContextCompat.getColor(QrTransactions.this, R.color.white_bg));
        }
    }

    public void toastPopup(Context context, int message) {
        Toast toast = Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT);
        View tView = toast.getView();
        tView.setBackgroundResource(R.drawable.toast_bg);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void refreshCalendar() {
        calendarAdapter.refreshDays();
        calendarAdapter.notifyDataSetChanged();
        String spanishmonthname = monthListCap
                .get(Integer.parseInt((String) DateFormat.format(Constants.MM, month)) - 1);
        String year = (String) DateFormat.format(Constants.YYYY, month);
        String fulldate = spanishmonthname + " " + year;
        title.setText(fulldate);
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

    GregorianCalendar gregorianCalendar;
    String showDate;

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
        tvDateFilter.setText(getResources().getString(R.string.today) + ", " + date + " de " + spanishmonthname);
        showDate = date + " de " + spanishmonthname;
    }

    private void hidShow() {
        String backgroundImageName = String.valueOf(searchinActive.getTag());

        if (backgroundImageName.equalsIgnoreCase("bg")) {
            searchinActive.setTag(R.drawable.ic_search_active);
            searchinActive.setImageResource(R.drawable.ic_search_active);
        }

        if (listFilterLayout.getVisibility() == View.GONE) {
            listFilterLayout.setVisibility(View.VISIBLE);
        }

        if (clearTextView.getVisibility() == View.GONE) {
            clearTextView.setVisibility(View.VISIBLE);
        }
    }

    private void callQRTransaction(String fromDateString, String toDateString) {
        JSONObject json = new JSONObject();
        try {
            String tcpKey = ((AzulApplication) this.getApplication()).getTcpKey();
            json.put("tcp", RSAHelper.ecryptRSA(QrTransactions.this, tcpKey));
            JSONObject payload = new JSONObject();
            payload.put("device", DeviceUtils.getDeviceId(this));
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

                list.add(new TransactionHistory(authCode, referenceNo, location, trTime, amount, merchantId, showDate));

            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        if (!TextUtils.isEmpty(mID)) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getMerchantId().equalsIgnoreCase(mID)) {
                    transactionFilteredList.add(list.get(i));
                }
            }
            if (!transactionFilteredList.isEmpty()) {
                historyAdapter = new TransactionHistoryAdapter(QrTransactions.this, transactionFilteredList);
                lvTransaction.setVisibility(View.VISIBLE);
                tvNoRecordFound.setVisibility(View.GONE);
                lvTransaction.setAdapter(historyAdapter);
            } else {
                lvTransaction.setVisibility(View.VISIBLE);
                tvNoRecordFound.setVisibility(View.GONE);
            }
        } else {
            historyAdapter = new TransactionHistoryAdapter(QrTransactions.this, list);
            lvTransaction.setAdapter(historyAdapter);
        }

    }
}