package com.example.dee_kay.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.dee_kay.myapplication.WcfObjects.Input;
import com.example.dee_kay.myapplication.WcfObjects.Output;
import com.threepin.fireexit_wcf.Configurator;
import com.threepin.fireexit_wcf.FireExitClient;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Booking extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {


    private Spinner spinner;
    private ArrayAdapter<CharSequence> adapter;
    private GlobalVariables gv;

    ImageView btn_start, btn_end;
    private Button btn_book;
    TextView tv_parkingName, tv_parkingCity,tv_parking_agent,tv_numOfbay, tv_chooseBaytype,tv_start,tv_end;
    int hour, minute,endHour,endMinute, hourFinal,minuteFinal, end_hourFinal,end_minuteFinal;

    private String BayType = "";
    private boolean book = false;

    private Output output;
    private Input input;
    private Handler handler;

    private String choosenBaytype, bookingStartTime,bookingEndTime;
    private boolean Booked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        gv = ((GlobalVariables)getBaseContext().getApplicationContext());
        handler =  new Handler();

        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this,R.array.StrTypeOfbay,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        tv_start = (TextView) findViewById(R.id.booking_startTime);
        tv_end = (TextView) findViewById(R.id.booking_endTime);
        tv_parkingName = (TextView) findViewById(R.id.parking_name);
        tv_parkingCity = (TextView) findViewById(R.id.parking_city);
        tv_parking_agent = (TextView) findViewById(R.id.parking_agent);

        tv_numOfbay= (TextView) findViewById(R.id.tv_numOfbays);
        btn_start = (ImageView) findViewById(R.id.btn_starTime);
        btn_end = (ImageView) findViewById(R.id.btn_endTime);
        btn_book = (Button) findViewById(R.id.btn_book);
        tv_chooseBaytype = (TextView) findViewById(R.id.bayType);


        btn_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tv_start.getText().length() != 0 || tv_end.getText().length() !=0) {
                    input = new Input();
                    input.book.User_ID = gv.getUserID();
                    input.book.BayType = choosenBaytype;
                    input.book.Parking_ID = gv.getParking_ID();
                    input.book.BookingStartTime = bookingStartTime;
                    input.book.BookingEndTime = bookingEndTime;
                    Booked = true;
                    new myAsync().execute();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Make sure choose your booking start/end times",Toast.LENGTH_LONG).show();
                }
            }
        });

        //Setting the adapter
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getBaseContext(),parent.getItemIdAtPosition(position)+ " selected",Toast.LENGTH_LONG).show();


                BayType = String.valueOf(parent.getItemIdAtPosition(position));

                int value = Integer.parseInt(BayType);
                if(value == 0)
                {
                    tv_chooseBaytype.setText("Special");
                    choosenBaytype = "Special";
                }
                else if(value == 1)
                {
                    tv_chooseBaytype.setText("Normal");
                    choosenBaytype = "Normal";
                }
                else
                {
                    tv_chooseBaytype.setText("Choose Bay Type");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Handling the timer images/buttons
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);

                //Set book to true when starting time has been clicked
                book = true;
                btn_end.setClickable(true);

                TimePickerDialog timePickerDialog = new TimePickerDialog(Booking.this,Booking.this,hour,minute,false);
                timePickerDialog.show();
            }
        });

        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar c = Calendar.getInstance();
                endHour = c.get(Calendar.HOUR_OF_DAY);
                endMinute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(Booking.this,Booking.this,hour,minute,false);
                timePickerDialog.show();
            }
        });

        new myAsync().execute();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        hourFinal = hourOfDay;
        minuteFinal = minute;

        end_hourFinal = hourOfDay;
        end_minuteFinal = minute;


        if (book == true) {
            tv_start.setText(hourFinal + ":" + minuteFinal);
            bookingStartTime = hourFinal + ":" + minuteFinal;
            book = false;
        } else if (book == false) {
            tv_end.setText(end_hourFinal + ":" + end_minuteFinal);
            bookingEndTime = end_hourFinal + ":" + end_minuteFinal;
        }


    }
        /**
         * For getting data from the db
         */
    class myAsync extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params)
        {
            if(Booked == false)
            {
                gv = ((GlobalVariables)getBaseContext().getApplicationContext());
                input = new Input();
                input.parking_id = gv.getParking_ID();
                FireExitClient client = new FireExitClient(Input.AZURE_URL);

                client.configure(new Configurator("http://tempuri.org/","IService1","GetSpeceficParking"));
                //passing the input class as a parameter to the service
                client.addParameter("request",input);

                output = new Output();

                try {
                    output = client.call(output);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Clearing input so that we should not send un-needed information
                input = null;
            }
            else if (Booked == true)
            {
                FireExitClient client = new FireExitClient(Input.AZURE_URL);
                client.configure(new Configurator("http://tempuri.org/","IService1","Book"));

                //passing the input class as a parameter to the service
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

                    if(out.parking != null) {
                        //"For searching a specific location

                        tv_parkingName.setText(out.parking.Parking_Name);
                        tv_parkingCity.setText(out.parking.Parking_City);
                        tv_numOfbay.setText(out.parking.Number_Of_bays + "");

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Could not retrieve parking information", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }


}
