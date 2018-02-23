package com.ivoafsilva.floowchallenge.ui;

import android.Manifest;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ivoafsilva.floowchallenge.BuildConfig;
import com.ivoafsilva.floowchallenge.R;
import com.ivoafsilva.floowchallenge.ViewModelFactory;
import com.ivoafsilva.floowchallenge.logic.LocationController;
import com.ivoafsilva.floowchallenge.util.L;
import com.ivoafsilva.floowchallenge.util.LocationUtils;
import com.ivoafsilva.floowchallenge.viewmodel.MapViewModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    // ------------------------------------ CONSTANTS -----------------------------------------

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
    private static final int DEFAULT_ZOOM = 13;

    /**
     * Key to be used when saving/retrieving whether the app has all the required permissions
     */
    private static final String KEY_HAS_PERMISSIONS = "has_all_permissions";

    /**
     * Key to be used when saving/retrieving tracking state
     */
    private static final String KEY_TRACKING_ENABLED = "tracking_enabled";
    /**
     * Key to be used when saving/retrieving last user locations
     */
    private static final String KEY_USER_LOCATIONS = "user_locations";

    // ------------------------------------ VARIABLES -----------------------------------------

    /**
     * The {@link AndroidViewModel} to use in this Activity
     */
    private MapViewModel mMapViewModel;
    /**
     * Map that will be shown to user
     */
    private GoogleMap mMap;

    /**
     * Polyline to be used when drawing user's path on {@code mMap}
     */
    private Polyline mPolyline;

    /**
     * List of coordinates to be drawn in the map.
     */
    private List<LatLng> mUserLocations;

    /**
     * Listener for location updates
     */
    private MyLocationListener mLocationListener;

    /**
     * Controller for subscribing and unsubscribing location updates
     */
    private LocationController mLocationController;

    /**
     * Boolean stating whether the location permission is granted
     */
    private boolean mHasLocationPermission;

    /**
     * Boolean stating whether the app is tracking device's location
     */
    private boolean mIsTrackingEnabled;

    // ------------------------------------ METHODS -----------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            L.v(TAG, "onCreate Strict Mode Activated");
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectCustomSlowCalls().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectActivityLeaks().detectLeakedClosableObjects().detectLeakedRegistrationObjects().penaltyLog().build());
        }
        //init vars
        mLocationController = new LocationController(this);
        mUserLocations = new ArrayList<>();
        // Retrieve saved instance state.
        if (savedInstanceState != null) {
            mIsTrackingEnabled = savedInstanceState.getBoolean(KEY_TRACKING_ENABLED, false);
            mHasLocationPermission = savedInstanceState.getBoolean(KEY_HAS_PERMISSIONS, false);
            mUserLocations = savedInstanceState.getParcelableArrayList(KEY_USER_LOCATIONS);
            L.v(TAG, "onCreate loaded from bundle: mIsTrackingEnabled=%s, mHasLocationPermission=%s, mUserLocations=%s", mIsTrackingEnabled, mHasLocationPermission, mUserLocations);
        }
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_map);

        ViewModelFactory factory = ViewModelFactory.getInstance(this.getApplication());
        mMapViewModel = ViewModelProviders.of(this, factory).get(MapViewModel.class);
        // Build the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Set the toggle button with the previous value
        ((ToggleButton) findViewById(R.id.tracking_toggle)).setChecked(mIsTrackingEnabled);
        L.v(TAG, "onCreate setup successful");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //if tracking was enabled before, keep it that way
        if (mHasLocationPermission && mIsTrackingEnabled) {
            subscribeToLocationEvents();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //if tracking was enabled, unsubscribe
        if (mHasLocationPermission && mIsTrackingEnabled) {
            unsubscribeToLocationEvents();
        }
    }

    /**
     * Saves the state of the activity if paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMap != null) {
            outState.putBoolean(KEY_TRACKING_ENABLED, mIsTrackingEnabled);
            outState.putBoolean(KEY_HAS_PERMISSIONS, mHasLocationPermission);
            outState.putParcelableArrayList(KEY_USER_LOCATIONS, (ArrayList<? extends Parcelable>) mUserLocations);
            L.v(TAG, "onSaveInstanceState saved: mIsTrackingEnable=%s, mHasLocationPermission=%s, mUserLocations=%s", mIsTrackingEnabled, mHasLocationPermission, mUserLocations);
        }
    }

    /**
     * Callback invoked when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        L.v(TAG, "onMapReady");
        mMap = googleMap;
        mPolyline = googleMap.addPolyline(new PolylineOptions());
        if (!mUserLocations.isEmpty()) {
            mPolyline.setPoints(mUserLocations);
        }
        // Prompt the user for permission.
        getLocationPermission();
        // Show button to track device's location
        showTrackingToggle();
        // Turn on the My Location layer and the related control on the map.
        updateMyLocationUI();
    }

    /**
     * Prompts the user for permission to device's location.
     */
    private void getLocationPermission() {
        if (hasPermissions(getApplicationContext(), PERMISSIONS)) {
            L.v(TAG, "getLocationPermission permission is already TRUE");
            mHasLocationPermission = true;
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
        mHasLocationPermission = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mHasLocationPermission = true;
                } else {
                    Toast.makeText(this, R.string.error_permission_location_required, Toast.LENGTH_LONG).show();
                }
            }
        }
        L.v(TAG, "onRequestPermissionsResult=%s", mHasLocationPermission);
        showTrackingToggle();
        updateMyLocationUI();
    }

    private void subscribeToLocationEvents() {
        mLocationListener = new MyLocationListener(this);
        mLocationController.subscribeToLocationEvents(mLocationListener);
    }

    private void unsubscribeToLocationEvents() {
        mLocationController.unsubscribeToLocationEvents();
        mLocationListener = null;
    }

    public void showTrackingToggle() {
        if (mHasLocationPermission) {
            findViewById(R.id.tracking_toggle).setVisibility(View.VISIBLE);
        }
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
            if (mHasLocationPermission) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        } catch (SecurityException e) {
            L.e(TAG, "Exception: %s", e);
        }
    }

    /**
     * Function called when the Tracking toggle button is pressed.
     */
    public void onTrackingToggleClicked(View view) {
        mIsTrackingEnabled = ((ToggleButton) view).isChecked();
        if (mIsTrackingEnabled) {
            subscribeToLocationEvents();
            mMapViewModel.startJourney();
        } else {
            mMapViewModel.endJourney();
            mUserLocations.clear();
            unsubscribeToLocationEvents();
        }
    }

    private GoogleMap getMap() {
        return mMap;
    }

    private MapViewModel getMapViewModel() {
        return mMapViewModel;
    }

    public Polyline getPolyline() {
        return mPolyline;
    }

    public List<LatLng> getUserLocations() {
        return mUserLocations;
    }

    // ------------------------------------ STATIC METHODS -----------------------------------------

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

    // ------------------------------------ STATIC CLASSES -----------------------------------------

    /**
     * Listener in charge of updating the UI when Location Updates arrive.
     */
    private static class MyLocationListener extends LocationCallback {
        /**
         * TAG prefix for logging
         */
        private final String TAG = MyLocationListener.class.getSimpleName();
        /**
         * Parent activity to access the Map. {@link WeakReference} so it doesn't leak.
         */
        WeakReference<MapActivity> mapActivityReference;

        MyLocationListener(MapActivity activity) {
            mapActivityReference = new WeakReference<>(activity);
        }

        @Override
        public void onLocationResult(LocationResult locationResult) {
            MapActivity mapActivity = mapActivityReference.get();
            if (mapActivity == null) {
                L.w(TAG, "onLocationResult called and MapActivity IS NULL. Returning.");
                return;
            }
            L.v(TAG, "onLocationResult %s", locationResult.getLocations());

            for (Location location : locationResult.getLocations()) {
                if (location == null) {
                    continue;
                }
                //Add location to ViewModel to be persisted
                mapActivity.getMapViewModel().addJourneyStep(location);
                //Add location to be drawn as the user's path
                mapActivity.getUserLocations().add(LocationUtils.toLatLng(location));
            }

            Location lastLocation = locationResult.getLastLocation();
            mapActivity.getPolyline().setPoints(mapActivity.getUserLocations());
            mapActivity.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), DEFAULT_ZOOM));
        }
    }
}
