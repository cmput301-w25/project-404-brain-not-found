package com.example.cmput301_team_project.model;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.Exclude;

public class Follow {
    private String follower;
    private String target;
    private String id;

    public Follow(String follower, String target) {
        this(follower, target, null);
    }
    public Follow(String follower, String target, @Nullable String id) {
        this.follower = follower;
        this.target = target;
        this.id = id;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public String getFollower() {
        return follower;
    }

    public String getTarget() {
        return target;
    }
}
