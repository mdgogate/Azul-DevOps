package com.azul.app.android.classes;

public class LocationFilterThirdGroup {
    private String name;
    private String merchantId;
    private String parent;
    private String locationType;

    public LocationFilterThirdGroup(String name, String merchantId, String parent, String locationType) {
        this.name = name;
        this.merchantId = merchantId;
        this.parent = parent;
        this.locationType = locationType;
    }


    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getStringAmount(){
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
}
