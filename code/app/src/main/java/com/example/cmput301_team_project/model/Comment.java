package com.example.cmput301_team_project.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

/**
 * The Comment class represents a type of post that users can leave on a (usually different) user's
 * Mood post.
 *
 * The methods involve getting the Comment's respective user that posted the comment, and the comment's
 * content.
 */
public class Comment {
    public String username;
    public String text;
    public Timestamp date;

    public Comment(String username, String text) {
        this.username = username;
        this.text = text;
    }

    /**
     * Gets the username of the user that posted the Comment.
     *
     * @return The comment poster's username as a String.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the Comment text posted by the user.
     *
     * @return The comment's text content as a String.
     */
    public String getText() {
        return text;
    }

    public void setTimestamp(Timestamp date) {
        this.date = date;
    }


}

