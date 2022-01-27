package com.sdp.appazul.api;

import android.os.Build;

import com.sdp.appazul.BuildConfig;

public class ServiceUrls {

    private ServiceUrls() {
    }

//    public static final String BASE_URL = "http://45.79.42.37:9101";
//    public static final String BASE_URL = "https://apidev.thephpprojects.com:9200";
    public static final String BASE_URL = BuildConfig.BASE_URL;

    public static final String REGISTER_USER = BASE_URL + "/api/registeruser";
    public static final String REGISTER_PIN = BASE_URL + "/api/pin/registerpin";
    public static final String LOGIN = BASE_URL + "/api/login";

    public static final String QR_TRANSACTION = BASE_URL + "/api/qrtransactions";
    public static final String GET_QR = BASE_URL + "/api/getqrcode";
    public static final String SETTLEMENT_TRANSACTION = BASE_URL + "/api/settledtransactions";
    public static final String REQUEST_TOKEN_BEFORE_LOGIN = BASE_URL + "/api/requesttoken";       // NOt in Use

    public static final String GET_PAYMENT_MERCHANTS = BASE_URL + "/api/paymentlinkgetmerchants";   // NOt in Use
    public static final String CREATE_PAYMENT_LINK = BASE_URL + "/api/paymentlinkcreate";
    public static final String PUSH_TOKEN = BASE_URL + "/api/pushtoken";

    public static final String TRANSACTION_WIDGET = BASE_URL + "/api/transactionwidget";
    public static final String TRANSACTION_WIDGET_QR = BASE_URL + "/api/transactionwidgetqr";

    public static final String DEREGISTER_USER = BASE_URL + "/api/deregister";
    public static final String GET_TRANSACTION_PDF = BASE_URL + "/api/transactionpdf";

    public static final String GET_APP_RELATED_PERMISSIONS = BASE_URL + "/api/getuserpermissions";
    public static final String PAYMENT_LINK_SEARCH = BASE_URL + "/api/paymentlinksearch";
    public static final String PAYMENT_LINK_INFO = BASE_URL + "/api/paymentlinkinfo";


    public static final String LOGIN_TOKEN = BASE_URL + "/api/logintoken";
    public static final String DASHBOARD_CALL = BASE_URL + "/api/dashboard";
    public static final String R_TOKEN = BASE_URL + "/api/rtoken";
    public static final String GET_USER = BASE_URL + "/api/getuser";
    public static final String PAYMENT_LINK_RE_GENERATE = BASE_URL + "/api/paymentlinkregenerate";
}

