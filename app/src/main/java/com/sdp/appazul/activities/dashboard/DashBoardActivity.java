package com.sdp.appazul.activities.dashboard;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.sdp.appazul.R;
import com.sdp.appazul.activities.home.BaseLoggedInActivity;

import com.sdp.appazul.activities.menuitems.WebActivity;
import com.sdp.appazul.activities.payment.PaymentLinkBottomSheet;
import com.sdp.appazul.activities.transactions.BottomDateFilterFragment;
import com.sdp.appazul.adapters.GridViewAdapter;
import com.sdp.appazul.adapters.MyAdapter;
import com.sdp.appazul.api.ApiManager;
import com.sdp.appazul.api.ServiceUrls;
import com.sdp.appazul.classes.Model;
import com.sdp.appazul.globals.AppAlters;
import com.sdp.appazul.globals.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.EncodingHelper;
import com.sdp.appazul.globals.GlobalFunctions;
import com.sdp.appazul.globals.KeyConstants;
import com.sdp.appazul.security.RSAHelper;
import com.sdp.appazul.utils.DateUtils;
import com.sdp.appazul.utils.DeviceUtils;
import com.sdp.appazul.utils.NetworkAddress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.getInstance;


public class DashBoardActivity extends BaseLoggedInActivity implements Consultant.BottomSheetCloseEvent, PaymentLinkBottomSheet.PaymentLinkSheetCloseEvent {

    String qrPermission;
    String paymentPermission;
    ViewPager viewPager;
    MyAdapter myadapter;
    ImageView locationFilterBurgerMenuBtn;
    RelativeLayout locationFilter;
    List<Model> models;
    List<Model> widgetList;
    TabLayout circleIndicator;
    RelativeLayout idParentLayout;
    GridView accessMenu;
    String[] numberWord;
    int[] transportImage = {R.drawable.ic_icon_consulta_dashboard, R.drawable.dashboard_link_de_pagos_icon, R.drawable.ic_icon_qr_code_dashboard_3};
    TextView tvUserName;
    TextView tvUserProf;
    TextView locationTxt;
    boolean submenuVisibility;
    SharedPreferences sharedPreferences;
    String locationJson;
    String finalString;
    String link;
    ImageView dashBoardBurgerMenu;
    ApiManager apiManager = new ApiManager(this);
    LinearLayout widgetContainer;
    List<String> permissionList;
    List<String> productPermissionList;
    List<String> monthList = new ArrayList<>();
    DateUtils utils = new DateUtils();
    GregorianCalendar month;
    TextView currentDayDate;
    Dialog dialog;
    GridViewAdapter gridViewAdapter;
    boolean isConsultClicked = false;
    boolean isLinkDePagoClicked = false;
    int locationType;
    Consultant consultant;
    PaymentLinkBottomSheet linkDePagoPopup;
    BottomNavigationView bottomNavigationView;
    RelativeLayout btnLogoutMenu;
    PaymentLinkBottomSheet paymentSheet;
    Context context;
    EncodingHelper encodingHelper = new EncodingHelper(this);
    String selectedCurrency;
    String selectedLocationId;
    String appVersion;
    GlobalFunctions gFunc = new GlobalFunctions(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dash_board);
        context = this;
        verifyStoragePermissions(this);
        getPreviousData();
        initControls();
        setMainMenuAdapter();
        onClickListeners();
        burgerMenu();
//        checkLocationPermission();
//        checkLocationPermissionForPhone();

