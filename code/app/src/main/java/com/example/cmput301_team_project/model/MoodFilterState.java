package com.example.cmput301_team_project.model;

import com.example.cmput301_team_project.enums.MoodEmotionEnum;

import java.io.Serializable;

public record MoodFilterState(Integer time, MoodEmotionEnum emotion, String text) implements Serializable {
    public static MoodFilterState getEmptyFilterState() {
        return new MoodFilterState(null, null, null);
    }

    public boolean verifyNonDatabaseFilters(Mood mood) {
        if(text == null) {
            return true;
        }
        return mood.getTrigger().contains(text);
    }
}
