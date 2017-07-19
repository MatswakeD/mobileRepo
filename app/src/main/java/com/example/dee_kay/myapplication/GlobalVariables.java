package com.example.dee_kay.myapplication;

import android.app.Application;

/**
 * Created by DEE-KAY on 2017/05/16.
 */
public class GlobalVariables extends Application
{
    private String userID ="empty";
    private String parking_ID= "empty";
    private String LoggedIN = "empty";
    private String LasrName = "empty", Firstname ="empty";
    private static GlobalVariables singleton = null;


    public static GlobalVariables getSingleton()
    {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setParking_ID(String parking_ID) {
        this.parking_ID = parking_ID;
    }

    public String getParking_ID() {
        return parking_ID;
    }

    public String getLasrName() {
        return LasrName;
    }

    public void setLasrName(String lasrName) {
        LasrName = lasrName;
    }

    public String getFirstname() {
        return Firstname;
    }

    public void setFirstname(String firstname) {
        Firstname = firstname;
    }

    public String getLoggedIN() {
        return LoggedIN;
    }

    public void setLoggedIN(String loggedIN) {
        LoggedIN = loggedIN;
    }
}
