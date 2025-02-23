package com.example.cmput301_team_project;

import com.google.firebase.firestore.CollectionReference;

/**
 * Singleton class to manage mood-related operations with the firestore database
 * Handles all mood-related queries
 */
public class MoodDatabaseService extends BaseDatabaseService {
    private static MoodDatabaseService instance = null;
    private final CollectionReference moodsRef;

    private MoodDatabaseService() {
        super();

        moodsRef = db.collection("moods");
    }

    public static MoodDatabaseService getInstance() {
        if(instance == null) {
            instance = new MoodDatabaseService();
        }
        return instance;
    }

    public void addMood(Mood mood) {
        moodsRef.add(mood);
    }
}
