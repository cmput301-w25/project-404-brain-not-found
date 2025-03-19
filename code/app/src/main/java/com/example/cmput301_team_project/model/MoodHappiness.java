package com.example.cmput301_team_project.model;

import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.example.cmput301_team_project.R;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

/**
 * Subclass of {@link Mood} that represents emotional state of happiness.
 * Defines some emotional-state-specific attributes, such as display colour and emoji
 */
public class MoodHappiness extends Mood {
    public MoodHappiness(MoodSocialSituationEnum socialSituation, String trigger, String author, String imageBase64, GeoPoint location) {
        super(socialSituation, trigger, author, imageBase64, location);
    }

    public MoodHappiness(MoodSocialSituationEnum socialSituation, String trigger, String author, Date date, String imageBase64, GeoPoint location) {
        super(socialSituation, trigger, author, date, imageBase64, location);
    }

    @Override
    public MoodEmotionEnum getEmotion() {
        return MoodEmotionEnum.HAPPINESS;
    }

    @Override
    @Exclude
    public int getColour() {
        return R.color.happiness;
    }

    @Override
    @Exclude
    public int getEmoji() {
        return R.string.emoji_happiness;
    }
    public int getDisplayName(){
        return R.string.mood_happiness;
    }
}
