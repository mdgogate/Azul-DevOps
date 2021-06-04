package com.azul.app.android.activities.registration;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;

import com.azul.app.android.R;
import com.azul.app.android.activities.dashboard.DashBoardActivity;
import com.azul.app.android.activities.home.BasicRegistrationActivity;
import com.azul.app.android.api.ApiManager;
import com.azul.app.android.api.ServiceUrls;
import com.azul.app.android.globals.AzulApplication;
import com.azul.app.android.globals.Constants;
import com.azul.app.android.globals.GlobalFunctions;
import com.azul.app.android.globals.KeyConstants;
import com.azul.app.android.security.RSAHelper;
import com.azul.app.android.utils.DeviceUtils;

import org.json.JSONException;
import org.json.JSONObject;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;


public class PinSetActivity extends BasicRegistrationActivity {

    private static final String LOG_TAG = "AZUL";
    private EditText editTextOne;
    private EditText editTextTwo;
    private EditText editTextThree;
    private EditText editTextFour;
    private EditText editTextOne1;
    private EditText editTextTwo1;
    private EditText editTextThree1;
    private EditText editTextFour1;
    private View viewOne;
    private View viewTwo;
    private View viewThree;
    private View viewFour;
    private View viewOne1;
    private View viewTwo1;
    private View viewThree1;
    private View viewFour1;
    Button mButton1;
    Button mButton2;
    Button mButton3;
    Button mButton4;
    Button mButton5;
    Button mButton6;
    Button mButton7;
    Button mButton8;
    Button mButton9;
    Button mButton0;
    RelativeLayout mButtonBack;
    ImageView back;
    ApiManager apiManager = new ApiManager(this);
    String enteredPin = "";
    GlobalFunctions globals = new GlobalFunctions(this);

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pin_access);
        editTextFour1 = findViewById(R.id.edit_text_four1);
        editTextThree1 = findViewById(R.id.edit_text_three1);
        editTextTwo1 = findViewById(R.id.edit_text_two1);
        editTextOne1 = findViewById(R.id.edit_text_one1);
        editTextOne = findViewById(R.id.edit_text_one);
        editTextTwo = findViewById(R.id.edit_text_two);
        editTextThree = findViewById(R.id.edit_text_three);
        editTextFour = findViewById(R.id.edit_text_four);
        viewFour1 = findViewById(R.id.view_four1);
        viewThree1 = findViewById(R.id.view_three1);
        viewTwo1 = findViewById(R.id.view_two1);
        viewOne1 = findViewById(R.id.view_one1);
        viewFour = findViewById(R.id.view_four);
        viewThree = findViewById(R.id.view_three);
        viewTwo = findViewById(R.id.view_two);
        viewOne = findViewById(R.id.view_one);

        mButton1 = findViewById(R.id.button_1);
        mButton2 = findViewById(R.id.button_2);
        mButton3 = findViewById(R.id.secondLoginButton);
        mButton4 = findViewById(R.id.button_4);
        mButton5 = findViewById(R.id.button_5);
        mButton6 = findViewById(R.id.button_6);
        mButton7 = findViewById(R.id.button_7);
        mButton8 = findViewById(R.id.button_8);
        mButton9 = findViewById(R.id.button_9);
        mButton0 = findViewById(R.id.button_0);
        mButtonBack = findViewById(R.id.approve_button);

        onCLickListeners();
        backToLogin();
    }

    private void backToLogin() {
        back = findViewById(R.id.backButton);
        back.setOnClickListener(v -> {
            Intent intent = new Intent(PinSetActivity.this, UserRegisterActivity.class);
            startActivity(intent);
        });
    }


    private void clearAll() {
        editTextFour1.setText("");
        viewFour1.setVisibility(View.INVISIBLE);
        editTextThree1.setText("");
        viewThree1.setVisibility(View.INVISIBLE);
        editTextTwo1.setText("");
        viewTwo1.setVisibility(View.INVISIBLE);
        editTextOne1.setText("");
        viewOne1.setVisibility(View.INVISIBLE);
        editTextFour.setText("");
        viewFour.setVisibility(View.INVISIBLE);
        editTextThree.setText("");
        viewThree.setVisibility(View.INVISIBLE);
        editTextTwo.setText("");
        viewTwo.setVisibility(View.INVISIBLE);
        editTextOne.setText("");
        viewOne.setVisibility(View.INVISIBLE);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void onCLickListeners() {
        mButton1.setOnClickListener(v1 -> enterNumber("1"));
        mButton2.setOnClickListener(v1 -> enterNumber("2"));
        mButton3.setOnClickListener(v1 -> enterNumber("3"));
        mButton4.setOnClickListener(v1 -> enterNumber("4"));
        mButton5.setOnClickListener(v1 -> enterNumber("5"));
        mButton6.setOnClickListener(v1 -> enterNumber("6"));
        mButton7.setOnClickListener(v1 -> enterNumber("7"));
        mButton8.setOnClickListener(v1 -> enterNumber("8"));
        mButton9.setOnClickListener(v1 -> enterNumber("9"));
        mButton0.setOnClickListener(v1 -> enterNumber("0"));


        mButtonBack.setOnClickListener(v1 -> {
            if (!editTextFour1.getText().toString().trim().isEmpty()) {
                editTextFour1.setText("");
                viewFour1.setVisibility(View.INVISIBLE);

            } else if (!editTextThree1.getText().toString().trim().isEmpty()) {
                editTextThree1.setText("");
                viewThree1.setVisibility(View.INVISIBLE);

            } else if (!editTextTwo1.getText().toString().trim().isEmpty()) {
                editTextTwo1.setText("");
                viewTwo1.setVisibility(View.INVISIBLE);

            } else if (!editTextOne1.getText().toString().trim().isEmpty()) {
                editTextOne1.setText("");
                viewOne1.setVisibility(View.INVISIBLE);
            } else if (!editTextFour.getText().toString().trim().isEmpty()) {
                editTextFour.setText("");
                viewFour.setVisibility(View.INVISIBLE);

            } else if (!editTextThree.getText().toString().trim().isEmpty()) {
                editTextThree.setText("");
                viewThree.setVisibility(View.INVISIBLE);

            } else if (!editTextTwo.getText().toString().trim().isEmpty()) {
                editTextTwo.setText("");
                viewTwo.setVisibility(View.INVISIBLE);

            } else if (!editTextOne.getText().toString().trim().isEmpty()) {
                editTextOne.setText("");
                viewOne.setVisibility(View.INVISIBLE);
            }


        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void enterNumber(String s) {
        Log.d(LOG_TAG, "value=" + s);
        try {
            checkEditTextAndSendToPresenter(s);
        } catch (Exception e) {
            Log.i("TAG", "exception: " + e);

        }

    }

    private void checkEditTextAndSendToPresenter(String value) {
        if (editTextOne.getText().toString().trim().isEmpty()) {
            editTextOne.requestFocus();
            editTextOne.setText(value);
            editTextTwo.requestFocus();
            viewOne.setVisibility(View.VISIBLE);
        } else if (editTextTwo.getText().toString().trim().isEmpty()) {
            editTextTwo.setText(value);
            editTextThree.requestFocus();
            viewTwo.setVisibility(View.VISIBLE);
        } else if (editTextThree.getText().toString().trim().isEmpty()) {
            editTextThree.setText(value);
            editTextFour.requestFocus();
            viewThree.setVisibility(View.VISIBLE);
        } else moreToDo(value);
    }

    private void moreToDo(String value) {
        if (editTextFour.getText().toString().trim().isEmpty()) {
            editTextFour.setText(value);
            editTextOne1.requestFocus();
            viewFour.setVisibility(View.VISIBLE);
        } else moreToDo1(value);
    }

    private void moreToDo1(String value) {
        if (editTextOne1.getText().toString().trim().isEmpty()) {
            editTextOne1.setText(value);
            editTextTwo1.requestFocus();
            viewOne1.setVisibility(View.VISIBLE);
        } else if (editTextTwo1.getText().toString().trim().isEmpty()) {
            editTextTwo1.setText(value);
            editTextThree1.requestFocus();
            viewTwo1.setVisibility(View.VISIBLE);
        } else if (editTextThree1.getText().toString().trim().isEmpty()) {
            editTextThree1.setText(value);
            editTextFour1.requestFocus();
            viewThree1.setVisibility(View.VISIBLE);
        } else moreToDo2(value);
    }

    private void moreToDo2(String value) {
        if (editTextFour1.getText().toString().trim().isEmpty()) {
            editTextFour1.setText(value);
            viewFour1.setVisibility(View.VISIBLE);
            compareFields();
        }
    }

    @SuppressLint("ShowToast")
    private void compareFields() {
        enteredPin =
                editTextOne.getText().toString().trim() +
                        editTextTwo.getText().toString().trim() +
                        editTextThree.getText().toString().trim() +
                        editTextFour.getText().toString().trim();
        String pin1 =
                editTextOne1.getText().toString().trim() +
                        editTextTwo1.getText().toString().trim() +
                        editTextThree1.getText().toString().trim() +
                        editTextFour1.getText().toString().trim();
        Log.i(LOG_TAG, "compareFields: " + enteredPin + " " + pin1);


        if (enteredPin.equals(pin1)) {
            callPinRegisterService(enteredPin);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(PinSetActivity.this);

            builder.setTitle(getResources().getString(R.string.alert_title))
                    .setMessage(getResources().getString(R.string.alert_message))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.alert_continue), (dialog, which) -> clearAll());

            AlertDialog alert = builder.create();
            alert.show();

        }
    }


    @SuppressLint("DefaultLocale")
    public void processPinRegisterServiceResponse(String jsonRegister) {
        try {
            JSONObject jsonObject = new JSONObject(jsonRegister);
            if (jsonObject.has(KeyConstants.STATUS_KEY) &&
                    jsonObject.getString(KeyConstants.STATUS_KEY).equals(KeyConstants.SUCCESS_KEY)) {


                callLoginServiceInternally();
            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    String sigKey = "";

    private void callLoginServiceInternally() {
        if (!TextUtils.isEmpty(enteredPin)) {
            JSONObject object = new JSONObject();
            try {

                object.put("pin", RSAHelper.ecryptRSA(this, enteredPin));
                object.put("device", RSAHelper.ecryptRSA(this, DeviceUtils.getDeviceId(this)));

                Signature pinSignature = Signature.getInstance("SHA1withRSA");
                KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
                keyStore.load(null);
                PrivateKey privateKey = (PrivateKey) keyStore.getKey(Constants.EM_SG_DATA, null);
                pinSignature.initSign(privateKey);
                byte[] byteData = enteredPin.getBytes();
                pinSignature.update(byteData);
                byte[] sigPinBytes = pinSignature.sign();
                object.put("sig", Base64.encodeToString(sigPinBytes, 0));

                sigKey = Base64.encodeToString(sigPinBytes, 0);

            } catch (Exception e) {
                Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
            }
            apiManager.callAPI(ServiceUrls.LOGIN, object);
        }
    }

    @SuppressLint("DefaultLocale")
    public void processLoginServiceResponse(String jsonRegister) {
        try {
            JSONObject jsonObject = new JSONObject(jsonRegister);
            Log.d("PINACVITY", " LOGIN RESPONSE ::: " + jsonRegister);

            if (jsonObject.has(KeyConstants.ACCESS_TOKEN_LABEL)) {
                callProcessRegistration(jsonObject);
            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void callProcessRegistration(JSONObject jsonObject) {

        SharedPreferences sharedPreferences = ((AzulApplication) this.getApplication()).getPrefs();
        sharedPreferences.edit().putString(Constants.FIRST_TIME, "Yes").apply();
        try {
            if (jsonObject.has(Constants.GROUP_NAME)) {
                sharedPreferences.edit().putString(Constants.GROUP_NAME, jsonObject.getString(Constants.GROUP_NAME)).apply();
            }
            if (jsonObject.has(Constants.FIRST_NAME)) {
                sharedPreferences.edit().putString(Constants.FIRST_NAME, jsonObject.getString(Constants.FIRST_NAME)).apply();
            }
            if (jsonObject.has(Constants.LAST_NAME)) {
                sharedPreferences.edit().putString(Constants.LAST_NAME, jsonObject.getString(Constants.LAST_NAME)).apply();
            }
            if (jsonObject.has(Constants.USER_NAME)) {
                sharedPreferences.edit().putString(Constants.USER_NAME, jsonObject.getString(Constants.USER_NAME)).apply();
            }
            sharedPreferences.edit().putString("SIG_LABEL", sigKey).apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(PinSetActivity.this, DashBoardActivity.class);
        intent.putExtra("LOC", jsonObject.toString());
        startActivity(intent);
        clearAll();
    }


    private void callPinRegisterService(String pin) {
        JSONObject object = new JSONObject();
        try {
            object.put("pin", RSAHelper.ecryptRSA(this, pin));
            object.put("device", RSAHelper.ecryptRSA(this, DeviceUtils.getDeviceId(this)));
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.REGISTER_PIN, object);
    }
}