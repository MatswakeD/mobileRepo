package com.example.dee_kay.myapplication;

import android.app.AlarmManager;
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
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

public class NFCtag extends AppCompatActivity {


    //Notification
    NotificationManager notificationManager;
    boolean isNotifactionAct = false;


    private CountDownTimer countDownTimer;

    NfcAdapter nfcAdapter;
    //Button btn_tag;
    TextView tv_nfcMessage;
    private int notifyID = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfctag);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        tv_nfcMessage = (TextView) findViewById(R.id.tv_nfcMessage);


    }


    @Override
    protected void onPostResume() {
        super.onPostResume();

        enableForgroundDispatchSystem();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableForgroundDispatchSystem();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {

        super.onNewIntent(intent);

        if(intent.hasExtra(nfcAdapter.EXTRA_TAG));
        {

            Toast.makeText(this,"NFC INTENT",Toast.LENGTH_SHORT).show();
            tv_nfcMessage.setText("Its working");
            int minites = 60;

            countDownTimer = new CountDownTimer(minites *1000,1000)
            {


                @Override
                public void onTick(long millisUntilFinished) {
                    tv_nfcMessage.setText("" + millisUntilFinished/1000);

                }

                @Override
                public void onFinish() {
                    tv_nfcMessage.setText("Done");

                    NotificationCompat.Builder notificatiionBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                            .setContentTitle("Parking Time")
                            .setContentText("Your parking time just elapsed !!!")
                            .setTicker("Parking Time").setSmallIcon(R.drawable.icon);


                    Intent intent2 = new Intent(getApplicationContext(), NFCtag.class);
                    TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
                    taskStackBuilder.addParentStack(NFCtag.class);
                    taskStackBuilder.addNextIntent(intent2);

                    PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
                    notificatiionBuilder.setContentIntent(pendingIntent);
                    notificatiionBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
                    notificatiionBuilder.setAutoCancel(true);


                    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(notifyID, notificatiionBuilder.build());


                }


            };
            countDownTimer.start();

            //Reading the message from nfc tag
                Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                if(parcelables !=null && parcelables.length >0)
                {

                    readTextFromMessage((NdefMessage) parcelables[0]);

                }

                //Writing the nfc
               // Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
               // NdefMessage ndefMessage = createNdefMessage("My strong content");
                //writeNdefMessage(tag,ndefMessage);


        }

    }


    private void readTextFromMessage( NdefMessage ndefMessage)
    {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();
        if(ndefRecords !=null && ndefRecords.length >0)
        {
            NdefRecord ndefRecord = ndefRecords[0];
            String tagContent = getTextFromNdefRecord(ndefRecord);
            tv_nfcMessage.setText(tagContent);
        }else
        {
            Toast.makeText(this,"no NDEF records found",Toast.LENGTH_LONG).show();
        }
    }


    private void enableForgroundDispatchSystem()
    {

        Intent intent = new Intent(this, NFCtag.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);

        IntentFilter [] intentFilters = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this,pendingIntent,intentFilters,null);
    }

    private void disableForgroundDispatchSystem()
    {
        nfcAdapter.disableForegroundDispatch(this);
    }

    private void formatTag(Tag tag, NdefMessage ndefMessage)
    {
        try {

            NdefFormatable ndefFormatable = NdefFormatable.get(tag);

            if(ndefFormatable == null)
            {
                Toast.makeText(this, "Tag is not ndef fortable",Toast.LENGTH_SHORT).show();
            }
            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();

        }catch (Exception e)
        {
            Log.e("formatTag", e.getMessage());
        }
    }

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

    private String getTextFromNdefRecord(NdefRecord ndefRecord)
    {
        String tagContent = null;

        byte[] paload = ndefRecord.getPayload();
        String textEncoding = ((paload[0] & 128)== 0) ? "UTF-8" : "UTF-16";
        int laguageSize =  paload[0] & 0063;
        try {
            tagContent = new String(paload,laguageSize +1, paload.length - laguageSize - 1,textEncoding);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        return tagContent;
    }
    private NdefMessage createNdefMessage(String content) throws UnsupportedEncodingException
    {
        NdefRecord ndefRecord = createTextRecord(content);
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ndefRecord});

        return ndefMessage;
    }

    public void ReadNFC(View view){

    }


    public void WriteNFC(View view)
    {

    }

}
