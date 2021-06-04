package com.azul.app.android.activities.registration;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.azul.app.android.R;
import com.azul.app.android.activities.home.BasicRegistrationActivity;
import com.azul.app.android.activities.home.MainMenuActivity;
import com.azul.app.android.activities.menuitems.WebActivity;
import com.azul.app.android.api.ApiManager;
import com.azul.app.android.api.ServiceUrls;
import com.azul.app.android.globals.AppAlters;
import com.azul.app.android.globals.Constants;
import com.azul.app.android.globals.KeyConstants;
import com.azul.app.android.security.RSAHelper;
import com.azul.app.android.utils.DeviceUtils;
import com.azul.app.android.utils.KeysUtils;
import com.azul.app.android.utils.NetworkAddress;

import org.json.JSONObject;

import java.security.KeyStore;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class UserRegisterActivity extends BasicRegistrationActivity {

    String mdpk = "";
    EditText editUsername;
    EditText editPassword;
    Button startSession;
    RelativeLayout mainQrButtonLayout;
    TextView forgotPasswordLabel;
    TextView register;
    ApiManager apiManager = new ApiManager(this);
    String activityName = "LoginOne";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login);
        initControls();
        login();

    }

    private void initControls() {
        register = findViewById(R.id.register);
        startSession = findViewById(R.id.startSession);
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.edit_password);
        mainQrButtonLayout = findViewById(R.id.mainQrButtonLayout);
        forgotPasswordLabel = findViewById(R.id.forgotPasswordLabel);
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
        }).setNegativeButton("No", null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void login() {
        startSession.setTransformationMethod(null);
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

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.REGISTER_USER, object);
    }

    @SuppressLint("DefaultLocale")
    public void processRegisterServiceResponse(String jsonRegister) {
        try {
            JSONObject jsonObject = new JSONObject(jsonRegister);
            if (jsonObject.has(KeyConstants.STATUS_KEY) && jsonObject.getString(KeyConstants.STATUS_KEY).equals(KeyConstants.FAIL_KEY)) {
                clearTextboxes();
            } else {
                callProcessRegistration();
            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
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

        startActivity(new Intent(UserRegisterActivity.this, PinSetActivity.class));
    }


    private void callPreviousActivity() {
        startActivity(new Intent(this, MainMenuActivity.class));
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            callPreviousActivity();
        }
        return true;
    }

    private void callWebActivity(String activityName, String webLink, String toolBarTitleText) {
        Intent intent = new Intent(UserRegisterActivity.this, WebActivity.class);
        intent.putExtra("backActivity", activityName);
        intent.putExtra("links", webLink);
        intent.putExtra("toolbarTitleText", toolBarTitleText);
        startActivity(intent);
    }
}