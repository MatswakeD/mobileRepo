package com.example.dee_kay.myapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dee_kay.myapplication.CustomAdaptors.HoursCustomAdapter;
import com.example.dee_kay.myapplication.WcfObjects.Input;
import com.example.dee_kay.myapplication.WcfObjects.Output;
import com.example.dee_kay.myapplication.WcfObjects.Hours;
import com.threepin.fireexit_wcf.Configurator;
import com.threepin.fireexit_wcf.FireExitClient;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;

public class NFC_TAG extends AppCompatActivity {

    Input input;
    Output output;
    Handler handler;


    Hours [] Hours = null;  //Goes to the adapter
    List<Hours> hoursList = null;  //Used for plotting on the view
    ListView listView;
    Hours hours;            //Used when getting the parking information

    String chooenHours;

    GlobalVariables gv;

    //Notification
    NotificationManager notificationManager;
    boolean isNotifactionAct = false;
    private int notifyID = 13;

    private CountDownTimer countDownTimer;
    private String User_id;


    TextView tv_nfcTag;
    NfcAdapter  nfcAdapter;
    private boolean isTosendHours  = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc__tag);

        input = new Input();
        handler = new Handler();


        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        tv_nfcTag = (TextView) findViewById(R.id.tv_nfcTag);


        input.parking_id = 14 + "";


    }


    @Override
    protected void onResume() {
        super.onResume();
        enableForgroundDispatchSystem();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableForgroundDispatchSystem();
    }

    /**
     * ForeGround dispatch
     */
    private void enableForgroundDispatchSystem()
    {
        Intent intent = new Intent(this, NFC_TAG.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);

        IntentFilter[] intentFilters = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this,pendingIntent,intentFilters,null);
    }

    /**
     * Disabling foreground
     */
    private void disableForgroundDispatchSystem()
    {
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent.hasExtra(nfcAdapter.EXTRA_TAG));
        {
            Toast.makeText(this,"NFC INTENT", Toast.LENGTH_SHORT).show();
            tv_nfcTag.setText("it working");


            new myAsync().execute();

        }
    }


    /**\
     * For handling list item when they are clicked
     */
    private void clickCallBack()
    {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {

                GlobalVariables gv = ((GlobalVariables)getBaseContext().getApplicationContext());
                User_id = gv.getUserID();


                if(gv.getUserID().equals("empty"))
                {
                    Toast.makeText(getBaseContext(),"User not logged in",Toast.LENGTH_LONG).show();

                    Login login = new Login();
                    FragmentManager manager = getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.activity_nfc__tag ,login, login.getTag()).commit();

                }else
                {
                    hours = (Hours) parent.getItemAtPosition(position);
                    chooenHours = hours.hours;
                    Toast.makeText((getBaseContext()), String.valueOf(chooenHours), Toast.LENGTH_LONG).show();

                    double doubleValue = Double.parseDouble(chooenHours.trim());
                    int minutes = (int)doubleValue;


                    //Checking if the user has chosen the parking time
                    if(chooenHours.length() != 0) {

                        //Sending the hours that the user has chosen
                         isTosendHours = true;
                        new myAsync().execute();

                        countDownTimer = new CountDownTimer((minutes) * 1000, 1000) {

                            String done;
                            @Override
                            public void onTick(long millisUntilFinished) {
                                tv_nfcTag.setText("" + millisUntilFinished / 1000);

                            }

                            @Override
                            public void onFinish() {
                                tv_nfcTag.setText("Done");

                                NotificationCompat.Builder notificatiionBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                                        .setContentTitle("Parking Time")
                                        .setContentText("Your parking time just elapsed !!!")
                                        .setTicker("Parking Time").setSmallIcon(R.drawable.icon);


                                Intent intent2 = new Intent(getApplicationContext(), NFC_TAG.class);
                                TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
                                taskStackBuilder.addParentStack(NFC_TAG.class);
                                taskStackBuilder.addNextIntent(intent2);

                                PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
                                notificatiionBuilder.setContentIntent(pendingIntent);
                                notificatiionBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
                                notificatiionBuilder.setAutoCancel(true);


                                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.notify(notifyID, notificatiionBuilder.build());


                                done = "done";
                                new myAsyncDone().execute();

                            }

                            //Telling the server that we done
                            class myAsyncDone extends AsyncTask {
                                @Override
                                protected Object doInBackground(Object[] params)
                                {

                                    FireExitClient client = new FireExitClient(Input.AZURE_URL);

                                        //Sending the parking ID and done, to decrement the user counter
                                        input.parking_id = 14 + "";
                                        input.Done = done;


                                        client.configure(new Configurator("http://tempuri.org/","IService1","Done"));
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
                                            if(out.Comfirmation == "OUT")
                                            {
                                                Toast.makeText(getApplicationContext(),"Out !!!",Toast.LENGTH_LONG).show();
                                            }

                                        }
                                    });
                                }
                            }


                        };
                        countDownTimer.start();

                    }
                    else {
                        Toast.makeText(getBaseContext(),"Please choose your time in the parking",Toast.LENGTH_LONG).show();
                    }


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
        listView = (ListView) findViewById(R.id.hoursList_view);

        hoursList = output.HoursList;
        int size = hoursList.size();
        Hours = new Hours[size];
        for(int i=0; i<size; i++ )
        {
            Hours[i] = hoursList.get(i);
        }

        ArrayAdapter<Hours> hoursAdapter = new HoursCustomAdapter(this,Hours);
        listView.setAdapter(hoursAdapter);

        clickCallBack();
    }


    /**
     * Communicate with the database
     */
    class myAsync extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params)
        {

            FireExitClient client = new FireExitClient(Input.AZURE_URL);

            if(isTosendHours == false)
            {
                client.configure(new Configurator("http://tempuri.org/","IService1","GetHours"));

                //passing the input class as a parameter to the service
                client.addParameter("request",input);
                output = new Output();

                try {
                    output = client.call(output);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            else if(isTosendHours == true)
            {
                client.configure(new Configurator("http://tempuri.org/","IService1","ChosenHours"));

                //use this to charge the user
                input.choosenHours = chooenHours;
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

                    if(out.HoursList.size() != 0)
                    {
                        plotParking();
                    }else if(out.Comfirmation.equals("IN"))
                    {
                        Toast.makeText(getApplicationContext(),"IN !!!",Toast.LENGTH_LONG).show();
                    }else
                    {
                        Toast.makeText(getApplicationContext(),"No hours",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }



    private void formatTag(Tag tag, NdefMessage ndefMessage)
    {
        try {

            NdefFormatable ndefFormatable = NdefFormatable.get(tag);

            if(ndefFormatable == null)
            {
                Toast.makeText(this, "Tag is not ndef formatable",Toast.LENGTH_SHORT).show();
            }
            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();

        }catch (Exception e)
        {
            Log.e("formatTag", e.getMessage());
        }
    }

    /**
     * Read the nfc tag
     * @param ndefMessage
     */
//    private void readTextFromMessage( NdefMessage ndefMessage)
//    {
//        NdefRecord[] ndefRecords = ndefMessage.getRecords();
//        if(ndefRecords !=null && ndefRecords.length >0)
//        {
//            NdefRecord ndefRecord = ndefRecords[0];
//            String tagContent = getTextFromNdefRecord(ndefRecord);
//            tv_nfcTag.setText(tagContent);
//        }else
//        {
//            Toast.makeText(this,"no NDEF records found",Toast.LENGTH_LONG).show();
//        }
//    }


    /**
     * Writing formatting and writing the tag
     * @param tag
     * @param ndefMessage
     */
    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage)
    {
        try{

            if (tag == null)
            {
                Toast.makeText(this,"Tag Object cannot be null",Toast.LENGTH_LONG).show();
                return;
            }

            Ndef ndef = Ndef.get(tag);

            if(ndef == null)
            {
                //formatting the tag with the ndef format and write the message
                formatTag(tag,ndefMessage);
            }else
            {
                ndef.connect();
                if(!ndef.isWritable())
                {
                    Toast.makeText(this,"Tag is not writable!",Toast.LENGTH_LONG).show();
                    ndef.close();
                    return;
                }

                ndef.writeNdefMessage(ndefMessage);
                ndef.close();
                Toast.makeText(this,"Tag is written!",Toast.LENGTH_LONG).show();

            }
        }catch (Exception e)
        {
            Log.e("writeNdefMessage", e.getMessage());
        }
    }

    /**
     *
     * @param content
     * @return
     * @throws UnsupportedEncodingException
     */
    private NdefRecord createTextRecord(String content) throws UnsupportedEncodingException
    {
        try
        {
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");

            final byte[]  text = content.getBytes("UTF-8");
            final int laguageSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payLoad = new ByteArrayOutputStream(1 + laguageSize  + textLength);

            payLoad.write((byte)  (laguageSize & 0x1F));
            payLoad.write(language,0,laguageSize);
            payLoad.write(text,0,textLength);
            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN,NdefRecord.RTD_TEXT,new byte[0],payLoad.toByteArray());

        }catch (Exception e)
        {
            Log.e("Something went wrong",e.getMessage());
        }

        return null;
    }


}
