package com.example.cmput301_team_project.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Utility class for retrieving location data and converting to addresses for the location part of
 * Mood posts.
 */
public class PlacesUtils {
    /**
     * Gets the last known location of the user's device.
     *
     * @param context The app context needed for location services.
     * @return A Task containing the last known location of the user's device if permission is
     * granted, returns an exception otherwise.
     */
    public static Task<Location> getLastLocation(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

            return fusedLocationProviderClient.getLastLocation()
                    .continueWith(Task::getResult);
        }

        return Tasks.forResult(null);
    }
    /**
     * Finds the distance (in kilometers) between 2 GeoPoints a and b.
     *
     * @param a The first {@link GeoPoint}.
     * @param b The second {@link GeoPoint}.
     * @return A float value representing the distance between GeoPoints a and b in kilometers.
     */
    public static float getDistanceKm(GeoPoint a, GeoPoint b) {
        float[] results = new float[1];
        Location.distanceBetween(a.getLatitude(), a.getLongitude(), b.getLatitude(), b.getLongitude(), results);

        return results[0] / 1000;
    }

    /**
     * converts latitude and longitude coordinates to a more readable address.
     *
     * @param context The context needed for location services.
     * @param latLng The latitude and longitude coordinates to be converted.
     * @return A String representing the converted address if successful, otherwise an exception is
     * thrown and the latitude and longitude coordinates are returned as a String.
     */
    public static String getAddressFromLatLng(Context context, LatLng latLng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0);
            }
        } catch (IOException e) {
            Log.e("Geocoder Error", "Geocoder failed: " + e.getMessage());
        }
        return String.format(Locale.getDefault(), "%f, %f", latLng.latitude, latLng.longitude);
    }
}
