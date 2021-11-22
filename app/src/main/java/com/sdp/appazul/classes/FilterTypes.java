package com.sdp.appazul.classes;

public class FilterTypes {

    String filterName;
    String filterType;
    String isSelected;

    public FilterTypes() {
    }

    public FilterTypes(String filterName, String filterType, String isSelected) {
        this.filterName = filterName;
        this.filterType = filterType;
        this.isSelected = isSelected;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public String getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(String isSelected) {
        this.isSelected = isSelected;
    }
}
