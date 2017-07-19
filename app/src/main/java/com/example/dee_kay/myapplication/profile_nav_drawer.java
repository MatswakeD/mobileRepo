package com.example.dee_kay.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dee_kay.myapplication.WcfObjects.Input;
import com.example.dee_kay.myapplication.WcfObjects.Output;
import com.threepin.fireexit_wcf.Configurator;
import com.threepin.fireexit_wcf.FireExitClient;

import java.util.Locale;

public class profile_nav_drawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FragmentManager FM;
    FragmentTransaction FT;
    NavigationView navigationView;
    Output output;
    Input input;
    String Deactivated = "S";
    Handler handler;

    TextView tv_LastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_nav_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        handler = new Handler();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        GlobalVariables gv = ((GlobalVariables)getBaseContext().getApplicationContext());
        String userID =  gv.getUserID();
        if(userID == "empty")
        {
            navigationView.getMenu().findItem(R.id.nav_logout_profile).setEnabled(false);
        }
        else
        {
            navigationView.getMenu().findItem(R.id.nav_logout_profile).setEnabled(true);

        }



        setTitle("Account");
        FM = getSupportFragmentManager();
        FT = FM.beginTransaction();
        FT.replace(R.id.content_profile_nav_drawer, new TabFragment()).commit();
        new myAsync().execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.profile_nav_drawer, menu);
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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
         if (id == R.id.nav_profilee) {

             setTitle("Account");
             FM = getSupportFragmentManager();
             FT = FM.beginTransaction();
             FT.replace(R.id.content_profile_nav_drawer, new TabFragment()).commit();
        }

        else if (id == R.id.nav_profile_home) {
            // Handle the camera action
            Intent Home = new Intent(this, MainActivity.class);
            startActivity(Home);

        } else if (id == R.id.nav_booking) {

            Booking_TabFragment BT = new Booking_TabFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.content_profile_nav_drawer, BT, BT.getTag()).commit();
        }
        else if (id == R.id.nav_history) {

        }

        else if (id == R.id.nav_logout_profile) {

            GlobalVariables gv = ((GlobalVariables)this.getApplicationContext());
            gv.setUserID("empty");

            Intent Home = new Intent(this, MainActivity.class);
            startActivity(Home);

            Toast.makeText(this,"Good Bye",Toast.LENGTH_LONG).show();
        }
         else if (id == R.id.nav_Deactivate) {

             GlobalVariables gv = ((GlobalVariables)getBaseContext().getApplicationContext());
             input = new Input();
             input.user.User_ID =  gv.getUserID();
             Deactivated = "D";

             new myAsync().execute();

         }
         else if (id == R.id.nav_activate_profile) {

             GlobalVariables gv = ((GlobalVariables)getBaseContext().getApplicationContext());
             input = new Input();
             input.user.User_ID =  gv.getUserID();
             Deactivated = "A";

             new myAsync().execute();

         }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /**
     * Used for fetching data from the db
     */
    class myAsync extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params)
        {

            FireExitClient client = new FireExitClient("http://eparkingservices.cloudapp.net/Service1.svc");

            if(Deactivated == "D")
            {
                client.configure(new Configurator("http://tempuri.org/", "IService1", "DeactivateProfile"));
                //passing the input class as a parameter to the service
                client.addParameter("request", input);
                output = new Output();


                try {
                    output = client.call(output);

                } catch (Exception e) {
                    e.printStackTrace();
                }


                GlobalVariables gv = ((GlobalVariables)getBaseContext().getApplicationContext());
                input = new Input();
                input.user.User_ID =  gv.getUserID();

                client.configure(new Configurator("http://tempuri.org/", "IService1", "GetProfileStatus"));
                //passing the input class as a parameter to the service
                client.addParameter("request", input);
                output = new Output();

                try {
                    output = client.call(output);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                FM = getSupportFragmentManager();
                FT = FM.beginTransaction();
                FT.replace(R.id.content_profile_nav_drawer, new TabFragment()).commit();

            }
            else if (Deactivated == "A")
            {

                client.configure(new Configurator("http://tempuri.org/", "IService1", "ActivateProfile"));
                //passing the input class as a parameter to the service
                client.addParameter("request", input);
                output = new Output();

                try {
                    output = client.call(output);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                GlobalVariables gv = ((GlobalVariables)getBaseContext().getApplicationContext());
                input = new Input();
                input.user.User_ID =  gv.getUserID();

                client.configure(new Configurator("http://tempuri.org/", "IService1", "GetProfileStatus"));
                //passing the input class as a parameter to the service
                client.addParameter("request", input);
                output = new Output();

                try {
                    output = client.call(output);

                } catch (Exception e) {
                    e.printStackTrace();
                }


                FM = getSupportFragmentManager();
                FT = FM.beginTransaction();
                FT.replace(R.id.content_profile_nav_drawer, new TabFragment()).commit();

            }else if(Deactivated == "S")
            {
                GlobalVariables gv = ((GlobalVariables)getBaseContext().getApplicationContext());
                input = new Input();
                input.user.User_ID =  gv.getUserID();

                client.configure(new Configurator("http://tempuri.org/", "IService1", "GetProfileStatus"));
                //passing the input class as a parameter to the service
                client.addParameter("request", input);
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

                    if(!out.Comfirmation.toUpperCase(Locale.ENGLISH).equals("DEACTIVATED"))
                    {
                        navigationView.getMenu().findItem(R.id.nav_Deactivate).setEnabled(true);

                        navigationView.getMenu().findItem(R.id.nav_booking).setEnabled(true);
                        navigationView.getMenu().findItem(R.id.nav_activate_profile).setEnabled(false);
                    }
                    else  {
                        navigationView.getMenu().findItem(R.id.nav_activate_profile).setEnabled(true);
                        navigationView.getMenu().findItem(R.id.nav_Deactivate).setEnabled(false);
                        navigationView.getMenu().findItem(R.id.nav_booking).setEnabled(false);
                    }


                }

            });
        }
    }


}
