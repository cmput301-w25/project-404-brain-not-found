package com.example.cmput301_team_project;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Singleton class to manage the connection to the Firestore database.
 * Provides a single instance of the database service throughout the app.
 * Handles all queries to the database.
 */
public class DatabaseService {
    private static DatabaseService instance = null;
    private FirebaseFirestore db;

    private DatabaseService()
    {
        db = FirebaseFirestore.getInstance();
    }

    public DatabaseService getInstance()
    {
        if(instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }
}
