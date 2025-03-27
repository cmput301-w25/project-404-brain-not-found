package com.example.cmput301_team_project.enums;

/**
 * Enumeration for the states of a users following relation to another user.
 *
 * NONE         - no relation between users (user hasn't requested to follow another user).
 * REQUESTED    - user has requested to follow another user (requested user will need to accept the request).
 * FOLLOWED     - the user is following the other user
 */
public enum FollowRelationshipEnum {
    NONE,
    REQUESTED,
    FOLLOWED
}
