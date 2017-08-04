package com.example.dee_kay.myapplication;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.dee_kay.myapplication.WcfObjects.Input;
import com.example.dee_kay.myapplication.WcfObjects.Output;

public class TAG_OUT extends AppCompatActivity {

    Input input;
    Output output;
    Handler handler;

    NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag__out);


        input = new Input();
        handler = new Handler();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);


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
    private void enableForegroundDispatchSystem()
    {
        Intent intent = new Intent(this, NFC_TAG.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);

        IntentFilter[] intentFilters = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this,pendingIntent,intentFilters,null);
    }

    /**
     * Disabling foreground
     */
    private void disableForegroundDispatchSystem()
    {
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent.hasExtra(nfcAdapter.EXTRA_TAG));
        {
            Toast.makeText(this,"NFC INTENT OUT", Toast.LENGTH_SHORT).show();

        }
    }


}
