package com.ivoafsilva.floowchallenge;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ivoafsilva.floowchallenge.util.L;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    // ------------------------ CONSTANTS -------------------------

    /**
     * TAG prefix for logging
     */
    private static final String TAG = MapActivity.class.getSimpleName();

    /**
     * Code used when requesting android.permission.ACCESS_FINE_LOCATION
     */
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    /**
     * Default camera zoom to be used
     */
    private static final int DEFAULT_ZOOM = 5;

    /**
     * Key to be used when saving/retrieving camera position
     */
    private static final String KEY_CAMERA_POSITION = "camera_position";

    /**
     * Key to be used when saving/retrieving last known location
     */
    private static final String KEY_LOCATION = "location";

    /**
     * The default location to be used when there are no permissions.
     * Location: The Floow Headquarters
     */
    private static final LatLng DEFAULT_LOCATION = new LatLng(53.3871073, -1.463731);

    // ------------------------ VARIABLES -------------------------

    /**
     * Map that will be shown to user
     */
    private GoogleMap mMap;

    /**
     * Variable that handles Camera Position
     */
    private CameraPosition mCameraPosition;

    /**
     * Google's database of places and businesses information
     */
    private GeoDataClient mGeoDataClient;

    /**
     * Access to and report of device's current place
     */
    private PlaceDetectionClient mPlaceDetectionClient;

    /**
     * Request and receive location updates
     */
    private FusedLocationProviderClient mFusedLocationProviderClient;

    /**
     * The geographical location where the device is currently located or the last-known
     * retrieved by Fused Location Provider.
     */
    private Location mLastKnownLocation;

    /**
     * Boolean stating whether the location permission is granted
     */
    private boolean misLocationPermissionGranted;

    // ------------------------ METHODS -------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.v(TAG, "onCreate started");

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
            L.v(TAG, "onCreate loaded from bundle: mCameraPosition=%s, mLastKnownLocation=%s");
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_map);

        // Init Location clients
        mGeoDataClient = Places.getGeoDataClient(this, null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

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
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            L.v(TAG, "onSaveInstanceState saved: mCameraPosition=%s, mLastKnownLocation=%s");
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
        updateLocationUI();
        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    /**
     * Prompts the user for permission to device's location.
     */
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            misLocationPermissionGranted = true;
            L.v(TAG, "getLocationPermission permission is already TRUE");
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            L.v(TAG, "getLocationPermission requesting to user");
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
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    misLocationPermissionGranted = true;
                }
            }
        }
        L.v(TAG, "onRequestPermissionsResult=%s", misLocationPermissionGranted);
        updateLocationUI();
        getDeviceLocation();
    }

    /**
     * Updates the map's UI settings based on the user's location permission.
     */
    private void updateLocationUI() {
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

    /**
     * Get the best and most recent location of the device.
     * May be null in rare cases when a location is not available.
     */
    private void getDeviceLocation() {
        L.v(TAG, "getDeviceLocation");
        try {
            if (misLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        L.v(TAG, "OnCompleteListener isTaskSuccessful=%s", task.isSuccessful());
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            L.v(TAG, "OnCompleteListener mLastKnownLocation=%s", mLastKnownLocation);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            // or position on the default location
                            L.d(TAG, "Current location is null. Using default=%s", DEFAULT_LOCATION);
                            L.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            L.e(TAG, "Exception: %s", e);
        }
    }
}
