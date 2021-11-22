package com.sdp.appazul.classes;

public class Model {
    private String period;
    private String transactionSum;
    private String transactionCount;
    private String amountToReceive;
    private String discount;
    private String dateFrom;
    private String dateTo;
    private String currency;

    public Model() {
    }


    public Model(Model model) {
        this.period = model.getPeriod();
        this.transactionSum = model.getTransactionSum();
        this.transactionCount = model.getTransactionCount();
        this.amountToReceive = model.getAmountToReceive();
        this.discount = model.getDiscount();
        this.dateFrom = model.getDateFrom();
        this.dateTo = model.getDateTo();
        this.currency = model.getCurrency();
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getTransactionSum() {
        return transactionSum;
    }

    public void setTransactionSum(String transactionSum) {
        this.transactionSum = transactionSum;
    }

    public String getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(String transactionCount) {
        this.transactionCount = transactionCount;
    }

    public String getAmountToReceive() {
        return amountToReceive;
    }

    public void setAmountToReceive(String amountToReceive) {
        this.amountToReceive = amountToReceive;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
