package com.example.cmput301_team_project.model;

import com.example.cmput301_team_project.enums.FollowRelationshipEnum;
import com.google.firebase.firestore.Exclude;

/**
 * The PublicUser class includes methods and values that represent a public Moodster user.
 *
 * The methods involve getting the public users username and display name, as well as getting and
 * setting user followRelationships.
 */
public class PublicUser {
    private final String username;
    private final String name;
    private FollowRelationshipEnum followRelationshipWithCurrUser;

    public PublicUser(String username, String name) {
        this(username, name, FollowRelationshipEnum.NONE);
    }

    public PublicUser(String username, String name, FollowRelationshipEnum followRelationshipWithCurrUser) {
        this.username = username;
        this.name = name;
        this.followRelationshipWithCurrUser = followRelationshipWithCurrUser;
    }

    /**
     * Gets the current user's username.
     *
     * @return The username as a String.
     */
    @Exclude
    public String getUsername() {
        return username;
    }

    /**
     * Gets the user's display name.
     *
     * @return The user's display name as a String.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the user's username in lowercase.
     *
     * @return The user's username as a lowercase String.
     */
    public String getUsernameLower() {
        return username.toLowerCase();
    }

    /**
     *  Gets the user's display name in lowercase.
     *
     * @return The user's display name as a lowercase String.
     */
    public String getNameLower() {
        return name.toLowerCase();
    }

    /**
     *  Gets the requested user's following relationship with the current user.
     *
     * @return A {@link FollowRelationshipEnum} that represents the current user's follow
     * relationship with the requested user.
     */
    @Exclude
    public FollowRelationshipEnum getFollowRelationshipWithCurrUser() {
        return followRelationshipWithCurrUser;
    }

    /**
     * Sets a new follow relationship between the current user and the requested one.
     *
     * @param followRelationshipWithCurrUser The new {@link FollowRelationshipEnum} representing
     *                                       the new follow relationship between users.
     */
    public void setFollowRelationshipWithCurrUser(FollowRelationshipEnum followRelationshipWithCurrUser) {
        this.followRelationshipWithCurrUser = followRelationshipWithCurrUser;
    }
}
