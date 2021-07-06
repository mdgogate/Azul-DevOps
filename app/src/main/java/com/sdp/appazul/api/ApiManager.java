package com.sdp.appazul.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sdp.appazul.activities.notifications.PushNotificationSettings;
import com.sdp.appazul.activities.dashboard.DashBoardActivity;
import com.sdp.appazul.activities.dashboard.QrCode;
import com.sdp.appazul.activities.home.MainMenuActivity;
import com.sdp.appazul.activities.payment.PaymentConfirmaActivity;
import com.sdp.appazul.activities.payment.PaymentDataValidateActivity;
import com.sdp.appazul.activities.registration.PinLoginActivity;
import com.sdp.appazul.activities.registration.UserRegisterActivity;
import com.sdp.appazul.activities.registration.PinSetActivity;
import com.sdp.appazul.activities.transactions.QrTransactions;
import com.sdp.appazul.activities.transactions.SettledTransactionsQuery;
import com.sdp.appazul.globals.AppAlters;
import com.sdp.appazul.R;

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
        } else if (context.getClass().equals(MainMenuActivity.class)) {
            MainMenuActivity mainMenuActivity = (MainMenuActivity) context;
            mainMenuActivity.getResponseForQr(responseData);
        }
    }

    public void settleTransactionResponse(String responseData) {

        if (context.getClass().equals(SettledTransactionsQuery.class)) {
            SettledTransactionsQuery code = (SettledTransactionsQuery) context;

            code.getSettleTrnResponse(responseData);
        }
    }

    public void qrResponseLogin(String responseData) {

        if (context.getClass().equals(MainMenuActivity.class)) {
            MainMenuActivity code = (MainMenuActivity) context;

            code.qrResponseBeforeLogin(responseData);
        }
    }

    public void paymentMerchantResponse(String responseData) {

        if (context.getClass().equals(PaymentConfirmaActivity.class)) {
            PaymentConfirmaActivity code = (PaymentConfirmaActivity) context;

            code.paymentLnkMerchantResponse(responseData);
        }
    }

    public void createPaymentLinkResponse(String responseData) {

        if (context.getClass().equals(PaymentDataValidateActivity.class)) {
            PaymentDataValidateActivity code = (PaymentDataValidateActivity) context;

            code.paymentLnkCreateResponse(responseData);
        }
    }

    @Override
    public void pushTokenRepsonse(String responseString) {
        if (context.getClass().equals(PushNotificationSettings.class)) {
            PushNotificationSettings code = (PushNotificationSettings) context;

            code.pushTokenResponse(responseString);
        }
    }

    @Override
    public void transactionWidgetResponse(String responseString) {
        if (context.getClass().equals(DashBoardActivity.class)) {
            DashBoardActivity code = (DashBoardActivity) context;

            code.transactionWidgetResponseDate(responseString);
        }
    }

    @Override
    public void transactionWidgetQrResponse(String responseString) {
        if (context.getClass().equals(DashBoardActivity.class)) {
            DashBoardActivity code = (DashBoardActivity) context;

            code.transactionWidgetQrResponseDate(responseString);
        }
    }

}