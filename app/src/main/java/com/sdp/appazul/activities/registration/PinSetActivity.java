package com.sdp.appazul.activities.registration;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.scottyab.rootbeer.Const;
import com.sdp.appazul.R;
import com.sdp.appazul.activities.dashboard.DashBoardActivity;
import com.sdp.appazul.activities.home.BasicRegistrationActivity;
import com.sdp.appazul.activities.notifications.PushNotificationSettings;
import com.sdp.appazul.api.ApiManager;
import com.sdp.appazul.api.ServiceUrls;
import com.sdp.appazul.classes.LoginData;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.KeyConstants;
import com.sdp.appazul.security.RSAHelper;
import com.sdp.appazul.utils.DeviceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


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
    Dialog customLoginAlertDialog;
    private HashMap<String, ArrayList<String>> namesOfList = null;
    private LoginData loginData;
    String pushToken;
    String status;

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
        mButton3 = findViewById(R.id.button_3);
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
            this.overridePendingTransition(R.anim.animation_leave,
                    R.anim.slide_nothing);
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


        if (enteredPin.equals(pin1)) {
            if (Boolean.TRUE.equals(isSamePinValidation())) {
                errorAlert(PinSetActivity.this, 1);
            } else if (isInOrder(Integer.parseInt(pin1))) {
                errorAlert(PinSetActivity.this, 0);
            } else {
                callPinRegisterService(enteredPin);
            }
        } else {
            errorAlert(PinSetActivity.this, 3);
        }
    }


    private boolean isSamePinValidation() {


        return editTextOne1.getText().toString().equalsIgnoreCase(editTextTwo1.getText().toString())
                && editTextOne1.getText().toString().equalsIgnoreCase(editTextThree1.getText().toString())
                && editTextOne1.getText().toString().equalsIgnoreCase(editTextFour1.getText().toString());

    }

    private boolean isInOrder(int number) {
        int digit = -1;
        while (number > 0) {
            if (digit != -1 && digit != (number % 10) + 1)
                return false;
            digit = number % 10;
            number /= 10;
        }
        return true;
    }


    public void errorAlert(Context activity, int errorType) {
        try {

            customLoginAlertDialog = new Dialog(activity);
            customLoginAlertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            customLoginAlertDialog.setCancelable(false);
            customLoginAlertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            customLoginAlertDialog.setContentView(R.layout.custom_login_alert_dialog);
            TextView textTitle = customLoginAlertDialog.findViewById(R.id.textTitle);
            TextView textMsg = customLoginAlertDialog.findViewById(R.id.textMsg);
            TextView tvTryCount = customLoginAlertDialog.findViewById(R.id.tvTryCount);

            Typeface typeface;
            typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Medium.ttf");

            tvTryCount.setTypeface(typeface);
            textMsg.setTypeface(typeface);
            textTitle.setTypeface(typeface);


            if (errorType == 1) {
                tvTryCount.setVisibility(View.INVISIBLE);
                textTitle.setText(getResources().getString(R.string.four_digits_same_error_title));
                textMsg.setText(getResources().getString(R.string.four_digits_same_error_msg));
            } else if (errorType == 3) {
                tvTryCount.setVisibility(View.INVISIBLE);
                textTitle.setText(getResources().getString(R.string.alert_title));
                textMsg.setText(getResources().getString(R.string.alert_message));
            } else {
                tvTryCount.setVisibility(View.INVISIBLE);
                textTitle.setText(getResources().getString(R.string.four_digits_same_error_title));
                textMsg.setText(getResources().getString(R.string.sequential_pin));
            }


            TextView btnContinue = customLoginAlertDialog.findViewById(R.id.btnContinue);
            btnContinue.setTypeface(typeface);

            btnContinue.setOnClickListener(view -> {


                clearAll();
                if (customLoginAlertDialog != null && customLoginAlertDialog.isShowing()) {
                    customLoginAlertDialog.dismiss();
                }
            });

            customLoginAlertDialog.show();

        } catch (
                Exception e) {
            Log.e("Exception : ", Log.getStackTraceString(e));
        }
    }

    @SuppressLint("DefaultLocale")
    public void processPinRegisterServiceResponse(String jsonRegister) {
        try {
            JSONObject jsonObject = new JSONObject(jsonRegister);
            if (jsonObject.has(KeyConstants.STATUS_KEY) &&
                    jsonObject.getString(KeyConstants.STATUS_KEY).equals(KeyConstants.SUCCESS_KEY)) {
                callLoginServiceInternally();
            } else {
                if (jsonObject.has(KeyConstants.STATUS_KEY) &&
                        jsonObject.getString(KeyConstants.STATUS_KEY).equals("failure") &&
                        jsonObject.getString("code").equalsIgnoreCase("2009")) {

                    validateSeconds(jsonObject);

                } else if (jsonObject.has(KeyConstants.STATUS_KEY) &&
                        jsonObject.getString(KeyConstants.STATUS_KEY).equals("failure") &&
                        jsonObject.getString("code").equalsIgnoreCase("2010")) {
                    deviceAlert(PinSetActivity.this, 1, 0);

                }
            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void validateSeconds(JSONObject jsonObject) {
        try {
            if (jsonObject.getString(Constants.SEC_LEFT).contains("-") || jsonObject.getString(Constants.SEC_LEFT).equalsIgnoreCase("0")) {
                callLoginServiceInternally();
            } else {
                int secondsLeft = Integer.parseInt(jsonObject.getString(Constants.SEC_LEFT));
                deviceAlert(PinSetActivity.this, 0, secondsLeft);
            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    public long calculateTime(long seconds) {
        return TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
    }

    Dialog deviceAlertDialog;

    public void deviceAlert(Context activity, int alertType, int timeInSeconds) {
        try {

            deviceAlertDialog = new Dialog(activity);
            deviceAlertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            deviceAlertDialog.setCancelable(false);
            deviceAlertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            deviceAlertDialog.setContentView(R.layout.custom_login_alert_dialog);
            TextView textTitle = deviceAlertDialog.findViewById(R.id.textTitle);
            TextView textMsg = deviceAlertDialog.findViewById(R.id.textMsg);
            TextView tvTryCount = deviceAlertDialog.findViewById(R.id.tvTryCount);

            Typeface typeface;
            typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Medium.ttf");

            tvTryCount.setTypeface(typeface);
            textMsg.setTypeface(typeface);
            textTitle.setTypeface(typeface);

            if (alertType == 0) {
                tvTryCount.setVisibility(View.VISIBLE);
                tvTryCount.setTextSize(11);
                textTitle.setText("Lo sentimos");
                textMsg.setText(getResources().getString(R.string.try_after_one_day));
                String tryCountTitle;


                int secondsLeft = timeInSeconds % 3600 % 60;
                int minutes = (int) Math.floor(timeInSeconds % 3600 / (double) 60);
                int hours = (int) Math.floor(timeInSeconds / (double) 3600);

                String HH = ((hours < 10) ? "0" : "") + hours;
                String MM = ((minutes < 10) ? "0" : "") + minutes;
                String SS = ((secondsLeft < 10) ? "0" : "") + secondsLeft;
                Log.d("TAG", "formatSeconds: " + HH + ":" + MM + ":" + SS);

                tryCountTitle = getResources().getString(R.string.try_after_label) + HH + "h :" + MM + "m:" + SS + "s";

                tvTryCount.setText(tryCountTitle);
            } else {
                tvTryCount.setVisibility(View.INVISIBLE);
                textTitle.setText(getResources().getString(R.string.error_label));
                textMsg.setText(getResources().getString(R.string.use_different_pin_message));
            }
            TextView btnContinue = deviceAlertDialog.findViewById(R.id.btnContinue);
            btnContinue.setTypeface(typeface);
            btnContinue.setText("Aceptar");

            btnContinue.setOnClickListener(view -> {

                clearAll();
                if (deviceAlertDialog != null && deviceAlertDialog.isShowing()) {
                    deviceAlertDialog.dismiss();
                }
            });

            deviceAlertDialog.show();

        } catch (
                Exception e) {
            Log.e("Exception : ", Log.getStackTraceString(e));
        }
    }


    String sigKey = "";

    private void callLoginServiceInternally() {
        if (!TextUtils.isEmpty(enteredPin)) {
            JSONObject object = new JSONObject();
            try {

                object.put("pin", RSAHelper.ecryptRSA(this, enteredPin));
                object.put(Constants.DEVICE, RSAHelper.ecryptRSA(this, DeviceUtils.getDeviceId(this)));

                Signature pinSignature = Signature.getInstance("SHA256withRSA");
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
//            apiManager.callAPI(ServiceUrls.LOGIN_TOKEN, object);
            apiManager.callAPI(ServiceUrls.LOGIN, object);
        }
    }

    @SuppressLint("DefaultLocale")
    public void processLoginServiceResponse(String jsonRegister) {
        try {
            JSONObject jsonObject = new JSONObject(jsonRegister);
            setLoginData(jsonObject);
            if (jsonObject.has(KeyConstants.ACCESS_TOKEN_LABEL)) {
                callProcessRegistration(jsonObject);
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    pushToken = task.getResult();
                    callApiPushToken(pushToken);
                });
            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void setLoginData(JSONObject jsonObject) {
        try {
            setOtherData(jsonObject);


            namesOfList = new HashMap<>();
            JSONArray jsonArray = jsonObject.has("AssignedUnits") ? jsonObject.getJSONArray("AssignedUnits") : new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonTrObject = jsonArray.getJSONObject(i);
                String nameAssignedUnits1 = "", idAssignedUnits1 = "";
                String nameAssignedUnits2 = "", idAssignedUnits2 = "";
                ArrayList<String> aa = new ArrayList<>();
                String nameAssignedUnits = jsonTrObject.has("Name") ? jsonTrObject.getString("Name") : "";

                parseAissignedLocationsArray(jsonTrObject, idAssignedUnits1, nameAssignedUnits1, aa);
                parseAissignedLocationsQrArray(jsonTrObject, idAssignedUnits2, nameAssignedUnits2, aa);


                namesOfList.put(nameAssignedUnits, aa);

            }


            ((AzulApplication) (this).getApplication()).setListOfData(namesOfList);
        } catch (Exception e) {
            Log.i("TAG", "ex->" + e.getMessage());
        }
    }

    private void setOtherData(JSONObject jsonObject) {
        loginData = new LoginData();
        try {
            String un = jsonObject.has("un") ? jsonObject.getString("un") : "";
            String name = jsonObject.has("Name") ? jsonObject.getString("Name") : "";
            String lastName = jsonObject.has("LastName") ? jsonObject.getString("LastName") : "";
            String identType = jsonObject.has("IdentType") ? jsonObject.getString("IdentType") : "";
            String identNum = jsonObject.has("IdentNum") ? jsonObject.getString("IdentNum") : "";
            String birthDate = jsonObject.has("BirthDate") ? jsonObject.getString("BirthDate") : "";
            String occupation = jsonObject.has("Occupation") ? jsonObject.getString("Occupation") : "";
            String phone = jsonObject.has("Phone") ? jsonObject.getString("Phone") : "";
            String cellPhone = jsonObject.has("CellPhone") ? jsonObject.getString("CellPhone") : "";
            String email = jsonObject.has("Email") ? jsonObject.getString("Email") : "";
            String role = jsonObject.has("Role") ? jsonObject.getString("Role") : "";
            String groupIndustrialSector = jsonObject.has("GroupIndustrialSector") ? jsonObject.getString("GroupIndustrialSector") : "";
            String groupIdentType = jsonObject.has("GroupIdentType") ? jsonObject.getString("GroupIdentType") : "";

            loginData.setCellPhone(cellPhone);
            loginData.setEmail(email);
            loginData.setRole(role);
            loginData.setGroupIndustrialSector(groupIndustrialSector);
            loginData.setGroupIdentType(groupIdentType);


            loginData.setUn(un);
            loginData.setName(name);
            loginData.setLastName(lastName);
            loginData.setIdentType(identType);
            loginData.setIdentNum(identNum);
            loginData.setBirthDate(birthDate);
            loginData.setOccupation(occupation);
            loginData.setPhone(phone);
            setDashboarData(jsonObject);
        } catch (Exception e) {
            Log.i("TAG", "ex->" + e.getMessage());
        }
    }

    private void setDashboarData(JSONObject jsonObject) {
        try {
            String groupIdentNum = jsonObject.has("GroupIdentNum") ? jsonObject.getString("GroupIdentNum") : "";
            String groupName = jsonObject.has("gn") ? jsonObject.getString("gn") : "";
            String lastLoginDate = jsonObject.has("LastLoginDate") ? jsonObject.getString("LastLoginDate") : "";
            loginData.setGroupIdentNum(groupIdentNum);
            loginData.setGroupName(groupName);
            loginData.setLastLoginDate(lastLoginDate);
            ((AzulApplication) this.getApplication()).setLoginData(loginData);
        } catch (Exception e) {
            Log.i("TAG", "ex->" + e.getMessage());
        }
    }

    private void parseAissignedLocationsQrArray(JSONObject jsonTrObject, String idAssignedUnits2, String nameAssignedUnits2, ArrayList<String> aa) {
        try {
            JSONArray jsonArrayAssignedUnits2 = jsonTrObject.getJSONArray("AssignedLocationsQR");
            for (int j = 0; j < jsonArrayAssignedUnits2.length(); j++) {
                JSONObject jsonAssignedUnits2 = jsonArrayAssignedUnits2.getJSONObject(j);
                String newIdAssignedUnits2 = jsonAssignedUnits2.has(Constants.MERCHANT_ID) ? jsonAssignedUnits2.getString(Constants.MERCHANT_ID) : "";
                String newNameAssignedUnits2 = jsonAssignedUnits2.has("Name") ? jsonAssignedUnits2.getString("Name") : "";
                aa.add(newIdAssignedUnits2 + "," + newNameAssignedUnits2);
            }
        } catch (Exception e) {
            Log.i("TAG", "ex->" + e.getMessage());
        }
    }

    private void parseAissignedLocationsArray(JSONObject jsonTrObject, String idAssignedUnits1, String nameAssignedUnits1, ArrayList<String> aa) {
        try {
            JSONArray jsonArrayAssignedUnits1 = jsonTrObject.getJSONArray("AssignedLocations");
            for (int j = 0; j < jsonArrayAssignedUnits1.length(); j++) {
                JSONObject jsonAssignedUnits1 = jsonArrayAssignedUnits1.getJSONObject(j);
                String newIdAssignedUnits1 = jsonAssignedUnits1.has(Constants.MERCHANT_ID) ? jsonAssignedUnits1.getString(Constants.MERCHANT_ID) : "";
                String newNameAssignedUnits1 = jsonAssignedUnits1.has("Name") ? jsonAssignedUnits1.getString("Name") : "";
                aa.add(newIdAssignedUnits1 + "," + newNameAssignedUnits1);
            }
        } catch (Exception e) {
            Log.i("TAG", "ex->" + e.getMessage());
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
        ((AzulApplication) ((PinSetActivity) this).getApplication()).setLocationDataShare(jsonObject.toString());

        startActivity(intent);
        clearAll();
    }


    private void callPinRegisterService(String pin) {
        JSONObject object = new JSONObject();
        try {
            object.put("pin", RSAHelper.ecryptRSA(this, pin));
            object.put(Constants.DEVICE, RSAHelper.ecryptRSA(this, DeviceUtils.getDeviceId(this)));
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.REGISTER_PIN, object);
    }

    private void getDashBoardData() {
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
        apiManager.callAPI(ServiceUrls.DASHBOARD_CALL, json);
    }

    public void responseForNewLogin(String responseString) {
        try {
            JSONObject jsonObject = new JSONObject(responseString);

            if (jsonObject.has(KeyConstants.ACCESS_TOKEN_LABEL)) {
                getDashBoardData();
            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }

    }

    public void responseForDashboard(String responseString) {
        Log.d("PinSetActivity", "responseForDashboard: " + responseString);
    }

    public void callApiPushToken(String tokenData) {
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
            json.put("tcp", RSAHelper.ecryptRSA(PinSetActivity.this, tcpKey));
            JSONObject payload = new JSONObject();
            payload.put("device", DeviceUtils.getDeviceId(this));
            payload.put("token", tokenData);
            payload.put("notify", status);

            json.put("payload", RSAHelper.encryptAES(payload.toString(), Base64.decode(tcpKey, 0), Base64.decode(vcr, 0)));
            Log.d("PinSetActivity", "callApiPushToken: "+payload.toString());

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.PUSH_TOKEN, json);
    }

    public void pushTokenResponse(String responseString) {
        Log.d("Data", "" + responseString);
    }
}