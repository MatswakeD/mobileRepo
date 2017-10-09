package com.example.dee_kay.myapplication;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dee_kay.myapplication.CustomAdaptors.ParkingHistorAdapter;
import com.example.dee_kay.myapplication.CustomAdaptors.ParkingList_Adapeter_Layout;
import com.example.dee_kay.myapplication.WcfObjects.Input;
import com.example.dee_kay.myapplication.WcfObjects.Output;
import com.example.dee_kay.myapplication.WcfObjects.Parking;
import com.example.dee_kay.myapplication.WcfObjects.ParkingTransc;
import com.threepin.fireexit_wcf.Configurator;
import com.threepin.fireexit_wcf.FireExitClient;

import java.io.IOException;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ParkingHistory extends Fragment {


    private View mView;
    private Output output;
    private Input input;
    private Handler handler;
    private GlobalVariables gv;
    private List<ParkingTransc> trans_list;
    private ParkingTransc HISTORY [];

    public ParkingHistory() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_parking_history, container, false);

        gv = (GlobalVariables) getActivity().getApplication(); //holds the user id
        handler = new Handler();

        new myAsync().execute();
        return mView;
    }


    /*
* Used for getting parking(s) from the data-store
* */
    class myAsync extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {

            FireExitClient client = new FireExitClient(Input.AZURE_URL);

            client.configure(new Configurator("http://tempuri.org/", "IService1", "getParkingHistory"));

            input = new Input();
            input.user.User_ID = gv.getUserID(); //getting user id

            //passing the input class as a parameter to the service
            client.addParameter("request", input);

            output = new Output();

            try {
                output = client.call(output);
            } catch (Exception e) {
                e.printStackTrace();
            }


            return output;
        }

        @Override
        protected void onPostExecute(final Object o) {
            super.onPostExecute(o);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Output out = (Output) o;

                    if(out.parkingTransc.size() != 0)
                    {

                        //Populating the list
                        plotParking();
                    }else
                    {
                        Snackbar.make(mView,"You do not have any parking history transactions made", Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    /**
     * For placing the parking onto the list view
     */
    protected void plotParking()
    {
        //Merging the parking list into an array
        ListView listView = (ListView) getView().findViewById(R.id.ParkingHistory_list);

        trans_list = output.parkingTransc;
        int size = trans_list.size();
        HISTORY = new ParkingTransc[size];
        for(int i=0; i<size; i++ )
        {
            HISTORY[i] = trans_list.get(i);
        }

        ArrayAdapter<ParkingTransc> historyAdapter = new ParkingHistorAdapter(getActivity(),HISTORY);
        listView.setAdapter(historyAdapter);

    }

}
