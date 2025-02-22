package com.example.cmput301_team_project;

import com.google.firebase.firestore.Exclude;

public class MoodSadness extends Mood {
    public MoodSadness(MoodSocialSituationEnum socialSituation, String trigger) {
        super(socialSituation, trigger);
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
