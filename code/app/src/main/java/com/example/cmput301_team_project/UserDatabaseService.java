package com.example.cmput301_team_project;

import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;

/**
 * Singleton class to manage user-related operations with the firestore database
 * Handles all user-related queries
 */
public class UserDatabaseService extends BaseDatabaseService {
    private static UserDatabaseService instance = null;
    private final CollectionReference usersRef;

    private UserDatabaseService() {
        super();

        usersRef = db.collection("users");
    }

    public static UserDatabaseService getInstance() {
        if(instance == null) {
            instance = new UserDatabaseService();
        }
        return instance;
    }

    public void addUser(AppUser user){
        usersRef.add(user);
    }
}
