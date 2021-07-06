package com.sdp.appazul.activities.transactions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.View;
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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

public class SettledTransactionsQuery extends AppCompatActivity {
    ListView listTransactions;
    TextView tvNoRecordFound;

    SettleTransactionAdapter adapter;
    List<SettleTransaction> list;
    RelativeLayout topBarLayout;
    RelativeLayout layoutSearchByEdittext;
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
    LinearLayout toDateLayoutw;
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
    ArrayList<SettleTransaction> filterarrayList = new ArrayList<>();
    String fechaFinal = "";
    String locationJson;
    ImageView btnSettleTrBack;
    ImageView settleTranLocationSelector;
    boolean submenuVisibility;
    LocationFilterBottomSheet bottomsheet;
    SharedPreferences sharedPreferences;
    String mID = "";
    TextView tvSelectedSettleLocation;
    ImageView searchinActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settled_transactions_query);
        initControls();
    }

    private void initControls() {
        searchinActive = findViewById(R.id.searchinActive);
        tvSelectedSettleLocation = findViewById(R.id.tvSelectedSettleLocation);
        Intent intent = getIntent();

        locationJson = intent.getStringExtra("LOCATION_RESPONSE");
        sharedPreferences = ((AzulApplication) this.getApplication()).getPrefs();

        String lastLocationName = sharedPreferences.getString(Constants.SELECTED_LOCATION_DASHBOARD, "");
        String lastLocationMid = sharedPreferences.getString(Constants.SELECTED_LOCATION_MID_DASHBOARD, "");
        mID = lastLocationMid;

        if (!TextUtils.isEmpty(lastLocationName)) {
            tvSelectedSettleLocation.setText(lastLocationName);
        } else {
            tvSelectedSettleLocation.setText("Pruebac");
        }

        btnSettleTrBack = findViewById(R.id.btnSettleTrBack);
        settleTranLocationSelector = findViewById(R.id.SettleTranLocationSelector);

        btnSettleTrBack.setOnClickListener(view -> {
            finish();
            Intent intent1 = new Intent(SettledTransactionsQuery.this, DashBoardActivity.class);
            intent1.putExtra("LOCATION_RESPONSE", locationJson);
            startActivityForResult(intent1, 1);
        });

        settleTranLocationSelector.setOnClickListener(btnBurgerMenuView -> {
            settleTranLocationSelector.setRotation(submenuVisibility ? 180 : 0);
            bottomsheet = new LocationFilterBottomSheet(locationJson, "SettledTransactionsQuery");
            bottomsheet.show(getSupportFragmentManager(), "bottomSheet");
        });

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

        etSearchBy.setOnClickListener(etSearchByView ->
                showSearchBar());
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
        layoutSearchByEdittext.setOnClickListener(layoutSearchView ->
                showSearchBar());
        clearEnteredText.setOnClickListener(clearEnteredTextView ->
                hideSearchBar());

        list = new ArrayList<>();
        listTransactions = findViewById(R.id.listTransactions);
        tvNoRecordFound = findViewById(R.id.tvNoRecordFound);
        tvFromActivity.setOnClickListener(tvFromActivityView ->
                openCalendarDialog()
        );
        tvToActivity.setOnClickListener(tvToActivityView ->
                openCalendarDialog()
        );
        initDate();
        setInitialAndFinalDate();
        callQRTransaction(formattedFromDate, formattedTODate, mID);
        monthList = utils.getmonthList();
        monthListCap = utils.getmonthListWithCaps();
        daysList = utils.getDaysList();

    }

    private void filterList(CharSequence charSequence, int type) {
        if (loList != null && loList.size() > 0) {
            searchingInList(loList, charSequence, type);
        } else if (filterarrayList != null && filterarrayList.size() > 0) {
            searchingInList(filterarrayList, charSequence, type);
        } else {
            searchingInList(list, charSequence, type);
        }
    }

    private void openCalendarDialog() {

        calendarDialog = new Dialog(this);
        calendarDialog.setContentView(R.layout.settle_calendar_dialog);
        calendarDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        calendarDialog.setCanceledOnTouchOutside(false);

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

        if (!TextUtils.isEmpty(tvFromActivity.getText().toString()) && !TextUtils.isEmpty(tvToActivity.getText().toString())) {
            tvFromDate.setText(tvFromActivity.getText().toString());
            tvToDate.setText(tvToActivity.getText().toString());
            btnConfirm.setEnabled(true);
            btnConfirm.setBackgroundResource(R.drawable.btn_confirm_gradient);
            btnConfirm.setTextColor(ContextCompat.getColor(SettledTransactionsQuery.this, R.color.white_bg));
        }

        calendarDialog.setOnShowListener(dialogInterface -> showcalendar());
        fromDateLayout.setOnClickListener(fromDateView -> {
            fromDateLayout.setBackgroundResource(R.drawable.spinner_background);
            toDateLayoutw.setBackgroundResource(R.drawable.tv_unselect_background);
            editText1Selected = true;
            editText2Selected = false;

            ivToIcon.setImageResource(R.drawable.date_box_arrows);
            ivFromIcon.setImageResource(R.drawable.up_arrow);
            showcalendar();
        });

        toDateLayoutw.setOnClickListener(toDateLayoutwView -> {
            toDateLayoutw.setBackgroundResource(R.drawable.spinner_background);
            fromDateLayout.setBackgroundResource(R.drawable.tv_unselect_background);
            editText1Selected = false;
            editText2Selected = true;

            ivFromIcon.setImageResource(R.drawable.date_box_arrows);
            ivToIcon.setImageResource(R.drawable.up_arrow);

            showcalendar();
        });


        btnConfirm.setOnClickListener(view -> {

            tvFromActivity.setText(tvFromDate.getText().toString());
            tvToActivity.setText(tvToDate.getText().toString());

            if (!TextUtils.isEmpty(selectedFromDateForApi) && !TextUtils.isEmpty(selectedToDateForApi)) {
                callQRTransaction(selectedFromDateForApi, selectedToDateForApi, mID);
            }
            if (calendarDialog != null && calendarDialog.isShowing()) {
                calendarDialog.dismiss();
            }
        });

        setDays();
        calendarDialog.show();
    }

    ApiManager apiManager = new ApiManager(SettledTransactionsQuery.this);

    private void callQRTransaction(String fromDateString, String toDateString, String mID) {
        Log.d("API CALL", "SET TRANS == " + fromDateString + " === " + toDateString + " === " + mID);
        JSONObject json = new JSONObject();
        try {
            String tcpKey = ((AzulApplication) this.getApplication()).getTcpKey();
            json.put("tcp", RSAHelper.ecryptRSA(SettledTransactionsQuery.this, tcpKey));
            JSONObject payload = new JSONObject();
            payload.put("device", DeviceUtils.getDeviceId(this));
            JSONObject pagosrequest = new JSONObject();
            pagosrequest.put("dateFrom", fromDateString);
            pagosrequest.put("dateTo", toDateString);
            pagosrequest.put("type", "liquidated");
            pagosrequest.put("merchantIdList", mID);

            payload.put("obtenerrequest", pagosrequest);
            json.put("payload", RSAHelper.encryptAES(payload.toString(), Base64.decode(tcpKey, 0)));

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

        if (!TextUtils.isEmpty(fechaFinal)) {
            String finalDate = utils.getStringFormatted(Constants.DD_MM_YYYY, utils.getDateFormatted(Constants.YYYY_MM_DD, fechaFinal));
            tvToActivity.setText(finalDate);
            selectedFinalDateString = finalDate;
            Date d = utils.getDateFormatted(Constants.YYYY_MM_DD, fechaFinal);
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

    public void showcalendar() {

        if (editText1Selected) {
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

        } else if (editText2Selected) {
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
                    if (utils.isCurrentDayCheck(currentDateTime, selectedDateString)) {

                        if (!TextUtils.isEmpty(tvToDate.getText().toString())) {
                            long dif = 0;
                            dif = printDifference(selectedDateString, tvToDate.getText().toString());
                            if (dif >= 31) {
                                AppAlters.showCustomAlert(this,
                                        "Utiliza un rango de fecha no \nmayor a 31 días",
                                        "Por favor inténtalo de nuevo.", "Continuar");
                                tvFromDate.setText(selectedDateString);
                                selectedInitialDate = selectedDate;


                            } else {

                                fromDateLayout.setBackgroundResource(R.drawable.tv_unselect_background);

                                String tempDate = tvToDate.getText().toString();
                                if (utils.isFromDateSmaller(selectedDateString, tvToDate.getText().toString())) {
                                    tvFromDate.setText(selectedDateString);
                                    tvToDate.setText(tempDate);
                                    selectedInitialDate = selectedDate;
                                    showConfirmBtn();

                                    String chossenFromDate = month.get(YEAR) + "/" +
                                            selectedMonth + "/"
                                            + gridvalue;

                                    selectedFromDateForApi = chossenFromDate;

                                } else {
                                    tvFromDate.setText(tempDate);
                                    tvToDate.setText(selectedDateString);
                                    selectedInitialDate = selectedFinalDate;
                                    selectedFinalDate = selectedDate;
                                    showConfirmBtn();

                                    String chossenFromDate = month.get(YEAR) + "/" +
                                            selectedMonth + "/"
                                            + gridvalue;
                                    selectedToDateForApi = chossenFromDate;

                                }
                            }
                        } else {

                            selectedInitialDate = selectedDate;
                            tvFromDate.setText(selectedDateString);

                        }


                        refreshCalendar();
                        showcalendar();
                        editText1Selected = false;
                        ivToIcon.setImageResource(R.drawable.date_box_arrows);
                        ivFromIcon.setImageResource(R.drawable.date_box_arrows);

                    } else {
                        toastPopup(this, R.string.current_date_compare);
                    }

                } else if (editText2Selected) {
                    if (utils.isCurrentDayCheck(currentDateTime, selectedDateString)) {
                        if (!TextUtils.isEmpty(tvFromDate.getText().toString())) {
                            long dif = 0;
                            dif = printDifference(tvFromDate.getText().toString(), selectedDateString);

                            if (dif >= 31) {
                                AppAlters.showCustomAlert(this,
                                        "Utiliza un rango de fecha no \nmayor a 31 días",
                                        "Por favor inténtalo de nuevo.", "Continuar");
                                tvToDate.setText(selectedDateString);
                                selectedFinalDate = selectedDate;

                            } else {
                                toDateLayoutw.setBackgroundResource(R.drawable.tv_unselect_background);


                                String tempDate = tvFromDate.getText().toString();
                                if (utils.isToDateSmaller(tvFromDate.getText().toString(), selectedDateString)) {
                                    tvFromDate.setText(selectedDateString);
                                    tvToDate.setText(tempDate);
                                    selectedFinalDate = selectedInitialDate;
                                    selectedInitialDate = selectedDate;

                                    String chossenToDate = month.get(YEAR) + "/" +
                                            selectedMonth + "/"
                                            + gridvalue;

                                    selectedToDateForApi = selectedFromDateForApi;
                                    selectedFromDateForApi = chossenToDate;
                                    Log.d("SELECTED DATE ", "TO DATE 44::: " + selectedToDateForApi);
                                    Log.d("SELECTED DATE ", "FROM DATE 55::: " + selectedFromDateForApi);


                                    showConfirmBtn();
                                } else {
                                    tvFromDate.setText(tempDate);
                                    tvToDate.setText(selectedDateString);
                                    selectedFinalDate = selectedDate;
                                    String chossenToDate = month.get(YEAR) + "/" +
                                            selectedMonth + "/"
                                            + gridvalue;

                                    selectedToDateForApi = chossenToDate;
                                    Log.d("SELECTED DATE ", "TO DATE 66::: " + selectedToDateForApi);
                                    Log.d("SELECTED DATE ", "FROM DATE 77::: " + selectedFromDateForApi);

                                    showConfirmBtn();

                                }
                            }
                        } else {
                            tvToDate.setText(selectedDateString);
                            selectedFinalDate = selectedDate;
                        }


                        refreshCalendar();
                        showcalendar();
                        editText2Selected = false;

                        ivToIcon.setImageResource(R.drawable.date_box_arrows);
                        ivFromIcon.setImageResource(R.drawable.date_box_arrows);


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
            btnConfirm.setTextColor(ContextCompat.getColor(SettledTransactionsQuery.this, R.color.white_bg));
        }
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

    List<SettleTransaction> transactionFilteredList;


    SimpleDateFormat olderFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    SimpleDateFormat format = new SimpleDateFormat(Constants.DD_MM_YYYY);

    public String loadJSONFromAsset() {
        InputStream is = null;
        String json = null;
        try {
            is = getAssets().open("dummySettleTrnJson.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, Constants.UTF_FORMAT);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return json;
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
        SimpleDateFormat olderDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat normalDateFormat = new SimpleDateFormat(Constants.DD_MM_YYYY);
        if (tempList != null && tempList.size() > 0) {
            listTransactions.setVisibility(View.VISIBLE);
            tvNoRecordFound.setVisibility(View.GONE);
            Collections.sort(tempList, new Comparator<SettleTransaction>() {
                @Override
                public int compare(SettleTransaction r1, SettleTransaction r2) {
                    try {
                        Date d1 = null;
                        Date d2 = null;
                        d1 = olderDateFormat.parse(r1.getTrnDateTime());
                        d2 = olderDateFormat.parse(r2.getTrnDateTime());

                        String trnDate1 = normalDateFormat.format(d1);
                        String trnDate2 = normalDateFormat.format(d2);
                        Date newR1 = normalDateFormat.parse(trnDate1);
                        Date newR2 = normalDateFormat.parse(trnDate2);
                        return (newR1.getTime() > newR2.getTime() ? -1 : 1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });

            if (adapter != null) {
                adapter.updateList(tempList);
                adapter.notifyDataSetChanged();
            }
        } else {
            listTransactions.setVisibility(View.GONE);
            tvNoRecordFound.setVisibility(View.VISIBLE);
        }
    }

    private void filterWithLotNo() {
        tvLotNo.setBackgroundResource(R.drawable.middle_navigation_bg);
        tvLotNo.setTextColor(ContextCompat.getColor(this, R.color.blue_3));

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

        etSearchBy.setText("");
    }

    private void filterWithCardNo() {
        tvCardNo.setBackgroundResource(R.drawable.middle_navigation_bg);
        tvCardNo.setTextColor(ContextCompat.getColor(this, R.color.blue_3));

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

        lotNoClick = false;
        approvalNoClick = false;
        cardNoClick = false;
        terminalNoClick = true;
        etSearchBy.setInputType(InputType.TYPE_CLASS_NUMBER);

        etSearchBy.setText("");
    }

    private void showSearchBar() {
        etSearchBy.setEnabled(true);
        if (topBarLayout.getVisibility() == View.GONE) {
            topBarLayout.setVisibility(View.VISIBLE);
        }
        if (clearEnteredText.getVisibility() == View.GONE) {
            clearEnteredText.setVisibility(View.VISIBLE);
        }
        String backgroundImageName = String.valueOf(searchinActive.getTag());
        if (backgroundImageName.equalsIgnoreCase("bg")) {
            searchinActive.setTag(R.drawable.ic_search_active);
            searchinActive.setImageResource(R.drawable.ic_search_active);
        }
    }

    private void hideSearchBar() {
        searchinActive.setTag("bg");
        searchinActive.setImageResource(R.drawable.search_in_active);
        etSearchBy.setEnabled(false);
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
    public void setContent(String content, String lastChildMidQr, int dimissFlag) {
        loList = new ArrayList<>();
        tvSelectedSettleLocation.setText(content);
        if (dimissFlag == 1) {
            bottomsheet.dismiss();
            callQRTransaction(formattedFromDate, formattedTODate, lastChildMidQr);
        }

        mID = lastChildMidQr;
        if (!TextUtils.isEmpty(lastChildMidQr)) {
            if (filterarrayList != null && filterarrayList.size() > 0) {
                for (int i = 0; i < filterarrayList.size(); i++) {
                    if (filterarrayList.get(i).getMerchantId().equalsIgnoreCase(lastChildMidQr)) {
                        loList.add(filterarrayList.get(i));
                    }
                }
                if (!loList.isEmpty()) {
                    listTransactions.setVisibility(View.VISIBLE);
                    tvNoRecordFound.setVisibility(View.GONE);
                    if (adapter != null) {
                        Collections.sort(loList, new Comparator<SettleTransaction>() {
                            @Override
                            public int compare(SettleTransaction r1, SettleTransaction r2) {
                                try {
                                    Date d1 = null;
                                    Date d2 = null;
                                    d1 = olderFormat.parse(r1.getTrnDateTime());
                                    d2 = olderFormat.parse(r2.getTrnDateTime());

                                    String trnDate1 = format.format(d1);
                                    String trnDate2 = format.format(d2);
                                    Date newR1 = format.parse(trnDate1);
                                    Date newR2 = format.parse(trnDate2);
                                    return (newR1.getTime() > newR2.getTime() ? -1 : 1);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                return 0;
                            }
                        });
                        adapter.updateList(loList);
                        adapter.notifyDataSetChanged();
                    } else {
                        adapter = new SettleTransactionAdapter(SettledTransactionsQuery.this, loList);
                        adapter.notifyDataSetChanged();
                    }
                    listTransactions.setAdapter(adapter);


                } else {
                    listTransactions.setVisibility(View.GONE);
                    tvNoRecordFound.setVisibility(View.VISIBLE);
                }
            } else {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getMerchantId().equalsIgnoreCase(lastChildMidQr)) {
                        loList.add(list.get(i));
                    }
                }
                if (!loList.isEmpty()) {
                    Collections.sort(loList, new Comparator<SettleTransaction>() {
                        @Override
                        public int compare(SettleTransaction r1, SettleTransaction r2) {
                            try {
                                Date d1 = null;
                                Date d2 = null;
                                d1 = olderFormat.parse(r1.getTrnDateTime());
                                d2 = olderFormat.parse(r2.getTrnDateTime());

                                String trnDate1 = format.format(d1);
                                String trnDate2 = format.format(d2);
                                Date newR1 = format.parse(trnDate1);
                                Date newR2 = format.parse(trnDate2);
                                return (newR1.getTime() > newR2.getTime() ? -1 : 1);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            return 0;
                        }
                    });
                    listTransactions.setVisibility(View.VISIBLE);
                    tvNoRecordFound.setVisibility(View.GONE);
                    if (adapter != null) {
                        adapter.updateList(loList);
                        adapter.notifyDataSetChanged();
                    } else {
                        adapter = new SettleTransactionAdapter(SettledTransactionsQuery.this, loList);
                        adapter.notifyDataSetChanged();
                    }
                    listTransactions.setAdapter(adapter);


                } else {
                    listTransactions.setVisibility(View.GONE);
                    tvNoRecordFound.setVisibility(View.VISIBLE);
                }

            }
        }


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
        try {
            JSONObject jsonTransactionOb = jsonResponse.getJSONObject("data");
            DecimalFormat df = new DecimalFormat("0.##");
            JSONArray jsonArray = jsonTransactionOb.getJSONArray("Transactions");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonTrObject = jsonArray.getJSONObject(i);

                String cardNumber = jsonTrObject.getString("CreditCard");
                String cardName = jsonTrObject.getString("CardType");
                String approvalNo = jsonTrObject.getString("Response");
                String transactionDate = jsonTrObject.getString("Date");
                String transactionTime = jsonTrObject.getString("Time");
                String amount = jsonTrObject.getString("Amount");
                String referenceNo = jsonTrObject.getString("TerminalReference");
                String settleDaate = jsonTrObject.getString("DateLiquidated");
                String terminalId = jsonTrObject.getString("TerminalId");
                String transactionType = jsonTrObject.getString("TransactionType");
//                String lotNo = jsonTrObject.getString("AuthorizationNumber");
                double lotNumber = jsonTrObject.getDouble("BatchHeader");
                String lotNo = df.format(lotNumber);
                String cardType = jsonTrObject.getString("CardBrand");
                String currency = jsonTrObject.getString("Currency");
                String merchantId = jsonTrObject.getString("LocationId");

                list.add(new SettleTransaction(merchantId, currency, cardNumber, cardType, transactionDate, transactionTime, cardName, approvalNo, amount, transactionType, terminalId,
                        lotNo, referenceNo, settleDaate));

            }

            Collections.sort(list, new Comparator<SettleTransaction>() {
                @Override
                public int compare(SettleTransaction r1, SettleTransaction r2) {
                    try {
                        Date d1 = null;
                        Date d2 = null;
                        d1 = olderFormat.parse(r1.getTrnDateTime());
                        d2 = olderFormat.parse(r2.getTrnDateTime());

                        String trnDate1 = format.format(d1);
                        String trnDate2 = format.format(d2);
                        Date newR1 = format.parse(trnDate1);
                        Date newR2 = format.parse(trnDate2);
                        return (newR1.getTime() > newR2.getTime() ? -1 : 1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });
            if (!TextUtils.isEmpty(locationId)) {
                for (SettleTransaction transaction : list) {
                    if (transaction.getMerchantId().equalsIgnoreCase(locationId)) {
                        transactionFilteredList.add(transaction);
                    }
                }
                if (transactionFilteredList != null && transactionFilteredList.size() > 0) {
                    adapter = new SettleTransactionAdapter(SettledTransactionsQuery.this, transactionFilteredList);
                    listTransactions.setVisibility(View.VISIBLE);
                    tvNoRecordFound.setVisibility(View.GONE);
                    listTransactions.setAdapter(adapter);
                } else {
                    listTransactions.setVisibility(View.GONE);
                    tvNoRecordFound.setVisibility(View.VISIBLE);
                }
            } else {
                adapter = new SettleTransactionAdapter(SettledTransactionsQuery.this, list);
                listTransactions.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}