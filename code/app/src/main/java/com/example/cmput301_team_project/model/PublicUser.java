package com.example.cmput301_team_project.model;

import com.example.cmput301_team_project.enums.FollowRelationshipEnum;

public class PublicUser {
    private final String username;
    private final String name;
    private final FollowRelationshipEnum followRelationshipWithCurrUser;

    public PublicUser(String username, String name, FollowRelationshipEnum followRelationshipWithCurrUser) {
        this.username = username;
        this.name = name;
        this.followRelationshipWithCurrUser = followRelationshipWithCurrUser;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public FollowRelationshipEnum getFollowRelationshipWithCurrUser() {
        return followRelationshipWithCurrUser;
    }
}
