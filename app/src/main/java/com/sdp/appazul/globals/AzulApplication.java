package com.sdp.appazul.globals;

import android.app.Application;
import android.content.SharedPreferences;

import com.sdp.appazul.classes.LocationFilter;
import com.sdp.appazul.classes.PaymentDetails;

public class AzulApplication extends Application {

    private SharedPreferences prefs;
    private String tcpKey = "";
    private String accToken = "";
    private LocationFilter locationFilter;
    private PaymentDetails details;
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


    public LocationFilter getLocationFilter() {
        return locationFilter;
    }

    public void setLocationFilter(LocationFilter locationFilter) {
        this.locationFilter = locationFilter;
    }

    public PaymentDetails getDetails() {
        return details;
    }

    public void setDetails(PaymentDetails details) {
        this.details = details;
    }
}
