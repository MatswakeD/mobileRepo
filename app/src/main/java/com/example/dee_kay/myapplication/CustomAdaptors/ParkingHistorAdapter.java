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
import com.example.dee_kay.myapplication.WcfObjects.ParkingTransc;

/**
 * Created by DEE-KAY on 2017/07/05.
 */

public class ParkingHistorAdapter extends ArrayAdapter<ParkingTransc>
{
    public ParkingHistorAdapter(Context context, ParkingTransc [] HISTORY ) {
        super(context, R.layout.history_custom_layout,HISTORY);
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.history_custom_layout,parent,false);

        String parking_name = getItem(position).ParkingName;
        String parking_city = getItem(position).ParkingSubCity;
        String parking_fee = getItem(position).ParkingFee + "";
        String parkingDateTime = getItem(position).DateTime + "";



        ImageView imageView = (ImageView) customView.findViewById(R.id.ParkingImage);
        TextView tv_parkingName = (TextView) customView.findViewById(R.id.tv_parkingName_parkingList);
        TextView tv_parking_city = (TextView) customView.findViewById(R.id.tv_parkingCity);

        TextView tv_parkingfee = (TextView) customView.findViewById(R.id.tv_paidAmount);
        TextView tv_parkingDateTime = (TextView) customView.findViewById(R.id.tv_dateTime);



        imageView.setImageResource(R.drawable.ic_place_black_24dp);
        tv_parkingName.setText(parking_name);
        tv_parking_city.setText(parking_city);
        tv_parkingfee.setText("Parking charges : R"+ parking_fee);
        tv_parkingDateTime.setText("Parking Date :"+parkingDateTime);

        return customView;
    }
}
