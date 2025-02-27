package com.example.cmput301_team_project;

/**
 * Singleton class to manage user-related operations with the firestore database
 * Handles all user-related queries
 */
public class UserDatabaseService extends BaseDatabaseService {
    private static UserDatabaseService instance = null;

    private UserDatabaseService() {
        super();
    }

    public static UserDatabaseService getInstance() {
        if(instance == null) {
            instance = new UserDatabaseService();
        }
        return instance;
    }
}
