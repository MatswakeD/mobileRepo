package com.example.dee_kay.myapplication.CustomAdaptors;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.dee_kay.myapplication.Home_Map;
import com.example.dee_kay.myapplication.Tab_ParkingList;

import static com.example.dee_kay.myapplication.Host_Home_Tab.int_items;


/**
 * Created by DEE-KAY on 2017/07/05.
 */

public class ParkingTabAdapter extends FragmentStatePagerAdapter
{

    private int tabs;

    public ParkingTabAdapter(FragmentManager fm, int tabs) {
        super(fm);
        this.tabs = tabs;
    }


    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0: {
                return new Home_Map();
            }
            case 1: {
                return new Tab_ParkingList();
            }


        }
        return null;
    }


    @Override
    public int getCount() {
        return tabs;
    }

//    public CharSequence getPageTitle(int position) {
//        switch (position) {
//            case 0: {
//                return "Home Map";
//            }
//            case 1: {
//                return "Parking List";
//            }
//
//        }
//        return null;
//    }


}
