package com.example.cmput301_team_project.model;

import com.google.firebase.firestore.Exclude;

/**
 * Represents a user in the application with authentication credentials.
 * This class stores the username, password (hashed), and salt for password hashing.
 */
public class AppUser {
    private String username;
    private String name;
    private String password;


    /**
     * Constructs an AppUser object with the specified username, password, and salt.
     *
     * @param username The username of the user.
     * @param password The hashed password of the user.
     */
    public AppUser(String username, String name, String password){
        this.username = username;
        this.name = name;
        this.password = password;
    }

    /**
     * Gets the user's username
     * @return A String consisting of the user's username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the usernames hashed password.
     * @return A String consisting of the user's hashed password.
     */
    @Exclude
    public String getPassword() {
        return password;
    }

    /**
     * Gets the users display name
     *
     * @return A String consisting of the user's display name.
     */
    public String getName() {
        return name;
    }

}
