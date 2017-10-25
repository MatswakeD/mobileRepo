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
import com.example.dee_kay.myapplication.WcfObjects.Parking;

/**
 * Created by DEE-KAY on 2017/07/05.
 */

public class ParkingList_Adapeter_Layout extends ArrayAdapter<Parking>
{

    public ParkingList_Adapeter_Layout(Context context,Parking [] PARKING ) {
        super(context, R.layout.parking_list_custom_layout,PARKING);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.parking_list_custom_layout,parent,false);

        //Getting the parking information from the database
        String parking_name = getItem(position).Parking_Name;
        String parking_city = getItem(position).Parking_City;
        double congestion = getItem(position).Congestion;

        ImageView imageView = (ImageView) customView.findViewById(R.id.ParkingImage);
        TextView tv_parkingName = (TextView) customView.findViewById(R.id.tv_parkingName_parkingList);
        TextView tv_parking_city = (TextView) customView.findViewById(R.id.tv_parkingCity);
        TextView tv_congestion = (TextView) customView.findViewById(R.id.tv_numBay);


        //Plotting information on the custom layout
        imageView.setImageResource(R.drawable.ic_place_black_24dp);
        tv_parkingName.setText(parking_name);
        tv_parking_city.setText(parking_city);
        tv_congestion.setText("Congestion "+congestion + " %");

        return customView;
    }
}
