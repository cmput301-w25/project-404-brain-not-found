package com.example.cmput301_team_project.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;


public class Comment {
    public String username;
    public String text;
    public Timestamp date;
    public Comment(String username, String text){
       this.username = username;
       this.text = text;
    }

    public String getUsername() {
        return username;
    }

    public String getText() {
        return text;
    }

    public void setTimestamp(Timestamp date) {
        this.date = date;
    }
}
