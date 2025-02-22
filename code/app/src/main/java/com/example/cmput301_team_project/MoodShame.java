package com.example.cmput301_team_project;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

public class MoodShame extends Mood {

    public MoodShame(MoodSocialSituationEnum socialSituation, String trigger) {
        super(socialSituation, trigger);
    }

    public MoodShame(MoodSocialSituationEnum socialSituation, String trigger, Date date) {
        super(socialSituation, trigger, date);
    }

    @Override
    public MoodEmotionEnum getEmotion() {
        return MoodEmotionEnum.SHAME;
    }

    @Override
    @Exclude
    public int getColour() {
        return R.color.shame;
    }

    @Override
    @Exclude
    public int getEmoji() {
        return R.string.emoji_shame;
    }
}
