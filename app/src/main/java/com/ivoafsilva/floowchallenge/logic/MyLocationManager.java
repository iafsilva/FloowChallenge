package com.ivoafsilva.floowchallenge.logic;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
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


public class MyLocationManager {

    /**
     * The default location to be used when there are no permissions.
     * Location: The Floow Headquarters
     */
    private static final LatLng DEFAULT_LOCATION = new LatLng(53.3871073, -1.463731);

    /**
     * The minimum update interval, in seconds, to receive location updates.
     */
    private static final long HIGH_ACCURACY_UPDATE_MIN_INTERVAL = 5;

    /**
     * The maximum update interval, in seconds, to receive location updates.
     */
    private static final long HIGH_ACCURACY_UPDATE_MAX_INTERVAL = 5;

    public static void bindLocationListenerIn(LifecycleOwner lifecycleOwner,
                                              LocationCallback listener, Context context) {
        new BoundLocationListener(lifecycleOwner, listener, context);
    }

    private static LocationRequest createLocationRequest() {
        //and setInterval(long) to 5 seconds. This would be appropriate for mapping applications that are showing your location in real-time.
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(TimeUnit.SECONDS.toMillis(HIGH_ACCURACY_UPDATE_MAX_INTERVAL));
        //locationRequest.setFastestInterval(TimeUnit.SECONDS.toMillis(HIGH_ACCURACY_UPDATE_MIN_INTERVAL));
        return locationRequest;
    }

    @SuppressWarnings("MissingPermission")
    public static class BoundLocationListener implements LifecycleObserver {
        /**
         * TAG prefix for logging
         */
        private static final String TAG = BoundLocationListener.class.getSimpleName();
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
        private final LocationCallback mListener;

        BoundLocationListener(LifecycleOwner lifecycleOwner,
                              LocationCallback listener, Context context) {
            mContext = context;
            mListener = listener;
            lifecycleOwner.getLifecycle().addObserver(this);
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        void addLocationListener() {
            L.v(TAG, "addLocationListener");
            //Get the location client
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
            //Get the last known location or a default one while the user waits for location updates
            getLastKnownOrDefaultLocation();
            //Request location updates
            mFusedLocationProviderClient.requestLocationUpdates(createLocationRequest(), mListener, Looper.myLooper());
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        void removeLocationListener() {
            L.v(TAG, "removeLocationListener");
            mFusedLocationProviderClient.removeLocationUpdates(mListener);
            mFusedLocationProviderClient = null;
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
    }
}
