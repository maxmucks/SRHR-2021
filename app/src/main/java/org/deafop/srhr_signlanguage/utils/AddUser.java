package org.deafop.srhr_signlanguage.utils;

public class AddUser {
    private String userContactNumber;
    private String userGender;
    private String userName;

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String str) {
        this.userName = str;
    }

    public String getUserContactNumber() {
        return this.userContactNumber;
    }

    public void setEmployeeContactNumber(String str) {
        this.userContactNumber = str;
    }

    public String getUserGender() {
        return this.userGender;
    }

    public void setUserGender(String str) {
        this.userGender = str;
    }
}
