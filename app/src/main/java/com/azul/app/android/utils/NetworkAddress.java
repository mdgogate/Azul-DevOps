package com.azul.app.android.utils;

import android.util.Log;

public class NetworkAddress {

    private final String[] urls = new String[3];

    public NetworkAddress() {
        urls[0] = "https://codigoqr.azul.com.do/solicitar";
        urls[1] = "https://portal.azul.com.do/Login/ForgotPassword";
        urls[2] = "https://portal.azul.com.do/ContactForm/AzulSAPIntegration/form.html";
    }

    public String getSpecificUrl(int index) {
        String returningUrl = urls[index];
        Log.i("returningUrl", returningUrl);
        return returningUrl;
    }
}
