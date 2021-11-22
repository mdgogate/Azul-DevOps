package com.sdp.appazul.activities.registration;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.sdp.appazul.R;
import com.sdp.appazul.activities.dashboard.DashBoardActivity;
import com.sdp.appazul.activities.home.BasicRegistrationActivity;
import com.sdp.appazul.activities.home.MainMenuActivity;
import com.sdp.appazul.api.ApiManager;
import com.sdp.appazul.api.ServiceUrls;
import com.sdp.appazul.classes.LoginData;
import com.sdp.appazul.globals.AzulApplication;
import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.GlobalFunctions;
import com.sdp.appazul.globals.KeyConstants;
import com.sdp.appazul.security.RSAHelper;
import com.sdp.appazul.security.SecurePreferences;
import com.sdp.appazul.utils.DeviceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;


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
    ImageView backButton;
    ApiManager apiManager = new ApiManager(this);
    String sigKey = "";
    SharedPreferences sscPref;
    GlobalFunctions globals = new GlobalFunctions(this);
    int errorCount = 0;
    int maxTryCount = 3;
    private HashMap<String, ArrayList<String>> namesOfList = null;
    private LoginData loginData;
    JSONObject locationResponse;
    int lastEnteredCount;
    Dialog errorAlertDialog;

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
        validateBlockCount();
        sigKey = sscPref.getString("SIG_LABEL", "");
    }

    private void validateBlockCount() {
        int loginTriedCount = sscPref.getInt(Constants.PRE_LOGIN_COUNTER, 0);

        if (loginTriedCount >= 3) {
            errorAlert(PinLoginActivity.this, loginTriedCount, maxTryCount, 1);
        }
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
            this.overridePendingTransition(R.anim.animation_leave,
                    R.anim.slide_nothing);
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
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
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
            object.put(Constants.DEVICE, RSAHelper.ecryptRSA(this, DeviceUtils.getDeviceId(this)));
            object.put("sig", sigKey);


            objectWithoutEnc.put("pin", pin);
            objectWithoutEnc.put(Constants.DEVICE, DeviceUtils.getDeviceId(this));
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
            SharedPreferences sharedPreferences = ((AzulApplication) this.getApplication()).getPrefs();
            JSONObject jsonObject = new JSONObject(jsonRegister);
            String status = jsonObject.has("Status") ? jsonObject.getString("Status") : "";
            String passwordChange = jsonObject.has("PasswordChange") ? jsonObject.getString("PasswordChange") : "";
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

            loginData = new LoginData();
            loginData.setUn(un);
            loginData.setName(name);
            loginData.setLastName(lastName);
            loginData.setIdentType(identType);
            loginData.setIdentNum(identNum);
            loginData.setBirthDate(birthDate);
            loginData.setOccupation(occupation);
            loginData.setPhone(phone);
            loginData.setCellPhone(cellPhone);
            loginData.setEmail(email);
            loginData.setRole(role);

            getOtherData(jsonObject);



            checkLocationsArray(jsonObject);


            checkUserStatus(jsonObject, passwordChange, status, sharedPreferences, jsonRegister);

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }

    private void getOtherData(JSONObject jsonObject) {
        try {
            String groupIndustrialSector = jsonObject.has("GroupIndustrialSector") ? jsonObject.getString("GroupIndustrialSector") : "";
            String groupIdentType = jsonObject.has("GroupIdentType") ? jsonObject.getString("GroupIdentType") : "";
            String groupIdentNum = jsonObject.has("GroupIdentNum") ? jsonObject.getString("GroupIdentNum") : "";
            String groupName = jsonObject.has("gn") ? jsonObject.getString("gn") : "";
            String lastLoginDate = jsonObject.has("LastLoginDate") ? jsonObject.getString("LastLoginDate") : "";
            loginData.setGroupIndustrialSector(groupIndustrialSector);
            loginData.setGroupIdentType(groupIdentType);
            loginData.setGroupIdentNum(groupIdentNum);
            loginData.setGroupName(groupName);
            loginData.setLastLoginDate(lastLoginDate);
            ((AzulApplication) this.getApplication()).setLoginData(loginData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkLocationsArray(JSONObject jsonObject) {
        namesOfList = new HashMap<>();
        JSONArray jsonArray;
        try {
            jsonArray = jsonObject.has("AssignedUnits") ? jsonObject.getJSONArray("AssignedUnits") : new JSONArray();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonTrObject = jsonArray.getJSONObject(i);

                parsingMoreData(jsonTrObject, namesOfList);

            }

            ((AzulApplication) (this).getApplication()).setListOfData(namesOfList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parsingMoreData(JSONObject jsonTrObject, HashMap<String, ArrayList<String>> namesOfList) {
        String nameAssignedUnits1;
        String idAssignedUnits1;
        String nameAssignedUnits2;
        String idAssignedUnits2;
        try {
            ArrayList<String> aa = new ArrayList<>();
            String nameAssignedUnits = jsonTrObject.has("Name") ? jsonTrObject.getString("Name") : "";
            JSONArray jsonArrayAssignedUnits1 = jsonTrObject.getJSONArray("AssignedLocations");
            for (int j = 0; j < jsonArrayAssignedUnits1.length(); j++) {
                JSONObject jsonAssignedUnits1 = jsonArrayAssignedUnits1.getJSONObject(j);
                idAssignedUnits1 = jsonAssignedUnits1.has(Constants.MERCHANT_ID) ? jsonAssignedUnits1.getString(Constants.MERCHANT_ID) : "";
                nameAssignedUnits1 = jsonAssignedUnits1.has("Name") ? jsonAssignedUnits1.getString("Name") : "";
                aa.add(idAssignedUnits1 + "," + nameAssignedUnits1);
            }


            JSONArray jsonArrayAssignedUnits2 = jsonTrObject.getJSONArray("AssignedLocationsQR");
            for (int j = 0; j < jsonArrayAssignedUnits2.length(); j++) {
                JSONObject jsonAssignedUnits2 = jsonArrayAssignedUnits2.getJSONObject(j);
                idAssignedUnits2 = jsonAssignedUnits2.has(Constants.MERCHANT_ID) ? jsonAssignedUnits2.getString(Constants.MERCHANT_ID) : "";
                nameAssignedUnits2 = jsonAssignedUnits2.has("Name") ? jsonAssignedUnits2.getString("Name") : "";
                aa.add(idAssignedUnits2 + "," + nameAssignedUnits2);
            }

            namesOfList.put(nameAssignedUnits, aa);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkUserStatus(JSONObject jsonObject, String passwordChange, String status, SharedPreferences sharedPreferences, String jsonRegister) {
        try {
            if (jsonObject.has(KeyConstants.ACCESS_TOKEN_LABEL) && !TextUtils.isEmpty(jsonObject.getString(KeyConstants.ACCESS_TOKEN_LABEL))) {
                locationResponse = jsonObject;
                statusOfUser(status, passwordChange, jsonObject);
                errorCount = 0;
                sharedPreferences.edit().putInt(Constants.PRE_LOGIN_COUNTER, errorCount).apply();
            } else {
                if (jsonRegister.contains("1010")) {

                    lastEnteredCount = sscPref.getInt(Constants.PRE_LOGIN_COUNTER, 0);

                    loginTriesCountLogic(lastEnteredCount, sharedPreferences);

                } else if (jsonRegister.contains("1009")) {
                    sharedPreferences = ((AzulApplication) this.getApplication()).getPrefs();
                    sharedPreferences.edit().putString(Constants.FIRST_TIME, "No").apply();
                    Intent intent = new Intent(PinLoginActivity.this, UserRegisterActivity.class);
                    startActivity(intent);
                    this.overridePendingTransition(R.anim.animation_enter,
                            R.anim.slide_nothing);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loginTriesCountLogic(int lastEnteredCount, SharedPreferences sharedPreferences) {
        Log.d("TAG", "lastEnteredCount : " + lastEnteredCount);
        if (lastEnteredCount <= 0) {
            errorCount++;
            sharedPreferences.edit().putInt(Constants.PRE_LOGIN_COUNTER, errorCount).apply();
            Log.d("TAG", "errorCount ++  : " + errorCount);
            Log.d("TAG", "PRE PRE_LOGIN_COUNTER ++  : " + sscPref.getInt(Constants.PRE_LOGIN_COUNTER, 0));
            counterLogic(errorCount);
        } else {
            lastEnteredCount++;
            sharedPreferences.edit().putInt(Constants.PRE_LOGIN_COUNTER, lastEnteredCount).apply();
            Log.d("TAG", "lastEnteredCount ++ : " + lastEnteredCount);
            Log.d("TAG", "LAST PRE_LOGIN_COUNTER  ++ : " + sscPref.getInt(Constants.PRE_LOGIN_COUNTER, 0));
            counterLogic(lastEnteredCount);
        }
    }

    private void counterLogic(int counterValue) {
        if (counterValue < maxTryCount) {
            errorAlert(PinLoginActivity.this, counterValue, maxTryCount, 0);
        } else if (counterValue == maxTryCount) {
            errorAlert(PinLoginActivity.this, counterValue, maxTryCount, 1);
        }
    }

    public void proceedToDashboardScreen(JSONObject jsonObject) {
        Intent intent = new Intent(PinLoginActivity.this, DashBoardActivity.class);
        ((AzulApplication) ((PinLoginActivity) this).getApplication()).setLocationDataShare(jsonObject.toString() );

        startActivity(intent);
        clearAll();
    }


    public void errorAlert(Context activity, int loginCount, int maxTryCount, int isPinBlock) {
        try {

            errorAlertDialog = new Dialog(activity);
            errorAlertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            errorAlertDialog.setCancelable(false);
            errorAlertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            errorAlertDialog.setContentView(R.layout.custom_login_alert_dialog);
            TextView textTitle = errorAlertDialog.findViewById(R.id.textTitle);
            TextView textMsg = errorAlertDialog.findViewById(R.id.textMsg);
            TextView tvTryCount = errorAlertDialog.findViewById(R.id.tvTryCount);

            Typeface typeface;
            typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Medium.ttf");

            tvTryCount.setTypeface(typeface);
            textMsg.setTypeface(typeface);
            textTitle.setTypeface(typeface);


            if (isPinBlock == 1) {
                tvTryCount.setVisibility(View.INVISIBLE);
                textTitle.setText(getResources().getString(R.string.pin_blocked_title));
                textMsg.setText(getResources().getString(R.string.pin_blocked_message));
            } else {
                tvTryCount.setVisibility(View.VISIBLE);
                String tryCountLabel = getResources().getString(R.string.login_try_count_label) + " " + loginCount + "/" + maxTryCount + ")";
                tvTryCount.setText(tryCountLabel);
                textTitle.setText(getResources().getString(R.string.login_pin_error_alert_title));
                textMsg.setText(getResources().getString(R.string.login_pin_error_alert_message));
            }


            TextView btnContinue = errorAlertDialog.findViewById(R.id.btnContinue);
            btnContinue.setTypeface(typeface);

            btnContinue.setOnClickListener(view -> {

                if (isPinBlock == 1) {
                    SharedPreferences sharedPreferences = ((AzulApplication) this.getApplication()).getPrefs();
                    sharedPreferences.edit().putString(Constants.FIRST_TIME, "No").apply();
                    Intent intent = new Intent(PinLoginActivity.this, UserRegisterActivity.class);
                    startActivity(intent);
                    this.overridePendingTransition(R.anim.animation_enter,
                            R.anim.slide_nothing);
                    sharedPreferences.edit().putInt(Constants.PRE_LOGIN_COUNTER, 0).apply();

                }

                clearAll();
                if (errorAlertDialog != null && errorAlertDialog.isShowing()) {
                    errorAlertDialog.dismiss();
                }
            });

            errorAlertDialog.show();

        } catch (
                Exception e) {
            Log.e("Exception : ", Log.getStackTraceString(e));
        }
    }


    private void statusOfUser(String status, String passwordChange, JSONObject jsonObject) {
        if (status.equals("Active") && passwordChange.equalsIgnoreCase("True")) {
            showDialog(PinLoginActivity.this, getString(R.string.active_temporal_password_title), getString(R.string.active_temporal_password), "active_temporal_password", getString(R.string.Cancel), getString(R.string.continue_lable));
        } else if (status.equalsIgnoreCase("New")) {
            showDialog(PinLoginActivity.this, getResources().getString(R.string.Status_Nuevo_title), getResources().getString(R.string.Status_Nuevo), "Status_Nuevo", getString(R.string.Cancel), getString(R.string.continue_lable));
        } else {
            statusMethod1(status, jsonObject);

        }
    }

    private void statusMethod1(String status, JSONObject jsonObject) {
        if (status.equals("Active")) {
            proceedToDashboardScreen(jsonObject);
        } else if (status.equalsIgnoreCase("NewExpired")) {
            showDialog(PinLoginActivity.this, getString(R.string.New_Expired_title), getString(R.string.New_Expired), "New_Expired", getString(R.string.no), getString(R.string.yes));
        } else {
            statusMethod2(status);
        }
    }

    private void statusMethod2(String status) {
        if (status.equalsIgnoreCase("ActiveExpired")) {
            showDialog(PinLoginActivity.this, getString(R.string.Active_expired_title), getString(R.string.Active_expired), "Active_expired", getString(R.string.Cancel), getString(R.string.continue_lable));

        } else if (status.equalsIgnoreCase(Constants.BLOCK_STATUS)) {
            showDialog(PinLoginActivity.this, getString(R.string.Blocked_title), getString(R.string.Blocked), Constants.BLOCK_STATUS, getString(R.string.Cancel), getString(R.string.continue_lable));

        } else {
            statusMethod3(status);

        }
    }

    private void statusMethod3(String status) {
        if (status.equalsIgnoreCase(Constants.DISABLED_STATUS)) {
            showDialog(PinLoginActivity.this, getString(R.string.Disabled_title), getString(R.string.Disabled), Constants.DISABLED_STATUS, "", "Aceptar");

        } else if (status.equalsIgnoreCase("UnderInvestigation")) {
            showDialog(PinLoginActivity.this, getString(R.string.Under_investigation_title), getString(R.string.Under_investigation), "Under_investigation", getString(R.string.no), getString(R.string.yes));

        } else {
            showDialog(PinLoginActivity.this, status, status, "", getString(R.string.Cancel), getString(R.string.continue_lable));

        }
    }

    private void statusPositiveButton(String statusName) {
        String linkLogin = "https://portal.azul.com.do/Login";
        String linkForgetPassword = "https://portal.azul.com.do/Login/ForgotPassword";
        Uri uri;
        if ("Status_Nuevo".equals(statusName) || "active_temporal_password".equals(statusName)) {
            uri = Uri.parse(linkLogin);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if ("New_Expired".equals(statusName) || "Active_expired".equals(statusName) || Constants.BLOCK_STATUS.equals(statusName)) {
            uri = Uri.parse(linkForgetPassword);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (Constants.DISABLED_STATUS.equals(statusName) || "Under_investigation".equals(statusName)) {
            callDeregisterApi();

        }
    }

    private void callDeregisterApi() {
        JSONObject json = new JSONObject();
        try {
            String tcpKey = ((AzulApplication) getApplicationContext().getApplicationContext()).getTcpKey();
            json.put("tcp", RSAHelper.ecryptRSA(getApplicationContext(), tcpKey));
            json.put(Constants.DEVICE, RSAHelper.ecryptRSA(getApplicationContext(), DeviceUtils.getDeviceId(getApplicationContext())));

        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        apiManager.callAPI(ServiceUrls.DEREGISTER_USER, json);
    }

    public void showDialog(Context activity, String statusTitle, String status, String
            statusName, String b2, String b1) {
        try {

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
            textTitle.setText(statusTitle);

            TextView btnContinue = dialog.findViewById(R.id.btnContinue);
            btnContinue.setTypeface(typeface);
            btnContinue.setText(b1);
            TextView btnCancel = dialog.findViewById(R.id.btnCancel);
            btnCancel.setTypeface(typeface);
            if (b2.equalsIgnoreCase("")) {
                btnCancel.setVisibility(View.INVISIBLE);
            } else {
                btnCancel.setVisibility(View.VISIBLE);
                btnCancel.setText(b2);
            }

            btnContinue.setOnClickListener(view -> {
                clearAll();
                dialog.dismiss();
                statusPositiveButton(statusName);
            });
            btnCancel.setOnClickListener(v -> {
                clearAll();
                dialog.dismiss();
            });
            dialog.show();
        } catch (
                Exception e) {
            Log.e("Exception : ", Log.getStackTraceString(e));
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PinLoginActivity.this, MainMenuActivity.class);
        startActivity(intent);
        this.overridePendingTransition(R.anim.animation_leave,
                R.anim.slide_nothing);
    }

    public void getDeregisterAPIResponse(String responseString) {
        try {
            JSONObject jsonObject = new JSONObject(responseString);
            String responseStatus = jsonObject.has("status") ? jsonObject.getString("status") : "";
            if (responseStatus.equalsIgnoreCase("success")) {
                ((AzulApplication) this.getApplication()).getPrefs().edit().clear().apply();
                startActivity(new Intent(this, UserRegisterActivity.class));
                finish();

            }
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
    }
}