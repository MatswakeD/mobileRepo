package com.example.dee_kay.myapplication.CustomAdaptors;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.dee_kay.myapplication.R;


/**
 * Created by DEE-KAY on 2017/07/05.
 */

public class AutoCompleteAdapter extends ArrayAdapter<String>
{
    String [] PARKING_NAMES;
    public AutoCompleteAdapter(Context context, String [] PARKING_NAMES ) {
        super(context, R.layout.search_row,PARKING_NAMES);
        this.PARKING_NAMES = PARKING_NAMES;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.search_row,parent,false);


        String parking_name = getItem(position);

        TextView tv_parkingName = (TextView) customView.findViewById(R.id.tv_searchRow);

        tv_parkingName.setText(parking_name);


        return customView;
    }
}
