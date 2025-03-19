package com.example.cmput301_team_project;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

/**
 * Subclass of {@link Mood} that represents emotional state of disgust.
 * Defines some emotional-state-specific attributes, such as display colour and emoji
 */
public class MoodDisgust extends Mood{
    public MoodDisgust(MoodSocialSituationEnum socialSituation, String trigger, String author, String imageBase64, GeoPoint location) {
        super(socialSituation, trigger, author, imageBase64, location);
    }

    public MoodDisgust(MoodSocialSituationEnum socialSituation, String trigger, String author, Date date, String imageBase64, GeoPoint location) {
        super(socialSituation, trigger, author, date, imageBase64, location);
    }

    @Override
    public MoodEmotionEnum getEmotion() {
        return MoodEmotionEnum.DISGUST;
    }

    @Override
    @Exclude
    public int getColour() {
        return R.color.disgust;
    }

    @Override
    @Exclude
    public int getEmoji() {
        return R.string.emoji_disgust;
    }

    public int getDisplayName(){
        return R.string.mood_disgusted;
    }
}
