package com.example.dee_kay.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dee_kay.myapplication.WcfObjects.Input;
import com.example.dee_kay.myapplication.WcfObjects.Output;
import com.threepin.fireexit_wcf.Configurator;
import com.threepin.fireexit_wcf.FireExitClient;

public class InAparking extends AppCompatActivity {


    Input input;
    Output output;
    Handler handler;




    TextView tv_parkingName,tv_parkingLocation,tv_status,tv_dateTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_aparking);

        input = new Input();
        handler = new Handler();

        tv_parkingName = (TextView) findViewById(R.id.tv_InparkingName);
        tv_parkingLocation = (TextView) findViewById(R.id.tv_onceoffParkingLocation);
        tv_status = (TextView) findViewById(R.id.tv_OnceoffStatus);
        tv_dateTime = (TextView) findViewById(R.id.tv_onceOFFtime);


        GlobalVariables gv = ((GlobalVariables) getBaseContext().getApplicationContext());
        String pID = getIntent().getStringExtra("parking_id");

        //Passing the parking ID
        input.user.User_ID = gv.getUserID();
        input.parking_id = pID;
        new myAsync().execute();

    }

    @Override
    public void onBackPressed() {

        MainActivity.ONBACKPRESS = true;
        Intent main = new Intent(InAparking.this, MainActivity.class);
        startActivity(main);

    }

    class myAsync extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params)
        {

            FireExitClient client = new FireExitClient(Input.AZURE_URL);
            client.configure(new Configurator("http://tempuri.org/","IService1","InParking"));

            //passing the input class as a parameter to the service
            client.addParameter("request",input);

            output = new Output();

            try
            {
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

                    try
                    {
                        if(!input.parking_id.equals(""))
                        {
                            tv_parkingName.setText(out.parking.Parking_Name);
                            tv_parkingLocation.setText(out.parking.Parking_City);
                            tv_status.setText("IN");
                            tv_dateTime.setText(out.intTime);

                        }else
                        {
                            Toast.makeText(InAparking.this,"User not in a parking", Toast.LENGTH_LONG).show();
                            tv_status.setText("OUT");
                        }


                    }catch (NullPointerException e)
                    {
                        e.printStackTrace();
                        Toast.makeText(InAparking.this, "CONNECTION TIME OUT", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}
