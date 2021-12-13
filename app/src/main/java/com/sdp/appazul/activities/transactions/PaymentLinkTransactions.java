package com.sdp.appazul.activities.transactions;

import androidx.core.content.ContextCompat;

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
import android.view.LayoutInflater;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.sdp.appazul.R;
import com.sdp.appazul.activities.dashboard.DashBoardActivity;
import com.sdp.appazul.activities.home.BaseLoggedInActivity;
import com.sdp.appazul.activities.home.MenuLocationFilter;
import com.sdp.appazul.activities.payment.PaymentLocationFilter;
import com.sdp.appazul.activities.payment.SetPaymentInfoActivity;
import com.sdp.appazul.adapters.DialogCalendarAdapter;
import com.sdp.appazul.adapters.PaymentSpinnerAdapter;
import com.sdp.appazul.adapters.PaymentTransactionAdapter;
import com.sdp.appazul.api.ApiManager;
import com.sdp.appazul.api.ServiceUrls;
import com.sdp.appazul.classes.FilterTypes;
import com.sdp.appazul.classes.LocationFilter;
import com.sdp.appazul.classes.PaymentTransactions;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;

public class PaymentLinkTransactions extends BaseLoggedInActivity implements PaymentTransactionAdapter.customItemSelectListener, ExtraFilterBottomMenu.ExtraFilterInterface {

