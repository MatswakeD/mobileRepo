package com.example.dee_kay.myapplication;

import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class Tabbed extends AppCompatActivity implements TabLayout.OnTabSelectedListener{

    ViewPager viewPager;
    TabLayout tabLayout;

    NavigationView navigationView;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawerLayout = (DrawerLayout)  findViewById(R.id.activity_tabbed);

        tabLayoutOnCreation();

        navigationViewListeners(drawerLayout, navigationView);

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    private void tabLayoutOnCreation() {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        viewPager = (ViewPager) findViewById(R.id.pager);

        tabLayout.addTab(tabLayout.newTab().setText("User Details"));
        tabLayout.addTab(tabLayout.newTab().setText("User Wallet"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);

        MainPager adapter = new MainPager(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        //this line right here, \/ beastly..
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(this);
    }



    @Override
    public void onBackPressed() {

            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return;
            }
                finish();
        super.onBackPressed();

    }

    public void navigationViewListeners(final DrawerLayout layout, NavigationView view) {

        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.nav_profile_home:
                        Toast.makeText(Tabbed.this, "home", Toast.LENGTH_LONG).show();
                        item.setChecked(true);
                        layout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.nav_booking:
                        Toast.makeText(Tabbed.this, "booking", Toast.LENGTH_LONG).show();
                        item.setChecked(true);
                        layout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.nav_logout_profile:
                        Toast.makeText(Tabbed.this, "logout pro", Toast.LENGTH_LONG).show();
                        item.setChecked(true);
                        layout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.nav_login_profile:
                        Toast.makeText(Tabbed.this, "login pro", Toast.LENGTH_LONG).show();
                        item.setChecked(true);
                        layout.closeDrawer(GravityCompat.START);
                        break;

                }
                return false;
            }
        });
    }


}
