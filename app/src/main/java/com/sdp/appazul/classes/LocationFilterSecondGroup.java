package com.sdp.appazul.classes;

public class LocationFilterSecondGroup {
    private String parentLocationId;
    private String name;
    private String code;
    private String currency;

    public LocationFilterSecondGroup(String mId, String name, String code) {
        this.parentLocationId = mId;
        this.name = name;
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getParentLocationId() {
        return parentLocationId;
    }

    public void setParentLocationId(String parentLocationId) {
        this.parentLocationId = parentLocationId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
