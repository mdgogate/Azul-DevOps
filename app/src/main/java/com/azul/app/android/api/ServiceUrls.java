package com.azul.app.android.api;

public class ServiceUrls {
    private ServiceUrls() {
    }

    public static final String BASE_URL = "http://45.79.42.37:9101";

    public static final String REGISTER_USER = BASE_URL + "/api/registeruser";
    public static final String REGISTER_PIN = BASE_URL + "/api/pin/registerpin";
    public static final String LOGIN = BASE_URL + "/api/login";
    public static final String QR_TRANSACTION = BASE_URL + "/api/qrtransactions";
    public static final String GET_QR = BASE_URL + "/api/getqrcode";
}

