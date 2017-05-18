package com.example.dee_kay.myapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dee_kay.myapplication.CustomAdaptors.BookingCustomAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class Tab_3 extends Fragment {


    String[] NAMES = {"Big B","Dimakatso", "Athule", "Nancy", "Pinkie", "Kgantshi", "A",
            "B","C", "D", "E", "F", "G", "H", "I"};
    String[] LOCATIONS = {"A","B", "C", "D", "E", "F"};
    String[] DATES = {"1","2", "3", "4", "5", "6"};

    public Tab_3() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_tab_3, container, false);

        ListView listView = (ListView) v.findViewById(R.id.listView);

        ArrayAdapter<String> listviewAdapter = new BookingCustomAdapter(getActivity(),NAMES);

        listView.setAdapter(listviewAdapter);
    

        return v;
    }

   
}
