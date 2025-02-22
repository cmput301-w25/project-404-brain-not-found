package com.example.cmput301_team_project;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

public class MoodSadness extends Mood {
    public MoodSadness(MoodSocialSituationEnum socialSituation, String trigger) {
        super(socialSituation, trigger);
    }

    public MoodSadness(MoodSocialSituationEnum socialSituation, String trigger, Date date) {
        super(socialSituation, trigger, date);
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
