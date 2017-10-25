package com.example.dee_kay.myapplication.CustomAdaptors;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.dee_kay.myapplication.R;
import com.example.dee_kay.myapplication.WcfObjects.Parking;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

/**
 * Created by DEE-KAY on 25 Oct 2017.
 */

public class ParkingMapView_Adapter extends ArrayAdapter<Parking> implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
        , LocationListener, View.OnClickListener  {
    GoogleMap mGoogleMap;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Marker OtherOlaces;

    MapView mapView;

    double lat;
    double log;
    String parking_name;
    String parking_city;
    double congestion;

    public ParkingMapView_Adapter(Context context,Parking [] PARKING ) {
        super(context, R.layout.parking_mapview_custom_layout,PARKING);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.parking_mapview_custom_layout,parent,false);

        //Getting the parking information from the database
         parking_name = getItem(position).Parking_Name;
         parking_city = getItem(position).Parking_City;
         congestion = getItem(position).Congestion;
        lat = getItem(position).Coordinates_ltd;
        log = getItem(position).Coordinates_lng;


        mapView = (MapView) customView.findViewById(R.id.listMapView);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }


        return customView;
    }

    /**
     * Handling API client
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext().getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
        mGoogleApiClient.connect();
    }
    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(),
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

    @Override
    public void onLocationChanged(Location location) {

    }

    /**
     * Draw parking(s) onto the GoogleMaps
     *
     * @throws IOException
     */
    public void geoLocation() throws IOException {
        Geocoder gc = new Geocoder(getContext().getApplicationContext());

            List<Address> list = gc.getFromLocation(lat,log, 20);

            if (list.size() != 0) {
                Address address = list.get(0);
                String locality = "Congestion " + congestion + " %";

                double lat = address.getLatitude();
                double lng = address.getLongitude();
                LatLng ll = new LatLng(lat, lng);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(ll);
                markerOptions.title(parking_name).snippet(locality);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                OtherOlaces = mGoogleMap.addMarker(markerOptions);

                gotoLocationZoom(ll, 14);
            }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize google Play Service
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                if (mGoogleApiClient == null) {
                    buildGoogleApiClient();
                }
//                mGoogleMap.setMyLocationEnabled(true);

            } else {
                //Request Location Permission
               // checkLocationPermission();
            }
        } else {
            //buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);

        }

        //Plotting a parking
        try {
            geoLocation();
        } catch (IOException e) {
            e.printStackTrace();
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
        mGoogleMap.animateCamera(updateCamera);
    }

}
