package com.sdp.appazul.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.sdp.appazul.activities.notifications.PushNotificationSettings;
import com.sdp.appazul.activities.dashboard.DashBoardActivity;
import com.sdp.appazul.activities.dashboard.QrCode;
import com.sdp.appazul.activities.home.MainMenuActivity;
import com.sdp.appazul.activities.payment.PaymentConfirmActivity;
import com.sdp.appazul.activities.payment.PaymentDataValidateActivity;
import com.sdp.appazul.activities.payment.QuickPayValidationActivity;
import com.sdp.appazul.activities.payment.SetPaymentInfoActivity;
import com.sdp.appazul.activities.registration.PinLoginActivity;
import com.sdp.appazul.activities.registration.UserRegisterActivity;
import com.sdp.appazul.activities.registration.PinSetActivity;
import com.sdp.appazul.activities.transactions.PaymentLinkDetails;
import com.sdp.appazul.activities.transactions.PaymentLinkTransactions;
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


        if (context.getClass().equals(PaymentConfirmActivity.class)) {
            PaymentConfirmActivity code = (PaymentConfirmActivity) context;

            code.paymentLnkMerchantResponse(responseData);
        } else if (context.getClass().equals(SetPaymentInfoActivity.class)) {
            SetPaymentInfoActivity code = (SetPaymentInfoActivity) context;

            code.paymentLnkMerchantResponse(responseData);
        } else if (context.getClass().equals(MainMenuActivity.class)) {
            MainMenuActivity code = (MainMenuActivity) context;

            code.paymentLnkMerchantResponse(responseData);
        }
    }

    public void createPaymentLinkResponse(String responseData) {

        if (context.getClass().equals(PaymentDataValidateActivity.class)) {
            PaymentDataValidateActivity code = (PaymentDataValidateActivity) context;

            code.paymentLnkCreateResponse(responseData);
        } else if (context.getClass().equals(QuickPayValidationActivity.class)) {
            QuickPayValidationActivity code = (QuickPayValidationActivity) context;

            code.paymentLnkCreateResponse(responseData);
        }
    }

    @Override
    public void pushTokenRepsonse(String responseString) {
        if (context.getClass().equals(PushNotificationSettings.class)) {
            PushNotificationSettings code = (PushNotificationSettings) context;

            code.pushTokenResponse(responseString);
        }else if (context.getClass().equals(PinSetActivity.class)) {
            PinSetActivity code = (PinSetActivity) context;

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

    @Override
    public void appPermissionsResponse(String responseString) {
        if (context.getClass().equals(MainMenuActivity.class)) {
            MainMenuActivity code = (MainMenuActivity) context;
            code.getappPermissionsResponse(responseString);
        } else if (context.getClass().equals(DashBoardActivity.class)) {
            DashBoardActivity code = (DashBoardActivity) context;
            code.getAppPermissionsResponse(responseString);
        }
    }

    @Override
    public void callingDeregisterUsersApi(String responseString) {
        if (context.getClass().equals(MainMenuActivity.class)) {
            MainMenuActivity mainMenuActivity = (MainMenuActivity) context;
            mainMenuActivity.getDeregisterAPIResponse(responseString);
        } else {

            PinLoginActivity loginActivity = (PinLoginActivity) context;
            loginActivity.getDeregisterAPIResponse(responseString);
        }
    }

    @Override
    public void getTransactionPdfResponse(String responseString) {
        DashBoardActivity dashBoardActivity = (DashBoardActivity) context;
        dashBoardActivity.getTransactionPdf(responseString);
    }

    @Override
    public void getPaymentSearchResponse(String responseString) {
        PaymentLinkTransactions transactions = (PaymentLinkTransactions) context;
        transactions.getPaymentSearchResponse(responseString);
    }

    @Override
    public void getPaymentLinkInfoResponse(String responseString) {
        PaymentLinkDetails details = (PaymentLinkDetails) context;
        details.getPaymentLinkInfoResponse(responseString);
    }

    @Override
    public void getNewLoginResponse(String responseString) {
        if (context.getClass().equals(PinSetActivity.class)) {
            PinSetActivity activity = (PinSetActivity) context;
            activity.responseForNewLogin(responseString);
        }
    }

    @Override
    public void getDataForDashboard(String responseString) {
        if (context.getClass().equals(PinSetActivity.class)) {
            PinSetActivity activity = (PinSetActivity) context;
            activity.responseForDashboard(responseString);
        }
    }

    @Override
    public void getNewTokenAtPreLogin(String responseString) {
        if (context.getClass().equals(MainMenuActivity.class)) {
            MainMenuActivity mainMenuActivity = (MainMenuActivity) context;
            mainMenuActivity.getRTokenResponse(responseString);
        }
    }

    @Override
    public void getUserResponse(String responseString) {
        if (context.getClass().equals(MainMenuActivity.class)) {
            MainMenuActivity mainMenuActivity = (MainMenuActivity) context;
            mainMenuActivity.getUserResponseData(responseString);
        }
    }

    @Override
    public void getRegenerateResponse(String responseString) {
        Log.d("TAG", "getRegenerateResponse: ");
    }


}