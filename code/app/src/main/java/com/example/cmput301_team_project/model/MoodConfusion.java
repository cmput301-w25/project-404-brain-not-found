package com.example.cmput301_team_project.model;

import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.example.cmput301_team_project.R;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

/**
 * Subclass of {@link Mood} that represents emotional state of confusion.
 * Defines some emotional-state-specific attributes, such as display colour and emoji
 */
public class MoodConfusion extends Mood {
    public MoodConfusion(MoodSocialSituationEnum socialSituation, String trigger, String author, String imageBase64, GeoPoint location, String address) {
        super(socialSituation, trigger, author, imageBase64, location, address);
    }

    public MoodConfusion(MoodSocialSituationEnum socialSituation, String trigger, String author, Date date, String imageBase64, GeoPoint location, String address) {
        super(socialSituation, trigger, author, date, imageBase64, location, address);
    }

    @Override
    public MoodEmotionEnum getEmotion() {
        return MoodEmotionEnum.CONFUSION;
    }

    @Override
    @Exclude
    public int getColour() {
        return R.color.confusion;
    }

    @Override
    @Exclude
    public int getEmoji() {
        return R.string.emoji_confusion;
    }

    public int getDisplayName(){
        return R.string.mood_confusion;
    }
}