    TextView tvFromActivity;
    TextView tvToActivity;
    boolean editText1Selected = false;
    boolean editText2Selected = false;
    Dialog calendarDialog;
    TextView tvFromDate;
    TextView tvToDate;
    TextView calendarTitle;
    Button btnConfirm;
    LinearLayout layoutDateFilter;
    LinearLayout linearLayout;
    TextView title;
    ImageView next;
    ImageView previous;
    GridView gridview;
    RelativeLayout calendarLayout;
    LinearLayout fromDateLayout;
    LinearLayout toDateLayout;
    ImageView ivFromIcon;
    ImageView ivToIcon;
    DateUtils utils = new DateUtils();
    GregorianCalendar selectedInitialDate;
    GregorianCalendar selectedFinalDate;
    String currentDateTime = "";
    String selectedInitialDateString = "";
    String selectedFinalDateString = "";
    String formattedFromDate;
    String formattedTODate;
    String fetchFinal = "";
    GregorianCalendar month;
    DialogCalendarAdapter calendarAdapter;
    String selectedFromDateForApi = "";
    String selectedToDateForApi = "";
    List<String> daysList = new ArrayList<>();
    List<String> monthList = new ArrayList<>();
    List<String> monthListCap = new ArrayList<>();
    ArrayList<String> desc;
    ArrayList<String> startDates = new ArrayList<>();
    ArrayList<String> nameOfEvent = new ArrayList<>();
    MenuLocationFilter locationsBottomSheet;
    RelativeLayout locationFilterMainLayout;
    ImageView settleTranLocationSelector;
    boolean subMenuVisibility;
    String locationJson;
    EditText etSearchBy;
    ImageView activeSearchImage;
    ImageView clearEnteredText;
    LinearLayout layoutSearch;
    ImageView btnBackToPrevious;
    PaymentTransactionAdapter transactionAdapter;
    ListView lvTransactions;
    Spinner spinnerFilter;
    ApiManager apiManager = new ApiManager(this);
    Map<String, String> defaultLocation;
    String responseCode;
    TextView tvLocationName;
    String mID;
    PaymentTransactionMenu paymentTransactionMenu;
    ExtraFilterBottomMenu filterBottomMenu;
    LocationFilter locationFilter = new LocationFilter();
    Map<String, String> defaultLocations = new HashMap<>();
    TextView tvNoRecordFound;
    List<PaymentTransactions> transactionsList;
    PaymentSpinnerAdapter spinnerAdapter;
    ChipGroup selectedChipGroup;
    List<FilterTypes> trnTypeArray;
    String selectedSpinnerFilter = Constants.AMOUNT;
    List<PaymentTransactions> tempList;
    List<PaymentTransactions> listWithChips;
    List<String> permissionList;
    List<String> productPermissionList;
    SharedPreferences sharedPreferences;
    String userName = "";
    int selectedCount = 0;
    String TAG = "TAG";
    ImageView btnClearSearchText;
    RelativeLayout actPaymentLinkTransactio;
    static int SNACK_LENGTH = 0;
    String selectedCurrency;
    Context context;
    String taxExemptFlag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_link_transactions);
        context = this;
        initControls();
    }

    private void initControls() {
        tvFromActivity = findViewById(R.id.tvFromActivity);
        actPaymentLinkTransactio = findViewById(R.id.actPaymentLinkTransactio);
        btnClearSearchText = findViewById(R.id.btnClearSearchText);
        tvToActivity = findViewById(R.id.tvToActivity);
        locationFilterMainLayout = findViewById(R.id.locationFilterMainLayout);
        settleTranLocationSelector = findViewById(R.id.SettleTranLocationSelector);
        etSearchBy = findViewById(R.id.etSearchBy);
        activeSearchImage = findViewById(R.id.activeSearchImage);
        clearEnteredText = findViewById(R.id.clearEnteredText);
        layoutSearch = findViewById(R.id.layoutSearch);
        btnBackToPrevious = findViewById(R.id.btnBackToPrevious);
        lvTransactions = findViewById(R.id.listTransactions);
        spinnerFilter = findViewById(R.id.spinnerFilter);
        tvLocationName = findViewById(R.id.tvLocationName);
        tvNoRecordFound = findViewById(R.id.tvNoRecordFound);
        selectedChipGroup = findViewById(R.id.selectedChipGroup);
        sharedPreferences = ((AzulApplication) this.getApplication()).getPrefs();
        userName = sharedPreferences.getString(Constants.USER_NAME, "");

        permissionList = ((AzulApplication) this.getApplication()).getFeaturePermissionsList();
        productPermissionList = ((AzulApplication) this.getApplication()).getProductPermissionsList();

        getIntentData();
        componentListeners();
        locationBottomSheetMenu();
        monthList = utils.getmonthList();
        monthListCap = utils.getmonthListWithCaps();
        daysList = utils.getDaysList();
        initDate();
        setInitialAndFinalDate();
        selectedFromDateForApi = formattedFromDate;
        selectedToDateForApi = formattedTODate;

        locationFilter = ((AzulApplication) this.getApplication()).getLocationFilter();
        if (locationFilter != null) {
            mID = locationFilter.getmId();
            taxExemptFlag = locationFilter.getTaxExempt();
            tvLocationName.setText(locationFilter.getLocationNameAndId().toLowerCase());
            selectedCurrency = locationFilter.getCurrency();
            responseCode = locationFilter.getPaymentCode();
            getPaymentDataFromApi(formattedFromDate, formattedTODate, mID);
        } else {
            getDefaultLocation(locationJson);
            defaultLocations = ((AzulApplication) this.getApplication()).getDefaultLocation();
            if (defaultLocations != null) {
                String defaultLocationName = defaultLocations.get("CHILD_LOC_NAME");
                String defaultLocationId = defaultLocations.get("CHILD_LOC_ID");
                String tax = defaultLocations.get("TAX_STATUS");
                String locationToDisplay = defaultLocationName + " - " + defaultLocationId;
                tvLocationName.setText(locationToDisplay);
                selectedCurrency = defaultLocations.get("CURR");
                mID = defaultLocationId;
                taxExemptFlag = tax;
            }
        }

        List<String> stringList = new ArrayList<>();
        stringList.add("Elige un filtro de búsqueda:");
        stringList.add("Monto");
        stringList.add("Link ID");

        spinnerAdapter = new PaymentSpinnerAdapter(stringList, PaymentLinkTransactions.this);
        spinnerFilter.setAdapter(spinnerAdapter);
        spinnerFilter.setSelection(1);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equalsIgnoreCase("1")) {
                    selectedSpinnerFilter = Constants.AMOUNT;
                    setAdapter(transactionsList);
                    etSearchBy.setText("");
                    etSearchBy.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                    btnClearSearchText.setVisibility(View.GONE);
                } else if (selectedItem.equalsIgnoreCase("2")) {
                    selectedSpinnerFilter = Constants.LINK_ID;
                    setAdapter(transactionsList);
                    etSearchBy.setText("");
                    etSearchBy.setInputType(InputType.TYPE_CLASS_TEXT);
                    btnClearSearchText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("TAG", "onNothingSelected: ");
            }
        });
    }


    private void getPaymentDataFromApi(String formattedFromDate, String formattedTODate, String mID) {

        JSONObject json = new JSONObject();
        try {
            String tcpKey = ((AzulApplication) PaymentLinkTransactions.this.getApplication()).getTcpKey();
            String vcr = ((AzulApplication) PaymentLinkTransactions.this.getApplication()).getVcr();
            json.put("tcp", RSAHelper.ecryptRSA(PaymentLinkTransactions.this, tcpKey));
            JSONObject payload = new JSONObject();
            payload.put("device", DeviceUtils.getDeviceId(PaymentLinkTransactions.this));
            payload.put("Code", "pruebas2");
            payload.put("merchantId", mID);
            payload.put("dateFrom", formattedFromDate);
            payload.put("dateTo", formattedTODate);
            json.put("payload", RSAHelper.encryptAES(payload.toString(), Base64.decode(tcpKey, 0), Base64.decode(vcr, 0)));
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.PAYMENT_LINK_SEARCH, json);
    }

    private void componentListeners() {
        layoutSearch.setOnClickListener(view -> showSearchBar());
        etSearchBy.setOnTouchListener((view, motionEvent) -> {
            if (MotionEvent.ACTION_UP == motionEvent.getAction())
                showSearchBar();
            return false;
        });

        btnBackToPrevious.setOnClickListener(view -> {
            finish();
            Intent intent1 = new Intent(PaymentLinkTransactions.this, DashBoardActivity.class);
            ((AzulApplication) ((PaymentLinkTransactions) this).getApplication()).setLocationDataShare(locationJson);

            startActivity(intent1);
            this.overridePendingTransition(R.anim.animation_leave,
                    R.anim.slide_nothing);
        });

        clearEnteredText.setOnClickListener(clearEnteredTextView ->
                hideSearchBar());
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
        btnClearSearchText.setOnClickListener(btnClearSearchTextView -> {
            etSearchBy.setText("");
            btnClearSearchText.setVisibility(View.GONE);
        });
        searchBySpinner();

        if (etSearchBy.getText().toString().equalsIgnoreCase("")) {
            btnClearSearchText.setVisibility(View.GONE);
        }
    }

    private void searchBySpinner() {
        etSearchBy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("TAG", "beforeTextChanged: ");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etSearchBy.getText().toString().equalsIgnoreCase("")) {
                    btnClearSearchText.setVisibility(View.GONE);
                } else {
                    btnClearSearchText.setVisibility(View.VISIBLE);
                }
                checkSearchText(charSequence);

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("TAG", "afterTextChanged: ");
            }
        });
    }

    private void checkSearchText(CharSequence charSequence) {
        if (selectedSpinnerFilter.equalsIgnoreCase(Constants.AMOUNT)) {
            if (listWithChips != null && !listWithChips.isEmpty()) {
                filterListWithAmount(charSequence, listWithChips);
            } else {
                filterListWithAmount(charSequence, transactionsList);
            }
        } else if (selectedSpinnerFilter.equalsIgnoreCase(Constants.LINK_ID)) {
            if (listWithChips != null && !listWithChips.isEmpty()) {
                filterListWithLinkId(charSequence, listWithChips);
            } else {
                filterListWithLinkId(charSequence, transactionsList);
            }
        }
    }

    private void filterListWithAmount(CharSequence charSequence, List<PaymentTransactions> amountFilterList) {
        tempList = new ArrayList<>();
        if (amountFilterList != null && !amountFilterList.isEmpty()) {
            for (PaymentTransactions pt : amountFilterList) {
                Log.d("PaymentLinkTransactions", pt.getAmount() + "  == charSequence: " + charSequence);
                if (pt.getAmount().contains(charSequence)) {
                    tempList.add(pt);
                }
            }

            if (!tempList.isEmpty()) {
                lvTransactions.setVisibility(View.VISIBLE);
                tvNoRecordFound.setVisibility(View.GONE);
                setAdapter(tempList);
            } else {
                lvTransactions.setVisibility(View.GONE);
                tvNoRecordFound.setVisibility(View.VISIBLE);
            }
        }
    }

    private void filterListWithLinkId(CharSequence charSequence, List<PaymentTransactions> linkIdFilterList) {
        tempList = new ArrayList<>();
        if (linkIdFilterList != null && !linkIdFilterList.isEmpty()) {
            for (PaymentTransactions pt : linkIdFilterList) {
                if (pt.getLinkId().trim().contains(charSequence)) {
                    tempList.add(pt);
                }
            }

            if (!tempList.isEmpty()) {
                lvTransactions.setVisibility(View.VISIBLE);
                tvNoRecordFound.setVisibility(View.GONE);
                setAdapter(tempList);
            } else {
                lvTransactions.setVisibility(View.GONE);
                tvNoRecordFound.setVisibility(View.VISIBLE);
            }
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
                    ((AzulApplication) ((PaymentLinkTransactions) context).getApplication()).setLocationFilter(locationFilterObj);

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getIntentData() {
        locationJson = ((AzulApplication) context.getApplicationContext()).getLocationDataShare();
    }

    private void locationBottomSheetMenu() {
        locationFilterMainLayout.setOnClickListener(btnBurgerMenuView -> {
            settleTranLocationSelector.setRotation(subMenuVisibility ? 180 : 0);
            locationsBottomSheet = new MenuLocationFilter(locationJson, "PAYMENT_TRANSACTION", 4);
            locationsBottomSheet.show(getSupportFragmentManager(), "bottomSheet");
        });
    }

    private void showSearchBar() {

        String backgroundImageName = String.valueOf(activeSearchImage.getTag());
        if (backgroundImageName.equalsIgnoreCase("bg")) {
            activeSearchImage.setTag(R.drawable.ic_search_active);
            activeSearchImage.setImageResource(R.drawable.ic_search_active);
        }
    }

    private void hideSearchBar() {

        filterBottomMenu = new ExtraFilterBottomMenu(trnTypeArray);
        filterBottomMenu.setExtraFilterInterface(this::getSelectedFilters);
        filterBottomMenu.show(this.getSupportFragmentManager(), "OTHER_FILTERS");
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
            btnConfirm.setTextColor(ContextCompat.getColor(PaymentLinkTransactions.this, R.color.white_bg));
        }

        calendarDialog.setOnShowListener(dialogInterface -> showCalendar());

        btnConfirm.setOnClickListener(view -> {

            tvFromActivity.setText(tvFromDate.getText().toString());
            tvToActivity.setText(tvToDate.getText().toString());


            if (!TextUtils.isEmpty(selectedFromDateForApi) && !TextUtils.isEmpty(selectedToDateForApi)) {
                getPaymentDataFromApi(selectedFromDateForApi, selectedToDateForApi, mID);
            }
            if (calendarDialog != null && calendarDialog.isShowing()) {
                calendarDialog.dismiss();
            }
        });

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

    public GregorianCalendar getDateOneMonthsBeforeDate(GregorianCalendar date) {
        return new GregorianCalendar(date.get(YEAR), (date.get(MONTH)),
                date.get(DAY_OF_MONTH));
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

    private void fromDateOperations(GregorianCalendar
                                            insideSelectedInitialDate, GregorianCalendar insideSelectedFinalDate) {
        String year = (String) DateFormat.format(Constants.YYYY, insideSelectedInitialDate);
        month = (GregorianCalendar) insideSelectedInitialDate.clone();
        String spanishMonthName = monthListCap
                .get(Integer.parseInt((String) DateFormat.format(Constants.MM, month)) - 1);
        String fromDateTitle = spanishMonthName + " " + year;
        title.setText(fromDateTitle);

        String initialMonth = "" + (insideSelectedInitialDate.get(MONTH) + 1);
        String initialDate = "" + insideSelectedInitialDate.get(DAY_OF_MONTH);
        Log.d("TAG", "initialMonth :: " + initialMonth + " =  " + initialDate);

        selectedInitialDateString = (initialDate.length() == 1 ? ("0" + initialDate) : initialDate) + "/"
                + (initialMonth.length() == 1 ? ("0" + initialMonth) : initialMonth) + "/"
                + insideSelectedInitialDate.get(YEAR);

        String selectedMonth = "" + (insideSelectedFinalDate.get(MONTH) + 1);
        String date = "" + insideSelectedFinalDate.get(DAY_OF_MONTH);

        Log.d("TAG", "selectedMonth :: " + selectedMonth + " =  " + date);
        Log.d("TAG", "selectedMonth Length :: " + selectedMonth.length());
        selectedFinalDateString = (date.length() == 1 ? ("0" + date) : date) + "/"
                + (selectedMonth.length() == 1 ? ("0" + selectedMonth) : selectedMonth) + "/"
                + insideSelectedFinalDate.get(YEAR);
        Log.d("TAG", "fromDateOperations: Final Dates " + selectedInitialDateString + " =  " + selectedInitialDateString);
        calendarAdapter = new DialogCalendarAdapter(this, month, false, selectedInitialDateString,
                selectedFinalDateString);
    }

    private void toDateOperations(GregorianCalendar
                                          insideSelectedInitialDate, GregorianCalendar insideSelectedFinalDate) {
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

    private void fromDateSelector(String selectedDateString, GregorianCalendar
            selectedDate, String datesForApi) {
        if (utils.isCurrentDayCheck(currentDateTime, selectedDateString)) {

            if (!TextUtils.isEmpty(tvToActivity.getText().toString())) {

                String tempDate = tvToActivity.getText().toString();
                if (utils.isFromDateSmaller(selectedDateString, tvToActivity.getText().toString())) {
                    tvFromActivity.setText(selectedDateString);
                    tvToActivity.setText(tempDate);
                    selectedInitialDate = selectedDate;

                    selectedFromDateForApi = datesForApi;
                    Log.d("API fromDateSelector", "TO SMALL " + selectedFromDateForApi + " == " + selectedToDateForApi);

                } else {
                    tvFromActivity.setText(tempDate);
                    tvToActivity.setText(selectedDateString);
                    selectedInitialDate = selectedFinalDate;
                    selectedFinalDate = selectedDate;

                    selectedFromDateForApi = selectedToDateForApi;
                    selectedToDateForApi = datesForApi;
                    Log.d("API fromDateSelector", "TO BIG " + selectedFromDateForApi + " == " + selectedToDateForApi);


                }
                getPaymentDataFromApi(selectedFromDateForApi, selectedToDateForApi, mID);
                showConfirmBtn();
                editText1Selected = false;

            } else {
                selectedInitialDate = selectedDate;
                tvFromActivity.setText(selectedDateString);
            }


            refreshCalendar();
            showCalendar();


        } else {
            Snackbar snackbar = Snackbar.make(actPaymentLinkTransactio, this.getResources().getString(R.string.current_date_compare), SNACK_LENGTH)
                    .setBackgroundTint(Color.parseColor("#0091DF"));
            snackbar.show();
        }
    }


    private void toDateSelector(String selectedDateString, GregorianCalendar
            gregorianCalendar, String datesForApi) {
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
                    Log.d("API toDateSelector", "TO SMALL " + selectedFromDateForApi + " == " + selectedToDateForApi);

                } else {
                    tvFromActivity.setText(tempDate);
                    tvToActivity.setText(selectedDateString);
                    selectedFinalDate = gregorianCalendar;
                    selectedToDateForApi = datesForApi;
                    Log.d("API toDateSelector", "TO BIG " + selectedFromDateForApi + " == " + selectedToDateForApi);

                }
                getPaymentDataFromApi(selectedFromDateForApi, selectedToDateForApi, mID);
                showConfirmBtn();
                editText2Selected = false;

            } else {
                tvToActivity.setText(selectedDateString);
                selectedFinalDate = gregorianCalendar;
            }


            refreshCalendar();
            showCalendar();


        } else {

            Snackbar snackbar = Snackbar.make(actPaymentLinkTransactio,
                    this.getResources().getString(R.string.current_date_compare), SNACK_LENGTH)
                    .setBackgroundTint(Color.parseColor("#0091DF"));
            snackbar.show();
        }
    }

    private void showConfirmBtn() {
        if (calendarDialog != null && calendarDialog.isShowing()) {
            calendarDialog.dismiss();
        }
    }


    public void setDays() {
        final TextView[] txtView = new TextView[7];
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/Gotham-Medium.ttf");
        for (int i = 0; i < 7; i++) {

            txtView[i] = new TextView(this);
            txtView[i].setText(daysList.get(i));
            txtView[i].setTextSize(13);
            txtView[i].setTextColor(ContextCompat.getColor(PaymentLinkTransactions.this, R.color.time_frame));
            txtView[i].setId(i);
            txtView[i].setPadding(8, 8, 0, 0);
            txtView[i].setTypeface(typeface);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0, MATCH_PARENT,
                    1);
            linearLayout.addView(txtView[i], param);
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }


    public void setContent(String name, String content, String merchantId, int dismissFlag, String taxExempt) {
        tvLocationName.setText(content);
        mID = merchantId;
        taxExemptFlag = taxExempt;
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(taxExempt)) {
            Log.d("Data", "" + name + taxExempt);
        }
        if (dismissFlag == 1) {
            getPaymentDataFromApi(selectedFromDateForApi, selectedToDateForApi, mID);
            locationsBottomSheet.dismiss();
        }
    }

    @Override
    public void onItemClickListener(String trnResponse, int position, String value, String status, String amountToShow, String selectedCurrency) {
        paymentTransactionMenu = new PaymentTransactionMenu(trnResponse, amountToShow, status, value, responseCode, selectedCurrency,taxExemptFlag);
        paymentTransactionMenu.show(this.getSupportFragmentManager(), "PaymentTransactionMenu");
    }

    public void getPaymentSearchResponse(String responseString) {
        Log.d("TAG", "getPaymentSearchResponse: ." + responseString);
        parsePaymentLinkData(responseString);
    }

    private void parsePaymentLinkData(String responseString) {

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(responseString);

            JSONObject jsonTransactionOb = jsonObject.getJSONObject("data");

            loadPaymentJson(jsonTransactionOb);

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void loadPaymentJson(JSONObject jsonTransactionOb) {
        try {

            checkListStatus();

            JSONArray parentLevelLocations = jsonTransactionOb.getJSONArray("Links");

            for (int i = 0; i < parentLevelLocations.length(); i++) {
                JSONObject parentData = parentLevelLocations.getJSONObject(i);
                PaymentTransactions transactionsData = new PaymentTransactions();
                transactionsData.setCreatedBy(parentData.getString("AddUser"));
                transactionsData.setLinkId(parentData.getString("LinkId"));
                transactionsData.setAmount(parentData.getString("Amount"));
                if (parentData.getString("ClientName").equalsIgnoreCase("")) {
                    transactionsData.setClientName("-");
                } else {
                    transactionsData.setClientName(parentData.getString("ClientName"));
                }
                if (parentData.getString("PayedAuthorizationNumber").equalsIgnoreCase("")) {
                    transactionsData.setPayedAuthorizationNumber("");
                } else {
                    transactionsData.setPayedAuthorizationNumber(parentData.getString("PayedAuthorizationNumber"));
                }
                transactionsData.setPaymentDate(parentData.getString("AddDate"));
                transactionsData.setTransactionStatus(parentData.getString("TransactionStatus"));
                transactionsData.setStatus(parentData.getString("Status"));
                transactionsData.setTransactionResponse(parentData.getString("TransactionResponse"));
                transactionsData.setCurrency(parentData.getString("Currency"));


                checkPermissionPart(permissionList, transactionsData);
            }

            setAdapter(transactionsList);

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void checkPermissionPart(List<String> permissionList, PaymentTransactions transactionsData) {
        if (permissionList.contains("APPPaymentLinksQueryOwnTransactions")
                && !permissionList.contains("APPPaymentLinksQueryAllTransactions")) {
            if (!userName.isEmpty()) {
                if (transactionsData.getCreatedBy().contains(userName)) {
                    transactionsList.add(new PaymentTransactions(transactionsData));
                }
            }
        } else if (permissionList.contains("APPPaymentLinksQueryAllTransactions")) {
            transactionsList.add(new PaymentTransactions(transactionsData));
        }
    }

    private void checkListStatus() {
        if (transactionsList != null) {
            transactionsList.clear();
        } else {
            transactionsList = new ArrayList<>();
        }
    }


    private void setAdapter(List<PaymentTransactions> transactionsList) {
        if (transactionsList != null && !transactionsList.isEmpty()) {
            Collections.sort(transactionsList, byDate);

            lvTransactions.setVisibility(View.VISIBLE);
            tvNoRecordFound.setVisibility(View.GONE);
            if (transactionAdapter != null) {
                transactionAdapter.updateUi(this, transactionsList);
            } else {
                transactionAdapter = new PaymentTransactionAdapter(this, transactionsList);
                lvTransactions.setAdapter(transactionAdapter);
                transactionAdapter.setCustomButtonListener(this::onItemClickListener);

            }
            transactionAdapter.notifyDataSetChanged();
        } else {
            lvTransactions.setVisibility(View.GONE);
            tvNoRecordFound.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void getSelectedFilters(List trnTypeFilters) {
        Log.d("TAG", "trnTypeFilters : " + trnTypeFilters.size());

        if (trnTypeArray != null) {
            trnTypeArray.clear();
            selectedChipGroup.removeAllViews();
            trnTypeArray = trnTypeFilters;
            addSelectedChips(trnTypeArray);
        } else {
            selectedChipGroup.removeAllViews();
            trnTypeArray = trnTypeFilters;
            addSelectedChips(trnTypeArray);
        }


    }

    private void addSelectedChips(List<FilterTypes> trnTypeArray) {
        if (trnTypeArray != null && !trnTypeArray.isEmpty()) {
            for (FilterTypes chipData : trnTypeArray) {
                if (chipData.getFilterName().equalsIgnoreCase("")
                        && chipData.getFilterName().equalsIgnoreCase(" ")
                        && chipData.getIsSelected().equalsIgnoreCase("false")) {
                    Log.d("TAG", "addSelectedChips: ");
                } else {
                    addChips(chipData);
                    filterDataWithChip(trnTypeArray);
                }
            }
        }
    }


    private void addChips(FilterTypes chipData) {
        if (chipData.getIsSelected().equalsIgnoreCase("true")) {
            LayoutInflater layoutInflater = LayoutInflater.from(this);

            Chip chip = (Chip) layoutInflater.inflate(R.layout.other_filter_chip_item, null, false);
            chip.setText(chipData.getFilterName());
            chip.setCloseIconVisible(false);
            chip.setChecked(true);
            chip.setCheckable(true);
            chip.setBackgroundColor(ContextCompat.getColor(this, R.color.unlink_background));
            Typeface typeface;
            typeface = Typeface.createFromAsset(this.getAssets(), "fonts/montserrat_semi_bold_font.ttf");
            chip.setTypeface(typeface);

            selectedChipGroup.addView(chip);
            chipListener(chip);
        }
    }

    private void chipListener(Chip chip) {

        chip.setOnCheckedChangeListener((compoundButton, b) -> {
            for (int i = 0; i < trnTypeArray.size(); i++) {
                if (trnTypeArray != null && trnTypeArray.get(i).getFilterName().equalsIgnoreCase(chip.getText().toString())) {
                    Log.d("TAG", "setOnCheckedChangeListener: " + chip.getText().toString());
                    trnTypeArray.get(i).setIsSelected("false");
                    selectedChipGroup.removeView(chip);
                    break;
                }
            }
            filterWithSelectedChip();
            for (int j = 0; j < trnTypeArray.size(); j++) {
                if (trnTypeArray.get(j).getIsSelected().equalsIgnoreCase("true")) {
                    selectedCount = trnTypeArray.get(j).getIsSelected().length();
                    Log.d("TAG", "selectedCount size :::: " + selectedCount);

                }
            }
        });
    }


    private void filterDataWithChip(List<FilterTypes> trnTypeArray) {

        if (trnTypeArray != null && !trnTypeArray.isEmpty()) {
            filterWithSelectedChip();
        }

    }


    private void filterWithSelectedChip() {
        listWithChips = new ArrayList<>();

        for (FilterTypes pt : trnTypeArray) {
            for (int i = 0; i < transactionsList.size(); i++) {
                PaymentTransactions transactions = transactionsList.get(i);
                if (pt.getFilterType().equalsIgnoreCase("STATUS") && pt.getIsSelected().equalsIgnoreCase("true")) {

                    checkStatusValue(transactions, pt);
                } else if (pt.getFilterType().equalsIgnoreCase("TRN_TYPE") && pt.getIsSelected().equalsIgnoreCase("true")) {

                    checkTransactionStatusValue(transactions, pt);
                }
            }
        }
        List<Integer> integers = selectedChipGroup.getCheckedChipIds();

        Log.d("TAG", integers.size() + "listWithChips size :::: " + listWithChips.size());
        if (!listWithChips.isEmpty()) {
            lvTransactions.setVisibility(View.VISIBLE);
            tvNoRecordFound.setVisibility(View.GONE);
            setAdapter(listWithChips);
        } else {
            if (integers.size() > 0) {
                lvTransactions.setVisibility(View.GONE);
                tvNoRecordFound.setVisibility(View.VISIBLE);
            } else {
                trnTypeArray.clear();
                setAdapter(transactionsList);
            }
        }
    }

    private void checkTransactionStatusValue(PaymentTransactions transactions, FilterTypes pt) {
        if (transactions.getTransactionStatus().contains(getFilterName(pt.getFilterName()))) {
            Log.d("TRN_TYPE", transactions.getTransactionStatus() + " :: filterWithSelectedChip: :: " + getFilterName(pt.getFilterName()));
            listWithChips.add(transactions);
        }
    }

    private void checkStatusValue(PaymentTransactions transactions, FilterTypes pt) {
        if (transactions.getStatus().contains(getFilterName(pt.getFilterName()))) {
            Log.d("STATUS", transactions.getStatus() + " :: filterWithSelectedChip: :: " + getFilterName(pt.getFilterName()));
            listWithChips.add(transactions);
        }
    }

    private String getFilterName(String inputText) {
        if (inputText.equalsIgnoreCase("Venta")) {
            return "Sale";
        } else if (inputText.equalsIgnoreCase("Sólo autorización")) {
            return "HoldOnly";
        } else if (inputText.equalsIgnoreCase("Autorización completada")) {
            return "HoldPosted";
        } else if (inputText.equalsIgnoreCase("Procesado")) {
            return "Processed";
        } else if (inputText.equalsIgnoreCase("Anulada")) {
            return "Voided";
        } else if (inputText.equalsIgnoreCase("Expirado")) {
            return "Expired";
        } else if (inputText.equalsIgnoreCase("Abierto")) {
            return "Open";
        } else if (inputText.equalsIgnoreCase("Generado")) {
            return "Generated";
        } else if (inputText.equalsIgnoreCase("Cancelado")) {
            return "Cancelled";
        }
        return "";
    }

    SimpleDateFormat olderFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private Comparator<PaymentTransactions> byDate = new Comparator<PaymentTransactions>() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

        public int compare(PaymentTransactions ord1, PaymentTransactions ord2) {

            Date inputDateT11Older = null;
            Date inputDateT2Older = null;

            try {
                inputDateT11Older = olderFormat.parse(ord1.getPaymentDate());
                inputDateT2Older = olderFormat.parse(ord2.getPaymentDate());
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