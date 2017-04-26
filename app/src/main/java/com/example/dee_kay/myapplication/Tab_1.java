package com.example.dee_kay.myapplication;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class Tab_1 extends Fragment {


    //Empty contractor
    public Tab_1() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab_1, container, false);

        FloatingActionButton myFab = (FloatingActionButton) v.findViewById(R.id.fab_EditProfile);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        return v;
    }

}
