package com.example.cmput301_team_project;

import android.app.Application;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.PersistentCacheSettings;

/**
 * MyApp class extends {@link Application} and initializes
 * Firebase Firestore settings upon application startup.
 */

public class MyApp extends Application {

    /**
     * Called when the application is created.
     * Initializes Firebase Firestore settings, including enabling local caching.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                .build();
        FirebaseFirestore.getInstance().setFirestoreSettings(settings);
    }
}
