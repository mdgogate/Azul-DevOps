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
}
