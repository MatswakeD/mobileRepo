package com.example.dee_kay.myapplication;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dee_kay.myapplication.CustomAdaptors.BookingCustomAdapter;
import com.example.dee_kay.myapplication.WcfObjects.Book;
import com.example.dee_kay.myapplication.WcfObjects.Input;
import com.example.dee_kay.myapplication.WcfObjects.Output;
import com.threepin.fireexit_wcf.Configurator;
import com.threepin.fireexit_wcf.FireExitClient;

import java.util.List;


/**
 * Class handles old/expired bookings
 */
public class Tab_3 extends Fragment {


    private GlobalVariables gv;
    Book[] BOOKINGS = null;
    List<Book> Booking_list = null;
    ListView listView;

    Input input;
    Output output;
    Handler  handler;

    public Tab_3() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        handler = new Handler();
        input = new Input();
        gv = ((GlobalVariables)getActivity().getApplicationContext());
        input.book.User_ID = gv.getUserID();
        View v = inflater.inflate(R.layout.fragment_tab_3, container, false);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_active_bookings);
       swipeRefreshLayout.setColorSchemeResources(R.color.refresh1,R.color.refresh2,R.color.refresh);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        new myAsync().execute();
                    }
                }, 2000);
            }
        });


        //Getting the booking of this user from the db
        new myAsync().execute();
        return v;
    }

    /**
     * For placing the parking onto the list view
     */
    protected void plotParking()
    {
        //Merging the parking list into an array
        listView = (ListView) getView().findViewById(R.id.listView);

        Booking_list = output.Booking_List_Old;
        int size = Booking_list.size();
        BOOKINGS = new Book[size];
        for(int i=0; i<size; i++ )
        {
            BOOKINGS[i] = Booking_list.get(i);
        }

        ArrayAdapter<Book> listviewAdapter = new BookingCustomAdapter(getActivity(),BOOKINGS);
        listView.setAdapter(listviewAdapter);

    }

    /**
     * For getting data from the db
     */
    class myAsync extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params)
        {

                FireExitClient client = new FireExitClient(Input.AZURE_URL);
                client.configure(new Configurator("http://tempuri.org/","IService1","GetBookings"));

                //passing the input class as a parameter to the service
                client.addParameter("request",input);

                output = new Output();
                try {
                    output = client.call(output);
                } catch (Exception e) {
                    e.printStackTrace();
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

                    if(out.Booking_List_Old != null) {
                        //"For searching a specific location
                         plotParking();
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "There are no bookings you have made", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}
