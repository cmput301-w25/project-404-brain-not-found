package com.example.cmput301_team_project.model;

import com.example.cmput301_team_project.enums.FollowRelationshipEnum;
import com.google.firebase.firestore.Exclude;

public class PublicUser {
    private final String username;
    private final String name;
    private final FollowRelationshipEnum followRelationshipWithCurrUser;

    public PublicUser(String username, String name) {
        this(username, name, FollowRelationshipEnum.NONE);
    }

    public PublicUser(String username, String name, FollowRelationshipEnum followRelationshipWithCurrUser) {
        this.username = username;
        this.name = name;
        this.followRelationshipWithCurrUser = followRelationshipWithCurrUser;
    }

    @Exclude
    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getUsernameLower() {
        return username.toLowerCase();
    }

    public String getNameLower() {
        return name.toLowerCase();
    }


    @Exclude
    public FollowRelationshipEnum getFollowRelationshipWithCurrUser() {
        return followRelationshipWithCurrUser;
    }
}
