package com.ivoafsilva.floowchallenge.logic;

import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ivoafsilva.floowchallenge.util.L;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * A controller for handling location related events.
 */
@SuppressWarnings("MissingPermission")
public class LocationController {
    // ------------------------------------ CONSTANTS -----------------------------------------
    /**
     * TAG prefix for logging
     */
    private static final String TAG = LocationController.class.getSimpleName();

    /**
     * The default location to be used when there are no permissions.
     * Location: The Floow Headquarters
     */
    private static final LatLng DEFAULT_LOCATION = new LatLng(53.3871073, -1.463731);

    /**
     * The maximum update interval, in seconds, to receive location updates.
     */
    private static final long HIGH_ACCURACY_UPDATE_MAX_INTERVAL = 5;
    // ------------------------------------ VARIABLES -----------------------------------------
    /**
     * Context to use
     */
    private final Context mContext;

    /**
     * Request and receive location updates
     */
    private FusedLocationProviderClient mFusedLocationProviderClient;

    /**
     * Listener to call when there are location updates
     */
    private LocationCallback mListener;

    // ------------------------------------ METHODS -----------------------------------------
    public LocationController(Context context) {
        //Force getting application context
        mContext = context.getApplicationContext();
    }

    /**
     * Gets the FusedLocationProvider and request for location updates
     */
    private void addLocationListener() {
        L.v(TAG, "addLocationListener Adding location listener");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        getLastKnownOrDefaultLocation();
        mFusedLocationProviderClient.requestLocationUpdates(createLocationRequest(), mListener, Looper.myLooper());
    }

    /**
     * Stops the location updates
     */
    private void removeLocationListener() {
        if (mListener != null && mFusedLocationProviderClient != null) {
            L.v(TAG, "removeLocationListener  Removing location listener");
            mFusedLocationProviderClient.removeLocationUpdates(mListener);
        } else {
            L.v(TAG, "removeLocationListener Attempting to remove listener but couldn't. mListener=%s, mFusedLocationClient=%s", mListener, mFusedLocationProviderClient);
        }
    }

    /**
     * Get the best and most recent location of the device.
     * May be null in rare cases when a location is not available.
     */
    private void getLastKnownOrDefaultLocation() {
        L.v(TAG, "getLastKnownOrDefaultLocation");
        Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                L.v(TAG, "OnCompleteListener isTaskSuccessful=%s result=%s", task.isSuccessful(), task.getResult());
                Location lastLocation = task.getResult();
                if (!task.isSuccessful() || lastLocation == null) {
                    L.v(TAG, "OnCompleteListener using DEFAULT_LOCATION=%s", DEFAULT_LOCATION);
                    lastLocation = new Location(TAG);
                    lastLocation.setLongitude(DEFAULT_LOCATION.longitude);
                    lastLocation.setLatitude(DEFAULT_LOCATION.latitude);
                }
                LocationResult locationResult = LocationResult.create(Collections.singletonList(lastLocation));
                mListener.onLocationResult(locationResult);
            }
        });
    }

    /**
     * Subscribes the received listener for Location Updates.
     */
    public void subscribeToLocationEvents(LocationCallback listener) {
        if (listener == null) {
            L.v(TAG, "subscribeToLocationEvents listener received is null. returning");
            return;
        }
        L.v(TAG, "subscribeToLocationEvents");
        mListener = listener;
        addLocationListener();
    }

    /**
     * Unsubscribes the previous listener from Location Updates and clears variables.
     */
    public void unsubscribeToLocationEvents() {
        L.v(TAG, "unsubscribeToLocationEvents");
        removeLocationListener();
        mFusedLocationProviderClient = null;
        mListener = null;
    }

    // ------------------------------------ STATIC METHODS -----------------------------------------

    /**
     * Static function to create a LocationRequest.
     * <p>Mapping applications that are showing your location in real-time:
     * <ul><li>setPriority to PRIORITY_HIGH_ACCURACY</li><li>setInterval to 5 seconds</li></ul></p>
     * <p>Applications that want updates but with low power impact.
     * <ul><li>setPriority to PRIORITY_BALANCED_POWER_ACCURACY</li> <li>setInterval 60 minutes (e.g)</li><li>setFastestInterval 1 minutes (e.g)</li></ul></p>
     * <p>Applications that have no requirement for location, but can take advantage when available:
     * <ul><li>setPriority to PRIORITY_NO_POWER</li></ul></p>
     */
    private static LocationRequest createLocationRequest() {
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(TimeUnit.SECONDS.toMillis(HIGH_ACCURACY_UPDATE_MAX_INTERVAL));
        return locationRequest;
    }
}
