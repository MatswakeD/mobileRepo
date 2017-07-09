package com.example.dee_kay.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
        {

            FragmentManager FM;
            FragmentTransaction FT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //Checking if the google services is correctly configured
        if (googleServiceAvailable()) {
            //Toast.makeText(this, "Google Services Available !!", Toast.LENGTH_LONG).show();


        } else {
            Toast.makeText(this, "Google Services is not Available  !!", Toast.LENGTH_LONG).show();

        }


        //Displaying the map when the main activity starts
        setTitle("HOME");
        FM = getSupportFragmentManager();
        FT = FM.beginTransaction();
        FT.replace(R.id.content_main, new Host_Home_Tab()).commit();

    }

    //Checking for google services permissions
    public boolean googleServiceAvailable()
    {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvalable = api.isGooglePlayServicesAvailable(this);
        if (isAvalable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvalable)) {
            Dialog dialog = api.getErrorDialog(this, isAvalable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Cant connect to play services", Toast.LENGTH_LONG).show();
        }
        return false;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_Register)
        {
            Registration registration = new Registration();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_main, registration, registration.getTag()).commit();
            return true;
        }

        if (id == R.id.action_login)
        {
            Login login = new Login();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_main, login, login.getTag()).commit();
            return true;
        }

            return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if(id == R.id.nav_homeMap)
        {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            ((Activity) this).overridePendingTransition(0,0);
        }

         else if (id == R.id.nav_login) {

            Login login = new Login();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_main, login, login.getTag()).commit();
        }
        else if(id == R.id.nav_opengate){

            Intent openGateIntent = new Intent(this, OpenGate.class);
            startActivity(openGateIntent);
        }

        else if(id == R.id.nav_register){

            Registration registration = new Registration();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_main, registration, registration.getTag()).commit();
        }

        else if(id == R.id.nav_aboutus){

            Intent openGateIntent = new Intent(this, Tabbed.class);
            startActivity(openGateIntent);
        }

        else if(id == R.id.nav_logout)
        {
            GlobalVariables gv = ((GlobalVariables)this.getApplicationContext());
            gv.setUserID("empty");

            Home_Map map = new Home_Map();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_main, map, map.getTag()).commit();

            Toast.makeText(this,"Good Bye",Toast.LENGTH_LONG).show();


            return true;
        }

        else if(id == R.id.nav_newProfile){

            Intent profile = new Intent(this, profile_nav_drawer.class);
            startActivity(profile);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }







            boolean doubleBackToExitPressedOnce = false;

            @Override
            public void onBackPressed() {

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                    return;
                }



                if (doubleBackToExitPressedOnce) {
                    finish();
                    Toast.makeText(this, "Application closed", Toast.LENGTH_LONG).show();
                    System.exit(0);
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Tap again to quit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 1500);
                return;

            }





}


