package com.example.dee_kay.myapplication;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    NfcAdapter nfcAdapter;
    boolean isTagout = false;


    TextView tv_parkingName, tv_parkingLocation, tv_status, tv_dateTime, tv_spent, tv_charges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_aparking);

        input = new Input();
        handler = new Handler();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        tv_parkingName = (TextView) findViewById(R.id.tv_InparkingName);
        tv_parkingLocation = (TextView) findViewById(R.id.tv_onceoffParkingLocation);
        tv_status = (TextView) findViewById(R.id.tv_OnceoffStatus);
        tv_dateTime = (TextView) findViewById(R.id.tv_onceOFFtime);
        tv_spent = (TextView) findViewById(R.id.tv_timeSpend);
        tv_charges = (TextView) findViewById(R.id.tv_charges);

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

    @Override
    protected void onResume() {
        super.onResume();
        enableForegroundDispatchSystem();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableForegroundDispatchSystem();
    }

    /**
     * ForeGround dispatch
     */
    private void enableForegroundDispatchSystem() {
        Intent intent = new Intent(this, InAparking.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] intentFilters = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    /**
     * Disabling foreground
     */
    private void disableForegroundDispatchSystem() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra(nfcAdapter.EXTRA_TAG)) ;
        {
            Toast.makeText(this, "NFC INTENT OUT", Toast.LENGTH_SHORT).show();
            isTagout = true;
            new myAsync().execute();
        }
    }

    private double Amount;
    private boolean isLoad = false;
    /**
     * For showing the dialog, and loading credits
     */
    private void ShowLoadCreditsDailog() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(InAparking.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_loadcredits, null);
        final EditText et_amount = (EditText) view.findViewById(R.id.et_amount);
        Button btnLoad = (Button) view.findViewById(R.id.btn_load);


        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!et_amount.toString().isEmpty()) {
                    Amount = Double.parseDouble(et_amount.getText().toString());

                    input.wallet.Balance = Amount;
                    //for sending the amount to the server
                    isLoad = true;

                    //Sending the amount to data
                    new myAsync().execute();
                } else {
                    Toast.makeText(InAparking.this, "Please fill out any empty fields !!", Toast.LENGTH_LONG).show();
                }
            }
        });

        //Setting the view to show
        mBuilder.setView(view);

        //Showing the dialog
        AlertDialog dialog = mBuilder.create();
        dialog.show();

    }

    class myAsync extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {

            FireExitClient client = new FireExitClient(Input.AZURE_URL);


            if(isLoad == true)
            {
                client.configure(new Configurator("http://tempuri.org/", "IService1", "EditWallet"));

                //passing the input class as a parameter to the service
                client.addParameter("request", input);
                output = new Output();

                try {
                    output = client.call(output);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }else {
                if (isTagout == false) {
                    client.configure(new Configurator("http://tempuri.org/", "IService1", "InParking"));

                    //passing the input class as a parameter to the service
                    client.addParameter("request", input);

                    output = new Output();
                    try {
                        output = client.call(output);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (isTagout == true) {
                    client.configure(new Configurator("http://tempuri.org/", "IService1", "TagOut"));

                    //passing the input class as a parameter to the service
                    client.addParameter("request", input);

                    output = new Output();
                    try {
                        output = client.call(output);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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
                    try
                    {

                        if(isLoad == true)
                        {
                            isLoad = false;
                            Toast.makeText(InAparking.this, "Recharge was successful, you can now tag out !! ", Toast.LENGTH_SHORT).show();
                        }
                        if (!out.Comfirmation.equals("ZERO"))
                        {
                            if (isTagout == true) {

                                tv_status.setText(out.Comfirmation);
                                tv_spent.setText("Time spent: " + out.timeSpent);
                                tv_charges.setText("Parking charges: R " + out.parkingRates);


                            } else { //user has not tagged out

                                if (!input.parking_id.equals("")) {
                                    tv_parkingName.setText(out.parking.Parking_Name);
                                    tv_parkingLocation.setText(out.parking.Parking_City);
                                    tv_status.setText("Parking Status : IN");
                                    tv_dateTime.setText("Tag in time :" + out.intTime);
                                    tv_spent.setText("Time spent: " + out.timeSpent);

                                } else {
                                    Toast.makeText(InAparking.this, "User not in a parking", Toast.LENGTH_LONG).show();
                                    tv_status.setText("OUT");
                                }
                            }

                        }else
                        {
                            //Calling the re-load credits method
                            ShowLoadCreditsDailog();
                        }

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        Toast.makeText(InAparking.this, "CONNECTION TIME OUT !!! IN", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}
