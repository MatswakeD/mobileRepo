package com.example.dee_kay.myapplication.WcfObjects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DEE-KAY on 2017/05/06.
 */
public class Output
{
    public String Comfirmation;

    public String transaction;
    public Double parkingRates;

    public int User_ID;
    public User user;

    public Contact contact;
    public uProfile profile;
    public Wallet wallet;
    public Parking parking;
    public VehicleJson vehicle ;
    public List<Parking> parkingList;
    public List<Book> Booking_List_Old;
    public List<Book> Booking_List_Active;
    public List<Hours> HoursList;
    public List<ParkingTransc> parkingTransc;


    public String INorOUT;
    public String intTime;

    public String timeSpent;
    public int parking_ID;
    public String Nobays;

    public Output()
    {
        this.parkingList = new ArrayList<>();
        this.Booking_List_Old = new ArrayList<>();
        this.Booking_List_Active = new ArrayList<>();
        this.HoursList = new ArrayList<>();
        this.parkingTransc = new ArrayList<>();

        this.user = new User();
        this.User_ID = 0;


        this.parking = new Parking();
        this.contact = new Contact();
        this.profile = new uProfile();
        this.wallet = new Wallet();
        this.vehicle = new VehicleJson();
    }
}
