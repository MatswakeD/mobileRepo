package com.example.dee_kay.myapplication.CustomAdaptors;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dee_kay.myapplication.R;

/**
 * Created by DEE-KAY on 2017/05/08.
 */

public class BookingCustomAdapter extends ArrayAdapter<String>
{

    public BookingCustomAdapter(Context context, String[] BOOKINGS ) {
        super(context, R.layout.custom_layout,BOOKINGS);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_layout,parent,false);

        String singleBookingItem = getItem(position);

        ImageView imageView = (ImageView) customView.findViewById(R.id.imageView);
        TextView tv_parkingName = (TextView) customView.findViewById(R.id.tv_parkingName);

       /// TextView tv_parkingLocation = (TextView) convertView.findViewById(R.id.tv_parkingLocation);
        //TextView tv_bookingTime = (TextView) convertView.findViewById(R.id.tv_bookingTime);

        imageView.setImageResource(R.drawable.ic_import_contacts_black_24dp);
        tv_parkingName.setText(singleBookingItem);

        return customView;
    }
}
