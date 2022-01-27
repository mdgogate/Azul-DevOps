package com.sdp.appazul.globals;

import android.app.Application;
import android.content.SharedPreferences;

import com.sdp.appazul.classes.CardLessPayment;
import com.sdp.appazul.classes.LocationFilter;
import com.sdp.appazul.classes.LoginData;
import com.sdp.appazul.classes.PaymentDetails;
import com.sdp.appazul.classes.TapOnPhone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import digital.paynetics.phos.sdk.entities.Transaction;


public class AzulApplication extends Application{

    private SharedPreferences prefs;
    private String tcpKey = "";
    private String accToken = "";
    private LocationFilter locationFilter;
    private PaymentDetails details;
    private List<String> featurePermissionsList = new ArrayList<>();
    private List<String> productPermissionsList = new ArrayList<>();
    private Map<String,String> defaultLocationQr = new HashMap<>();
    private Map<String,String> defaultLocation = new HashMap<>();
    private LoginData loginData;
    private HashMap<String, ArrayList<String>> loginDataList;
    private String authData = "";
    private String locationDataShare = "";
    private String vcr = "";
    private String rAccToken = "";
    private String pdfJson = "";
    private boolean isPdfAvailable = false;
    private CardLessPayment cardLessPayment;
    private TapOnPhone tap;
    List<TapOnPhone> tapOnPhoneList;
    private Transaction transaction;
    private Transaction declinedTransaction;

    public Transaction getDeclinedTransaction() {
        return declinedTransaction;
    }

    public void setDeclinedTransaction(Transaction declinedTransaction) {
        this.declinedTransaction = declinedTransaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public List<TapOnPhone> getTapOnPhoneList() {
        return tapOnPhoneList;
    }

    public void setTapOnPhoneList(List<TapOnPhone> tapOnPhoneList) {
        this.tapOnPhoneList = tapOnPhoneList;
    }

    public TapOnPhone getTap() {
        return tap;
    }

    public void setTap(TapOnPhone tap) {
        this.tap = tap;
    }

    public CardLessPayment getCardLessPayment() {
        return cardLessPayment;
    }

    public void setCardLessPayment(CardLessPayment cardLessPayment) {
        this.cardLessPayment = cardLessPayment;
    }

    public String getLocationDataShare() {
        return locationDataShare;
    }

    public void setLocationDataShare(String locationDataShare) {
        this.locationDataShare = locationDataShare;
    }

    public HashMap<String, ArrayList<String>> getLoginDataList() {
        return loginDataList;
    }

    public void setLoginDataList(HashMap<String, ArrayList<String>> loginDataList) {
        this.loginDataList = loginDataList;
    }



    public Map<String, String> getDefaultLocation() {
        return defaultLocation;
    }

    public void setDefaultLocation(Map<String, String> defaultLocation) {
        this.defaultLocation = defaultLocation;
    }

    public Map<String, String> getDefaultLocationQr() {
        return defaultLocationQr;
    }

    public void setDefaultLocationQr(Map<String, String> defaultLocationQr) {
        this.defaultLocationQr = defaultLocationQr;
    }

    public List<String> getProductPermissionsList() {
        return productPermissionsList;
    }

    public void setProductPermissionsList(List<String> productPermissionsList) {
        this.productPermissionsList = productPermissionsList;
    }

    public List<String> getFeaturePermissionsList() {
        return featurePermissionsList;
    }

    public void setFeaturePermissionsList(List<String> featurePermissionsList) {
        this.featurePermissionsList = featurePermissionsList;
    }

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

    public LoginData getLoginData() {
        return loginData;
    }

    public void setLoginData(LoginData loginData) {
        this.loginData = loginData;
    }

    public HashMap<String, ArrayList<String>> getListOfData() {
        return loginDataList;
    }

    public void setListOfData(HashMap<String, ArrayList<String>> loginDataList) {
        this.loginDataList = loginDataList;
    }

    ArrayList<LoginData> dataArrayList = new ArrayList<>();

    public ArrayList<LoginData> getDataArrayList() {
        return dataArrayList;
    }

    public void setDataArrayList(ArrayList<LoginData> dataArrayList) {
        this.dataArrayList = dataArrayList;
    }

    public String getAuthData() {
        return authData;
    }

    public void setAuthData(String authData) {
        this.authData = authData;
    }

    public String getVcr() {
        return vcr;
    }

    public void setVcr(String vcr) {
        this.vcr = vcr;
    }

    public String getrAccToken() {
        return rAccToken;
    }

    public void setrAccToken(String rAccToken) {
        this.rAccToken = rAccToken;
    }

    public boolean isPdfAvailable() {
        return isPdfAvailable;
    }

    public void setPdfAvailable(boolean pdfAvailable) {
        isPdfAvailable = pdfAvailable;
    }

    public String getPdfJson() {
        return pdfJson;
    }

    public void setPdfJson(String pdfJson) {
        this.pdfJson = pdfJson;
    }
}
