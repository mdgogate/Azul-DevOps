package com.sdp.appazul.classes;

public class PaymentDetails {

    String selectedLocationID;
    String selectedLocation;
    String selectedTrnType;
    String selectedOrderNumber;
    String selectedName;
    String selectedEmail;
    double enteredTaxAmount;
    double enteredTotalAmount;


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

    public double getEnteredTaxAmount() {
        return enteredTaxAmount;
    }

    public void setEnteredTaxAmount(double enteredTaxAmount) {
        this.enteredTaxAmount = enteredTaxAmount;
    }

    public double getEnteredTotalAmount() {
        return enteredTotalAmount;
    }

    public void setEnteredTotalAmount(double enteredTotalAmount) {
        this.enteredTotalAmount = enteredTotalAmount;
    }
}
