package com.example.cmput301_team_project.model;

import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.example.cmput301_team_project.R;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

/**
 * Subclass of {@link Mood} that represents emotional state of disgust.
 * Defines some emotional-state-specific attributes, such as display colour and emoji
 */
public class MoodDisgust extends Mood{
    public MoodDisgust(MoodSocialSituationEnum socialSituation, String trigger, boolean isPublic, String author, String imageBase64, GeoPoint location) {
        super(socialSituation, trigger, isPublic, author, imageBase64, location);
    }

    public MoodDisgust(MoodSocialSituationEnum socialSituation, String trigger, boolean isPublic, String author, Date date, String imageBase64, GeoPoint location) {
        super(socialSituation, trigger, isPublic, author, date, imageBase64, location);
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

    @Exclude
    public int getDisplayName(){
        return R.string.mood_disgusted;
    }
}
