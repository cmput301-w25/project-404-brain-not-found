package com.example.cmput301_team_project.utils;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

/**
 * The LocationPermissionManager utility class manages the users location permission on Mood posts.
 *
 * methods involve requesting permission and checking if permission is granted by the user
 */
public class LocationPermissionManager {
    /**
     * Callback interface for handling if permission is granted.
     */
    public interface PermissionGrantedCallback {
        void onPermissionGranted();
    }

    private final String permission = Manifest.permission.ACCESS_FINE_LOCATION;
    private final ActivityResultLauncher<String> locationPermissionActivity;
    private final Fragment fragment;

    /**
     * Creates a new {@code LocationPermissionManager} instance to request for location permission.
     *
     * @param fragment The fragment that will request permission.
     * @param callback The callback to be invoked when permission is/isn't granted.
     */
    public LocationPermissionManager(Fragment fragment, PermissionGrantedCallback callback) {
        this.fragment = fragment;
        locationPermissionActivity = fragment.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (result) {
                        callback.onPermissionGranted();
                    }
                }
        );
    }

    /**
     * Checks if location permission is granted by the user.
     *
     * @return a boolean value based on if permission is granted (true) or not granted (false).
     */
    public boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(fragment.requireContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests location permission from the user.
     */
    public void requestPermission() {
        locationPermissionActivity.launch(permission);
    }
}
