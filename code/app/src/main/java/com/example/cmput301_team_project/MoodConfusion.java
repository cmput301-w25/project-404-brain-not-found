package com.example.cmput301_team_project;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

/**
 * Subclass of {@link Mood} that represents emotional state of confusion.
 * Defines some emotional-state-specific attributes, such as display colour and emoji
 */
public class MoodConfusion extends Mood {
    public MoodConfusion(MoodSocialSituationEnum socialSituation, String trigger) {
        super(socialSituation, trigger);
    }
    public MoodConfusion(MoodSocialSituationEnum socialSituation, String trigger, String location) {
        super(socialSituation, trigger, location);
    }
    public MoodConfusion(MoodSocialSituationEnum socialSituation, String trigger, Date date) {
        super(socialSituation, trigger, date);
    }
    public MoodConfusion(MoodSocialSituationEnum socialSituation, String trigger, Date date, String location) {
        super(socialSituation, trigger, date, location);
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
}
