package com.azul.app.android.api;

public interface CallbackListener {

    void userRegisterResponse(String jsonUserRegister);
    void pinRegisterResponse(String jsonPinRegister);
    void loginResponse(String jsonLogin);
    void qrTransactionResponse(String jsonQrTransactionData);
    void getQrResponse(String responseString);
}
