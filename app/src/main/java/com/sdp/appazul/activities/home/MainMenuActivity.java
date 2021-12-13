package com.sdp.appazul.activities.home;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.text.HtmlCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sdp.appazul.BuildConfig;
import com.sdp.appazul.R;
import com.sdp.appazul.activities.dashboard.DashBoardActivity;
import com.sdp.appazul.activities.dashboard.QrCode;
import com.sdp.appazul.activities.menuitems.BurgerMenuBottomSheet;
import com.sdp.appazul.activities.menuitems.WebActivity;
import com.sdp.appazul.activities.notifications.PushNotificationSettings;
import com.sdp.appazul.activities.payment.PaymentLocationFilter;
import com.sdp.appazul.activities.payment.QuickSetPaymentActivity;
import com.sdp.appazul.activities.registration.PinLoginActivity;
import com.sdp.appazul.activities.registration.UserRegisterActivity;
import com.sdp.appazul.api.ApiManager;
import com.sdp.appazul.api.ServiceUrls;
import com.sdp.appazul.classes.LocationFilter;
import com.sdp.appazul.classes.LocationFilterThirdGroup;
import com.sdp.appazul.globals.AppAlters;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.GlobalFunctions;
import com.sdp.appazul.globals.KeyConstants;
import com.sdp.appazul.security.RSAHelper;
import com.sdp.appazul.security.SecurePreferences;
import com.sdp.appazul.utils.DeviceUtils;
import com.sdp.appazul.utils.DownloadHandlerObject;
import com.sdp.appazul.utils.KeysUtils;
import com.sdp.appazul.utils.NetworkAddress;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.sdp.appazul.globals.AzulApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kotlin.text.Regex;

public class MainMenuActivity extends BasicRegistrationActivity {

    private Button secondLoginButton;
    LinearLayout qrFormButton;
    ImageView btnBurgerMenu;
    SharedPreferences sscPref;
    GlobalFunctions globals = new GlobalFunctions(this);
    LinearLayout layoutQrCode;
    LinearLayout layoutTarjeta;
    ApiManager apiManager = new ApiManager(this);
    String midQrLogin;
    ImageView azulLogoLayout;
    RelativeLayout welcomeText;
    ScrollView qrLayout;
    LinearLayout azulLayout;
    LinearLayout callCenter;
    LinearLayout qrLocationButton;
    CardView qrViewPager;
    ImageView backBtnMenu;
    ImageView imageView2;
    ImageView imgDownloadQrCode;
    ImageView imgShareQrCode;
    RelativeLayout layoutDownloadShare;
    Context context;
    String pushToken;
    boolean submenuVisibility;
    String locationFilterJson;
    MenuLocationFilter menuLocationFilter;
    PaymentLocationFilter qrLocationFilter;
    HomeLocationFilter homeQrLocationFilter;
    int locationFlag = 0;
    LocationFilter locationFilter = new LocationFilter();
    Dialog dialog;
    RelativeLayout downloadLayout;
    RelativeLayout shareLayout;

