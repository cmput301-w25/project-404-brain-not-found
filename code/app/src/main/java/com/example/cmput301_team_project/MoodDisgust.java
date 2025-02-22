package com.example.cmput301_team_project;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

/**
 * Subclass of {@link Mood} that represents emotional state of disgust.
 * Defines some emotional-state-specific attributes, such as display colour and emoji
 */
public class MoodDisgust extends Mood{
    public MoodDisgust(MoodSocialSituationEnum socialSituation, String trigger) {
        super(socialSituation, trigger);
    }

    public MoodDisgust(MoodSocialSituationEnum socialSituation, String trigger, Date date) {
        super(socialSituation, trigger, date);
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
}
