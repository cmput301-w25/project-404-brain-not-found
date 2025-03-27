package com.example.cmput301_team_project.model;

import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.utils.PlacesUtils;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

/**
 * Represents the state of filters applied to a MoodList.
 *
 * @param time      the date and time to be filtered by (Nullable)
 * @param emotion   the emotion to be filtered by (Nullable)
 * @param text      the text in the trigger/reason to be filtered by (Nullable)
 * @param location  the location to be filtered by (Nullable)
 */
public record MoodFilterState(Integer time, MoodEmotionEnum emotion, String text, GeoPoint location) implements Serializable {
    /**
     * Returns the empty filter state where all filters are set to null/not applied
     *
     * @return The new empty filter state.
     */
    public static MoodFilterState getEmptyFilterState() {
        return new MoodFilterState(null, null, null, null);
    }

    /**
     * Verifies if the Mood has certain filters applicable to it that aren't the result of a
     * Firestore query.
     *
     * @param mood The Mood to be checked.
     * @return a boolean value representing if the Mood has the specified values that aren't able to
     * be filtered by a Firestore query (trigger text and location).
     */
    public boolean verifyNonDatabaseFilters(Mood mood) {
        boolean textFilter = (text == null) || (mood.getTrigger().toLowerCase().contains(text.toLowerCase()));
        boolean locationFilter = (location == null) || (mood.getLocation() != null && PlacesUtils.getDistanceKm(location, mood.getLocation()) < 5);
        return textFilter && locationFilter;
    }
}
