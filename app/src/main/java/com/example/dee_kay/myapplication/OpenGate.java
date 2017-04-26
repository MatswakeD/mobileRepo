package com.example.dee_kay.myapplication;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class OpenGate extends AppCompatActivity  implements ZXingScannerView.ResultHandler{

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 10 ;

    private ZXingScannerView zXingScannerView;
    private Button btnScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_gate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Check camera permissions, then open the camera for scanning
        check();

    }


    /*
     For checking camera permissions
  */
    protected void check()
    {
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA )!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }
        else {
            //For Opening the camera
            scan();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_CAMERA :
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //Open the camera
                    scan();
                }else
                {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.CAMERA))
                    {
                        new AlertDialog.Builder(this).setTitle("Read Camera permission").
                                setMessage("You need to grant camera permission to san the QR Code/ Bar Code.").show();
                    }else
                    {
                        new AlertDialog.Builder(this).setTitle("Read Camera permission").
                                setMessage("You denied camera permission, so the application cannot use your camera").show();
                    }
                }
                break;
            }
        }

    }

    /*
      This function will open the camera for scanning the QR Code
    */
    public void scan ()
    {
        zXingScannerView = new ZXingScannerView(getApplicationContext());
        setContentView(zXingScannerView);
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
    }


    /*
    @Override
    protected void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();
    }
    */

    @Override
    public void handleResult(Result result)
    {
        Toast.makeText(getApplicationContext(),result.getText(),Toast.LENGTH_SHORT).show();
        zXingScannerView.resumeCameraPreview(this);
    }
}
