package com.example.cmput301_team_project;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

/**
 * Subclass of {@link Mood} that represents emotional state of happiness.
 * Defines some emotional-state-specific attributes, such as display colour and emoji
 */
public class MoodHappiness extends Mood {
    public MoodHappiness(MoodSocialSituationEnum socialSituation, String trigger, String author, String imageBase64) {
        super(socialSituation, trigger, author, imageBase64);
    }

    public MoodHappiness(MoodSocialSituationEnum socialSituation, String trigger, String author, Date date, String imageBase64) {
        super(socialSituation, trigger, author, date, imageBase64);
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
