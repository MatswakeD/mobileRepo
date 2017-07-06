package com.example.dee_kay.myapplication;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


/**
 * Created by Tshepo on 11/12/2016.
 */

public class MainPager extends FragmentStatePagerAdapter {

    //counts number of tabs.
    private int tabCount;

    public MainPager(FragmentManager fm, int numTabs)
    {
        super(fm);
        this.tabCount = numTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position)
        {
            case 0:
                return new Tab_1();

            case 1:
                return new Tab_2();

            default:
                return null;
        }

        //return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
