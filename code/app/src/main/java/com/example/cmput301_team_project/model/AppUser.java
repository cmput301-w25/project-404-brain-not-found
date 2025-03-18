package com.example.cmput301_team_project.model;

/**
 * Represents a user in the application with authentication credentials.
 * This class stores the username, password (hashed), and salt for password hashing.
 */
public class AppUser {
    private String username;
    private String password;
    private String salt;

    /**
     * Constructs an AppUser object with the specified username, password, and salt.
     *
     * @param username The username of the user.
     * @param password The hashed password of the user.
     * @param salt The salt used for password hashing.
     */
    public AppUser(String username, String password, String salt){
        this.username = username;
        this.password = password;
        this.salt = salt;
    }
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }
}
