package com.azul.app.android.classes;

public class Model {
    private String amount;
    private String month;
    private String timeFrame;
    private String netSalesAmountNumber;
    private String totalDiscountNumber;
    private String quantityNumber;


    public Model(String amount, String month, String timeFrame, String netSalesAmountNumber, String totalDiscountNumber, String quantityNumber) {
        this.amount = amount;
        this.month = month;
        this.timeFrame = timeFrame;
        this.netSalesAmountNumber = netSalesAmountNumber;
        this.totalDiscountNumber = totalDiscountNumber;
        this.quantityNumber = quantityNumber;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getTimeFrame() {
        return timeFrame;
    }

    public void setTimeFrame(String timeFrame) {
        this.timeFrame = timeFrame;
    }

    public String getNetSalesAmountNumber() {
        return netSalesAmountNumber;
    }

    public void setNetSalesAmountNumber(String netSalesAmountNumber) {
        this.netSalesAmountNumber = netSalesAmountNumber;
    }

    public String getTotalDiscountNumber() {
        return totalDiscountNumber;
    }

    public void setTotalDiscountNumber(String totalDiscountNumber) {
        this.totalDiscountNumber = totalDiscountNumber;
    }

    public String getQuantityNumber() {
        return quantityNumber;
    }

    public void setQuantityNumber(String quantityNumber) {
        this.quantityNumber = quantityNumber;
    }
}
