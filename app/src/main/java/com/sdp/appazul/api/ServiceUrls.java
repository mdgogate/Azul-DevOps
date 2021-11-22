package com.sdp.appazul.api;

import android.os.Build;

import com.sdp.appazul.BuildConfig;

public class ServiceUrls {

    private ServiceUrls() {
    }

//    public static final String BASE_URL = "http://45.79.42.37:9101";
//    public static final String BASE_URL = "https://apidev.thephpprojects.com:9200";
    public static final String BASE_URL = BuildConfig.BASE_URL;

    public static final String REGISTER_USER = BASE_URL + "/api/registeruser";   //                   //Real_Api_Working
    public static final String REGISTER_PIN = BASE_URL + "/api/pin/registerpin"; //                   //Real_Api_Working
    public static final String LOGIN = BASE_URL + "/api/login";                   //                  //Real_Api_Working

    public static final String QR_TRANSACTION = BASE_URL + "/api/qrtransactions";     //              //Real_Api_Working
    public static final String GET_QR = BASE_URL + "/api/getqrcode";                //                           //Real_Api_Working
    public static final String SETTLEMENT_TRANSACTION = BASE_URL + "/api/settledtransactions";  //    //Real_Api_Working
    public static final String REQUEST_TOKEN_BEFORE_LOGIN = BASE_URL + "/api/requesttoken";    //     //Real_Api_Working

    public static final String GET_PAYMENT_MERCHANTS = BASE_URL + "/api/paymentlinkgetmerchants";   //Real_Api_Working
    public static final String CREATE_PAYMENT_LINK = BASE_URL + "/api/paymentlinkcreate";     //      //Error
    public static final String PUSH_TOKEN = BASE_URL + "/api/pushtoken";            //                //Real_Api_Working

    public static final String TRANSACTION_WIDGET = BASE_URL + "/api/transactionwidget";        //               //Real_Api_Working
    public static final String TRANSACTION_WIDGET_QR = BASE_URL + "/api/transactionwidgetqr";   //             //Real_Api_Working

    public static final String DEREGISTER_USER = BASE_URL + "/api/deregister";                 //                 //Real_Api_Working
    public static final String GET_TRANSACTION_PDF = BASE_URL + "/api/transactionpdf";          //                 //Real_Api_Working

    public static final String GET_APP_RELATED_PERMISSIONS = BASE_URL + "/api/getuserpermissions"; //          //Real_Api_Working
    public static final String PAYMENT_LINK_SEARCH = BASE_URL + "/api/paymentlinksearch";      //            //Real_Api_Working
    public static final String PAYMENT_LINK_INFO = BASE_URL + "/api/paymentlinkinfo";         //             //Real_Api_Working


    public static final String LOGIN_TOKEN = BASE_URL + "/api/logintoken";
    public static final String DASHBOARD_CALL = BASE_URL + "/api/dashboard";
    public static final String R_TOKEN = BASE_URL + "/api/rtoken";
    public static final String GET_USER = BASE_URL + "/api/getuser";
}

