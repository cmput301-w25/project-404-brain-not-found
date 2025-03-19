package com.example.cmput301_team_project.model;

public class PublicUser {
    private final String username;
    private final String name;

    public PublicUser(String username, String name) {
        this.username = username;
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }
}
