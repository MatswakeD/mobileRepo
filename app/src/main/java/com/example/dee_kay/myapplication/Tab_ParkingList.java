package com.example.dee_kay.myapplication;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dee_kay.myapplication.CustomAdaptors.BookingCustomAdapter;
import com.example.dee_kay.myapplication.CustomAdaptors.ParkingList_Adapeter_Layout;
import com.example.dee_kay.myapplication.R;
import com.example.dee_kay.myapplication.WcfObjects.Input;
import com.example.dee_kay.myapplication.WcfObjects.Output;
import com.example.dee_kay.myapplication.WcfObjects.Parking;
import com.threepin.fireexit_wcf.Configurator;
import com.threepin.fireexit_wcf.FireExitClient;

import java.io.IOException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Tab_ParkingList extends Fragment {



    Parking[] PARKING = null;
    List<Parking> parking_list = null;
    ListView listView;
    Parking parking;
     private boolean book = false;

    GlobalVariables gv;

    Output output;
    Handler handler;
    public Tab_ParkingList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab__parking_list, container, false);

        handler = new Handler();

        //Used for storing IDs globally
         gv = ((GlobalVariables)getActivity().getApplicationContext());

        //Getting the parking(s) from db
        new myAsync().execute();


        return v;
    }

    /**\
     * For handling list item when they are clicked
     */
    private void clickCallBack()
    {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {

                if(gv.getUserID().equals("empty"))
                {
                    Toast.makeText(getActivity(),"User not logged in",Toast.LENGTH_LONG).show();
                }else
                {
                    parking = (Parking) parent.getItemAtPosition(position);
                    gv.setParking_ID(parking.Parking_ID + "");
                    Toast.makeText(getActivity(), String.valueOf(gv.getParking_ID()), Toast.LENGTH_LONG).show();

                    //Switching to a booking class
                    Intent i = new Intent(getActivity(), Booking.class);
                    startActivity(i);
                    ((Activity) getActivity()).overridePendingTransition(0,0);
                }
            }
        });

    }

    /**
     * For placing the parking onto the list view
     */
    protected void plotParking()
    {
        //Merging the parking list into an array
        listView = (ListView) getView().findViewById(R.id.Parking_list_view);

        parking_list = output.parkingList;
        int size = parking_list.size();
        PARKING = new Parking[size];
        for(int i=0; i<size; i++ )
        {
            PARKING[i] = parking_list.get(i);
        }

        ArrayAdapter<Parking> parkingAdapter = new ParkingList_Adapeter_Layout(getActivity(),PARKING);
        listView.setAdapter(parkingAdapter);


            clickCallBack();


    }

    /**
     * For getting data from the db
     */
    class myAsync extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params)
        {

            FireExitClient client = new FireExitClient(Input.AZURE_URL);


            if(book == false)
            {
                client.configure(new Configurator("http://tempuri.org/","IService1","GetParkings"));
                //passing the input class as a parameter to the service
                client.addParameter("request","");

                output = new Output();

                try {
                    output = client.call(output);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(book == true)
            {
                client.configure(new Configurator("http://tempuri.org/","IService1","Book"));
                //passing the input class as a parameter to the service
                client.addParameter("request","");

                output = new Output();

                try {
                    output = client.call(output);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return output;
        }

        @Override
        protected void onPostExecute( final Object o) {
            super.onPostExecute(o);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Output out = (Output)o;

                    if(out.parkingList.size() != 0) {
                        //"For searching a specific location
                        plotParking();
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "Could not retrieve parking(s), please try to load the page", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }


}
