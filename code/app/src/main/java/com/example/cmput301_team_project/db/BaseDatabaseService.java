package com.example.cmput301_team_project.db;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Base class of DatabaseService
 * Provides a shared FirebaseFirestore instance for all database services
 */
public abstract class BaseDatabaseService {
    protected FirebaseFirestore db;

    protected BaseDatabaseService() {
        db = FirebaseFirestore.getInstance();
    }

    protected BaseDatabaseService(FirebaseFirestore db) {
        this.db = db;
    }
}
