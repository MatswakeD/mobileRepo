package com.example.dee_kay.myapplication;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dee_kay.myapplication.WcfObjects.Input;
import com.example.dee_kay.myapplication.WcfObjects.Output;
import com.example.dee_kay.myapplication.WcfObjects.Parking;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.threepin.fireexit_wcf.Configurator;
import com.threepin.fireexit_wcf.FireExitClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.dee_kay.myapplication.Login.USER_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home_Map extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener
        ,LocationListener{

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    Marker OtherOlaces;

    MapView mapView;
    View mView;
    static final LatLng Myhome = new LatLng(-26.179948, 27.995744);

    Button btnBottom;
    EditText et_Search;

    Output output;
    Sessions session;
    private String User_id = "";
    private String userIDgv = "";
    Handler handler;

    public Home_Map() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Saving the fragment data
        super.onCreate(savedInstanceState);
        setRetainInstance(true);


        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_first_, container, false);
        getActivity().setTitle("MAP");

        handler = new Handler();
        session = new Sessions();



        et_Search = (EditText) mView.findViewById(R.id.et_search);
        btnBottom = (Button) mView.findViewById(R.id.btnSearch);

        btnBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(et_Search.getVisibility() == View.VISIBLE)
                {
                    Toast.makeText(getActivity(),"Visible now", Toast.LENGTH_LONG).show();
                    et_Search.setVisibility(View.INVISIBLE);
                }
                else
                {
                    et_Search.setVisibility(View.VISIBLE);
                }
            }
        });


        Bundle b = getArguments();
        if(b != null){
            this.User_id = b.getString(USER_ID);

        }

        GlobalVariables gv = ((GlobalVariables)getActivity().getApplicationContext());
        userIDgv = gv.getUserID();



        return mView;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mapView = (MapView) mView.findViewById(R.id.map);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);


        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(USER_ID,User_id);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null)
        {
            User_id = savedInstanceState.getString(USER_ID,"");
        }

            new myAsync().execute();

    }

    @Override
    public void onPause() {
        super.onPause();

        if(mGoogleApiClient != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        }
    }



    /*
 * Used for getting parking(s) from the data-store
 * */
    class myAsync extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params)
        {

            FireExitClient client = new FireExitClient("http://eparkingservices.cloudapp.net/Service1.svc");
            client.configure(new Configurator("http://tempuri.org/","IService1","Parkings"));

            //passing the input class as a parameter to the service
            client.addParameter("request","");


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


                    if(out.parkingList.size() != 0) {
                        //"For searching a specific location
                        try {

                            geoLocation();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "Could not retrieve parkings, please try to load the page", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getActivity());
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize google Play Service
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);




            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);

        }

        if(mGoogleMap !=null)
        {
            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker)
                {
                    View v = getActivity().getLayoutInflater().inflate(R.layout.infor_window,null);

                    TextView tv_tittle = (TextView) v.findViewById(R.id.tv_tittle);
                    TextView tv_location = (TextView) v.findViewById(R.id.tv_location);


                    LatLng ll = marker.getPosition();

                    if(marker != mCurrLocationMarker)
                    {
                        tv_tittle.setText(marker.getTitle());
                        tv_location.setText(marker.getSnippet());
                    }
                    else {
                        tv_tittle.setText(marker.getTitle());
                        tv_location.setText(marker.getSnippet());
                    }

                    return v;
                }
            });

        }


        if(mGoogleMap != null){
            mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {


                    if(marker != mCurrLocationMarker)
                    {
                        if(!userIDgv.equals("empty"))
                        {
                            //Switching to a booking class
                            Intent i = new Intent(getActivity(), Booking.class);
                            startActivity(i);
                            ((Activity) getActivity()).overridePendingTransition(0,0);

                            // LatLng ll = marker.getPosition();
                            // gotoLocationZoom(ll,17);
                        }
                        else
                        {
                            Toast.makeText(getActivity(),"User not logged in", Toast.LENGTH_LONG).show();
                        }
                    }


                }
            });
        }

    }


    /*
    * Used for zooming the camera
    * */
    private void gotoLocationZoom(LatLng ll, float zoom)
    {
        CameraUpdate updateCamera = CameraUpdateFactory.newLatLngZoom(ll,zoom);
        mGoogleMap.moveCamera(updateCamera);
    }

    /*
    * Used for drawing parking(s) on the map
    * */
    public void geoLocation()throws IOException
    {
        Geocoder gc = new Geocoder(getActivity());

        List<Parking> parkingList =  output.parkingList;


            for (int i = 0; i < parkingList.size(); i++) {
                List<Address> list = gc.getFromLocationName(parkingList.get(i).Parking_Name + "" + parkingList.get(i).Parking_City, 1);
                Address address = list.get(0);
                String locality = address.getFeatureName();

                // Toast.makeText(getActivity(),locality,Toast.LENGTH_LONG).show();

                double lat = address.getLatitude();
                double lng = address.getLongitude();
                LatLng ll = new LatLng(lat, lng);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(ll);
                markerOptions.title("Parking(s) Around").snippet(locality);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                OtherOlaces = mGoogleMap.addMarker(markerOptions);

                CameraUpdate updateCamera = CameraUpdateFactory.newLatLngZoom(ll, 13);
                mGoogleMap.moveCamera(updateCamera);


            }

    }



    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position").snippet(latLng + "");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,13));

        //optionally, stop location updates if only current location is needed
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            new AlertDialog.Builder(getActivity())
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Prompt the user once explanation has been shown
                            requestPermissions(
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION );
                        }
                    })
                    .create()
                    .show();


        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_LOCATION :{
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }


    }

}
