package com.example.cmput301_team_project;

/**
 * Singleton class to manage mood-related operations with the firestore database
 * Handles all mood-related queries
 */
public class MoodDatabaseService extends BaseDatabaseService {
    private static MoodDatabaseService instance = null;

    private MoodDatabaseService() {
        super();
    }

    public static MoodDatabaseService getInstance() {
        if(instance == null) {
            instance = new MoodDatabaseService();
        }
        return instance;
    }
}
