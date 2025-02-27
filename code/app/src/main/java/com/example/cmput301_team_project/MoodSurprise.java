package com.example.cmput301_team_project;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

/**
 * Subclass of {@link Mood} that represents emotional state of surprise.
 * Defines some emotional-state-specific attributes, such as display colour and emoji
 */
public class MoodSurprise extends Mood {

    public MoodSurprise(MoodSocialSituationEnum socialSituation, String trigger) {
        super(socialSituation, trigger);
    }

    public MoodSurprise(MoodSocialSituationEnum socialSituation, String trigger, String location) {
        super(socialSituation, trigger, location);
    }

    public MoodSurprise(MoodSocialSituationEnum socialSituation, String trigger, Date date) {
        super(socialSituation, trigger, date);
    }
    public MoodSurprise(MoodSocialSituationEnum socialSituation, String trigger, Date date, String location) {
        super(socialSituation, trigger, date, location);
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
}
