package com.example.dee_kay.myapplication.WcfObjects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DEE-KAY on 2017/05/06.
 */
public class Output
{
    public String textValue;
    public String Comfirmation;

    public int User_ID;
    public User user;

    public Contact contact;
    public uProfile profile;
    public Wallet wallet;

    public List<Parking> parkingList;
    public Output()
    {
        this.parkingList = new ArrayList<>();
        this.user = new User();
        this.User_ID = 0;


        this.contact = new Contact();
        this.profile = new uProfile();
        this.wallet = new Wallet();
    }
}
