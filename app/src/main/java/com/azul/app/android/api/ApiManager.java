package com.azul.app.android.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.azul.app.android.activities.dashboard.QrCode;
import com.azul.app.android.activities.registration.PinLoginActivity;
import com.azul.app.android.activities.registration.UserRegisterActivity;
import com.azul.app.android.activities.registration.PinSetActivity;
import com.azul.app.android.activities.transactions.QrTransactions;
import com.azul.app.android.globals.AppAlters;
import com.azul.app.android.R;

import org.json.JSONObject;

public class ApiManager implements CallbackListener {

    private final Context context;

    public ApiManager(Context context) {
        this.context = context;
    }

    ProgressDialog progressDialog;

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            for (NetworkInfo networkInfo : info)
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
        }
        return false;
    }

    /*
     * Call API service and execute service.
     */
    public void callAPI(String url, JSONObject requestObject) {
        if (isConnectingToInternet()) {
            ApiService apiService = new ApiService(context, ApiManager.this, url, requestObject);
            apiService.execute();
        } else {
            AppAlters.showAlert(context, "", context.getString(R.string.no_internet_msg),
                    context.getString(R.string.continue_lable));
        }
    }

    // Action for USER_LOGIN service /
    @Override
    public void userRegisterResponse(String jsonUserRegister) {
        if (context.getClass().equals(UserRegisterActivity.class)) {
            UserRegisterActivity activity = (UserRegisterActivity) context;
            activity.processRegisterServiceResponse(jsonUserRegister);
        }
    }

    public void pinRegisterResponse(String jsonPinRegister) {
        if (context.getClass().equals(PinSetActivity.class)) {
            PinSetActivity activity = (PinSetActivity) context;
            activity.processPinRegisterServiceResponse(jsonPinRegister);
        }
    }

    public void loginResponse(String jsonLogin) {
        if (context.getClass().equals(PinLoginActivity.class)) {
            PinLoginActivity activity = (PinLoginActivity) context;
            activity.processLoginServiceResponse(jsonLogin);
        }
        if (context.getClass().equals(PinSetActivity.class)) {
            PinSetActivity accessActivity = (PinSetActivity) context;
            accessActivity.processLoginServiceResponse(jsonLogin);
        }
    }

    public void qrTransactionResponse(String responseData) {
        if (context.getClass().equals(QrTransactions.class)) {
            QrTransactions activity = (QrTransactions) context;
            activity.qrTransactionResponse(responseData);
        }
        if (context.getClass().equals(QrCode.class)) {
            QrCode activity = (QrCode) context;
            activity.qrTransactionResponse(responseData);
        }
    }


    public void getQrResponse(String responseData) {

        if (context.getClass().equals(QrCode.class)) {
            QrCode code = (QrCode) context;

            code.getResponseForQr(responseData);
        }
    }

}