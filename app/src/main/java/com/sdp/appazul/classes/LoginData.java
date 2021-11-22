package com.sdp.appazul.classes;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginData {
    String un;
    String name;
    String lastName;
    String identType;
    String identNum;
    String birthDate;
    String occupation;
    String phone;
    String cellPhone;
    String email;
    String role;
    String groupIndustrialSector;
    String groupIdentType;
    String groupIdentNum;
    String groupName;
    String lastLoginDate;
    HashMap<String, ArrayList<String>> data;

    public String getUn() {
        return un;
    }

    public void setUn(String un) {
        this.un = un;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getIdentType() {
        return identType;
    }

    public void setIdentType(String identType) {
        this.identType = identType;
    }

    public String getIdentNum() {
        return identNum;
    }

    public void setIdentNum(String identNum) {
        this.identNum = identNum;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getGroupIndustrialSector() {
        return groupIndustrialSector;
    }

    public void setGroupIndustrialSector(String groupIndustrialSector) {
        this.groupIndustrialSector = groupIndustrialSector;
    }

    public String getGroupIdentType() {
        return groupIdentType;
    }

    public void setGroupIdentType(String groupIdentType) {
        this.groupIdentType = groupIdentType;
    }

    public String getGroupIdentNum() {
        return groupIdentNum;
    }

    public void setGroupIdentNum(String groupIdentNum) {
        this.groupIdentNum = groupIdentNum;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public HashMap<String, ArrayList<String>> getData() {
        return data;
    }

    public void setData(HashMap<String, ArrayList<String>> data) {
        this.data = data;
    }
}
