package com.example.dee_kay.myapplication.CustomAdaptors;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Switch;

import com.example.dee_kay.myapplication.Tab_1;
import com.example.dee_kay.myapplication.Tab_2;
import com.example.dee_kay.myapplication.Tab_3;

import static com.example.dee_kay.myapplication.TabFragment.int_items;

/**
 * Created by DEE-KAY on 2017/04/15.
 */

public class MyAdapter extends FragmentPagerAdapter
{
    public MyAdapter(FragmentManager fm) {
        super(fm);
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

        }
        return null;
    }


    @Override
    public int getCount() {
        return int_items;
    }

    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: {
                return "Profile";
            }
            case 1: {
                return "Credits";
            }

        }
        return null;
    }

}