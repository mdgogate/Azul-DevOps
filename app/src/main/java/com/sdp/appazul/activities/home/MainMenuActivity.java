package com.sdp.appazul.activities.home;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
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

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.messaging.FirebaseMessaging;
import com.sdp.appazul.BuildConfig;
import com.sdp.appazul.R;
import com.sdp.appazul.activities.menuitems.BurgerMenuBottomSheet;
import com.sdp.appazul.activities.menuitems.WebActivity;
import com.sdp.appazul.activities.payment.QuickSetPaymentActivity;
import com.sdp.appazul.activities.registration.PinLoginActivity;
import com.sdp.appazul.activities.registration.UserRegisterActivity;
import com.sdp.appazul.api.ApiManager;
import com.sdp.appazul.api.ServiceUrls;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.GlobalFunctions;
import com.sdp.appazul.globals.KeyConstants;
import com.sdp.appazul.security.RSAHelper;
import com.sdp.appazul.security.SecurePreferences;
import com.sdp.appazul.utils.DeviceUtils;
import com.sdp.appazul.utils.KeysUtils;
import com.sdp.appazul.utils.NetworkAddress;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.google.android.material.tabs.TabLayout;
import com.sdp.appazul.globals.AzulApplication;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

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
    SharedPreferences sharedPreferences;
    ImageView azulLogoLayout;
    RelativeLayout welcomeText;
    ScrollView qrLayout;
    LinearLayout azulLayout;
    LinearLayout callCenter;
    LinearLayout qrLocationButton;
    CardView qrViewPager;
    ImageView backBtnMenu;
    ImageView imgDownloadQrCode;
    ImageView imgShareQrCode;
    RelativeLayout layoutDownloadShare;
    Context context;
    String pushToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_two);
        initControls();
        burgerMenu();
        callingApiForLocations();
        locationMenu();
        toConfirmPin();
        webView();
        furtherAction();
        sharedPreferences = ((AzulApplication) this.getApplication()).getPrefs();
        context = this;

        midQrLogin = sharedPreferences.getString(Constants.SELECTED_LOCATION_MID_QRCODE_LOGIN, "");
        getPushToken();

        boolean notificationStatus = NotificationManagerCompat.from(this).areNotificationsEnabled();
        if (!notificationStatus) {
            customPermissionDialog();
        }
    }

    private void getPushToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                return;
            }
            pushToken = task.getResult();
            Log.d("TAG", "FCM TOKEN :::  " + pushToken);
        });
    }

    public void customPermissionDialog() {
        Dialog dialog = new Dialog(MainMenuActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.notification_permission_dialog);
        TextView textMsg = dialog.findViewById(R.id.textMsg);
        Typeface typeface;
        typeface = Typeface.createFromAsset(MainMenuActivity.this.getAssets(), "fonts/Roboto-Medium.ttf");
        textMsg.setTypeface(typeface);
        TextView btnNo = dialog.findViewById(R.id.btnNo);
        TextView btnYes = dialog.findViewById(R.id.btnYes);
        btnYes.setTypeface(typeface);

        textMsg.setText(Html.fromHtml("¿Permitir que " + "<font><b>" + "APP AZUL" + "</b></font>" + " envíe notificaciones?"));

        btnYes.setOnClickListener(view -> {
            startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", getPackageName(), null)));
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        });
        btnNo.setOnClickListener(view -> {
            if (pushToken != null) {
                if (!TextUtils.isEmpty(((AzulApplication) this.getApplication()).getTcpKey())) {
                    callApiPushToken();
                }
            }
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    String status;

    @Override
    protected void onResume() {
        super.onResume();
        if (pushToken != null) {
            if (!TextUtils.isEmpty(((AzulApplication) this.getApplication()).getTcpKey())) {
                callApiPushToken();
            }
        }
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
            json.put("tcp", RSAHelper.ecryptRSA(MainMenuActivity.this, tcpKey));
            JSONObject payload = new JSONObject();
            payload.put(Constants.DEVICE, DeviceUtils.getDeviceId(this));
            JSONObject pagosrequest = new JSONObject();
            pagosrequest.put("token", pushToken);
            pagosrequest.put("notify", status);

            payload.put("obtenerrequest", pagosrequest);
            json.put("payload", RSAHelper.encryptAES(payload.toString(), Base64.decode(tcpKey, 0)));

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.PUSH_TOKEN, json);
    }

    private void burgerMenu() {
        btnBurgerMenu = findViewById(R.id.burgerMenuLoginBtn);
        btnBurgerMenu.setOnClickListener(btnBurgerMenuView -> {
            BurgerMenuBottomSheet bottomsheet = new BurgerMenuBottomSheet();
            bottomsheet.show(getSupportFragmentManager(), Constants.BOTTOM_SHEET);
        });
    }

    private void toConfirmPin() {
        secondLoginButton = findViewById(R.id.secondLoginButton);
        secondLoginButton.setOnClickListener(secondLoginButtonView -> {

            String firstTime = sscPref.getString(Constants.FIRST_TIME, "");
            if (firstTime.equals("Yes")) {
                Intent intent = new Intent(MainMenuActivity.this, PinLoginActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainMenuActivity.this, UserRegisterActivity.class);
                startActivity(intent);
            }


        });
    }

    private void webView() {
        qrFormButton = findViewById(R.id.qrFormButton);
        qrFormButton.setOnClickListener(qrFormButtonView ->
                callWebActivity());
    }

    private void callWebActivity() {
        Intent intent = new Intent(MainMenuActivity.this, WebActivity.class);
        intent.putExtra("backActivity", "LoginTwo");
        intent.putExtra("links", new NetworkAddress().getSpecificUrl(0));
        intent.putExtra("toolbarTitleText", getResources().getString(R.string.qr_bar));
        startActivity(intent);
    }


    private void initControls() {
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
        backBtnMenu.setOnClickListener(backBtnMenuView -> {
            azulLogoLayout.setVisibility(View.VISIBLE);
            welcomeText.setVisibility(View.VISIBLE);
            azulLayout.setVisibility(View.VISIBLE);
            callCenter.setVisibility(View.VISIBLE);
            qrFormButton.setVisibility(View.VISIBLE);

            backBtnMenu.setVisibility(View.GONE);
            qrLayout.setVisibility(View.GONE);
        });

        qrLocationButton.setOnClickListener(qrLocationButtonView -> {
            layoutQrCode.setRotation(submenuVisibility ? 180 : 0);
            menuLocationFilter = new MenuLocationFilter(locationFilterJson, "Menu");
            menuLocationFilter.show(getSupportFragmentManager(), Constants.BOTTOM_SHEET);
        });
        verifystoragepermissions(this);

        imgDownloadQrCode.setOnClickListener(imgDownloadQrCodeView -> {
                    imgDownloadQrCode.setVisibility(View.GONE);
                    imgShareQrCode.setVisibility(View.GONE);
                    Bitmap bitmap = getScreenShotFromView(qrViewPager);
                    if (bitmap != null) {
                        saveMediaToStorage(bitmap, "result", 0);
                        imgDownloadQrCode.setVisibility(View.VISIBLE);
                        imgShareQrCode.setVisibility(View.VISIBLE);
                    }
                }
        );

        imgShareQrCode.setOnClickListener(imgDownloadQrCodeView -> {
                    imgDownloadQrCode.setVisibility(View.GONE);
                    imgShareQrCode.setVisibility(View.GONE);
                    Bitmap bitmap = getScreenShotFromView(qrViewPager);
                    if (bitmap != null) {
                        File file = saveMediaToStorage(bitmap, "result", 1);
                        imgDownloadQrCode.setVisibility(View.VISIBLE);
                        imgShareQrCode.setVisibility(View.VISIBLE);
                        Log.i("chase", "filepath: " + file.getAbsolutePath());
                        Uri uri = FileProvider.getUriForFile(MainMenuActivity.this,
                                BuildConfig.APPLICATION_ID + ".provider", file);


                        Intent shareIntent = new Intent();
                        shareIntent.setData(uri);
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "Paga seleccionando este Código QR AZUL desde tu App Popular o App tPago");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent.setType("image/*");
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY);
                        for (ResolveInfo resolveInfo : resInfoList) {
                            String packageName = resolveInfo.activityInfo.packageName;
                            this.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }

                        startActivity(Intent.createChooser(shareIntent, "share via"));

                    }
                }
        );


    }

    public static void verifystoragepermissions(Activity activity) {

        int permissions = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissions != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, permissionstorage, REQUEST_EXTERNAL_STORAGE);
        }
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] permissionstorage = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};


    private File saveMediaToStorage(Bitmap bitmap, String filename, int share) {
        Date date = new Date();

        CharSequence format = android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", date);
        String dirpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "";

        String path = dirpath + "/" + filename + "-" + format + ".jpeg";

        File imagePath = new File(path);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            if (share == 0) {
                Toast toast = Toast.makeText(getApplicationContext(), "Código QR guardado", Toast.LENGTH_LONG);
                View view = toast.getView();
                TextView view1 = (TextView) view.findViewById(android.R.id.message);
                view1.setTextColor(Color.WHITE);
                view.setBackgroundResource(R.drawable.toast_msg_back_sky_blue);
                toast.show();
            }

        } catch (Exception e) {
            Log.e("GREC", e.getMessage(), e);
        }
        return imagePath;
    }

    private Bitmap getScreenShotFromView(View v) {
        Bitmap screenshot = null;
        try {
            screenshot = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(screenshot);
            v.draw(canvas);
        } catch (Exception e) {
            Log.d("", "");
        }
        // return the bitmap
        return screenshot;
    }

    private void callingApiForLocations() {
        JSONObject object = new JSONObject();
        try {
            String randomPass = "";
            int capacity = 32;
            randomPass = KeysUtils.getRandomAlphanumDigitKey(capacity);
            object.put(Constants.DEVICE, RSAHelper.ecryptRSA(this, DeviceUtils.getDeviceId(this)));
            object.put("av", RSAHelper.ecryptRSA(this, randomPass));
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.QR_CODE_BEFORE_LOGIN, object);
    }


    private void furtherAction() {
        sscPref = new SecurePreferences(this, globals.getPPRString(), globals.getSSCString());
        ((AzulApplication) this.getApplication()).setPrefs(sscPref);
    }

    public void qrResponseBeforeLogin(String responseData) {
        locationFilterJson = responseData;
    }

    boolean submenuVisibility;
    String locationFilterJson;
    MenuLocationFilter menuLocationFilter;

    private void locationMenu() {
        layoutTarjeta.setOnClickListener(layoutTarjetaView -> {
            String firstTime = sscPref.getString(Constants.FIRST_TIME, "");
            if (firstTime.equals("Yes")) {
                Intent intent = new Intent(MainMenuActivity.this, QuickSetPaymentActivity.class);
                intent.putExtra("LOCATION_RESPONSE", locationFilterJson);
                startActivity(intent);
            }
        });

        layoutQrCode.setOnClickListener(btnBurgerMenuView -> {
            String firstTime = sscPref.getString(Constants.FIRST_TIME, "");
            if (firstTime.equals("Yes")) {

                if (midQrLogin != null && midQrLogin.length() > 0) {
                    azulLogoLayout.setVisibility(View.GONE);
                    welcomeText.setVisibility(View.GONE);
                    azulLayout.setVisibility(View.GONE);
                    callCenter.setVisibility(View.GONE);
                    qrFormButton.setVisibility(View.GONE);

                    backBtnMenu.setVisibility(View.VISIBLE);
                    qrLayout.setVisibility(View.VISIBLE);
                    callGetQrApi(midQrLogin);
                } else {
                    azulLogoLayout.setVisibility(View.VISIBLE);
                    welcomeText.setVisibility(View.VISIBLE);
                    azulLayout.setVisibility(View.VISIBLE);
                    callCenter.setVisibility(View.VISIBLE);
                    qrFormButton.setVisibility(View.VISIBLE);

                    backBtnMenu.setVisibility(View.GONE);
                    qrLayout.setVisibility(View.GONE);
                    layoutQrCode.setRotation(submenuVisibility ? 180 : 0);
                    menuLocationFilter = new MenuLocationFilter(locationFilterJson, "Menu");
                    menuLocationFilter.show(getSupportFragmentManager(), Constants.BOTTOM_SHEET);
                }
            }
        });
    }

    public void setContent(String content, String locNameselected, String lastChildMidQr, int dismissFlag) {
        midQrLogin = lastChildMidQr;
        locName = locNameselected;
        Log.d("", "" + content);

        if (dismissFlag == 1) {
            azulLogoLayout.setVisibility(View.GONE);
            welcomeText.setVisibility(View.GONE);
            azulLayout.setVisibility(View.GONE);
            callCenter.setVisibility(View.GONE);
            qrFormButton.setVisibility(View.GONE);

            backBtnMenu.setVisibility(View.VISIBLE);
            qrLayout.setVisibility(View.VISIBLE);

            callGetQrApi(lastChildMidQr);
            menuLocationFilter.dismiss();
        }
    }

    private void callGetQrApi(String lastLocationMid) {
        Log.d("", "" + lastLocationMid);
        JSONObject json = new JSONObject();
        try {
            String tcpKey = ((AzulApplication) this.getApplication()).getTcpKey();
            json.put("tcp", RSAHelper.ecryptRSA(MainMenuActivity.this, tcpKey));
            JSONObject payload = new JSONObject();
            payload.put(Constants.DEVICE, DeviceUtils.getDeviceId(this));
            JSONObject pagosrequest = new JSONObject();
            pagosrequest.put("merchantId", "24000000007");

            payload.put("obtenerrequest", pagosrequest);
            json.put("payload", RSAHelper.encryptAES(payload.toString(), Base64.decode(tcpKey, 0)));
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.GET_QR, json);
    }

    String locName = "";
    TabLayout circleIndicatorTwo;

    public void getResponseForQr(String responseData) {
        try {
            qrSetView(responseData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ImageView imgViewQrCode;
    TextView qrTitle;
    TextView qrMerchantId;
    TextView qrTId;

    private void qrSetView(String svgResponse) {
        imgViewQrCode = findViewById(R.id.imgViewQrCode);
        qrTitle = findViewById(R.id.qrTitle);
        qrMerchantId = findViewById(R.id.qrMerchantId);
        qrTId = findViewById(R.id.qrTId);

        if (!TextUtils.isEmpty(locName)) {
            qrTitle.setText(locName);
        }
        qrMerchantId.setText("MID: " + midQrLogin);

        SVG svg = null;
        try {
            if (!TextUtils.isEmpty(svgResponse)) {
                svg = SVG.getFromString(svgResponse);
                PictureDrawable pd = new PictureDrawable(svg.renderToPicture());
                imgViewQrCode.setImageDrawable(pd);
            }

        } catch (
                SVGParseException e) {
            e.printStackTrace();
        }
    }

}
