package com.example.dee_kay.myapplication;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dee_kay.myapplication.CustomAdaptors.BookingTabAdapter;
import com.example.dee_kay.myapplication.CustomAdaptors.ParkingTabAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class Host_Home_Tab extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 2;

    public Host_Home_Tab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_host__home__tab, container, false);



        tabLayout = (TabLayout)v.findViewById(R.id.ParkingTAB);
        viewPager = (ViewPager)v.findViewById(R.id.viewpager_parking);


        //set an adapter
        viewPager.setAdapter(new ParkingTabAdapter(getChildFragmentManager()));

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });


        return v;
    }

}
