package com.sdp.appazul.classes;

public class PaymentTransactions {
    String linkId;
    String amount;
    String clientName;
    String paymentDate;
    String transactionStatus;
    String payedAuthorizationNumber;
    String status;
    String transactionResponse;
    String createdBy;
    String currency;


    public PaymentTransactions() {
    }

    public PaymentTransactions(PaymentTransactions transactions) {
        this.linkId = transactions.getLinkId();
        this.amount = transactions.getAmount();
        this.clientName = transactions.getClientName();
        this.paymentDate = transactions.getPaymentDate();
        this.transactionStatus = transactions.getTransactionStatus();
        this.payedAuthorizationNumber = transactions.getPayedAuthorizationNumber();
        this.status = transactions.getStatus();
        this.transactionResponse = transactions.getTransactionResponse();
        this.createdBy = transactions.getCreatedBy();
        this.currency = transactions.getCurrency();
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getPayedAuthorizationNumber() {
        return payedAuthorizationNumber;
    }

    public void setPayedAuthorizationNumber(String payedAuthorizationNumber) {
        this.payedAuthorizationNumber = payedAuthorizationNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionResponse() {
        return transactionResponse;
    }

    public void setTransactionResponse(String transactionResponse) {
        this.transactionResponse = transactionResponse;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
