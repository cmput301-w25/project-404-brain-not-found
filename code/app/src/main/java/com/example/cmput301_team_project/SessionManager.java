package com.example.cmput301_team_project;

/**
 * A singleton class that manages the user session.
 * This class stores the currently logged-in user's information and provides access to it.
 */
public class SessionManager {
    private static SessionManager instance = null;
    private String currentUser;

    private SessionManager(){

    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentUser(String currUser){
        currentUser = currUser;
    }

    public String getCurrentUser() {
        return currentUser;
    }


}
