package com.example.dee_kay.myapplication;

import android.app.Application;

/**
 * Created by DEE-KAY on 2017/05/16.
 */
public class GlobalVariables extends Application
{
    private String userID ="empty";
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
}
