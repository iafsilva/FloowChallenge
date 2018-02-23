package com.ivoafsilva.floowchallenge.util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;


/**
 * Class containing utility methods for {@link Location} related operations
 */
public class LocationUtils {
    /**
     * Convert a {@link Location} object to {@link LatLng}
     */
    public static LatLng toLatLng(Location location) {
        if (location == null) {
            return null;
        }
        return new LatLng(location.getLatitude(), location.getLongitude());
    }
}
