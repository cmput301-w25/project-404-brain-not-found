package com.example.cmput301_team_project.utils;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class LocationPermissionManager {
    public interface PermissionGrantedCallback {
        void onPermissionGranted();
    }

    private final String permission = Manifest.permission.ACCESS_FINE_LOCATION;
    private final ActivityResultLauncher<String> locationPermissionActivity;
    private final Fragment fragment;

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

    public boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(fragment.requireContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        locationPermissionActivity.launch(permission);
    }
}
