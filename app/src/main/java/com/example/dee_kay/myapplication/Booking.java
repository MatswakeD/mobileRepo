package com.example.dee_kay.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.util.Calendar;

public class Booking extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {

    private Button btn_start,btn_end;
    private TextView tv_start,tv_end;
    int hour, minute,endHour,endMinute, hourFinal,minuteFinal, end_hourFinal,end_minuteFinal;
    private boolean book = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);


        btn_start = (Button)findViewById(R.id.btn_Starting);
        btn_end = (Button) findViewById(R.id.btn_endTime);

        btn_end.setClickable(false);

        tv_start = (TextView)findViewById(R.id.tv_StartingTime);
        tv_end = (TextView)findViewById(R.id.tv_endTime);

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

        if(book == true)
        {
            tv_start.setText(hourFinal +":" + minuteFinal);
            book = false;
        }
        else
        {
            tv_end.setText(end_hourFinal +":" + end_minuteFinal);
        }


    }
}
