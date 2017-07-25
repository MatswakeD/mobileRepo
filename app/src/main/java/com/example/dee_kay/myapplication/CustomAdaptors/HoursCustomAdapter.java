package com.example.dee_kay.myapplication.CustomAdaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dee_kay.myapplication.R;
import com.example.dee_kay.myapplication.WcfObjects.Hours;

/**
 * Created by DEE-KAY on 19 Jul 2017.
 */

public class HoursCustomAdapter extends ArrayAdapter<Hours>
{
    public HoursCustomAdapter(Context context, Hours[] Hours ) {
        super(context, R.layout.custom_layout,Hours);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_hours_layout,parent,false);

        String Hours = getItem(position).hours ;
        Double hoursFee = getItem(position).HourFee;


        TextView tv_hours = (TextView) customView.findViewById(R.id.tv_hours);
        TextView tv_hoursFee = (TextView) customView.findViewById(R.id.tv_hourFee);

        //imageView.setImageResource(R.drawable.ic_import_contacts_black_24dp);
        tv_hours.setText(Hours );
        tv_hoursFee.setText("R " + hoursFee);

        return customView;
    }
}
