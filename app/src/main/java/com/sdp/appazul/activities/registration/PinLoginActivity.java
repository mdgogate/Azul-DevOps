package com.sdp.appazul.activities.registration;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.sdp.appazul.R;
import com.sdp.appazul.activities.dashboard.DashBoardActivity;
import com.sdp.appazul.activities.home.BasicRegistrationActivity;
import com.sdp.appazul.activities.home.MainMenuActivity;
import com.sdp.appazul.api.ApiManager;
import com.sdp.appazul.api.ServiceUrls;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.GlobalFunctions;
import com.sdp.appazul.globals.KeyConstants;
import com.sdp.appazul.security.RSAHelper;
import com.sdp.appazul.security.SecurePreferences;
import com.sdp.appazul.utils.DeviceUtils;

import org.json.JSONObject;


public class PinLoginActivity extends BasicRegistrationActivity {

    private EditText editTextOne;
    private EditText editTextTwo;
    private EditText editTextThree;
    private EditText editTextFour;
    private View viewOne;
    private View viewTwo;
    private View viewThree;
    private View viewFour;
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
    TextView backButton;
    ApiManager apiManager = new ApiManager(this);
    String sigKey = "";
    SharedPreferences sscPref;
    GlobalFunctions globals = new GlobalFunctions(this);

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_confirm_pin);
        editTextOne = findViewById(R.id.edit_text_one);
        editTextTwo = findViewById(R.id.edit_text_two);
        editTextThree = findViewById(R.id.edit_text_three);
        editTextFour = findViewById(R.id.edit_text_four);
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
        backToSecondLogin();
        furtherAction();
        sigKey = sscPref.getString("SIG_LABEL", "");
    }

    private void furtherAction() {
        sscPref = new SecurePreferences(this, globals.getPPRString(), globals.getSSCString());
        ((AzulApplication) this.getApplication()).setPrefs(sscPref);
    }

    private void backToSecondLogin() {
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(PinLoginActivity.this, MainMenuActivity.class);
            startActivity(intent);
        });
    }


    private void clearAll() {
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
            if (!editTextFour.getText().toString().trim().isEmpty()) {
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
            viewFour.setVisibility(View.VISIBLE);
            compareFields();
        }
    }

    @SuppressLint("ShowToast")
    private void compareFields() {
        String pin =
                editTextOne.getText().toString().trim() +
                        editTextTwo.getText().toString().trim() +
                        editTextThree.getText().toString().trim() +
                        editTextFour.getText().toString().trim();

        if (pin.length() == 4) {
            callLoginService(pin);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(PinLoginActivity.this);
            builder.setTitle(getResources().getString(R.string.alert_title))
                    .setMessage(getResources().getString(R.string.alert_message))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.alert_continue), (dialog, which) -> clearAll());

            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    private void callLoginService(String pin) {
        JSONObject object = new JSONObject();
        JSONObject objectWithoutEnc = new JSONObject();
        try {
            object.put("pin", RSAHelper.ecryptRSA(this, pin));
            object.put("device", RSAHelper.ecryptRSA(this, DeviceUtils.getDeviceId(this)));
            object.put("sig", sigKey);


            objectWithoutEnc.put("pin", pin);
            objectWithoutEnc.put("device", DeviceUtils.getDeviceId(this));
            objectWithoutEnc.put("sig", sigKey);

            Log.d("Request UnEnc Object : ", "Request UnEnc Object : " + objectWithoutEnc.toString());

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.LOGIN, object);
    }


    @SuppressLint("DefaultLocale")
    public void processLoginServiceResponse(String jsonRegister) {
        try {

            JSONObject jsonObject = new JSONObject(jsonRegister);
            if (jsonObject.has(KeyConstants.ACCESS_TOKEN_LABEL) && !TextUtils.isEmpty(jsonObject.getString(KeyConstants.ACCESS_TOKEN_LABEL))) {

                proceedtoDashboardScreen(jsonObject);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(PinLoginActivity.this);

                builder.setTitle(getResources().getString(R.string.alert_title))
                        .setMessage(getResources().getString(R.string.alert_message)).setPositiveButton(getResources().getString(R.string.alert_continue), (dialog, which) -> clearAll());

                AlertDialog alert = builder.create();
                alert.show();
            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    public void proceedtoDashboardScreen(JSONObject jsonObject) {
        Intent intent = new Intent(PinLoginActivity.this, DashBoardActivity.class);
        intent.putExtra("LOC", jsonObject.toString());
        startActivity(intent);
        clearAll();
    }
}