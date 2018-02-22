package com.ivoafsilva.floowchallenge.util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;


public class LocationUtils {

    public static LatLng toLatLng(Location location) {
        if (location == null) {
            return null;
        }
        return new LatLng(location.getLatitude(), location.getLongitude());
    }
}
