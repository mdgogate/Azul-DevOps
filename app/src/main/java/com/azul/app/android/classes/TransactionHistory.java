package com.azul.app.android.classes;

public class TransactionHistory {

     String authorizationCode;
     String referenceNo;
     String location;
     String time;
     String amount;
     String transactionDate;
     String merchantId;

    public TransactionHistory(String authorizationCode, String referenceNo, String location, String time, String amount, String merchantId,String date) {
        this.authorizationCode = authorizationCode;
        this.referenceNo = referenceNo;
        this.location = location;
        this.time = time;
        this.amount = amount;
        this.merchantId = merchantId;
        this.transactionDate = date;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }
}
