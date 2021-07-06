package com.sdp.appazul.api;

public class ServiceUrls {
    private ServiceUrls() {
    }

    public static final String BASE_URL = "http://45.79.42.37:9101";

    public static final String REGISTER_USER = BASE_URL + "/api/registeruser";
    public static final String REGISTER_PIN = BASE_URL + "/api/pin/registerpin";
    public static final String LOGIN = BASE_URL + "/api/login";

    public static final String QR_TRANSACTION = BASE_URL + "/api/qrtransactions";
    public static final String GET_QR = BASE_URL + "/api/getqrcode";
    public static final String SETTLEMENT_TRANSACTION = BASE_URL + "/api/settledtransactions";
    public static final String QR_CODE_BEFORE_LOGIN = BASE_URL + "/api/requesttoken";

    public static final String GET_MERCHANTS_FOR_PAYMENT_LINK = BASE_URL + "/api/paymentlinkgetmerchants";
    public static final String CREATE_PAYMENT_LINK = BASE_URL + "/api/paymentlinkcreate";
    public static final String PUSH_TOKEN = BASE_URL + "/api/pushtoken";

    public static final String TRANSACTION_WIDGET = BASE_URL + "/api/transactionwidget";
    public static final String TRANSACTION_WIDGET_QR = BASE_URL + "/api/transactionwidgetqr";
}

