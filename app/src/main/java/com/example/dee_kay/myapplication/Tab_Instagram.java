package com.example.dee_kay.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


/**
 * Created by Tshepo on 11/12/2016.
 */

public class Tab_Instagram extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    View view;
    LinearLayout linearLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_instagram, container, false);

        linearLayout = (LinearLayout) view.findViewById(R.id.twitter_layout);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.instagram_swipe_down_layout);

        //Toast.makeText(getContext(), "TAB 2: INSTAGRAM", Toast.LENGTH_LONG).show();
        linearLayout.setBackgroundColor(Color.CYAN);
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
        }, 2500);
    }
}
