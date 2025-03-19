package com.example.cmput301_team_project;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

/**
 * Subclass of {@link Mood} that represents emotional state of fear.
 * Defines some emotional-state-specific attributes, such as display colour and emoji
 */
public class MoodFear extends Mood {
    public MoodFear(MoodSocialSituationEnum socialSituation, String trigger, String author, String imageBase64, GeoPoint location) {
        super(socialSituation, trigger, author, imageBase64, location);
    }

    public MoodFear(MoodSocialSituationEnum socialSituation, String trigger, String author, Date date, String imageBase64, GeoPoint location) {
        super(socialSituation, trigger, author, date, imageBase64, location);
    }

    @Override
    public MoodEmotionEnum getEmotion() {
        return MoodEmotionEnum.FEAR;
    }

    @Override
    @Exclude
    public int getColour() {
        return R.color.fear;
    }

    @Override
    @Exclude
    public int getEmoji() {
        return R.string.emoji_fear;
    }

    public int getDisplayName(){
        return R.string.mood_fear;
    }
}
