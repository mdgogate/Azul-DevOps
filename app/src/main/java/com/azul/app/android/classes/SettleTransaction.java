package com.azul.app.android.classes;

public class SettleTransaction {
    String CardNumber;
    String cardType;
    String TrnDateTime;
    String CardName;
    String ApprovalNo;
    String Amount;
    String TrnType;
    String TerminalId;
    String LotNo;
    String ReferenceNo;
    String settlementDate;

    public SettleTransaction(String cardNumber,String cardType, String trnDateTime, String CardName, String approvalNo, String amount, String trnType, String terminalId, String lotNo, String referenceNo, String settlementDate) {
        CardNumber = cardNumber;
        this.cardType = cardType;
        TrnDateTime = trnDateTime;
        this.CardName = CardName;
        ApprovalNo = approvalNo;
        Amount = amount;
        TrnType = trnType;
        TerminalId = terminalId;
        LotNo = lotNo;
        ReferenceNo = referenceNo;
        this.settlementDate = settlementDate;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardNumber() {
        return CardNumber;
    }

    public void setCardNumber(String cardNumber) {
        CardNumber = cardNumber;
    }

    public String getTrnDateTime() {
        return TrnDateTime;
    }

    public void setTrnDateTime(String trnDateTime) {
        TrnDateTime = trnDateTime;
    }

    public String getCardName() {
        return CardName;
    }

    public void setCardName(String cardName) {
        CardName = cardName;
    }

    public String getApprovalNo() {
        return ApprovalNo;
    }

    public void setApprovalNo(String approvalNo) {
        ApprovalNo = approvalNo;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getTrnType() {
        return TrnType;
    }

    public void setTrnType(String trnType) {
        TrnType = trnType;
    }

    public String getTerminalId() {
        return TerminalId;
    }

    public void setTerminalId(String terminalId) {
        TerminalId = terminalId;
    }

    public String getLotNo() {
        return LotNo;
    }

    public void setLotNo(String lotNo) {
        LotNo = lotNo;
    }

    public String getReferenceNo() {
        return ReferenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        ReferenceNo = referenceNo;
    }

    public String getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(String settlementDate) {
        this.settlementDate = settlementDate;
    }
}
