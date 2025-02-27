package com.example.cmput301_team_project;

public class AppUser {
    public String username;
    public String password;
    public String salt;

    public AppUser(String username, String password, String salt){
        this.username = username;
        this.password = password;
        this.salt = salt;
    }
    public String getUsername() {
        return username;
    }

}
