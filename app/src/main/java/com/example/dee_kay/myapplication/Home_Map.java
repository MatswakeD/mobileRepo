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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dee_kay.myapplication.WcfObjects.Input;
import com.example.dee_kay.myapplication.WcfObjects.Output;
import com.example.dee_kay.myapplication.WcfObjects.Parking;
import com.example.dee_kay.myapplication.WcfObjects.User;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.threepin.fireexit_wcf.Configurator;
import com.threepin.fireexit_wcf.FireExitClient;

import java.io.IOException;

import java.util.List;
import java.util.Locale;

import static com.example.dee_kay.myapplication.Login.USER_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home_Map extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
        , LocationListener, View.OnClickListener {

    GoogleMap mGoogleMap;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    Marker OtherOlaces;

    MapView mapView;
    View mView;

    Output output;
    private String User_id = "";
    private String userIDgv = "";
    Handler handler;
    FloatingActionButton btnNFC, btnSearch;

    //for searching
    EditText et_search;
    boolean IsSearchig = false;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    private static final LatLngBounds myBounds = new LatLngBounds(new LatLng(-0, 0), new LatLng(0, 0));


    GlobalVariables gv = null;
    Intent intentService;   //Notification service

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

        handler = new Handler();

        //for searching
        et_search = (EditText) mView.findViewById(R.id.et_search);
        recyclerView = (RecyclerView) mView.findViewById(R.id.RecyclerView);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        //then setAdapter

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Button for tag in
        btnNFC = (FloatingActionButton) mView.findViewById(R.id.floatingBTN_nfcTab);
        btnNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nfc = new Intent(getActivity(), NFC_TAG.class);
                startActivity(nfc);
            }
        });

        //Setting the text box to visible
        setSearchTextBoxToVisible();

        //For searching a specific parking
        Search();

        gv = ((GlobalVariables) getActivity().getApplicationContext());
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
        outState.putString(USER_ID, User_id);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            User_id = savedInstanceState.getString(USER_ID, "");
        }

        new myAsync().execute();

    }

    @Override
    public void onPause() {
        super.onPause();

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onClick(View v) {

    }

    /*
 * Used for getting parking(s) from the data-store
 * */
    class myAsync extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {

            FireExitClient client = new FireExitClient(Input.AZURE_URL);


            client.configure(new Configurator("http://tempuri.org/", "IService1", "GetParkings"));

            //passing the input class as a parameter to the service
            client.addParameter("request", "");


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
                    if (out.parkingList.size() != 0) {

                        try {

                            if (IsSearchig == false) {
                                //Plotting the parking(s) on the map
                                geoLocation();
                            } else if (IsSearchig == true) {
                                geoSearch(et_search.getText().toString());
                                IsSearchig = false;
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Could not retrieve parking(s), please try to load the page", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    /**
     * Setting Google map type
     *
     * @param googleMap
     */
    protected void setMapType(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
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
        } else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);

        }

        if (mGoogleMap != null) {
            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getActivity().getLayoutInflater().inflate(R.layout.infor_window, null);

                    TextView tv_tittle = (TextView) v.findViewById(R.id.tv_tittle);
                    TextView tv_location = (TextView) v.findViewById(R.id.tv_location);


                    LatLng ll = marker.getPosition();

                    if (marker != mCurrLocationMarker) {
                        tv_tittle.setText(marker.getTitle());
                        tv_location.setText(marker.getSnippet());

                    } else {
                        tv_tittle.setText(marker.getTitle());
                        tv_location.setText(marker.getSnippet());
                    }

                    return v;
                }
            });

        }


        if (mGoogleMap != null) {
            mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {


                    if (marker != mCurrLocationMarker) {
                        if (!userIDgv.equals("empty")) {

                        } else {
                            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_LONG).show();
                        }
                    }


                }
            });
        }

    }


    /**
     * For zooming the camera
     *
     * @param ll
     * @param zoom
     */
    private void gotoLocationZoom(LatLng ll, float zoom) {
        CameraUpdate updateCamera = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        // mGoogleMap.moveCamera(updateCamera);
        mGoogleMap.animateCamera(updateCamera);
    }


    /**
     * For setting the text box visible
     */
    public void setSearchTextBoxToVisible() {
        btnSearch = (FloatingActionButton) mView.findViewById(R.id.btnFB_search);
        btnSearch.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                et_search.setVisibility(View.VISIBLE);
                return true;
            }
        });

    }

    /**
     * Used for searching a specific location
     */
    private void Search() {
        btnSearch = (FloatingActionButton) mView.findViewById(R.id.btnFB_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IsSearchig = true;
                new myAsync().execute();

            }
        });

    }


    /**
     * For searching a specific parking on the map
     */
    public void geoSearch(String parking) throws IOException {
        Geocoder gc = new Geocoder(getActivity());
        List<Parking> parkingList = output.parkingList;


        boolean isFound = false;
        if (mCurrLocationMarker != null) {
            mGoogleMap.clear();
        }

        int parkingSize = parkingList.size();

        for (int i = 0; i < parkingSize; i++) {

            if (parking.equals(parkingList.get(i).Parking_Name)) {

                isFound = true;
                List<Address> listName = gc.getFromLocation(parkingList.get(i).Coordinates_ltd, parkingList.get(i).Coordinates_lng, 20);

                String locality = parkingList.get(i).Parking_City + "\n" + "Number of bays " + parkingList.get(i).Number_Of_bays;
                Address address = listName.get(0);

                double lat = address.getLatitude();
                double lng = address.getLongitude();
                LatLng ll = new LatLng(lat, lng);


                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(ll);
                markerOptions.title(parkingList.get(i).Parking_Name).snippet(locality);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                OtherOlaces = mGoogleMap.addMarker(markerOptions);
                //Zooming in the location
                gotoLocationZoom(ll, 17);

                et_search.setVisibility(View.INVISIBLE);
            } else {
                isFound = false;
                //Toast.makeText(getActivity(),"No parking by that name in our system !!",Toast.LENGTH_LONG).show();
            }

            if (isFound == false) {
                Toast.makeText(getActivity(), "No parking by that name in our system !!", Toast.LENGTH_LONG).show();
            }

        }

    }

    /**
     * Draw parking(s) onto the GoogleMaps
     *
     * @throws IOException
     */
    public void geoLocation() throws IOException {
        Geocoder gc = new Geocoder(getActivity());
        Parking parking = new Parking();

        List<Parking> parkingList = output.parkingList;
        int parkingSize = parkingList.size();


        for (int i = 0; i < parkingSize; i++) {

            List<Address> list = gc.getFromLocation(parkingList.get(i).Coordinates_ltd, parkingList.get(i).Coordinates_lng, 20);

            if (list.size() != 0) {
                Address address = list.get(0);
                String locality = parkingList.get(i).Parking_City + "\n" + "Number of bays " + parkingList.get(i).Number_Of_bays;


                double lat = address.getLatitude();
                double lng = address.getLongitude();
                LatLng ll = new LatLng(lat, lng);

                //Storing the parking lat-long
                parking.Parking_Name = parkingList.get(i).Parking_Name;
                parking.Coordinates_lng = lng;
                parking.Coordinates_ltd = lat;

                //storing the object in global variable class
                gv.ParkingLatLong.add(parking);


                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(ll);
                markerOptions.title(parkingList.get(i).Parking_Name).snippet(locality);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                OtherOlaces = mGoogleMap.addMarker(markerOptions);

                gotoLocationZoom(ll, 6);
            }


        }


    }

    protected synchronized void buildGoogleApiClient() {
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
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private User userCurrentLocation = null;

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
        markerOptions.title(gv.getFirstname() + " Current Position").snippet(latLng + "");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

        //zoom in the camera to user current location
        gotoLocationZoom(latLng, 14);

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

        //For storing user current location
        userCurrentLocation = new User();
        userCurrentLocation.lat = location.getLatitude();
        userCurrentLocation.lng = location.getLongitude();

        gv.lat = location.getLatitude();
        gv.lng = location.getLongitude();


        //Starting the service, with the current user location.
        intentService = new Intent(getActivity(), ClosestParking.class);
        intentService.putExtra("currentLocation", userCurrentLocation);
        getActivity().startService(intentService);
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
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        }
                    })
                    .create()
                    .show();


        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
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
