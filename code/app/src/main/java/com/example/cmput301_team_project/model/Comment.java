package com.example.cmput301_team_project.model;

import java.util.Date;

public class Comment {
    public String username;
    public String text;

    public String getUsername() {
        return username;
    }

    public String getText() {
        return text;
    }

    public Comment(String username, String text){
       this.username = username;
       this.text = text;
    }
}
