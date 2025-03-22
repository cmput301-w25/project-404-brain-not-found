package com.example.cmput301_team_project.db;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.PersistentCacheSettings;

/**
 * Base class of DatabaseService
 * Provides a shared FirebaseFirestore instance for all database services
 */
public abstract class BaseDatabaseService {
    protected FirebaseFirestore db;

    protected BaseDatabaseService() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                .build();

        db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);
    }

    protected BaseDatabaseService(FirebaseFirestore db) {
        this.db = db;
    }
}
