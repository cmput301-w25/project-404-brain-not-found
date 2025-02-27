package com.example.cmput301_team_project;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

/**
 * Subclass of {@link Mood} that represents emotional state of fear.
 * Defines some emotional-state-specific attributes, such as display colour and emoji
 */
public class MoodFear extends Mood {
    public MoodFear(MoodSocialSituationEnum socialSituation, String trigger, String imageBase64) {
        super(socialSituation, trigger, imageBase64);
    }

    public MoodFear(MoodSocialSituationEnum socialSituation, String trigger, Date date, String imageBase64) {
        super(socialSituation, trigger, date, imageBase64);
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
}
