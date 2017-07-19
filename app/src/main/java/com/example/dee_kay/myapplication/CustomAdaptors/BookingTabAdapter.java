package com.example.dee_kay.myapplication.CustomAdaptors;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.dee_kay.myapplication.Tab_1;
import com.example.dee_kay.myapplication.Tab_2;
import com.example.dee_kay.myapplication.Tab_3;
import com.example.dee_kay.myapplication.Tab_4;

import static com.example.dee_kay.myapplication.Booking_TabFragment.items;
import static com.example.dee_kay.myapplication.TabFragment.int_items;

/**
 * Created by DEE-KAY on 2017/06/25.
 */

public class BookingTabAdapter extends FragmentPagerAdapter {
    public BookingTabAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: {
                return new Tab_4();
            }
            case 1: {
                return new Tab_3();
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return items;
    }

    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: {
                return "Active Bookings";
            }
            case 1: {
                return "Old Bookings";
            }

        }
        return null;
    }
}
