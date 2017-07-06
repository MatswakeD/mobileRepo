package com.example.dee_kay.myapplication;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Tshepo on 11/12/2016.
 */

public class Tab_Twitter extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_twitter, container, false);


        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.twitter_swipe_down_layout);
        //Toast.makeText(getContext(), "TAB 1: TWITTER", Toast.LENGTH_LONG).show();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout();
            }
        });

        return view;
    }



    private void refreshLayout()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 1000);
    }
}
