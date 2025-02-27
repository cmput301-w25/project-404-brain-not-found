package com.example.cmput301_team_project;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

/**
 * Subclass of {@link Mood} that represents emotional state of happiness.
 * Defines some emotional-state-specific attributes, such as display colour and emoji
 */
public class MoodHappiness extends Mood {
    public MoodHappiness(MoodSocialSituationEnum socialSituation, String trigger) {
        super(socialSituation, trigger);
    }
    public MoodHappiness(MoodSocialSituationEnum socialSituation, String trigger, String location) {
        super(socialSituation, trigger, location);
    }
    public MoodHappiness(MoodSocialSituationEnum socialSituation, String trigger, Date date) {
        super(socialSituation, trigger, date);
    }
    public MoodHappiness(MoodSocialSituationEnum socialSituation, String trigger, Date date, String location) {
        super(socialSituation, trigger, date, location);
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
}
