package com.example.cmput301_team_project.model;

import java.util.Date;

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

    /**
     * Constructs a new Comment object based on the username and text.
     *
     * @param username The username of the user posting the comment.
     * @param text The text/content of the comment to be posted.
     */
    public Comment(String username, String text){
       this.username = username;
       this.text = text;
    }
}
