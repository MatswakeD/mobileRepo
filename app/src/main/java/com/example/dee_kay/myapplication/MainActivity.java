package com.example.dee_kay.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

import com.example.dee_kay.myapplication.DataBase.DateBaseHelper;
import com.example.dee_kay.myapplication.WcfObjects.Input;
import com.example.dee_kay.myapplication.WcfObjects.Output;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.threepin.fireexit_wcf.Configurator;
import com.threepin.fireexit_wcf.FireExitClient;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * MainActivity handles the start up of the program
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static boolean ONBACKPRESS = false;
    NavigationView navigationView;
    FragmentManager FM;
    FragmentTransaction FT;

    DateBaseHelper myDB;
    Input input;
    Output output;
    Handler handler;
    String filename = "file.txt";
    GlobalVariables gv = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        myDB = new DateBaseHelper(this);
        handler = new Handler();
        gv =  ((GlobalVariables) getApplicationContext());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Checking if the google services is correctly configured
        if (googleServiceAvailable()) {
            //Toast.makeText(this, "Google Services Available !!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Google Services is not Available  !!", Toast.LENGTH_LONG).show();

        }

        if (ONBACKPRESS == false) {
            //Getting data from the local database
            getDataFromFile();
        } else if (ONBACKPRESS) {
            //Displaying the map when the main activity starts
            setTitle("HOME");
            FM = getSupportFragmentManager();
            FT = FM.beginTransaction();
            FT.replace(R.id.content_main, new Host_Home_Tab()).commit();

            if(gv.getUserID().equals("empty"))
            {
                //If the user is no longer logged in
                enableNavLabels();
            }else
            {
                //if the user is still logged in
                loggedInNavLabels();
            }


        }


    }


    /**
     * Checking for google services permissions
     */

    public boolean googleServiceAvailable() {
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


    /**
     * Plugging the action bar menu
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Handling action bar menu clicks
     *
     * @param item
     * @return
     */
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
        if (id == R.id.action_Register) {
            Registration registration = new Registration();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_main, registration, registration.getTag()).commit();
            return true;
        }

        if (id == R.id.action_login) {
            Login login = new Login();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_main, login, login.getTag()).commit();
            return true;
        }
        if (id == R.id.action_exit) {

            finish();  //exiting the program completely
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * handling menu item clicks
     *
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_homeMap) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            ((Activity) this).overridePendingTransition(0, 0);
        } else if (id == R.id.nav_login) {

            Login login = new Login();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_main, login, login.getTag()).commit();
        } else if (id == R.id.nav_nfc) {

            Intent nfc = new Intent(this, NFC_TAG.class);
            startActivity(nfc);
        } else if (id == R.id.nav_nfcTagout) {

            Intent tagout = new Intent(this, InAparking.class);
            startActivity(tagout);
        } else if (id == R.id.nav_register) {

            Registration registration = new Registration();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_main, registration, registration.getTag()).commit();
        } else if (id == R.id.nav_aboutus) {


        } else if (id == R.id.nav_logout) {
            GlobalVariables gv = ((GlobalVariables) this.getApplicationContext());
            gv.setUserID("empty");

            Home_Map map = new Home_Map();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_main, map, map.getTag()).commit();

            Toast.makeText(this, "Good Bye " + gv.getLasrName(), Toast.LENGTH_LONG).show();
            navigationView.getMenu().findItem(R.id.nav_login).setEnabled(false);

            saveFile(filename,"");
            navigationView.getMenu().findItem(R.id.nav_logout).setEnabled(false);
            navigationView.getMenu().findItem(R.id.nav_login).setEnabled(true);


        } else if (id == R.id.nav_newProfile) {

            Intent profile = new Intent(this, profile_nav_drawer.class);
            startActivity(profile);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Handling a back button
     */
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        if (doubleBackToExitPressedOnce) {

            //Closing all other active activities
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);

            //Then closing main activity
            if (getIntent().getBooleanExtra("EXIT", false)) {
                finish();
            }

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


    /**
     * Getting data from a local data
     */
    public void getDataFromFile() {
        //read file
        String readFileInfor = readFile(filename);

        if (!readFileInfor.equals("")) {
            input = new Input();

            //Splitting the string from the file
            String[] seperated = readFileInfor.split(":");
            String email = seperated[0];
            String pass = seperated[1];

            input.user.Email = email;
            input.user.Password = pass;

            //Sending the data for verification
            new myAsync().execute();
        } else {
            Registration registration = new Registration();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_main, registration, registration.getTag()).commit();
        }


    }


    /**
     *
     * @param file
     * @param text
     */
    public void saveFile(String file, String text)
    {
        FileOutputStream fos;
        try{

            fos = MainActivity.this.getApplicationContext().openFileOutput(file, Context.MODE_PRIVATE);
            fos.write(text.getBytes());
            fos.close();
            Toast.makeText(MainActivity.this,"Saved",Toast.LENGTH_LONG).show();
        }catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,"Error saving file!!",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Read user information from file
     *
     * @param file
     * @return
     */
    public String readFile(String file) {
        String text = "";
        try {

            FileInputStream fis = openFileInput(file);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            text = new String(buffer);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "File cannot be read!!", Toast.LENGTH_LONG).show();
        }
        return text;
    }

    /**
     * Showing the user that they can not access other part of the application
     * If they are not logged in
     * So they should log in first before they can access other part of the application
     */
    protected void enableNavLabels() {
        navigationView.getMenu().findItem(R.id.nav_newProfile).setEnabled(false);
        navigationView.getMenu().findItem(R.id.nav_logout).setEnabled(false);
        navigationView.getMenu().findItem(R.id.nav_newProfile).setTitle("Log in to access your account");


    }

    /**
     * For setting the log in and log out clickable or un-clickable
     * Based on if the user successfully logged in
     */
    protected void loggedInNavLabels() {
        navigationView.getMenu().findItem(R.id.nav_newProfile).setEnabled(true);
        navigationView.getMenu().findItem(R.id.nav_logout).setEnabled(true);
        navigationView.getMenu().findItem(R.id.nav_login).setEnabled(false);
    }


    class myAsync extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {

            FireExitClient client = new FireExitClient(Input.AZURE_URL);
            client.configure(new Configurator("http://tempuri.org/", "IService1", "SignIn"));

            //passing the input class as a parameter to the service
            client.addParameter("request", input);

            output = new Output();

            try {
                output = client.call(output);
            } catch (Exception e) {
                e.printStackTrace();
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

                    try {

                        if (out.Comfirmation.equals("true")) {
                            if (out.User_ID > 0) {


                                gv.setUserID(out.User_ID + "");
                                gv.setFirstname(out.user.FirstName);
                                gv.setLasrName(out.user.LastName);
                                gv.setLoggedIN("IN");

                                String userID = gv.getUserID();
                                if (userID.equals("empty")) {

                                    //if the user was not logged in successfully
                                    enableNavLabels();
                                } else {

                                    //if the user is logged in successfully
                                    loggedInNavLabels();

                                    if (out.INorOUT.equals("OUT")) {

                                        //Displaying the map when the main activity starts
                                        setTitle("HOME");
                                        FM = getSupportFragmentManager();
                                        FT = FM.beginTransaction();
                                        FT.replace(R.id.content_main, new Host_Home_Tab()).commit();

                                    } else if (out.INorOUT.equals("IN")) {


                                        //Passing the parking ID to the next activity
                                        String parkingID = out.parking_ID + "";
                                        Intent inParking = new Intent(MainActivity.this, InAparking.class);
                                        inParking.putExtra("parking_id", parkingID);
                                        startActivity(inParking);

                                    }
                                    //Welcome to message
                                    Toast.makeText(MainActivity.this, "Welcome " + out.user.LastName, Toast.LENGTH_LONG).show();
                                }
                            }

                        } else {

                            Toast.makeText(MainActivity.this, "Incorrect Password Or User name", Toast.LENGTH_LONG).show();

                            GlobalVariables gv = ((GlobalVariables) getBaseContext().getApplicationContext());
                            String userID = gv.getUserID();
                            if (userID.equals("empty")) {

                                //Logging in was unsuccessful
                                enableNavLabels();
                            } else {

                                //Logging was successful
                                loggedInNavLabels();
                            }

                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "CONNECTION TIME OUT", Toast.LENGTH_LONG).show();


                        GlobalVariables gv = ((GlobalVariables) getBaseContext().getApplicationContext());
                        String userID = gv.getUserID();
                        if (userID.equals("empty")) {

                            //Logging in was unsuccessful
                            enableNavLabels();
                        } else {
                            //Logging was successful
                            loggedInNavLabels();


                        }

                        //Retrying to load
                        new myAsync().execute();
                        String id = gv.getUserID();
                        if (id.equals("empty")) {
                            //Logging in was unsuccessful
                            enableNavLabels();
                        } else {
                            //Logging was successful
                            loggedInNavLabels();


                        }


                    }
                }
            });
        }
    }

}


