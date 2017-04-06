package com.map.freefood.freefoodmap;

import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

//import android.location.Address; //Not working

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class Map extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, LocationListener
{
    //Map
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    //User's current location variables
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Location mLastLocation;

/*  This is not working
    //Addresses
    private String getAddress(LatLng latLng)
    {
        Geocoder geocoder = new Geocoder(this);
        String addressText = "";
        List<Address> addresses = null;
        Address address = null;
        try
        {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (null != addresses && !addresses.isEmpty())
            {
                address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                {
                    addressText += (i == 0)?address.getAddressLine(i):("\n" + address.getAddressLine(i));
                }
            }
        }
        catch (IOException e)
        {
            return addressText;
        }
    }
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }


    protected void onStart()
    {
        super.onStart();
        mGoogleApiClient.connect();
    }


    protected void onStop()
    {
        super.onStop();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
        {
            mGoogleApiClient.disconnect();
        }
    }


    //-----Fine Location of User-----//
    private void setUpMap()
    {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        //Used to place a marker on the user's location (Not necessary) Note: it doesn't seem to follow where the user goes
        mMap.setMyLocationEnabled(true);
        LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);
        if (null != locationAvailability && locationAvailability.isLocationAvailable())
        {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null)
            {
                LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                //this is where the marker is added. You can also use custom markers (images) to mark the user
                placeMarkerOnMap(currentLocation);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));
            }
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    //-----Food Locations-----//
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng zoomPosition = new LatLng(40.0072, -105.2675);   //This acts as a "dummy" position used for a default zoom position. There should be no marker here

        /*
        The lines below are where we can bring in our coordinates of the food locations.
        We could also bring the other details about the event in at ".title("details")" and .snippet
        Could use arrays for this??
         */
        LatLng location1 = new LatLng(40.0077, -105.2699);
        mMap.addMarker(new MarkerOptions().position(location1).title("Atlas").snippet("Details here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location1));

        LatLng location2 = new LatLng(40.0071, -105.2621);
        mMap.addMarker(new MarkerOptions().position(location2).title("Engineering").snippet("Details Here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location2));

        LatLng location3 = new LatLng(40.0060, -105.2674);
        mMap.addMarker(new MarkerOptions().position(location3).title("Farrand").snippet("Details Here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location3));

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMarkerClickListener(this);                //Possibly use this listener to link to another activity/bring up details/other features

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zoomPosition, 14.5f)); //controls how zoomed in it is in the start. The "dummy" position is used here
    }


    //Places a marker on the users location (Not necessary)
    protected void placeMarkerOnMap (LatLng location)
    {
        MarkerOptions markerOptions = new MarkerOptions().position(location);
        mMap.addMarker(markerOptions);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        setUpMap();

        //-----User's Location-----//
        mMap.setMyLocationEnabled(true);
        LocationAvailability locationAvailability =
                LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);
        if (null != locationAvailability && locationAvailability.isLocationAvailable())
        {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null)
            {
                LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation
                        .getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14.5f));       //This will override our "dummy" position zoom
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
