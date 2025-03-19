package com.example.cmput301_team_project;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

/**
 * Subclass of {@link Mood} that represents emotional state of shame.
 * Defines some emotional-state-specific attributes, such as display colour and emoji
 */
public class MoodShame extends Mood {

    public MoodShame(MoodSocialSituationEnum socialSituation, String trigger, String author, String imageBase64, GeoPoint location) {
        super(socialSituation, trigger, author, imageBase64, location);
    }

    public MoodShame(MoodSocialSituationEnum socialSituation, String trigger, String author, Date date, String imageBase64, GeoPoint location) {
        super(socialSituation, trigger, author, date, imageBase64, location);
    }

    @Override
    public MoodEmotionEnum getEmotion() {
        return MoodEmotionEnum.SHAME;
    }

    @Override
    @Exclude
    public int getColour() {
        return R.color.shame;
    }

    @Override
    @Exclude
    public int getEmoji() {
        return R.string.emoji_shame;
    }

    public int getDisplayName(){
        return R.string.mood_shame;
    }
}