        Log.d("DashBoardActivity", "APP Version ::::::: " +  context.getString(R.string.version_label).concat(" ").concat(gFunc.getAppVersion()));
    }


    private void getAppRelatedPermissions() {
        JSONObject json = new JSONObject();
        try {
            String tcpKey = ((AzulApplication) this.getApplication()).getTcpKey();
            String vcr = ((AzulApplication) this.getApplicationContext()).getVcr();
            json.put("tcp", RSAHelper.ecryptRSA(this, tcpKey));
            JSONObject payload = new JSONObject();
            payload.put(Constants.DEVICE, DeviceUtils.getDeviceId(this));

            json.put(Constants.PAYLOAD, RSAHelper.encryptAES(payload.toString(), Base64.decode(tcpKey, 0), Base64.decode(vcr, 0)));
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.GET_APP_RELATED_PERMISSIONS, json);
    }

    private void setMainMenuAdapter() {
        numberWord = new String[]{getResources().getString(R.string.consultant), getResources().getString(R.string.linkPage), getResources().getString(R.string.qr_code)};
        accessMenu = findViewById(R.id.accessMenu);
        gridViewAdapter = new GridViewAdapter(DashBoardActivity.this, numberWord, transportImage);
        accessMenu.setAdapter(gridViewAdapter);
        accessMenuFunction();
    }

    private void getPreviousData() {
        Intent intent = getIntent();

        locationJson = ((AzulApplication) context.getApplicationContext()).getLocationDataShare();

        finalString = locationJson;

        if (intent.getStringExtra(Constants.GENERATE_LINK) != null) {
            link = intent.getStringExtra(Constants.GENERATE_LINK);
            if (link != null && link.equalsIgnoreCase("LINK")) {
                paymentSheet = new PaymentLinkBottomSheet(this, finalString);
                paymentSheet.show(getSupportFragmentManager(), Constants.BOTTOM_SHEET);
            }
        }
    }


    private void getWidgetDataFromAPi(int locationType, String lastLocationMid) {
        JSONObject newJsonObject = new JSONObject();
        try {
            String tcpKey = ((AzulApplication) this.getApplication()).getTcpKey();
            String vcr = ((AzulApplication) this.getApplication()).getVcr();

            newJsonObject.put("tcp", RSAHelper.ecryptRSA(DashBoardActivity.this, tcpKey));
            JSONObject newPayload = new JSONObject();
            newPayload.put(Constants.DEVICE, DeviceUtils.getDeviceId(this));
            newPayload.put("merchantId", lastLocationMid);

            newJsonObject.put(Constants.PAYLOAD, RSAHelper.encryptAES(newPayload.toString(), Base64.decode(tcpKey, 0), Base64.decode(vcr, 0)));

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        if (locationType == 0) {
            apiManager.callAPI(ServiceUrls.TRANSACTION_WIDGET, newJsonObject);
        } else {
            apiManager.callAPI(ServiceUrls.TRANSACTION_WIDGET_QR, newJsonObject);
        }

    }

    @Override
    public void onBackPressed() {
        Log.d("BACK", "");
    }

    Map<String, String> defaultLocations = new HashMap<>();

    private void initControls() {

        currentDayDate = findViewById(R.id.currentDayDate);
        viewPager = findViewById(R.id.viewPager);
        circleIndicator = findViewById(R.id.circleIndicator);
        dashBoardBurgerMenu = findViewById(R.id.dashBoardBurgerMenu);
        widgetContainer = findViewById(R.id.widgetContainer);
        idParentLayout = findViewById(R.id.idParentLayout);
        tvUserName = findViewById(R.id.user_name);
        tvUserProf = findViewById(R.id.user_prof);
        locationTxt = findViewById(R.id.locationTxt);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        btnLogoutMenu = findViewById(R.id.btnLogoutMenu);

        btnLogoutMenu.setOnClickListener(btnLogoutMenuView -> {
            DashboardMenuBottomSheet menuBottomSheet = new DashboardMenuBottomSheet(finalString, DashBoardActivity.this);
            menuBottomSheet.show(getSupportFragmentManager(), Constants.BOTTOM_SHEET);
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        sharedPreferences = ((AzulApplication) this.getApplication()).getPrefs();

        String firstName = sharedPreferences.getString(Constants.FIRST_NAME, "");
        String lastName = sharedPreferences.getString(Constants.LAST_NAME, "");
        String groupName = sharedPreferences.getString(Constants.GROUP_NAME, "");
        String lastLocationName = sharedPreferences.getString(Constants.SELECTED_LOCATION_DASHBOARD, "");
        String lastMid = sharedPreferences.getString(Constants.SELECTED_LOCATION_MID_DASHBOARD, "");
        String lastLocationType = sharedPreferences.getString(Constants.SELECTED_LOCATION_TYPE_DASHBOARD, "");
        String locCurrency = sharedPreferences.getString(Constants.SELECTED_CUURENCY_DASHBOARD, "");


        circleIndicator.setOnTouchListener((view, motionEvent) -> {
            fragmentStatusCheck();
            return false;
        });

        String fullName = firstName + " " + lastName;
        if (!TextUtils.isEmpty(fullName)) {
            tvUserName.setText(fullName);
        }
        if (!TextUtils.isEmpty(groupName)) {
            tvUserProf.setText(groupName);
        }

        viewPager.setOnTouchListener((view, motionEvent) -> {
            fragmentStatusCheck();
            return false;
        });


        permissionList = ((AzulApplication) this.getApplication()).getFeaturePermissionsList();
        productPermissionList = ((AzulApplication) this.getApplication()).getProductPermissionsList();
        getAppRelatedPermissions();
        widgetPermissionCall(locCurrency, lastLocationName, lastLocationType, lastMid);
        monthList = utils.getmonthList();
        month = (GregorianCalendar) getInstance();


        String spanishMonthName = monthList
                .get(Integer.parseInt((String) DateFormat.format(Constants.MM, month)) - 1);
        String year = (String) DateFormat.format(Constants.YYYY, month);

        String date = "" + month.get(DAY_OF_MONTH);

        String currentDateToDisplay = date + " de " + spanishMonthName + " del " + year;
        currentDayDate.setText(currentDateToDisplay);
//        paymentLinkRegenerateApiCall();
    }

    private void paymentLinkRegenerateApiCall() {
        JSONObject newJsonObject = new JSONObject();
        try {
            String tcpKey = ((AzulApplication) this.getApplication()).getTcpKey();
            String vcr = ((AzulApplication) this.getApplication()).getVcr();

            newJsonObject.put("tcp", RSAHelper.ecryptRSA(DashBoardActivity.this, tcpKey));
            JSONObject newPayload = new JSONObject();
            newPayload.put(Constants.DEVICE, DeviceUtils.getDeviceId(DashBoardActivity.this));
            newPayload.put("Code", "AZÚL 2");
            newPayload.put("LinkId", "93b4fdfc");

            newJsonObject.put(Constants.PAYLOAD, RSAHelper.encryptAES(newPayload.toString(), Base64.decode(tcpKey, 0), Base64.decode(vcr, 0)));

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.PAYMENT_LINK_RE_GENERATE, newJsonObject);

    }

    private void widgetPermissionCall(String locCurrency, String lastLocationName, String lastLocationType, String lastMid) {
        getApiCall(locCurrency, lastLocationName, lastLocationType, lastMid);
    }

    private void getApiCall(String locCurrency, String lastLocationName, String lastLocationType, String lastMid) {
        widgetContainer.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(lastLocationName)) {
            locationTxt.setText(lastLocationName);
            int selectedLocationType;
            if (lastLocationType.equalsIgnoreCase(Constants.ASSIGNED_LOCATION)) {
                selectedLocationType = 0;
            } else {
                selectedLocationType = 1;
            }

            selectedLocationId = lastMid;
            selectedCurrency = locCurrency;
//            getWidgetDataFromAPi(selectedLocationType, lastMid);
        } else {
            getDefaultLocation(finalString);
            defaultLocations = ((AzulApplication) this.getApplication()).getDefaultLocation();

            if (defaultLocations != null) {

                String lastChildLocationName = defaultLocations.get("CHILD_LOC_NAME");
                String lastLocationId = defaultLocations.get("CHILD_LOC_ID");
                selectedLocationId = lastLocationId;
                selectedCurrency = defaultLocations.get("CURR");
                String locationToDisplay = lastChildLocationName + " - " + lastLocationId;
                locationTxt.setText(locationToDisplay.toLowerCase());
//                getWidgetDataFromAPi(0, lastLocationId);

            }

        }
    }

    Map<String, String> defaultLocation;

    private void getDefaultLocation(String locationResponse) {
        try {
            JSONObject jsonResponse = new JSONObject(locationResponse);
            JSONArray parentLevelLocations = jsonResponse.getJSONArray("AssignedUnits");
            parseAssignUnits(parentLevelLocations);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void parseAssignUnits(JSONArray parentLevelLocations) {
        try {
            for (int i = 0; i < parentLevelLocations.length(); i++) {
                JSONObject parentData = parentLevelLocations.getJSONObject(i);
                String parentLocationName = parentData.getString("Name");
                JSONArray assignedLocationsObject = parentData.getJSONArray(Constants.ASSIGNED_LOCATION);
                parseInnerLocations(parentLocationName, assignedLocationsObject);
                long abcParent = (long) i;
                sharedPreferences.edit().putLong(Constants.SELECTED_PARENT_DASHBOARD, abcParent).apply();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseInnerLocations(String parentLocationName, JSONArray assignedLocationsObject) {
        try {
            for (int j = 0; j < assignedLocationsObject.length(); j++) {
                JSONObject secondPositionLocation = assignedLocationsObject.getJSONObject(0);
                String secondPositionLocationId = secondPositionLocation.getString(Constants.MERCHANT_ID);
                String secondPositionLocationName = secondPositionLocation.getString("Name");
                String secondPositionLocationCurrency = secondPositionLocation.getString("CurrencyCode");
                String secondPositionTax = "";
                setDataToAppMemory(j, secondPositionLocationCurrency, secondPositionLocation, parentLocationName, secondPositionLocationId, secondPositionLocationName, secondPositionTax);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setDataToAppMemory(int j, String secondPositionLocationCurrency, JSONObject secondPositionLocation, String parentLocationName, String secondPositionLocationId, String secondPositionLocationName, String secondPositionTax) {
        try {
            if (secondPositionLocation.has("TaxExempt")) {
                secondPositionTax = secondPositionLocation.getString("TaxExempt");
            }
            defaultLocation = new HashMap<>();
            defaultLocation.put("PARENT_LOC_NAME", parentLocationName);
            defaultLocation.put("CHILD_LOC_ID", secondPositionLocationId);
            defaultLocation.put("CHILD_LOC_NAME", secondPositionLocationName);
            defaultLocation.put("TAX_STATUS", secondPositionTax);
            defaultLocation.put("CURR", secondPositionLocationCurrency);
            ((AzulApplication) (this).getApplication()).setDefaultLocation(defaultLocation);
            long abc = (long) j;
            long mId = Long.parseLong(secondPositionLocationId);
            sharedPreferences.edit().putLong(Constants.SELECTED_CHILD_DASHBOARD_STRING, 0).apply();
            sharedPreferences.edit().putString(Constants.SELECTED_LOCATION_DASHBOARD, secondPositionLocationName + " - " + mId).apply();
            sharedPreferences.edit().putString(Constants.SELECTED_LOCATION_MID_DASHBOARD, secondPositionLocationId).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void errorAlert(Context activity, int errorType) {
        try {

            dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setContentView(R.layout.feature_permission_dialog);
            TextView textTitle = dialog.findViewById(R.id.textTitle);
            TextView textMsg = dialog.findViewById(R.id.textMsg);
            TextView btnCancel = dialog.findViewById(R.id.btnCancel);

            Typeface typeface;
            typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Medium.ttf");

            btnCancel.setTypeface(typeface);
            textMsg.setTypeface(typeface);
            textTitle.setTypeface(typeface);


            if (errorType == 1) {
                btnCancel.setVisibility(View.VISIBLE);
                textTitle.setText(getResources().getString(R.string.not_permitted_title));
                textMsg.setText(getResources().getString(R.string.get_blue_payment_link));
            } else if (errorType == 3) {
                textTitle.setText(getResources().getString(R.string.no_access_for_functionality));
                textMsg.setText(getResources().getString(R.string.no_access_for_functionality_msg));
                btnCancel.setVisibility(View.INVISIBLE);
            } else {
                btnCancel.setVisibility(View.VISIBLE);
                textTitle.setText(getResources().getString(R.string.not_permitted_title));
                textMsg.setText(getResources().getString(R.string.get_blue_qr_here));
            }


            TextView btnContinue = dialog.findViewById(R.id.btnContinue);
            btnContinue.setTypeface(typeface);

            btnContinue.setOnClickListener(view -> {
                if (errorType == 1) {
                    String qrLink = new NetworkAddress().getSpecificUrl(3);
                    String toolBarTitleText = getResources().getString(R.string.Paymen_link_request);
                    callWebActivity(qrLink, toolBarTitleText);
                } else if (errorType == 0) {
                    String qrLink = new NetworkAddress().getSpecificUrl(0);
                    String toolBarTitleText = getResources().getString(R.string.qr_bar);
                    callWebActivity(qrLink, toolBarTitleText);
                }
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            });

            btnCancel.setOnClickListener(view -> {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        } catch (Exception e) {
            Log.e("Exception : ", Log.getStackTraceString(e));
        }
    }

    private void callWebActivity(String webLink, String toolBarTitleText) {
        Intent intent = new Intent(DashBoardActivity.this, WebActivity.class);
        intent.putExtra("backActivity", Constants.DASH_BOARD);
        intent.putExtra("links", webLink);
        intent.putExtra("toolbarTitleText", toolBarTitleText);
        ((AzulApplication) ((DashBoardActivity) this).getApplication()).setLocationDataShare(finalString);

        startActivity(intent);
    }

    private void onClickListeners() {
        viewPager.setPageTransformer(false, (page, position) -> {
            if (viewPager.getCurrentItem() == 0) {
                page.setTranslationX(-70);
            } else if (viewPager.getCurrentItem() == myadapter.getCount() - 1) {
                page.setTranslationX(70);
            } else {
                page.setTranslationX(0);
            }
        });

    }

    AllTypeLocationFilter locationBottomSheet;

    private void burgerMenu() {
        locationFilterBurgerMenuBtn = findViewById(R.id.locationFilterBurgerMenuBtn);
        locationFilter = findViewById(R.id.locationFilter);

        locationFilterBurgerMenuBtn.setOnClickListener(btnBurgerMenuView -> {
            locationFilterBurgerMenuBtn.setRotation(submenuVisibility ? 180 : 0);
            locationBottomSheet = new AllTypeLocationFilter(finalString, "Dashboard", 0);
            locationBottomSheet.show(getSupportFragmentManager(), Constants.BOTTOM_SHEET);
        });
        locationFilter.setOnClickListener(btnBurgerMenuView -> {
            locationFilterBurgerMenuBtn.setRotation(submenuVisibility ? 180 : 0);
            locationBottomSheet = new AllTypeLocationFilter(finalString, "Dashboard", 0);
            locationBottomSheet.show(getSupportFragmentManager(), Constants.BOTTOM_SHEET);
            fragmentStatusCheck();
        });
    }


    public void setContent(String selectedLocationType, String midSelected, String content, int i, String currency) {
        locationTxt.setText(content.toLowerCase());

        if (selectedLocationType.equalsIgnoreCase(Constants.ASSIGNED_LOCATION)) {
            locationType = 0;
            if (permissionList != null && !permissionList.isEmpty() && permissionList.contains("GenerateStatements")) {
                bottomNavigationView.findViewById(R.id.createStatement).setVisibility(View.VISIBLE);
            }
        } else {
            locationType = 1;
            bottomNavigationView.findViewById(R.id.createStatement).setVisibility(View.GONE);
        }
        if (i == 1) {
//            getWidgetDataFromAPi(locationType, midSelected);
            locationBottomSheet.dismiss();
        }
        selectedLocationId = midSelected;
        selectedCurrency = currency;
    }


    private void accessMenuFunction() {


        accessMenu.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0:
                    consultantMenu();
                    break;

                case 1:
                    GenerateStatementFragment myFragment3 = (GenerateStatementFragment) getSupportFragmentManager().findFragmentByTag(Constants.GENERATE_STATUS);
                    if (myFragment3 != null && myFragment3.isVisible()) {
                        getSupportFragmentManager().beginTransaction().
                                remove(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer)).commit();
                    } else {
                        openLinkDePagoMenu();
                    }
                    break;
                case 2:
                    GenerateStatementFragment myFragment2 = (GenerateStatementFragment) getSupportFragmentManager().findFragmentByTag(Constants.GENERATE_STATUS);
                    if (myFragment2 != null && myFragment2.isVisible()) {
                        getSupportFragmentManager().beginTransaction().
                                remove(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer)).commit();
                    } else {
                        openGenerateQr();
                    }
                    break;
                default:
                    break;

            }
        });

    }

    private void openConsultantMenu() {
        if (gridViewAdapter != null) {
            gridViewAdapter.updateUi(this, getResources().getString(R.string.consultant), 1);
            gridViewAdapter.notifyDataSetChanged();
        }
        isConsultClicked = true;
        consultant = new Consultant(finalString);
        consultant.setBottomSheetCloseEvent(this);
        consultant.show(getSupportFragmentManager(), Constants.BOTTOM_SHEET);
    }


    private void openLinkDePagoMenu() {

        if (productPermissionList != null) {
            if (productPermissionList.contains(Constants.HAS_PAYMENT_LINK)) {
                if (gridViewAdapter != null) {
                    gridViewAdapter.updateUi(this, getResources().getString(R.string.linkPage), 1);
                    gridViewAdapter.notifyDataSetChanged();
                }
                isLinkDePagoClicked = true;
                linkDePagoPopup = new PaymentLinkBottomSheet(this, finalString);
                linkDePagoPopup.setPaymentLinkSheetCloseEvent(this);
                linkDePagoPopup.show(getSupportFragmentManager(), Constants.BOTTOM_SHEET);
            } else {
                errorAlert(this, 1);
            }
        } else {
            AppAlters.errorAlert(this, 4);
        }
    }

    private void openGenerateQr() {
        if (productPermissionList != null) {

            if (productPermissionList.contains("HasQR")) {
                if (permissionList != null && !permissionList.isEmpty()) {
                    checkEnablePermitsPermission(permissionList);
                } else {
                    AppAlters.errorAlert(this, 4);
                }

            } else {
                errorAlert(this, 0);
            }
        } else {
            AppAlters.errorAlert(this, 4);
        }
    }

    private void checkEnablePermitsPermission(List<String> permissionList) {
        if (permissionList.contains("QRQueryTransactions") || permissionList.contains("QRViewCode")) {
            startActivityWithIntent(new Intent(DashBoardActivity.this, QrCode.class));
            ((AzulApplication) ((DashBoardActivity) this).getApplication()).setLocationDataShare(finalString);

            this.overridePendingTransition(R.anim.animation_enter,
                    R.anim.slide_nothing);
        } else {
            errorAlert(this, 3);
        }
    }

    private void consultantMenu() {
        GenerateStatementFragment myFragment = (GenerateStatementFragment) getSupportFragmentManager().findFragmentByTag(Constants.GENERATE_STATUS);
        if (myFragment != null && myFragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer)).commit();
        } else {
            openConsultantMenu();
        }
    }

    public void fragmentStatusCheck() {
        GenerateStatementFragment myFragment = (GenerateStatementFragment) getSupportFragmentManager().findFragmentByTag(Constants.GENERATE_STATUS);
        if (myFragment != null && myFragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer)).commit();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        fragmentStatusCheck();
        return super.onTouchEvent(event);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        Fragment selectedFragment;

        switch (item.getItemId()) {

            case R.id.add:
                break;
            case R.id.createStatement:
                if (locationType == 0) {
                    GenerateStatementFragment myFragment = (GenerateStatementFragment) getSupportFragmentManager().findFragmentByTag(Constants.GENERATE_STATUS);
                    if (myFragment != null && myFragment.isVisible()) {
                        getSupportFragmentManager().beginTransaction().
                                remove(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer)).commit();
                    } else {

                        BottomDateFilterFragment bottomDateFilterActivity = new BottomDateFilterFragment(selectedLocationId);
                        bottomDateFilterActivity.show(this.getSupportFragmentManager(), "bottomSheetDateFilter");
//
//                        selectedFragment = new GenerateStatementFragment(selectedLocationId);
//                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment, Constants.GENERATE_STATUS).commit();
                    }
                }
                break;
            default:
        }
        return true;
    };

    public void transactionWidgetQrResponseDate(String responseString) {
        parseJsonData(responseString);
    }

    private void parseJsonData(String responseString) {
        models = new ArrayList<>();
        widgetList = new ArrayList<>();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(responseString);
            JSONObject jsonTransactionOb = jsonObject.getJSONObject("data");
            JSONArray jsonArray = jsonTransactionOb.getJSONArray("Periods");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonTrObject = jsonArray.getJSONObject(i);

                String period = jsonTrObject.getString("Period");
                String transactionSum = jsonTrObject.getString("TransactionSum");
                String transactionCount = jsonTrObject.getString("TransactionCount");
                String amountToReceive = jsonTrObject.getString("AmountToReceive");
                String discount = jsonTrObject.getString("Discount");
                String dateFrom = jsonTrObject.getString("DateFrom");
                String dateTo = jsonTrObject.getString("DateTo");

                Model model = new Model();
                model.setPeriod(period);
                model.setTransactionSum(transactionSum);
                model.setTransactionCount(transactionCount);
                model.setAmountToReceive(amountToReceive);
                model.setDiscount(discount);
                model.setDateFrom(dateFrom);
                model.setDateTo(dateTo);
                model.setCurrency(selectedCurrency);
                models.add(new Model(model));

            }

            for (int i = 0; i < models.size(); i++) {
                Collections.swap(models, 1, 0);
            }

            myadapter = new MyAdapter(models, this);
            viewPager.setAdapter(myadapter);
            myadapter.notifyDataSetChanged();
            circleIndicator.setupWithViewPager(viewPager, true);

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    public void transactionWidgetResponseDate(String responseString) {
        parseJsonData(responseString);
    }


    public void getAppPermissionsResponse(String responseString) {
        clearListData();
        if (responseString != null && !responseString.isEmpty() && !responseString.equalsIgnoreCase("[]")) {
            parsePermissions(responseString);
        } else {
            AppAlters.errorAlert(this, 4);
        }
    }

    private void parsePermissions(String responseString) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(responseString);
            JSONObject jsonPermissionData = jsonObject.getJSONObject("data");
            JSONArray permitArray = jsonPermissionData.getJSONArray("EnabledPermits");
            if (!permitArray.toString().equalsIgnoreCase("[]")) {
                checkEnablePermitsData(permitArray, jsonPermissionData);
            } else {
                AppAlters.errorAlert(this, 4);
            }

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void checkEnablePermitsData(JSONArray permitArray, JSONObject jsonPermissionData) {
        try {
            permissionList = new ArrayList<>();
            productPermissionList = new ArrayList<>();
            for (int i = 0; i < permitArray.length(); i++) {
                permissionList.add(permitArray.get(i).toString());
            }

            if (!permissionList.isEmpty()) {
                ((AzulApplication) (this).getApplication()).setFeaturePermissionsList(permissionList);
            }
            if (jsonPermissionData.has(Constants.HAS_QR) &&
                    jsonPermissionData.getString(Constants.HAS_QR).equalsIgnoreCase("true")) {
                qrPermission = Constants.HAS_QR;
                productPermissionList.add(qrPermission);
            } else {
                if (productPermissionList.contains(Constants.HAS_QR)) {
                    productPermissionList.remove(Constants.HAS_QR);
                }
            }

            if (jsonPermissionData.has(Constants.HAS_PAYMENT_LINK) &&
                    jsonPermissionData.getString(Constants.HAS_PAYMENT_LINK).equalsIgnoreCase("true")) {
                paymentPermission = Constants.HAS_PAYMENT_LINK;
                productPermissionList.add(paymentPermission);
            } else {
                if (productPermissionList.contains(Constants.HAS_PAYMENT_LINK)) {
                    productPermissionList.remove(Constants.HAS_PAYMENT_LINK);
                }
            }
            ((AzulApplication) (this).getApplication()).setProductPermissionsList(productPermissionList);
            CheckWidgetPermission();
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void CheckWidgetPermission() {
        if (permissionList != null && !permissionList.isEmpty()) {
            if (permissionList.contains("GenerateStatements")) {
                widgetContainer.setVisibility(View.VISIBLE);
                bottomNavigationView.findViewById(R.id.createStatement).setVisibility(View.VISIBLE);
            } else {
                widgetContainer.setVisibility(View.GONE);
                bottomNavigationView.findViewById(R.id.createStatement).setVisibility(View.GONE);
            }
        } else {
            AppAlters.errorAlert(this, 5);
        }
    }

    private void clearListData() {
        if (permissionList != null && !permissionList.isEmpty()) {
            permissionList.clear();
        }
        if (productPermissionList != null && !productPermissionList.isEmpty()) {
            productPermissionList.clear();
        }
    }

    public void getTransactionPdf(String responseString) {
        String pdfJson = ((AzulApplication) this.getApplicationContext()).getPdfJson();

        if (responseString != null && responseString.equalsIgnoreCase("")) {
            AppAlters.showCustomAlert(this, this.getResources().getString(R.string.alert_sorry_title), this.getResources().getString(R.string.no_pdf_message_new), Constants.ACCEPT_BUTTON);
        } else if (responseString != null && responseString.contains("Error: MerchantId not found")) {
            AppAlters.showCustomAlert(this, this.getResources().getString(R.string.alert_sorry_title), this.getResources().getString(R.string.no_pdf_message_new), Constants.ACCEPT_BUTTON);
        } else if (responseString != null && responseString.contains("Error: Error generating report (System.Exception)")) {
            AppAlters.showCustomAlert(this, this.getResources().getString(R.string.alert_sorry_title), this.getResources().getString(R.string.no_pdf_message_new), Constants.ACCEPT_BUTTON);
        } else if (responseString != null && responseString.contains("Error: The remote server returned an error: (500) Internal Server Error.:")) {
            AppAlters.showCustomAlert(this, this.getResources().getString(R.string.alert_sorry_title), this.getResources().getString(R.string.no_pdf_message_new), Constants.ACCEPT_BUTTON);
        } else {
            boolean pdfFound = ((AzulApplication) this.getApplicationContext()).isPdfAvailable();
            if (Boolean.TRUE.equals(pdfFound)) {
                DownloadFile downloadFile = new DownloadFile(this);
                downloadFile.execute(ServiceUrls.GET_TRANSACTION_PDF, pdfJson);
            }
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        String[] permissionArray = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE};
        int permission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, permissionArray, 1);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (gridViewAdapter != null && Boolean.TRUE.equals(isConsultClicked)) {
            gridViewAdapter.updateUi(this, getResources().getString(R.string.consultant), 0);
            gridViewAdapter.notifyDataSetChanged();
            isConsultClicked = false;
        }
        if (gridViewAdapter != null && Boolean.TRUE.equals(isLinkDePagoClicked)) {
            gridViewAdapter.updateUi(this, getResources().getString(R.string.linkPage), 0);
            gridViewAdapter.notifyDataSetChanged();
            isLinkDePagoClicked = false;
        }
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public void onClosed(int closedEvent) {
        if (closedEvent == 1 && Boolean.TRUE.equals(isConsultClicked)) {
            gridViewAdapter.updateUi(this, getResources().getString(R.string.consultant), 0);
            gridViewAdapter.notifyDataSetChanged();
            isConsultClicked = false;
        }
    }

    @Override
    public void onPaymentSheetClosed(int paymentClosedEvent) {
        if (paymentClosedEvent == 1 && Boolean.TRUE.equals(isLinkDePagoClicked)) {
            gridViewAdapter.updateUi(this, getResources().getString(R.string.linkPage), 0);
            gridViewAdapter.notifyDataSetChanged();
            isLinkDePagoClicked = false;
        }
    }
}