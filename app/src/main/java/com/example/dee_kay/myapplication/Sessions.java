package com.example.dee_kay.myapplication;

/**
 * Created by DEE-KAY on 2017/05/15.
 */

public class Sessions
{
    private int Userid;
    public Sessions(int UserId)
    {
        this.Userid = UserId;
    }

    public Sessions()
    {

    }

    public int getUserid() {
        return Userid;
    }

    public void setUserid(int userid) {
        Userid = userid;
    }
}
