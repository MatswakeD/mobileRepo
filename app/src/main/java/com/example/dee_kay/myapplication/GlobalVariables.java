package com.example.dee_kay.myapplication;

import android.app.Application;

/**
 * Created by DEE-KAY on 2017/05/16.
 */
public class GlobalVariables extends Application
{
    private String userID ="empty";
    private String parking_ID= "empty";
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
}
