package com.example.dee_kay.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dee_kay.myapplication.CustomAdaptors.BookingTabAdapter;
import com.example.dee_kay.myapplication.R;




public class Booking_TabFragment extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 2;

    public Booking_TabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_booking__tab, container, false);


        tabLayout = (TabLayout)v.findViewById(R.id.Bookingtabs);
        viewPager = (ViewPager)v.findViewById(R.id.viewpager);

        //set an adapter
        viewPager.setAdapter(new BookingTabAdapter(getChildFragmentManager()));

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        return v;
    }




}