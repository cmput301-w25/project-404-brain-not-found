package com.example.cmput301_team_project;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

/**
 * Subclass of {@link Mood} that represents emotional state of sadness.
 * Defines some emotional-state-specific attributes, such as display colour and emoji
 */
public class MoodSadness extends Mood {
    public MoodSadness(MoodSocialSituationEnum socialSituation, String trigger, String imageBase64) {
        super(socialSituation, trigger, imageBase64);
    }

    public MoodSadness(MoodSocialSituationEnum socialSituation, String trigger, Date date, String imageBase64) {
        super(socialSituation, trigger, date, imageBase64);
    }

    @Override
    public MoodEmotionEnum getEmotion() {
        return MoodEmotionEnum.SADNESS;
    }

    @Override
    @Exclude
    public int getColour() {
        return R.color.sadness;
    }

    @Override
    @Exclude
    public int getEmoji() {
        return R.string.emoji_sadness;
    }
}
