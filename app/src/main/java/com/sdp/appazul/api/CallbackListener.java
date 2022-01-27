package com.sdp.appazul.api;

public interface CallbackListener {

    void userRegisterResponse(String jsonUserRegister);
    void pinRegisterResponse(String jsonPinRegister);
    void loginResponse(String jsonLogin);
    void qrTransactionResponse(String jsonQrTransactionData);
    void getQrResponse(String responseString);
    void settleTransactionResponse(String settleResponseString);

    void qrResponseLogin(String responseString);

    void paymentMerchantResponse(String responseString);

    void createPaymentLinkResponse(String responseString);

    void pushTokenRepsonse(String responseString);

    void transactionWidgetResponse(String responseString);

    void transactionWidgetQrResponse(String responseString);

    void callingDeregisterUsersApi(String responseString);

    void appPermissionsResponse(String responseString);

    void getTransactionPdfResponse(String responseString);

    void getPaymentSearchResponse(String responseString);

    void getPaymentLinkInfoResponse(String responseString);

    void getNewLoginResponse(String responseString);

    void getDataForDashboard(String responseString);

    void getNewTokenAtPreLogin(String responseString);

    void getUserResponse(String responseString);

    void getRegenerateResponse(String responseString);
}
