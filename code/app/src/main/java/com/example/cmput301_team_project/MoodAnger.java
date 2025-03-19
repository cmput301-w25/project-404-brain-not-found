package com.example.cmput301_team_project;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

/**
 * Subclass of {@link Mood} that represents emotional state of anger.
 * Defines some emotional-state-specific attributes, such as display colour and emoji
 */
public class MoodAnger extends Mood {
    public MoodAnger(MoodSocialSituationEnum socialSituation, String trigger, String author, String imageBase64, GeoPoint location) {
        super(socialSituation, trigger, author, imageBase64, location);
    }

    public MoodAnger(MoodSocialSituationEnum socialSituation, String trigger, String author, Date date, String imageBase64, GeoPoint location) {
        super(socialSituation, trigger, author, date, imageBase64, location);
    }

    @Override
    public MoodEmotionEnum getEmotion() {
        return MoodEmotionEnum.ANGER;
    }

    @Exclude
    @Override
    public int getColour() {
        return R.color.anger;
    }

    @Override
    @Exclude
    public int getEmoji() {
        return R.string.emoji_anger;
    }

    public int getDisplayName(){
        return R.string.mood_anger;
    }
}