    DownloadHandlerObject downloadHandlerObject = new DownloadHandlerObject(this);
    RelativeLayout downloadableLayout;
    ProgressDialog progressDialog;
    int shareFlag = 0;
    String locName = "";
    ImageView imgViewQrCode;
    TextView qrTitle;
    TextView qrMerchantId;
    TextView qrTId;
    ImageView replaceDownloadableImage;
    String imgResponse;
    String qrPermission;
    String paymentPermission;
    List<String> permissionList;
    List<String> productPermissionList;
    Dialog alertDialog;
    String status;
    RelativeLayout login_two_layout;
    static int con = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_two);
        initControls();
        burgerMenu();

        locationMenu();
        toConfirmPin();
        webView();
        furtherAction();
        String firstTime = sscPref.getString(Constants.FIRST_TIME, "");
        if (firstTime.equals("Yes")) {
            callingApiForLocations();
        }
        context = this;
        locationFilter = ((AzulApplication) this.getApplication()).getLocationFilter();


        createUpdateUiHandler();
    }

    private void getPushToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                return;
            }
            pushToken = task.getResult();
        });
    }

    public void customPermissionDialog() {
        dialog = new Dialog(MainMenuActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.notification_permission_dialog);
        TextView textMsg = dialog.findViewById(R.id.textMsg);
        Typeface typeface;
        typeface = Typeface.createFromAsset(MainMenuActivity.this.getAssets(), Constants.FONT_ROBOTO_MEDIUM);
        textMsg.setTypeface(typeface);
        TextView btnNo = dialog.findViewById(R.id.btnNo);
        TextView btnYes = dialog.findViewById(R.id.btnYes);
        btnYes.setTypeface(typeface);

        textMsg.setText(HtmlCompat.fromHtml("¿Permitir que " + "<font><b>" + "APP AZUL" + "</b></font>" + " envíe notificaciones?", HtmlCompat.FROM_HTML_MODE_LEGACY));

        btnYes.setOnClickListener(view -> {
            startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", getPackageName(), null)));
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        });
        btnNo.setOnClickListener(view -> {

            if (pushToken != null && !TextUtils.isEmpty(((AzulApplication) this.getApplication()).getTcpKey())) {
                callApiPushToken();
            }

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void callApiPushToken() {
        JSONObject json = new JSONObject();
        try {
            boolean notificationStatus = NotificationManagerCompat.from(this).areNotificationsEnabled();
            if (notificationStatus) {
                status = "true";
            } else {
                status = "false";
            }
            String tcpKey = ((AzulApplication) this.getApplication()).getTcpKey();
            String vcr = ((AzulApplication) this.getApplication()).getVcr();
            json.put("tcp", RSAHelper.ecryptRSA(MainMenuActivity.this, tcpKey));
            JSONObject payload = new JSONObject();
            payload.put("device", DeviceUtils.getDeviceId(this));
            payload.put("token", pushToken);
            payload.put("notify", status);

            json.put(Constants.PAYLOAD, RSAHelper.encryptAES(payload.toString(), Base64.decode(tcpKey, 0), Base64.decode(vcr, 0)));

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.PUSH_TOKEN, json);
    }

    private void burgerMenu() {
        btnBurgerMenu = findViewById(R.id.burgerMenuLoginBtn);
        btnBurgerMenu.setOnClickListener(btnBurgerMenuView -> {
            BurgerMenuBottomSheet bottomsheet = new BurgerMenuBottomSheet(locationFilterJson);
            bottomsheet.show(getSupportFragmentManager(), Constants.BOTTOM_SHEET);
        });
    }

    private void toConfirmPin() {
        secondLoginButton = findViewById(R.id.secondLoginButton);
        secondLoginButton.setOnClickListener(secondLoginButtonView -> {

            String firstTime = sscPref.getString(Constants.FIRST_TIME, "");
            Intent loginIntent = null;
            if (firstTime.equals("Yes")) {
                loginIntent = new Intent(MainMenuActivity.this, PinLoginActivity.class);
                this.overridePendingTransition(R.anim.animation_enter,
                        R.anim.slide_nothing);
            }
            startActivity(loginIntent);


        });
    }

    private void webView() {
        qrFormButton = findViewById(R.id.qrFormButton);
        qrFormButton.setOnClickListener(qrFormButtonView ->
                callWebActivity(new NetworkAddress().getSpecificUrl(0), getResources().getString(R.string.qr_bar)));
    }

    private void callWebActivity(String webLink, String toolBarTitleText) {
        Intent intent = new Intent(MainMenuActivity.this, WebActivity.class);
        intent.putExtra("backActivity", Constants.PRE_LOGIN);
        intent.putExtra("links", webLink);
        intent.putExtra("toolbarTitleText", toolBarTitleText);
        startActivity(intent);
        this.overridePendingTransition(R.anim.animation_enter,
                R.anim.slide_nothing);
    }


    private void initControls() {
        imageView2 = findViewById(R.id.imageView2);
        login_two_layout = findViewById(R.id.login_two_layout);
        downloadLayout = findViewById(R.id.downloadLayout);
        shareLayout = findViewById(R.id.shareLayout);
        imgDownloadQrCode = findViewById(R.id.imgDownloadQrCode);
        imgShareQrCode = findViewById(R.id.imgShareQrCode);
        layoutDownloadShare = findViewById(R.id.layoutDownloadShare);
        qrLocationButton = findViewById(R.id.qrLocationButton);
        qrViewPager = findViewById(R.id.qrViewPager);
        backBtnMenu = findViewById(R.id.backBtnMenu);
        azulLayout = findViewById(R.id.azulLayout);
        callCenter = findViewById(R.id.callCenter);
        azulLogoLayout = findViewById(R.id.imageViewIcon);
        welcomeText = findViewById(R.id.welcomeText);
        qrLayout = findViewById(R.id.qrLayout);
        layoutQrCode = findViewById(R.id.layoutQrCode);
        layoutTarjeta = findViewById(R.id.layoutTarjeta);
        secondLoginButton = findViewById(R.id.secondLoginButton);
        secondLoginButton.setTransformationMethod(null);

        downloadableLayout = findViewById(R.id.downloadableLayout);
        replaceDownloadableImage = findViewById(R.id.replaceDownloadableImage);
        imgViewQrCode = findViewById(R.id.imgViewQrCode);
        qrTitle = findViewById(R.id.qrTitle);
        qrMerchantId = findViewById(R.id.qrMerchantId);
        qrTId = findViewById(R.id.qrTId);

        backBtnMenu.setOnClickListener(backBtnMenuView -> {
            azulLogoLayout.setVisibility(View.VISIBLE);
            btnBurgerMenu.setVisibility(View.VISIBLE);
            imageView2.setVisibility(View.VISIBLE);
            welcomeText.setVisibility(View.VISIBLE);
            azulLayout.setVisibility(View.VISIBLE);
            callCenter.setVisibility(View.VISIBLE);
            backBtnMenu.setVisibility(View.GONE);
            qrLayout.setVisibility(View.GONE);
            if (productPermissionList != null && !productPermissionList.contains(Constants.HAS_QR)) {
                qrFormButton.setVisibility(View.VISIBLE);
            } else {
                qrFormButton.setVisibility(View.GONE);
                RelativeLayout.LayoutParams paramsq = (RelativeLayout.LayoutParams) callCenter.getLayoutParams();
                paramsq.setMargins(0, 96, 0, 0);
                callCenter.setLayoutParams(paramsq);
            }


        });


        callCenter.setOnClickListener(callCenterView -> {
                    CallingCallCenter();
                }
        );

        qrLocationButton.setOnClickListener(qrLocationButtonView -> {
            layoutQrCode.setRotation(submenuVisibility ? 180 : 0);

            homeQrLocationFilter = new HomeLocationFilter(locationFilterJson, "Menu", 1);
            homeQrLocationFilter.show(getSupportFragmentManager(), Constants.BOTTOM_SHEET);
            locationFlag = 0;
        });
        verifyStoragePermissions(this);

        downloadQrListener();

        shareLayout.setOnClickListener(imgDownloadQrCodeView -> {
                    if (imgResponse != null && !TextUtils.isEmpty(imgResponse)) {
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
                }
        );


    }

    private void CallingCallCenter() {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:8095442985"));
            startActivity(intent);
            this.overridePendingTransition(R.anim.animation_enter,
                    R.anim.slide_nothing);

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }


    private void downloadQrListener() {
        downloadLayout.setOnClickListener(imgDownloadQrCodeView -> {
                    Message message = new Message();
                    message.what = 0;
                    updateUIHandler.sendMessage(message);
                    if (imgResponse != null && !TextUtils.isEmpty(imgResponse)) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // do your stuff
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        try {

                                            saveImage(getBitmapFromView(downloadableLayout), 0);
                                            Message endMessage = new Message();
                                            endMessage.what = 1;
                                            updateUIHandler.sendMessage(endMessage);

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
                }
        );
    }

    private Handler updateUIHandler = null;

    private void createUpdateUiHandler() {
        if (updateUIHandler == null) {
            updateUIHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // Means the message is sent from child thread.
                    if (msg.what == 0) {
                        progressDialog = ProgressDialog.show(MainMenuActivity.this, null,
                                getResources().getString(R.string.loading_lbl), false, false);
                        progressDialog.setCancelable(false);

                    } else if (msg.what == 1) {
                        if ((progressDialog != null) && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            CallSnackBar();
                        }
                    }
                }

                private void CallSnackBar() {
                    if (shareFlag == 0) {
                        Snackbar snackbar = Snackbar.make(login_two_layout, context.getResources().getString(R.string.save_qrcode), con)
                                .setBackgroundTint(Color.parseColor("#0091DF"));
                        snackbar.show();
                    }
                }
            };
        }
    }


    public static void verifyStoragePermissions(Activity activity) {
        String[] permissionArray = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        // Check if we have write permission
        int permission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, permissionArray, 1);
        }
    }

    private void callingApiForLocations() {
        JSONObject object = new JSONObject();
        try {
            String randomPass;
            int capacity = 32;
            randomPass = KeysUtils.getRandomAlphanumDigitKey(capacity);
            object.put(Constants.DEVICE, RSAHelper.ecryptRSA(this, DeviceUtils.getDeviceId(this)));
            object.put("av", RSAHelper.ecryptRSA(this, randomPass));
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.R_TOKEN, object);
//        apiManager.callAPI(ServiceUrls.REQUEST_TOKEN_BEFORE_LOGIN, object);
    }


    private void getAppRelatedPermissions() {
        JSONObject json = new JSONObject();
        try {
            String tcpKey = ((AzulApplication) this.getApplication()).getTcpKey();
            String vcr = ((AzulApplication) this.getApplication()).getVcr();
            json.put("tcp", RSAHelper.ecryptRSA(this, tcpKey));
            JSONObject payload = new JSONObject();
            payload.put(Constants.DEVICE, DeviceUtils.getDeviceId(this));

            json.put(Constants.PAYLOAD, RSAHelper.encryptAES(payload.toString(), Base64.decode(tcpKey, 0), Base64.decode(vcr, 0)));
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.GET_APP_RELATED_PERMISSIONS, json);
    }

    //Permission api
    public void getAppPermissionsResponse(String responseString) {
        permissionList = new ArrayList<>();
        productPermissionList = new ArrayList<>();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(responseString);
            JSONObject jsonPermissionData = jsonObject.getJSONObject("data");
            JSONArray permitArray = jsonPermissionData.getJSONArray("EnabledPermits");
            for (int i = 0; i < permitArray.length(); i++) {
                permissionList.add(permitArray.get(i).toString());
            }

            CheckPermissionListSize(permissionList);
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

            if (productPermissionList != null && !productPermissionList.contains(Constants.HAS_QR)) {
                qrFormButton.setVisibility(View.VISIBLE);
            } else {
                qrFormButton.setVisibility(View.GONE);
                RelativeLayout.LayoutParams paramsq = (RelativeLayout.LayoutParams) callCenter.getLayoutParams();
                paramsq.setMargins(0, 96, 0, 0);
                callCenter.setLayoutParams(paramsq);
            }
            getPushToken();
            boolean notificationStatus = NotificationManagerCompat.from(this).areNotificationsEnabled();
            if (!notificationStatus) {
                customPermissionDialog();
            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void CheckPermissionListSize(List<String> permissionList) {
        if (!permissionList.isEmpty()) {
            ((AzulApplication) (this).getApplication()).setFeaturePermissionsList(permissionList);
        }
    }


    private void furtherAction() {
        sscPref = new SecurePreferences(this, globals.getPPRString(), globals.getSSCString());
        ((AzulApplication) this.getApplication()).setPrefs(sscPref);
    }

    public void qrResponseBeforeLogin(String responseData) {
        locationFilterJson = responseData;
        checkUserStatus(locationFilterJson);
        getAppRelatedPermissions();
    }

    private void checkUserStatus(String loginResponse) {
        try {
            if (loginResponse != null && !loginResponse.equalsIgnoreCase("")) {

                JSONObject jsonObject = new JSONObject(loginResponse);
                String userStatusValue = jsonObject.getString("Status");
                validateUser(userStatusValue);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void validateUser(String userStatusValue) {

        if (userStatusValue != null && !userStatusValue.isEmpty()) {
            if (userStatusValue.equalsIgnoreCase(Constants.BLOCK_STATUS)) {
                showDialog(0, MainMenuActivity.this, getResources().getString(R.string.Blocked_title), getResources().getString(R.string.Blocked));
            } else if (userStatusValue.equalsIgnoreCase(Constants.DISABLED_STATUS) ||
                    userStatusValue.equalsIgnoreCase("Deshabilitado") ||
                    userStatusValue.equalsIgnoreCase("Disabled")) {
                showDialog(1, MainMenuActivity.this, getResources().getString(R.string.Disabled_title), getResources().getString(R.string.Disabled));
            } else if (userStatusValue.equalsIgnoreCase("UnderInvestigation") ||
                    userStatusValue.equalsIgnoreCase("En investigación")) {
                showDialog(1, MainMenuActivity.this, getResources().getString(R.string.Under_investigation_title), getResources().getString(R.string.Under_investigation));
            } else if (userStatusValue.equalsIgnoreCase("New")) {
                showDialog(3, MainMenuActivity.this, getResources().getString(R.string.Status_Nuevo_title), getResources().getString(R.string.Status_Nuevo));
            } else if (userStatusValue.equalsIgnoreCase("NewExpired")) {
                showDialog(4, MainMenuActivity.this, getResources().getString(R.string.New_Expired_title), getResources().getString(R.string.New_Expired));
            } else if (userStatusValue.equalsIgnoreCase("ActiveExpired")) {
                showDialog(5, MainMenuActivity.this, getResources().getString(R.string.Active_expired_title), getResources().getString(R.string.Active_expired));
            }
        }
    }

    private void callDeregisterApi() {

        JSONObject json = new JSONObject();
        try {
            String tcpKey = ((AzulApplication) MainMenuActivity.this.getApplicationContext()).getTcpKey();
            json.put("tcp", RSAHelper.ecryptRSA(MainMenuActivity.this, tcpKey));
            json.put(Constants.DEVICE, RSAHelper.ecryptRSA(MainMenuActivity.this, DeviceUtils.getDeviceId(MainMenuActivity.this)));

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.DEREGISTER_USER, json);
    }


    public void errorAlert(Context activity, int errorType) {
        try {

            alertDialog = new Dialog(activity);
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertDialog.setCancelable(false);
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            alertDialog.setContentView(R.layout.feature_permission_dialog);
            TextView textTitle = alertDialog.findViewById(R.id.textTitle);
            TextView textMsg = alertDialog.findViewById(R.id.textMsg);
            TextView btnCancel = alertDialog.findViewById(R.id.btnCancel);

            Typeface typeface;
            typeface = Typeface.createFromAsset(activity.getAssets(), Constants.FONT_ROBOTO_MEDIUM);

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


            TextView btnContinue = alertDialog.findViewById(R.id.btnContinue);
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
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            });

            btnCancel.setOnClickListener(view -> {
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.show();

        } catch (
                Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void locationMenu() {
        layoutTarjeta.setOnClickListener(layoutTarjetaView -> {
            String firstTime = sscPref.getString(Constants.FIRST_TIME, "");
            //Permission check
            if (productPermissionList != null) {
                openTarjetaModule(firstTime);
            } else {
                AppAlters.errorAlert(this, 4);
            }
        });

        layoutQrCode.setOnClickListener(btnBurgerMenuView -> {
            String firstTime = sscPref.getString(Constants.FIRST_TIME, "");
            if (firstTime.equals("Yes")) {
                if (productPermissionList != null
                ) {
                    openQrWithPermission();
                } else {
                    AppAlters.errorAlert(this, 4);
                }

            }
        });
    }

    private void openQrWithPermission() {
        if (productPermissionList.contains(Constants.HAS_QR)) {
            if (permissionList != null) {
                if (permissionList.contains("QRViewCode")) {
                    openQrScreen();
                } else {
                    errorAlert(MainMenuActivity.this, 3);
                }
            } else {
                AppAlters.errorAlert(this, 4);
            }
        } else {
            errorAlert(MainMenuActivity.this, 0);
        }
    }

    private void openQrScreen() {
        if (midQrLogin != null && midQrLogin.length() > 0) {
            azulLogoLayout.setVisibility(View.GONE);
            imageView2.setVisibility(View.GONE);
            welcomeText.setVisibility(View.GONE);
            azulLayout.setVisibility(View.GONE);
            callCenter.setVisibility(View.GONE);
            qrFormButton.setVisibility(View.GONE);
            btnBurgerMenu.setVisibility(View.GONE);


            backBtnMenu.setVisibility(View.VISIBLE);
            qrLayout.setVisibility(View.VISIBLE);
            callGetQrApi(midQrLogin);
        } else {
            insideLocationFilter();
        }
    }

    private void openTarjetaModule(String firstTime) {
        if (firstTime.equals("Yes")) {
            if (productPermissionList.contains(Constants.HAS_PAYMENT_LINK)) {
                openQuickPaymentMenu();
            } else {
                errorAlert(MainMenuActivity.this, 1);
            }

        }
    }

    private void openQuickPaymentMenu() {
        if (permissionList != null) {
            if (permissionList.contains("APPPaymentLinksSale")) {

                layoutQrCode.setRotation(submenuVisibility ? 180 : 0);
                menuLocationFilter = new MenuLocationFilter(locationFilterJson, "Menu", 1);
                menuLocationFilter.show(getSupportFragmentManager(), Constants.BOTTOM_SHEET);
                locationFlag = 1;
            } else {
                errorAlert(MainMenuActivity.this, 3);
            }
        } else {
            AppAlters.errorAlert(this, 4);
        }
    }

    private void insideLocationFilter() {
        azulLogoLayout.setVisibility(View.VISIBLE);
        imageView2.setVisibility(View.VISIBLE);
        welcomeText.setVisibility(View.VISIBLE);
        azulLayout.setVisibility(View.VISIBLE);
        callCenter.setVisibility(View.VISIBLE);
        if (productPermissionList != null && !productPermissionList.contains(Constants.HAS_QR)) {
            qrFormButton.setVisibility(View.VISIBLE);
        } else {
            qrFormButton.setVisibility(View.GONE);
            RelativeLayout.LayoutParams paramsq = (RelativeLayout.LayoutParams) callCenter.getLayoutParams();
            paramsq.setMargins(0, 96, 0, 0);
            callCenter.setLayoutParams(paramsq);
        }

        btnBurgerMenu.setVisibility(View.VISIBLE);
        backBtnMenu.setVisibility(View.GONE);
        qrLayout.setVisibility(View.GONE);
        layoutQrCode.setRotation(submenuVisibility ? 180 : 0);
        homeQrLocationFilter = new HomeLocationFilter(locationFilterJson, "Menu", 1);
        homeQrLocationFilter.show(getSupportFragmentManager(), Constants.BOTTOM_SHEET);
        locationFlag = 0;
    }

    public void setContent(String selectedCode, String parentLocation, String locNameselected,
                           String lastChildMidQr, int dismissFlag, LocationFilterThirdGroup locationFilterThirdGroup) {

        if (dismissFlag == 1) {

            if (locationFlag == 1) {
                Intent intent = new Intent(MainMenuActivity.this, QuickSetPaymentActivity.class);

                ((AzulApplication) ((MainMenuActivity) this).getApplication()).setLocationDataShare(locationFilterJson);
                 intent.putExtra("CODE", selectedCode);
                intent.putExtra(Constants.LOCATION_NAME_SELECTED, locNameselected);
                intent.putExtra(Constants.LOCATION_ID_SELECTED, lastChildMidQr);
                intent.putExtra("TAX_STATUS", locationFilterThirdGroup.getTaxExempt());
                intent.putExtra("CURRENCY", locationFilterThirdGroup.getCurrency());
                intent.putExtra(Constants.LOCATION_PARENT_NAME_SELECTED, parentLocation);
                startActivity(intent);
                this.overridePendingTransition(R.anim.animation_enter,
                        R.anim.slide_nothing);

            } else {
                midQrLogin = lastChildMidQr;
                locName = locNameselected;
                azulLogoLayout.setVisibility(View.GONE);
                imageView2.setVisibility(View.GONE);
                welcomeText.setVisibility(View.GONE);
                azulLayout.setVisibility(View.GONE);
                callCenter.setVisibility(View.GONE);
                qrFormButton.setVisibility(View.GONE);
                RelativeLayout.LayoutParams paramsq = (RelativeLayout.LayoutParams) callCenter.getLayoutParams();
                paramsq.setMargins(0, 96, 0, 0);
                callCenter.setLayoutParams(paramsq);
                btnBurgerMenu.setVisibility(View.GONE);
                backBtnMenu.setVisibility(View.VISIBLE);
                qrLayout.setVisibility(View.VISIBLE);

                callGetQrApi(lastChildMidQr);
                homeQrLocationFilter.dismiss();
                qrTitle.setText("-");
                qrMerchantId.setText("-");
                imgViewQrCode.setImageResource(0);
                imgViewQrCode.setVisibility(View.INVISIBLE);

            }
        }
    }

    private void callGetQrApi(String lastLocationMid) {
        Log.d("", "" + lastLocationMid);
        JSONObject json = new JSONObject();
        try {
            String tcpKey = ((AzulApplication) this.getApplication()).getTcpKey();
            String vcr = ((AzulApplication) this.getApplication()).getVcr();
            json.put("tcp", RSAHelper.ecryptRSA(MainMenuActivity.this, tcpKey));
            JSONObject payload = new JSONObject();
            payload.put(Constants.DEVICE, DeviceUtils.getDeviceId(this));
            payload.put("merchantId", lastLocationMid);

            json.put(Constants.PAYLOAD, RSAHelper.encryptAES(payload.toString(), Base64.decode(tcpKey, 0), Base64.decode(vcr, 0)));
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.GET_QR, json);
    }


    public void getResponseForQr(String responseData) {
        try {
            qrSetView(responseData);
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }


    private void qrSetView(String svgResponse) {
        imgResponse = svgResponse;
        imgViewQrCode.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(locName)) {
            qrTitle.setText(locName);
        }
        qrMerchantId.setText("MID: " + midQrLogin);

        SVG svg;
        SVG svgForMarket;


        try {
            if (!TextUtils.isEmpty(svgResponse)) {
                svg = SVG.getFromString(svgResponse);
                svgForMarket = SVG.getFromString(svgResponse);
                PictureDrawable pd = new PictureDrawable(svg.renderToPicture());
                PictureDrawable pdForMarket = new PictureDrawable(svgForMarket.renderToPicture());
                imgViewQrCode.setImageDrawable(pd);
                replaceDownloadableImage.setImageDrawable(pdForMarket);

            }

        } catch (
                SVGParseException e) {
            e.printStackTrace();
        }
    }

    public void paymentLnkMerchantResponse(String responseData) {

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(responseData);
            Log.d("DATA", "Link Merchant Data ==>  " + jsonObject.toString());
            parseLinkData(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void parseLinkData(JSONObject responseData) {

        if (responseData != null) {
            Log.d("TAG", "parseLinkData: ");
        }

    }

    public void getDeregisterAPIResponse(String responseString) {
        if (responseString != null) {
            Log.d("TAF", "getDeregisterAPIResponse: " + responseString);
        }
        ((AzulApplication) this.getApplication()).getPrefs().edit().clear().apply();
        startActivity(new Intent(this, UserRegisterActivity.class));
        finish();

    }


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

    private void saveImage(Bitmap finalBitmap, int downloadAndShare) throws IOException {

        if (downloadAndShare == 0) {
            downloadHandlerObject.saveImage(finalBitmap, String.valueOf(System.currentTimeMillis()), 0);
            shareFlag = 0;
        } else {
            downloadHandlerObject.saveImage(finalBitmap, String.valueOf(System.currentTimeMillis()), 1);
            shareFlag = 1;
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (message.what == 1) {
                progressDialog = ProgressDialog.show(MainMenuActivity.this, null,
                        getResources().getString(R.string.loading_lbl), false, false);
                progressDialog.setCancelable(false);

            } else if (message.what == 2) {
                dismissProgressDialog(progressDialog, shareFlag);
            }
        }

        private void dismissProgressDialog(ProgressDialog progressDialog, int shareFlag) {
            if ((progressDialog != null) && progressDialog.isShowing()) {
                progressDialog.dismiss();

                if (shareFlag == 0) {
                    Snackbar snackbar = Snackbar.make(login_two_layout, context.getResources().getString(R.string.save_qrcode), con)
                            .setBackgroundTint(Color.parseColor("#0091DF"));
                    snackbar.show();
                }
            }

        }
    };


    @Override
    public void onBackPressed() {
        Log.d("TAG", "onBackPressed: ");
    }


    public void showDialog(int dialogAction, Context activity, String statusTitle, String status) {
        try {

            Dialog messageDialog = new Dialog(activity);
            messageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            messageDialog.setCancelable(false);
            messageDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            messageDialog.setContentView(R.layout.custom_alert_dialog);
            TextView textTitle = messageDialog.findViewById(R.id.textTitle);
            TextView textMsg = messageDialog.findViewById(R.id.textMsg);

            Typeface typeface;
            typeface = Typeface.createFromAsset(activity.getAssets(), Constants.FONT_ROBOTO_MEDIUM);

            textMsg.setTypeface(typeface);
            textMsg.setText(status);
            textTitle.setTypeface(typeface);
            textTitle.setText(statusTitle);

            TextView btnContinue = messageDialog.findViewById(R.id.btnContinue);
            btnContinue.setTypeface(typeface);
            btnContinue.setText("Aceptar");
            TextView btnCancel = messageDialog.findViewById(R.id.btnCancel);
            btnCancel.setTypeface(typeface);
            btnCancel.setText("NO");
            btnCancel.setVisibility(View.INVISIBLE);

            btnContinue.setOnClickListener(view -> {
                if (dialogAction == 0) {
                    openLoginScreen();
                } else if (dialogAction == 1) {
                    callDeregisterApi();
                }
                messageDialog.dismiss();
            });

            messageDialog.show();
        } catch (
                Exception e) {
            Log.e("Exception : ", Log.getStackTraceString(e));
        }
    }

    private void openLoginScreen() {
        Intent intent = new Intent(MainMenuActivity.this, UserRegisterActivity.class);
        startActivity(intent);
        this.overridePendingTransition(R.anim.animation_enter,
                R.anim.slide_nothing);
    }

    public void getRTokenResponse(String responseString) {
        try {
            JSONObject jsonObject = new JSONObject(responseString);

            if (jsonObject.has(KeyConstants.ACCESS_TOKEN_LABEL)) {
                callingGetUser();
            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void callingGetUser() {
        JSONObject object = new JSONObject();
        try {
            String randomPass;
            int capacity = 32;
            randomPass = KeysUtils.getRandomAlphanumDigitKey(capacity);
            object.put(Constants.DEVICE, RSAHelper.ecryptRSA(this, DeviceUtils.getDeviceId(this)));
            object.put("av", RSAHelper.ecryptRSA(this, randomPass));
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.GET_USER, object);
    }

    public void getUserResponseData(String responseString) {
        locationFilterJson = responseString;
        checkUserStatus(responseString);
        getAppRelatedPermissions();
    }
}
