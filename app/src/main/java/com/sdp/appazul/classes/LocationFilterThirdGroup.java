package com.sdp.appazul.classes;

public class LocationFilterThirdGroup {
    private String name;
    private String merchantId;
    private String parent;
    private String locationType;
    private String taxExempt;
    private String parentCode;
    private String parentLocationId;
    private String currency;

    public LocationFilterThirdGroup() {
    }

    public LocationFilterThirdGroup(String parentLocationId, String parentCode, String name, String merchantId, String parent, String locationType, String tax) {
        this.parentLocationId = parentLocationId;
        this.name = name;
        this.merchantId = merchantId;
        this.parent = parent;
        this.locationType = locationType;
        this.taxExempt = tax;
        this.parentCode = parentCode;
    }

    public LocationFilterThirdGroup(LocationFilterThirdGroup thirdGroup) {
        this.parentLocationId = thirdGroup.getParentLocationId();
        this.name = thirdGroup.getName();
        this.merchantId = thirdGroup.getMerchantId();
        this.parent = thirdGroup.getParent();
        this.locationType = thirdGroup.getLocationType();
        this.taxExempt = thirdGroup.getTaxExempt();
        this.parentCode = thirdGroup.getParentCode();
        this.currency = thirdGroup.getCurrency();
    }

    public LocationFilterThirdGroup(String parentLocationId, String name, String merchantId, String parent, String locationType) {
        this.parentLocationId = parentLocationId;
        this.parentCode = parentCode;
        this.name = name;
        this.merchantId = merchantId;
        this.parent = parent;
        this.locationType = locationType;
    }

    public String getParentLocationId() {
        return parentLocationId;
    }

    public void setParentLocationId(String parentLocationId) {
        this.parentLocationId = parentLocationId;
    }

    public String getTaxExempt() {
        return taxExempt;
    }

    public void setTaxExempt(String taxExempt) {
        this.taxExempt = taxExempt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getStringAmount() {
        return String.format(merchantId);
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
