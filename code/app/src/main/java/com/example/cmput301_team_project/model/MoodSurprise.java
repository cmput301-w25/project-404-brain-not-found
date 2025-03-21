package com.example.cmput301_team_project.model;

import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.example.cmput301_team_project.R;
import com.google.firebase.firestore.Exclude;

import java.util.Date;

/**
 * Subclass of {@link Mood} that represents emotional state of surprise.
 * Defines some emotional-state-specific attributes, such as display colour and emoji
 */
public class MoodSurprise extends Mood {

    public MoodSurprise(MoodSocialSituationEnum socialSituation, String trigger, boolean isPublic, String author, String imageBase64) {
        super(socialSituation, trigger, isPublic, author, imageBase64);
    }

    public MoodSurprise(MoodSocialSituationEnum socialSituation, String trigger, boolean isPublic, String author, Date date, String imageBase64) {
        super(socialSituation, trigger, isPublic, author, date, imageBase64);
    }

    @Override
    public MoodEmotionEnum getEmotion() {
        return MoodEmotionEnum.SURPRISE;
    }

    @Override
    @Exclude
    public int getColour() {
        return R.color.surprise;
    }

    @Override
    @Exclude
    public int getEmoji() {
        return R.string.emoji_surprise;
    }

    @Exclude
    public int getDisplayName(){
        return R.string.mood_surprise;
    }
}
