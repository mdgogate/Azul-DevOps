package com.sdp.appazul.classes;

public class TransactionHistory {

     String referenceNo;
     String location;
     String time;
     String trDate;
     String amount;
     String transactionDate;
     String merchantId;



    public TransactionHistory( String referenceNo, String location, String trDate,String time, String amount, String merchantId, String date) {

        this.referenceNo = referenceNo;
        this.location = location;
        this.time = time;
        this.trDate = trDate;
        this.amount = amount;
        this.merchantId = merchantId;
        this.transactionDate = date;
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

    public String getTrDate() {
        return trDate;
    }

    public void setTrDate(String trDate) {
        this.trDate = trDate;
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
