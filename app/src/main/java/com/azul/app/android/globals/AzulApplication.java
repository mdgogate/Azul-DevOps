package com.azul.app.android.globals;

import android.app.Application;
import android.content.SharedPreferences;

public class AzulApplication extends Application {

    private SharedPreferences prefs;
    private String tcpKey = "";
    private String accToken = "";

    public SharedPreferences getPrefs() {
        return prefs;
    }

    public void setPrefs(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    public String getTcpKey() {
        return tcpKey;
    }

    public void setTcpKey(String tcpKey) {
        this.tcpKey = tcpKey;
    }

    public String getAccToken() {
        return accToken;
    }

    public void setAccToken(String accToken) {
        this.accToken = accToken;
    }
}
