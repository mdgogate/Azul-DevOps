package com.sdp.appazul.classes;

public class PaymentDetails {

    String selectedLocationID;
    String selectedLocation;
    String selectedTrnType;
    String selectedOrderNumber;
    String selectedName;
    String selectedEmail;
    String enteredTaxAmount;
    double enteredTotalAmount;
    String currency;

    int switchFlag;


    public String getSelectedName() {
        return selectedName;
    }

    public void setSelectedName(String selectedName) {
        this.selectedName = selectedName;
    }

    public String getSelectedEmail() {
        return selectedEmail;
    }

    public void setSelectedEmail(String selectedEmail) {
        this.selectedEmail = selectedEmail;
    }

    public String getSelectedLocationID() {
        return selectedLocationID;
    }

    public void setSelectedLocationID(String selectedLocationID) {
        this.selectedLocationID = selectedLocationID;
    }

    public String getSelectedLocation() {
        return selectedLocation;
    }

    public void setSelectedLocation(String selectedLocation) {
        this.selectedLocation = selectedLocation;
    }

    public String getSelectedTrnType() {
        return selectedTrnType;
    }

    public void setSelectedTrnType(String selectedTrnType) {
        this.selectedTrnType = selectedTrnType;
    }

    public String getSelectedOrderNumber() {
        return selectedOrderNumber;
    }

    public void setSelectedOrderNumber(String selectedOrderNumber) {
        this.selectedOrderNumber = selectedOrderNumber;
    }

    public String getEnteredTaxAmount() {
        return enteredTaxAmount;
    }

    public void setEnteredTaxAmount(String enteredTaxAmount) {
        this.enteredTaxAmount = enteredTaxAmount;
    }

    public double getEnteredTotalAmount() {
        return enteredTotalAmount;
    }

    public void setEnteredTotalAmount(double enteredTotalAmount) {
        this.enteredTotalAmount = enteredTotalAmount;
    }

    public int getSwitchFlag() {
        return switchFlag;
    }

    public void setSwitchFlag(int switchFlag) {
        this.switchFlag = switchFlag;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
