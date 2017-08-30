package com.example.dee_kay.myapplication;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dee_kay.myapplication.CustomAdaptors.BookingCustomAdapter;
import com.example.dee_kay.myapplication.WcfObjects.Book;
import com.example.dee_kay.myapplication.WcfObjects.Input;
import com.example.dee_kay.myapplication.WcfObjects.Output;
import com.example.dee_kay.myapplication.WcfObjects.Parking;
import com.threepin.fireexit_wcf.Configurator;
import com.threepin.fireexit_wcf.FireExitClient;

import java.util.List;


/**
 * This class will be used to show active bookings
 * Active bookings
 */
public class Tab_4 extends Fragment {

    private GlobalVariables gv;
    Book[] BOOKINGS = null;
    List<Book> Booking_list = null;
    ListView listView;

    Input input;
    Output output;
    Handler handler;

    Book book;
    boolean isCancelled = false;

    public Tab_4() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab_4, container, false);
        handler = new Handler();
        input = new Input();
        gv = ((GlobalVariables)getActivity().getApplicationContext());
        input.book.User_ID = gv.getUserID();

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_old_bookings);
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
        listView = (ListView) getView().findViewById(R.id.listView_ActiveBookings);

        Booking_list = output.Booking_List_Active;
        int size = Booking_list.size();
        BOOKINGS = new Book[size];
        for(int i=0; i<size; i++ )
        {
            BOOKINGS[i] = Booking_list.get(i);
        }

        ArrayAdapter<Book> listviewAdapter = new BookingCustomAdapter(getActivity(),BOOKINGS);
        listView.setAdapter(listviewAdapter);

        LongClickCallBack();
    }

    /**\
     * For handling list item when they are clicked
     * for cancelling the active booking
     */
    private void LongClickCallBack()
    {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View viewClicked,final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Do wish to cancel this booking !!")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                book = (Book) parent.getItemAtPosition(position);
                                Toast.makeText(getActivity(), String.valueOf(book.Booking_ID), Toast.LENGTH_LONG).show();
                                isCancelled = true;
                                input.book.Booking_ID = book.Booking_ID;
                                new myAsync().execute();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("Cancel Active Booking");
                alert.show();


                return true;
            }
        });

    }


    /**
     * For getting data from the db
     */
    class myAsync extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params)
        {

            FireExitClient client = new FireExitClient(Input.AZURE_URL);

            if(isCancelled == false)
            {
                client.configure(new Configurator("http://tempuri.org/","IService1","GetActiveBookings"));


                //passing the input class as a parameter to the service
                client.addParameter("request",input);

                output = new Output();
                try {
                    output = client.call(output);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }else
            {
                client.configure(new Configurator("http://tempuri.org/","IService1","CancelBooking"));

             //Passing the bookingID to the cancel booking method in the server
                client.addParameter("request",input);

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

                    if(out.Booking_List_Active != null) {
                        //"For searching a specific location
                        plotParking();
                    }
                    else if (isCancelled == true)
                    {
                        isCancelled = false;
                        Toast.makeText(getActivity(), "Booking Cancelled Successfully !! ", Toast.LENGTH_LONG).show();
                    }else
                    {
                        Toast.makeText(getActivity(), "There are no active bookings", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }




}
