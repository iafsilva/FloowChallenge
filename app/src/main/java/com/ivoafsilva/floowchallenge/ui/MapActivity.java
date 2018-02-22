package com.ivoafsilva.floowchallenge.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.ivoafsilva.floowchallenge.R;
import com.ivoafsilva.floowchallenge.logic.MyLocationManager;
import com.ivoafsilva.floowchallenge.util.L;

import java.util.Collections;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    // ------------------------ CONSTANTS -------------------------

    /**
     * TAG prefix for logging
     */
    private static final String TAG = MapActivity.class.getSimpleName();

    /**
     * Code used when requesting all permissions
     */
    private static final int PERMISSIONS_REQUEST_CODE = 1;

    /**
     * Permissions to be requested for the app to run properly
     */
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION};

    /**
     * Default camera zoom to be used
     */
    private static final int DEFAULT_ZOOM = 10;

    /**
     * Key to be used when saving/retrieving last known location
     */
    private static final String KEY_LOCATION = "location";

    // ------------------------ VARIABLES -------------------------

    /**
     * Map that will be shown to user
     */
    private GoogleMap mMap;

    /**
     * The geographical location where the device is currently located or the last-known
     * retrieved by Fused Location Provider.
     */
    private Location mLastKnownLocation;

    /**
     * Boolean stating whether the location permission is granted
     */
    private boolean misLocationPermissionGranted;

    /**
     * Listener for location updates
     */
    private MyLocationListener mLocationListener;

    // ------------------------ METHODS -------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.v(TAG, "onCreate started");

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            L.v(TAG, "onCreate loaded from bundle: mLastKnownLocation=%s");
        }
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_map);

        mLocationListener = new MyLocationListener();
        // Build the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        L.v(TAG, "onCreate setup successful");
    }

    /**
     * Saves the state of the activity if paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMap != null) {
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            L.v(TAG, "onSaveInstanceState saved: mLastKnownLocation=%s", mLastKnownLocation);
        }
    }

    /**
     * Callback invoked when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        L.v(TAG, "onMapReady");
        // Prompt the user for permission.
        getLocationPermission();
        // Turn on the My Location layer and the related control on the map.
        updateMyLocationUI();
        // Set last known position in the map
        updateMapWithLastKnownLocation();
    }

    /**
     * Prompts the user for permission to device's location.
     */
    private void getLocationPermission() {
        if (hasPermissions(getApplicationContext(), PERMISSIONS)) {
            L.v(TAG, "getLocationPermission permission is already TRUE");
            misLocationPermissionGranted = true;
            bindLocationListener();
        } else {
            L.v(TAG, "getLocationPermission requesting to user");
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback invoked with Permission Query results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        misLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    misLocationPermissionGranted = true;
                    bindLocationListener();
                } else {
                    Toast.makeText(this, "This application requires Location Permission", Toast.LENGTH_LONG).show();
                }
            }
        }
        L.v(TAG, "onRequestPermissionsResult=%s", misLocationPermissionGranted);
        updateMyLocationUI();
    }

    private void bindLocationListener() {
        L.v(TAG, "bindLocationListener");
        MyLocationManager.bindLocationListenerIn(this, mLocationListener, getApplicationContext());
    }

    /**
     * Updates the map's UI settings based on the user's location permission.
     */
    private void updateMyLocationUI() {
        L.v(TAG, "updateMyLocationUI");
        if (mMap == null) {
            return;
        }

        try {
            if (misLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
            }
        } catch (SecurityException e) {
            L.e(TAG, "Exception: %s", e);
        }
    }

    private void updateMapWithLastKnownLocation() {
        if (misLocationPermissionGranted && mLastKnownLocation != null) {
            L.v(TAG, "updateMapWithLastKnownLocation location=%s", mLastKnownLocation);
            LocationResult locationResult = LocationResult.create(Collections.singletonList(mLastKnownLocation));
            mLocationListener.onLocationResult(locationResult);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private class MyLocationListener extends LocationCallback {

        private final String TAG = MyLocationListener.class.getSimpleName();

        @Override
        public void onLocationResult(LocationResult locationResult) {
            L.v(TAG, "onLocationResult %s", locationResult.getLocations());

            for (Location location : locationResult.getLocations()) {
                if (location == null) {
                    continue;
                }
                mLastKnownLocation = location;
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        }
    }
}
