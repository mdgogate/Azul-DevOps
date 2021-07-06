package com.sdp.appazul.classes;

public class SettleTransaction {
    String cardNumber;
    String cardType;
    String trnDateTime;
    String cardName;
    String approvalNo;
    String amount;
    String trnType;
    String terminalId;
    String lotNo;
    String referenceNo;
    String settlementDate;
    String currency;
    String time;
    String merchantId;

    public SettleTransaction(String mId, String currency, String cardNumber, String cardType, String trnDate, String transactionTime, String cardName, String approvalNo, String amount, String trnType, String terminalId, String lotNo, String referenceNo, String settlementDate) {
        this.merchantId = mId;
        this.currency = currency;
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.trnDateTime = trnDate;
        time = transactionTime;
        this.cardName = cardName;
        this.approvalNo = approvalNo;
        this.amount = amount;
        this.trnType = trnType;
        this.terminalId = terminalId;
        this.lotNo = lotNo;
        this.referenceNo = referenceNo;
        this.settlementDate = settlementDate;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getTrnDateTime() {
        return trnDateTime;
    }

    public void setTrnDateTime(String trnDateTime) {
        this.trnDateTime = trnDateTime;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getApprovalNo() {
        return approvalNo;
    }

    public void setApprovalNo(String approvalNo) {
        this.approvalNo = approvalNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTrnType() {
        return trnType;
    }

    public void setTrnType(String trnType) {
        this.trnType = trnType;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getLotNo() {
        return lotNo;
    }

    public void setLotNo(String lotNo) {
        this.lotNo = lotNo;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(String settlementDate) {
        this.settlementDate = settlementDate;
    }
}
