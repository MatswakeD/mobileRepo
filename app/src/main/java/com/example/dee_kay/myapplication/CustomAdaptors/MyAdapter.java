package com.example.dee_kay.myapplication.CustomAdaptors;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Switch;

import com.example.dee_kay.myapplication.Tab_1;
import com.example.dee_kay.myapplication.Tab_2;
import com.example.dee_kay.myapplication.Tab_3;
import com.example.dee_kay.myapplication.Tab_5_Vehicle;

import static com.example.dee_kay.myapplication.TabFragment.int_items;

/**
 * Created by DEE-KAY on 2017/04/15.
 */

public class MyAdapter extends FragmentPagerAdapter
{
    private int tabs;
    public MyAdapter(FragmentManager fm, int tabs) {
        super(fm);
        this.tabs = tabs;
    }


    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0: {
                return new Tab_1();
            }
            case 1: {
                return new Tab_2();
            }
            case 2: {

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
//                return "Profile";
//            }
//            case 1: {
//                return "Credits";
//            }
//            case 2: {
//
//            }
//
//
//        }
//        return null;
//    }

}