package com.sdp.appazul.activities.TapOnPhone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.sdp.appazul.R;
import com.sdp.appazul.activities.dashboard.DashBoardActivity;
import com.sdp.appazul.activities.home.MenuLocationFilter;
import com.sdp.appazul.adapters.DialogCalendarAdapter;
import com.sdp.appazul.adapters.SingleDateAdapter;
import com.sdp.appazul.adapters.TapOnPhoneAdapter;
import com.sdp.appazul.classes.LocationFilter;
import com.sdp.appazul.classes.LocationFilterThirdGroup;
import com.sdp.appazul.classes.TapOnPhone;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.KeyConstants;
import com.sdp.appazul.utils.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
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

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;

public class TapTransactions extends AppCompatActivity implements TapOnPhoneAdapter.PaymentItemSelectListener, ToPRefundCancelMenu.ToPBottomSheetCloseEvent {

    RelativeLayout act_top_transaction;
    RelativeLayout locationFilter;
    TextView tvSelectedLocation;
    TextView tvFromActivity;
    TextView tvToActivity;
    ImageView tranLocationSelector;
    RelativeLayout layoutSinceDatePicker;
    LinearLayout layoutUntilDatePicker;
    ListView lvTransaction;
    LinearLayout layoutDateRangeFilter;
    TextView tvNoRecordFound;
    ImageView btnTransactionBack;
    boolean submenuVisibility;
    MenuLocationFilter homeLocationBottomSheet;
    boolean editText1Selected = false;
    boolean editText2Selected = false;
    String formattedFromDate;
    String formattedTODate;
    String selectedFromDateForApi = "";
    String selectedToDateForApi = "";
    List<String> daysList = new ArrayList<>();
    List<String> monthList = new ArrayList<>();
    List<String> monthListCap = new ArrayList<>();
    GregorianCalendar selectedInitialDate;
    DateUtils utils = new DateUtils();
    GregorianCalendar selectedFinalDate;
    String currentDateTime = "";
    String selectedInitialDateString = "";
    String selectedFinalDateString = "";
    String fetchFinal = "";
    String locationJson;
    Context context;
    SharedPreferences sharedPreferences;
    String mID = "";
    LocationFilter locationFilterData = new LocationFilter();
    String lastLocationName;
    String lastLocationMid;
    String selectedCurrency;
    RelativeLayout act_set_payment;
    Map<String, String> defaultLocations = new HashMap<>();
    String responseCode;
    Map<String, String> defaultLocation;
    List<TapOnPhone> tapOnPhoneList;
    List<TapOnPhone> oldList;
    List<TapOnPhone> filterTapOnPhoneList;
    ToPRefundCancelMenu refundCancelMenu;
    String locID = "39038540050";
    String Aprobada = "Aprobada";
    String TAG = "PHOS_SDK";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_transactions);
        initControls();
    }

    private void initControls() {
        context = this;
        act_top_transaction = findViewById(R.id.act_top_transaction);
        tvSelectedLocation = findViewById(R.id.tvSelectedLocation);
        locationFilter = findViewById(R.id.locationFilter);
        tvFromActivity = findViewById(R.id.tvFromActivity);
        tvToActivity = findViewById(R.id.tvToActivity);
        tranLocationSelector = findViewById(R.id.tran_locationSelector);
        tvSelectedLocation = findViewById(R.id.tvSelectedLocation);
        layoutSinceDatePicker = findViewById(R.id.layoutSinceDatePicker);
        layoutUntilDatePicker = findViewById(R.id.layoutUntilDatePicker);
        lvTransaction = findViewById(R.id.lvTransaction);
        layoutDateRangeFilter = findViewById(R.id.layoutDateRangeFilter);
        tvFromActivity = findViewById(R.id.tvFromActivity);
        tvToActivity = findViewById(R.id.tvToActivity);
        tvNoRecordFound = findViewById(R.id.tvNoRecordFound);
        btnTransactionBack = findViewById(R.id.btnTransactionBack);
        locationJson = ((AzulApplication) context.getApplicationContext()).getLocationDataShare();

        locationFilterData = ((AzulApplication) this.getApplication()).getLocationFilter();

        if (locationFilterData != null) {
            lastLocationName = locationFilterData.getLocationNameAndId();
            lastLocationMid = locationFilterData.getmId();
            mID = lastLocationMid;
            responseCode = locationFilterData.getPaymentCode();
            selectedCurrency = locationFilterData.getCurrency();
            tvSelectedLocation.setText(lastLocationName.toLowerCase());
        } else {
            getDefaultLocation(locationJson);
            defaultLocations = ((AzulApplication) this.getApplication()).getDefaultLocation();
            if (defaultLocations != null) {
                String defaultLocationName = defaultLocations.get("CHILD_LOC_NAME");
                String defaultLocationId = defaultLocations.get("CHILD_LOC_ID");
                String locationToDisplay = defaultLocationName + " - " + defaultLocationId;
                tvSelectedLocation.setText(locationToDisplay);
                selectedCurrency = defaultLocations.get("CURR");
                lastLocationName = locationToDisplay;
                mID = defaultLocationId;

            }
        }

        btnTransactionBack.setOnClickListener(view -> {
            finish();
            Intent intent1 = new Intent(TapTransactions.this, DashBoardActivity.class);
            startActivity(intent1);
            this.overridePendingTransition(R.anim.animation_leave,
                    R.anim.slide_nothing);
        });

        locationFilter.setOnClickListener(btnBurgerMenuView -> {
            tranLocationSelector.setRotation(submenuVisibility ? 180 : 0);
            homeLocationBottomSheet = new MenuLocationFilter(locationJson, "TAP_TRANSACTIONS", 4);
            homeLocationBottomSheet.show(getSupportFragmentManager(), "bottomSheet");
        });


        layoutSinceDatePicker.setOnClickListener(view -> {
            editText1Selected = true;
            editText2Selected = false;
            openCalendarDialog();
        });

        initDate();
        setInitialAndFinalDate();
        selectedFromDateForApi = formattedFromDate;
        selectedToDateForApi = formattedTODate;
        monthList = utils.getmonthList();
        monthListCap = utils.getmonthListWithCaps();
        daysList = utils.getDaysList();


        callGetTransactionsFromPhos(formattedFromDate);
    }


    private void callGetTransactionsFromPhos(String filterDate) {
        try {

            Date date = null;
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
            date = format.parse(filterDate);
            progressDialog = ProgressDialog.show(context, null,
                    "procesando... ", false, false);
            progressDialog.setCancelable(false);

            PhosSdk.getInstance().getTransactionHistory(1, 50, date, null, null, new TransactionListCallback() {
                @Override
                public void onSuccess(Transactions transactions, @Nullable @org.jetbrains.annotations.Nullable Map<String, String> map) {

                    setDataToAdapter(transactions.getItems());
                }

                @Override
                public void onFailure(PhosException e, @Nullable @org.jetbrains.annotations.Nullable Map<String, String> map) {
                    if ((progressDialog != null) && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            });
        } catch (ParseException e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }


    private void setDataToAdapter(ArrayList<Transaction> items) {
        if (!items.isEmpty() && items.size() > 0) {


            lvTransaction.setVisibility(View.VISIBLE);
            tvNoRecordFound.setVisibility(View.GONE);

            TapOnPhoneAdapter tapOnPhoneAdapter = new TapOnPhoneAdapter(context, items);
            lvTransaction.setAdapter(tapOnPhoneAdapter);
            tapOnPhoneAdapter.setCustomButtonListener(this::onItemClickListener);
            tapOnPhoneAdapter.notifyDataSetChanged();
        } else {
            lvTransaction.setVisibility(View.GONE);
            tvNoRecordFound.setVisibility(View.VISIBLE);
        }
        if ((progressDialog != null) && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ((progressDialog != null) && progressDialog.isShowing()) {
            progressDialog.dismiss();
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
                    ((AzulApplication) ((TapTransactions) context).getApplication()).setLocationFilter(locationFilterObj);

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public GregorianCalendar getDateOneMonthsBeforeDate(GregorianCalendar date) {
        return new GregorianCalendar(date.get(YEAR), (date.get(MONTH)),
                date.get(DAY_OF_MONTH));
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

    Dialog calendarDialog;
    Button btnConfirm;
    TextView title;
    TextView calendarTitle;
    GregorianCalendar month;
    LinearLayout layoutDateFilter;
    LinearLayout linearLayout;
    ImageView next;
    ImageView previous;
    GridView gridview;
    ArrayList<String> desc;
    RelativeLayout calendarLayout;
    LinearLayout fromDateLayout;
    LinearLayout toDateLayout;
    ImageView ivFromIcon;
    ImageView ivToIcon;

    ArrayList<String> startDates = new ArrayList<>();
    ArrayList<String> nameOfEvent = new ArrayList<>();

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

        calendarTitle.setText("Selecciona una fecha");
        calendarDialog.setOnShowListener(dialogInterface -> showCalendarView());

        setDays();
        calendarDialog.show();
    }

    SingleDateAdapter calendarAdapter;

    public void showCalendarView() {

        Log.d("TAG", "showCalendarView: 111");
        if (editText1Selected) {
            Log.d("TAG", "showCalendarView: 222");
            fromDateBox();
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

    protected void setNextMonth() {
        calendarAdapter.refreshDays();
        calendarAdapter.notifyDataSetChanged();
        if (month.get(MONTH) == month.getActualMaximum(MONTH)) {
            month.set((month.get(YEAR) + 1), month.getActualMinimum(MONTH), 1);
        } else {
            month.set(MONTH, month.get(MONTH) + 1);
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

    protected void setPreviousMonth() {
        calendarAdapter.refreshDays();
        calendarAdapter.notifyDataSetChanged();
        if (month.get(MONTH) == month.getActualMinimum(MONTH)) {
            month.set((month.get(YEAR) - 1), month.getActualMaximum(MONTH), 1);
        } else {
            month.set(MONTH, month.get(MONTH) - 1);
        }
    }

    private void calendarOnClickListener() {
        gridview.setOnItemClickListener((parent, v, position, id) -> {
            desc = new ArrayList<>();
            String selectedGridDate = SingleDateAdapter.dayString.get(position);
            String[] separatedTime = selectedGridDate.split("-");
            String gridValueString = separatedTime[2].replaceFirst("^0*", "");
            ((SingleDateAdapter) parent.getAdapter()).setSelected(v);
            int gridValue = Integer.parseInt(gridValueString);

            calendarClickOperations(gridValue, position);
            ((SingleDateAdapter) parent.getAdapter()).setSelected(v);

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
            Snackbar snackbar = Snackbar.make(act_top_transaction,
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
                } else {
                    tvFromActivity.setText(tempDate);
                    tvToActivity.setText(selectedDateString);
                    selectedInitialDate = selectedFinalDate;
                    selectedFinalDate = selectedDate;

                    selectedFromDateForApi = selectedToDateForApi;
                    selectedToDateForApi = datesForApi;
                }

                callGetTransactionsFromPhos(selectedFromDateForApi);
                showConfirmBtn();
                editText1Selected = false;

            } else {
                selectedInitialDate = selectedDate;
                tvFromActivity.setText(selectedDateString);
            }


            refreshCalendar();
            showCalendarView();


        } else {
            Snackbar snackbar = Snackbar.make(act_top_transaction,
                    this.getResources().getString(R.string.current_date_compare), SNACK_LENGTH)
                    .setBackgroundTint(Color.parseColor("#0091DF"));
            snackbar.show();
        }
    }

    static int SNACK_LENGTH = 0;

    private void showConfirmBtn() {
        if (calendarDialog != null && calendarDialog.isShowing()) {
            calendarDialog.dismiss();
        }
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

        calendarAdapter = new SingleDateAdapter(this, month, false, selectedInitialDateString);
    }

    public void setDays() {
        final TextView[] txtView = new TextView[7];
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/Gotham-Medium.ttf");
        for (int i = 0; i < 7; i++) {

            txtView[i] = new TextView(this);
            txtView[i].setText(daysList.get(i));
            txtView[i].setTextSize(13);
            txtView[i].setTextColor(ContextCompat.getColor(TapTransactions.this, R.color.time_frame));
            txtView[i].setId(i);
            txtView[i].setPadding(8, 8, 0, 0);
            txtView[i].setTypeface(typeface);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0, MATCH_PARENT,
                    1);
            linearLayout.addView(txtView[i], param);
        }
    }

    public void setContent(String s, String merchantId, int dismissFlag, String code, LocationFilterThirdGroup locationFilterThirdGroup) {
        tvSelectedLocation.setText(s);
        mID = merchantId;
        if (dismissFlag == 1) {
            homeLocationBottomSheet.dismiss();
        }
        responseCode = code;
    }


    @Override
    public void onClosed(Transaction tap, int option) {
        if (option == 1) {
            OpenCancelConfirmationDialog(1, tap, "¿Estás seguro que deseas \n anular esta transacción?", "Sí, anular");
        } else {
            OpenCancelConfirmationDialog(2, tap, "¿Estás seguro que deseas \n devolver esta transacción?", "Sí, devolver");
        }
    }

    Dialog confirmationDialog;

    private void OpenCancelConfirmationDialog(int option, Transaction tap, String messageName, String buttonText) {
        confirmationDialog = new Dialog(this);
        confirmationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmationDialog.setCancelable(true);
        confirmationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        confirmationDialog.setContentView(R.layout.top_cancel_confirmation_dg);
        RelativeLayout btnCancelPayment = confirmationDialog.findViewById(R.id.btnCancelPayment);
        ImageView btnCloaseCancelDialog = confirmationDialog.findViewById(R.id.btnCloaseCancelDialog);
        TextView tvButtonText = confirmationDialog.findViewById(R.id.tvButtonText);
        TextView tvCancelAlertText = confirmationDialog.findViewById(R.id.tvCancelAlertText);
        tvCancelAlertText.setText(messageName);
        tvButtonText.setText(buttonText);

        btnCancelPayment.setOnClickListener(btnCancelPaymentView -> {
            if (option == 1) {
                if (confirmationDialog != null && confirmationDialog.isShowing()) {
                    confirmationDialog.dismiss();
                }
                callVoidTransaction(tap);
            } else {
                if (confirmationDialog != null && confirmationDialog.isShowing()) {
                    confirmationDialog.dismiss();
                }
                callRefundOperation(tap);
            }
        });
        btnCloaseCancelDialog.setOnClickListener(btnCloaseCancelDialogView -> {
            if (confirmationDialog != null && confirmationDialog.isShowing()) {
                confirmationDialog.dismiss();
            }
        });
        confirmationDialog.show();


    }

    private void callRefundOperation(Transaction tap) {
        PhosSdk.getInstance().makeRefundWithAmount(this, tap.getTransactionKey(), tap.getAmount(), false, true, new TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction, @Nullable @org.jetbrains.annotations.Nullable Map<String, String> map) {
                callReceiptScreen(transaction);
            }

            @Override
            public void onFailure(@Nullable @org.jetbrains.annotations.Nullable Transaction transaction, PhosException e, @Nullable @org.jetbrains.annotations.Nullable Map<String, String> map) {
                callDeclinedScreen(transaction);
            }
        });

    }
    private void callDeclinedScreen(Transaction transaction) {
        ((AzulApplication) ((TapTransactions) this).getApplication()).setDeclinedTransaction(transaction);
        Intent intent = new Intent(TapTransactions.this, DeclinedTransactionActivity.class);
        startActivity(intent);
        this.overridePendingTransition(R.anim.animation_enter,
                R.anim.slide_nothing);
    }

    private void callReceiptScreen(Transaction transaction) {
        Intent intent = new Intent(this, RefundReceiptActivity.class);
        ((AzulApplication) ((TapTransactions) this).getApplication()).setLocationDataShare(locationJson);
        ((AzulApplication) ((TapTransactions) this).getApplication()).setTransaction(transaction);
        startActivity(intent);
        this.overridePendingTransition(R.anim.animation_enter,
                R.anim.slide_nothing);
    }

    @Override
    public void onItemClickListener(Transaction trnObject) {
        refundCancelMenu = new ToPRefundCancelMenu(mID, lastLocationName, trnObject);
        refundCancelMenu.setBottomSheetCloseEvent(this::onClosed);
        refundCancelMenu.show(this.getSupportFragmentManager(), "TOP_REFUND_CANCEL_MENU");
    }

    private void callVoidTransaction(Transaction tap) {

        PhosSdk.getInstance().makeVoid(this, tap.getTransactionKey(), false, true, new TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction, @Nullable @org.jetbrains.annotations.Nullable Map<String, String> map) {
                callVoidReceiptScreen(transaction);
            }

            @Override
            public void onFailure(@Nullable @org.jetbrains.annotations.Nullable Transaction transaction, PhosException e, @Nullable @org.jetbrains.annotations.Nullable Map<String, String> map) {
                callDeclinedScreen(transaction);
            }
        });
    }

    private void callVoidReceiptScreen(Transaction transaction) {
        Intent intent = new Intent(this, VoidReceiptActivity.class);
        ((AzulApplication) ((TapTransactions) this).getApplication()).setLocationDataShare(locationJson);
        ((AzulApplication) ((TapTransactions) this).getApplication()).setTransaction(transaction);
        startActivity(intent);
        this.overridePendingTransition(R.anim.animation_enter,
                R.anim.slide_nothing);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}