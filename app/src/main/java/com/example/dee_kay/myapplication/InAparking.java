package com.example.dee_kay.myapplication;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class InAparking extends AppCompatActivity {


    TextView tv_parkingName,tv_parkingLocation,tv_status,tv_dateTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_aparking);

        tv_parkingName = (TextView) findViewById(R.id.tv_InparkingName);
        tv_parkingLocation = (TextView) findViewById(R.id.tv_onceoffParkingLocation);
        tv_status = (TextView) findViewById(R.id.tv_OnceoffStatus);
        tv_dateTime = (TextView) findViewById(R.id.tv_onceOFFtime);




    }

    @Override
    public void onBackPressed() {

        MainActivity.ONBACKPRESS = true;
        Intent main = new Intent(InAparking.this, MainActivity.class);
        startActivity(main);

    }

}
