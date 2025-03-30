package com.example.cmput301_team_project.model;

import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.utils.PlacesUtils;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

public record MoodFilterState(Integer time, MoodEmotionEnum emotion, String text, GeoPoint location) implements Serializable {
    public static MoodFilterState getEmptyFilterState() {
        return new MoodFilterState(null, null, null, null);
    }

    public boolean verifyNonDatabaseFilters(Mood mood) {
        boolean textFilter = (text == null) || (mood.getTrigger().toLowerCase().contains(text.toLowerCase()));
        boolean locationFilter = (location == null) || (mood.getLocation() != null && PlacesUtils.getDistanceKm(location, mood.getLocation()) < 5);
        return textFilter && locationFilter;
    }
}
