package com.sdp.appazul.classes;

public class TapOnPhone {

    String cardNumber;
    String approveNumber;
    String amount;
    String transactionDate;
    String merchantId;
    String cardType;
    String transactionType;
    String TrnType;
    String locName;

    public TapOnPhone() {
    }

    public TapOnPhone(String cardNumber, String approveNumber, String amount, String transactionDate, String merchantId, String cardType, String transactionType) {
        this.cardNumber = cardNumber;
        this.approveNumber = approveNumber;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.merchantId = merchantId;
        this.cardType = cardType;
        this.transactionType = transactionType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getApproveNumber() {
        return approveNumber;
    }

    public void setApproveNumber(String approveNumber) {
        this.approveNumber = approveNumber;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
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

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getTrnType() {
        return TrnType;
    }

    public void setTrnType(String trnType) {
        TrnType = trnType;
    }

    public String getLocName() {
        return locName;
    }

    public void setLocName(String locName) {
        this.locName = locName;
    }
}
