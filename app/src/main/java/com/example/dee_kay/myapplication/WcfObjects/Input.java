package com.example.dee_kay.myapplication.WcfObjects;

import java.io.Serializable;

/**
 * Created by DEE-KAY on 2017/05/06.
 */

public class Input implements Serializable
{

    public static final String AZURE_URL = "http://eparkingservices.cloudapp.net/Service1.svc";
    public User user;
    public Contact contact;
    public Wallet wallet;
    public Input()
    {
        this.user = new User();
        this.contact = new Contact();
        this.wallet = new Wallet();
    }


}

