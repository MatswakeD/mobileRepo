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
import com.example.dee_kay.myapplication.WcfObjects.Book;

/**
 * Created by DEE-KAY on 2017/05/08.
 */

public class BookingCustomAdapter extends ArrayAdapter<Book>
{

    public BookingCustomAdapter(Context context, Book[] BOOKINGS ) {
        super(context, R.layout.custom_layout,BOOKINGS);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_layout,parent,false);

        String parkingName = getItem(position).Parking_Name;
        String location = getItem(position).Parking_Subcity;
        String parkingFee = getItem(position).Parkng_Fee;
        String bookingDate = getItem(position).BookingDate;


        TextView tv_parkingName = (TextView) customView.findViewById(R.id.tv_parkingName);
        TextView tv_location = (TextView) customView.findViewById(R.id.tv_parkingLocation);
        TextView tv_parkingFee = (TextView) customView.findViewById(R.id.tv_parkingFee);
        TextView tv_bookingDate = (TextView) customView.findViewById(R.id.tv_bookingDate);

        //imageView.setImageResource(R.drawable.ic_import_contacts_black_24dp);
        tv_parkingName.setText(parkingName);
        tv_location.setText(location);
        tv_parkingFee.setText(parkingFee);
        tv_bookingDate.setText(bookingDate);


        return customView;
    }
}
