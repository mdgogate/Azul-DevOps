package com.sdp.appazul.activities.registration;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.sdp.appazul.R;
import com.sdp.appazul.activities.home.BasicRegistrationActivity;
import com.sdp.appazul.activities.home.MainMenuActivity;
import com.sdp.appazul.activities.menuitems.WebActivity;
import com.sdp.appazul.api.ApiManager;
import com.sdp.appazul.api.ServiceUrls;
import com.sdp.appazul.globals.AppAlters;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.GlobalFunctions;
import com.sdp.appazul.globals.KeyConstants;
import com.sdp.appazul.security.RSAHelper;
import com.sdp.appazul.security.SecurePreferences;
import com.sdp.appazul.utils.DeviceUtils;
import com.sdp.appazul.utils.KeysUtils;
import com.sdp.appazul.utils.NetworkAddress;

import org.json.JSONObject;

import java.net.URI;
import java.security.KeyStore;

public class UserRegisterActivity extends BasicRegistrationActivity {

    String mdpk = "";
    EditText editUsername;
    EditText editPassword;
    RelativeLayout startSession;
    RelativeLayout mainQrButtonLayout;
    TextView forgotPasswordLabel;
    TextView register;
    ApiManager apiManager = new ApiManager(this);
    String activityName = "LoginOne";
    Context context;
    ImageView nextIcon;
    RelativeLayout user_name;
    RelativeLayout rel_Password;
    TextView user_error;
    TextView password_error;
    GlobalFunctions globals = new GlobalFunctions(this);
    int errorCount = 0;
    int maxTryCount = 3;
    SharedPreferences sscPref;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login);
        initControls();
        context = this;
        furtherAction();
        //validateBlockCount();
    }

    private void furtherAction() {
        sscPref = new SecurePreferences(this, globals.getPPRString(), globals.getSSCString());
        ((AzulApplication) this.getApplication()).setPrefs(sscPref);
    }

    private void initControls() {
        nextIcon = findViewById(R.id.nextIcon);
        register = findViewById(R.id.register);
        startSession = findViewById(R.id.startSession);
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.edit_password);
        mainQrButtonLayout = findViewById(R.id.mainQrButtonLayout);
        forgotPasswordLabel = findViewById(R.id.forgotPasswordLabel);
        user_name = findViewById(R.id.user_name);
        rel_Password = findViewById(R.id.rel_Password);
        user_error = findViewById(R.id.user_error);
        password_error = findViewById(R.id.password_error);
        new KeyInitDelegate().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        mainQrButtonLayout.setOnClickListener(mainQRView -> {
            String qrLink = new NetworkAddress().getSpecificUrl(0);
            String toolBarTitleText = getResources().getString(R.string.qr_bar);
            callWebActivity(activityName, qrLink, toolBarTitleText);
        });

        forgotPasswordLabel.setOnClickListener(v -> {
            String forgotPasswordLink = new NetworkAddress().getSpecificUrl(1);
            String toolBarTitleText = getResources().getString(R.string.forgot_bar);
            callWebActivity(activityName, forgotPasswordLink, toolBarTitleText);
        });

        register.setOnClickListener(v ->
                memberRegisteration());

        startSession.setOnClickListener(v -> {
            String username = editUsername.getText().toString().trim();
            String password = editPassword.getText().toString().trim();
            if (TextUtils.isEmpty(username)) {
                AppAlters.showAlert(UserRegisterActivity.this, getString(R.string.error_label),
                        getString(R.string.username_error), getString(R.string.ok_lable));
            } else if (TextUtils.isEmpty(password)) {
                AppAlters.showAlert(UserRegisterActivity.this, getString(R.string.error_label),
                        getString(R.string.pass_error), getString(R.string.ok_lable));
            } else {

                callLoginService(username, password);
            }

        });

        final Typeface typefaceBold = ResourcesCompat.getFont(getApplicationContext(), R.font.vag_bold);
        final Typeface typefaceLight = ResourcesCompat.getFont(getApplicationContext(), R.font.vag_light);


        editUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i(UserRegisterActivity.this.getLocalClassName(), "editUsername " + s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) editUsername.setTypeface(typefaceLight);
                else editUsername.setTypeface(typefaceBold);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i(UserRegisterActivity.this.getLocalClassName(), "editUsername afterTextChanged");
            }
        });

        editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i(UserRegisterActivity.this.getLocalClassName(), "editPassword " + s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) editPassword.setTypeface(typefaceLight);
                else editPassword.setTypeface(typefaceBold);

                if (!TextUtils.isEmpty(editUsername.getText().toString())
                        && !TextUtils.isEmpty(editPassword.getText().toString())) {
                    startSession.setBackgroundResource(R.drawable.button_background);
                    nextIcon.setVisibility(View.VISIBLE);
                } else {
                    nextIcon.setVisibility(View.GONE);
                    startSession.setBackgroundResource(R.drawable.button_disabled_background);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i(UserRegisterActivity.this.getLocalClassName(), "editUsername afterTextChanged");
            }
        });

    }

    private void memberRegisteration() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserRegisterActivity.this);
        builder.setTitle(getResources().getString(R.string.register_bar))
                .setMessage(getString(R.string.alert)).setPositiveButton("Si", (dialog, which) -> {
            Intent intent = new Intent(getApplicationContext(), WebActivity.class);
            intent.putExtra("backActivity", "LoginOne");
            intent.putExtra("links", new NetworkAddress().getSpecificUrl(2));
            intent.putExtra("toolbarTitleText", getResources().getString(R.string.register_bar));
            startActivity(intent);
            this.overridePendingTransition(R.anim.animation_enter,
                    R.anim.slide_nothing);
        }).setNegativeButton("No", null);
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void callLoginService(String username, String password) {
        JSONObject object = new JSONObject();
        try {
            object.put("username", RSAHelper.ecryptRSA(this, username));
            object.put("password", RSAHelper.ecryptRSA(this, password));
            object.put("device", RSAHelper.ecryptRSA(this, DeviceUtils.getDeviceId(this)));
            object.put("app_version", RSAHelper.ecryptRSA(this, DeviceUtils.getAppVersion(this)));
            object.put("os_type", RSAHelper.ecryptRSA(this, Constants.ANDROID_LABEL));
            object.put("mdpk", mdpk);
            Log.d("TAG", "callLoginService: " + DeviceUtils.getDeviceId(this));
            Log.d("TAG", "Register User Req : " + object.toString());
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.REGISTER_USER, object);
    }

    @SuppressLint("DefaultLocale")
    public void processRegisterServiceResponse(String jsonRegister) {
        try {
            sharedPreferences = ((AzulApplication) this.getApplication()).getPrefs();
            JSONObject jsonObject = new JSONObject(jsonRegister);
            String status = jsonObject.has("UserStatus") ? jsonObject.getString("UserStatus") : "";
            String PasswordChange = jsonObject.has("PasswordChange") ? jsonObject.getString("PasswordChange") : "false";
            if (jsonObject.has("raccess-token")) {
                ((AzulApplication) ((Activity) context).getApplication()).setrAccToken(jsonObject.getString("raccess-token"));
            }
            if (jsonObject.has(KeyConstants.STATUS_KEY) && jsonObject.getString(KeyConstants.STATUS_KEY).equals(KeyConstants.FAIL_KEY)) {
                clearTextboxes();
            } else if (jsonRegister.contains("1010") || status.isEmpty() || status.equalsIgnoreCase("")) {
                checkUserDetails(jsonObject);
            } else {
                statusOfUser(status, PasswordChange, jsonObject);
            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void checkUserDetails(JSONObject jsonObject) {
        try {

            String userExist = jsonObject.getString("Exists");
            String userBlock = jsonObject.getString("IsBlocked");
            int triedCount = jsonObject.getInt("RetryCount");
            if (userExist.equalsIgnoreCase(Constants.BOOLEAN_FALSE)) {
                errorAlert(UserRegisterActivity.this, getString(R.string.login_incorrect_credentials_title), getString(R.string.login_incorrect_credentials), 0);
            } else {
                if (userBlock.equalsIgnoreCase(Constants.BOOLEAN_TRUE) && triedCount >= 3) {
                    errorAlert(UserRegisterActivity.this, getString(R.string.login_user_block_title), getString(R.string.login_user_block), 1);
                } else if (userBlock.equalsIgnoreCase(Constants.BOOLEAN_TRUE) && triedCount == 0) {
                    showDialog(UserRegisterActivity.this, getString(R.string.Blocked_title), getString(R.string.Blocked), Constants.BLOCK_STATUS, getString(R.string.Cancel), getString(R.string.continue_lable));
                } else if (userBlock.equalsIgnoreCase(Constants.BOOLEAN_FALSE) && triedCount < 3) {
                    errorAlert(UserRegisterActivity.this, getString(R.string.login_incorrect_credentials_title), getString(R.string.login_incorrect_credentials), 0);
                }
            }

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void counterLogic(int counterValue) {
        if (counterValue < maxTryCount) {
            errorAlert(UserRegisterActivity.this, getString(R.string.login_incorrect_credentials_title), getString(R.string.login_incorrect_credentials), 0);
        } else if (counterValue >= maxTryCount) {
            errorCount = 0;
            sharedPreferences.edit().putInt(Constants.LOGIN_COUNTER, errorCount).apply();
            errorAlert(UserRegisterActivity.this, getString(R.string.login_user_block_title), getString(R.string.login_user_block), 1);
        }
    }

    public void errorAlert(Context activity, String a, String b, int isPinBlock) {
        try {
            user_name.setBackgroundResource(R.drawable.login_with_red_border);
            rel_Password.setBackgroundResource(R.drawable.login_with_red_border);
            user_error.setVisibility(View.VISIBLE);
            password_error.setVisibility(View.VISIBLE);

            Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setContentView(R.layout.custom_login_alert_dialog);
            TextView textTitle = dialog.findViewById(R.id.textTitle);
            TextView textMsg = dialog.findViewById(R.id.textMsg);
            TextView tvTryCount = dialog.findViewById(R.id.tvTryCount);
            tvTryCount.setVisibility(View.GONE);

            Typeface typeface;
            typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Medium.ttf");

            tvTryCount.setTypeface(typeface);
            textMsg.setTypeface(typeface);
            textMsg.setText(b);
            textTitle.setTypeface(typeface);
            textTitle.setText(a);

            TextView btnContinue = dialog.findViewById(R.id.btnContinue);
            btnContinue.setTypeface(typeface);

            btnContinue.setOnClickListener(view -> {
                if (isPinBlock == 1) {
                    String link_forgetPassword = "https://portal.azul.com.do/Login/ForgotPassword";
                    Uri uri = Uri.parse(link_forgetPassword);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }

                clearTextboxes();
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    user_name.setBackgroundResource(R.drawable.bd_edit_text);
                    rel_Password.setBackgroundResource(R.drawable.bd_edit_text);
                    user_error.setVisibility(View.INVISIBLE);
                    password_error.setVisibility(View.INVISIBLE);
                }
            });

            dialog.show();

        } catch (
                Exception e) {
            Log.e("Exception : ", Log.getStackTraceString(e));
        }
    }

    private void clearTextboxes() {
        editPassword.setText("");
        editUsername.setText("");
    }

    public void hideKeyBoard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @SuppressLint("StaticFieldLeak")
    private class KeyInitDelegate extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                KeyStore mKeyStore = KeyStore.getInstance("AndroidKeyStore");
                mKeyStore.load(null);
                mdpk = KeysUtils.getEMSgData(UserRegisterActivity.this);
            } catch (Exception e) {
                Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.d("--", "---Done");
        }
    }


    public void callProcessRegistration() {
        errorCount = 0;
        sharedPreferences.edit().putInt(Constants.LOGIN_COUNTER, errorCount).apply();
        startActivity(new Intent(UserRegisterActivity.this, PinSetActivity.class));
        this.overridePendingTransition(R.anim.animation_enter,
                R.anim.slide_nothing);
    }

    private void callWebActivity(String activityName, String webLink, String toolBarTitleText) {
        Intent intent = new Intent(UserRegisterActivity.this, WebActivity.class);
        intent.putExtra("backActivity", activityName);
        intent.putExtra("links", webLink);
        intent.putExtra("toolbarTitleText", toolBarTitleText);
        startActivity(intent);
    }

    private void statusOfUser(String status, String PasswordChange, JSONObject jsonObject) {
        if (status.equals("Active") && PasswordChange.equalsIgnoreCase("True")) {
            showDialog(UserRegisterActivity.this, getString(R.string.active_temporal_password_title), getString(R.string.active_temporal_password), "active_temporal_password", getString(R.string.Cancel), getString(R.string.continue_lable));
        } else if (status.equalsIgnoreCase("New")) {
            showDialog(UserRegisterActivity.this, getResources().getString(R.string.Status_Nuevo_title), getResources().getString(R.string.Status_Nuevo), "Status_Nuevo", getString(R.string.Cancel), getString(R.string.continue_lable));
        } else {
            statusMethod1(status);

        }
    }

    private void statusMethod1(String status) {
        if (status.equals("Active")) {
            callProcessRegistration();
        } else if (status.equalsIgnoreCase("NewExpired")) {
            showDialog(UserRegisterActivity.this, getString(R.string.New_Expired_title), getString(R.string.New_Expired), "New_Expired", getString(R.string.no), getString(R.string.yes));
        } else {
            statusMethod2(status);
        }
    }

    private void statusMethod2(String status) {
        if (status.equalsIgnoreCase("ActiveExpired")) {
            showDialog(UserRegisterActivity.this, getString(R.string.Active_expired_title), getString(R.string.Active_expired), "Active_expired", getString(R.string.Cancel), getString(R.string.continue_lable));
        }  else {
            statusMethod3(status);
        }
    }

    private void statusMethod3(String status) {
        if (status.equalsIgnoreCase(Constants.DISABLED_STATUS)) {
            showDialog(UserRegisterActivity.this, getString(R.string.Disabled_title), getString(R.string.Disabled), Constants.DISABLED_STATUS, getString(R.string.no), getString(R.string.yes));

        } else if (status.equalsIgnoreCase("UnderInvestigation")) {
            showDialog(UserRegisterActivity.this, getString(R.string.Under_investigation_title), getString(R.string.Under_investigation), "Under_investigation", getString(R.string.no), getString(R.string.yes));

        } else {
            showDialog(UserRegisterActivity.this, status, status, "", getString(R.string.Cancel), getString(R.string.continue_lable));

        }
    }

    private void statusPositiveButton(String status_name) {
        String link_login = "https://portal.azul.com.do/Login";
        String link_forgetPassword = "https://portal.azul.com.do/Login/ForgotPassword";
        Uri uri;
        if ("Status_Nuevo".equals(status_name) || "active_temporal_password".equals(status_name)) {
            uri = Uri.parse(link_login);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if ("New_Expired".equals(status_name) || "Active_expired".equals(status_name) || Constants.BLOCK_STATUS.equals(status_name)) {
            uri = Uri.parse(link_forgetPassword);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (Constants.DISABLED_STATUS.equals(status_name) || "Under_investigation".equals(status_name)) {

            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:809 544 2985"));
            startActivity(intent);
        }
    }

    public void showDialog(Context activity, String status_title, String status, String status_name, String b2, String b1) {
        try {
            user_name.setBackgroundResource(R.drawable.login_with_red_border);
            rel_Password.setBackgroundResource(R.drawable.login_with_red_border);

            Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setContentView(R.layout.custom_alert_dialog);
            TextView textTitle = dialog.findViewById(R.id.textTitle);
            TextView textMsg = dialog.findViewById(R.id.textMsg);

            Typeface typeface;
            typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Medium.ttf");

            textMsg.setTypeface(typeface);
            textMsg.setText(status);
            textTitle.setTypeface(typeface);
            textTitle.setText(status_title);

            TextView btnContinue = dialog.findViewById(R.id.btnContinue);
            btnContinue.setTypeface(typeface);
            btnContinue.setText(b1);
            TextView btnCancel = dialog.findViewById(R.id.btnCancel);
            btnCancel.setTypeface(typeface);
            btnCancel.setText(b2);

            btnContinue.setOnClickListener(view -> {
                clearTextboxes();
                statusPositiveButton(status_name);
                dialog.dismiss();
                user_name.setBackgroundResource(R.drawable.bd_edit_text);
                rel_Password.setBackgroundResource(R.drawable.bd_edit_text);
            });
            btnCancel.setOnClickListener(v -> {
                clearTextboxes();
                dialog.dismiss();
                user_name.setBackgroundResource(R.drawable.bd_edit_text);
                rel_Password.setBackgroundResource(R.drawable.bd_edit_text);
            });
            dialog.show();
        } catch (
                Exception e) {
            Log.e("Exception : ", Log.getStackTraceString(e));
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}