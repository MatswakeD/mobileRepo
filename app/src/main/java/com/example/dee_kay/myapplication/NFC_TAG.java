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
import android.os.Parcelable;
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

    GlobalVariables gv;

    TextView tv_parkingName,tv_parkingLocation,tv_status,tv_dateTime;
    NfcAdapter  nfcAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc__tag);

        input = new Input();
        handler = new Handler();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);


        tv_parkingName = (TextView) findViewById(R.id.tv_InparkingName);
        tv_parkingLocation = (TextView) findViewById(R.id.tv_onceoffParkingLocation);
        tv_status = (TextView) findViewById(R.id.tv_OnceoffStatus);
        tv_dateTime = (TextView) findViewById(R.id.tv_onceOFFtime);



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

        boolean tagged = true;
        if(intent.hasExtra(nfcAdapter.EXTRA_TAG));
        {
            Toast.makeText(this,"NFC INTENT", Toast.LENGTH_SHORT).show();


            if(tagged == false)
            {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                NdefMessage ndefMessage = createNdefMessage("My String test.....");

                writeNdefMessage(tag,ndefMessage);
                tagged = true;

            }else if(tagged == true)
            {
                Parcelable [] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                if(parcelables != null && parcelables.length > 0)
                {
                    readTextFromMessage((NdefMessage)parcelables[0]);
                }else
                {
                    Toast.makeText(this,"No NDEF messages found!!", Toast.LENGTH_LONG).show();
                }
            }

            //new myAsync().execute();

        }
    }



    /**
     * For placing the parking onto the list view
     */
    protected void plotParking()
    {
        //Merging the parking list into an array
        //listView = (ListView) findViewById(R.id.hoursList_view);

        hoursList = output.HoursList;
        int size = hoursList.size();
        Hours = new Hours[size];
        for(int i=0; i<size; i++ )
        {
            Hours[i] = hoursList.get(i);
        }

        ArrayAdapter<Hours> hoursAdapter = new HoursCustomAdapter(this,Hours);
        listView.setAdapter(hoursAdapter);

    }


    /**
     * Communicate with the database
     */
    class myAsync extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params)
        {
                FireExitClient client = new FireExitClient(Input.AZURE_URL);
                client.configure(new Configurator("http://tempuri.org/","IService1","TagIn"));

                gv =  ((GlobalVariables)getBaseContext().getApplicationContext());

                input.user.User_ID = gv.getUserID();
                //Default parking..
                input.parking_id = 14 + "";

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

                    try
                    {
                        if(out.Comfirmation.equals("IN"))
                        {
                            tv_parkingName.setText(out.parking.Parking_Name);
                            tv_parkingLocation.setText(out.parking.Parking_City);
                            tv_status.setText(out.Comfirmation);
                            tv_dateTime.setText(out.intTime);

                        }else
                        {
                            Toast.makeText(NFC_TAG.this,"User not in a parking", Toast.LENGTH_LONG).show();
                            tv_status.setText("OUT");
                        }


                    }catch (NullPointerException e)
                    {
                        e.printStackTrace();
                        Toast.makeText(NFC_TAG.this,"CONNECTION TIME OUT", Toast.LENGTH_LONG).show();
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
                return;
            }
            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();

            Toast.makeText(this, "Tag written",Toast.LENGTH_SHORT).show();
        }catch (Exception e)
        {
            Log.e("formatTag", e.getMessage());
        }
    }


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
     * Read the nfc tag
     * @param ndefMessage
     */
    private void readTextFromMessage( NdefMessage ndefMessage)
    {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();
        if(ndefRecords !=null && ndefRecords.length >0)
        {
            NdefRecord ndefRecord = ndefRecords[0];
            String tagContent = getTextFromNdefRecord(ndefRecord);
            Toast.makeText(this,tagContent,Toast.LENGTH_LONG).show();
        }else
        {
            Toast.makeText(this,"no NDEF records found",Toast.LENGTH_LONG).show();
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
            Log.e("createTextRecord",e.getMessage());
        }

        return null;
    }



    private NdefMessage createNdefMessage(String content)
    {
        NdefMessage ndefMessage;

        NdefRecord ndefRecord = null;
        try {
            ndefRecord = createTextRecord(content);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ndefMessage = new NdefMessage(new NdefRecord[]{ndefRecord});

        return ndefMessage;
    }


    public String getTextFromNdefRecord(NdefRecord ndefRecord)
    {
        String tagContent = null;

        byte[] payload = ndefRecord.getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
        int languageSize = payload[0] & 0063;
        try {
            tagContent = new String(payload,languageSize + 1,payload.length - languageSize -1,textEncoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return tagContent;
    }


}
