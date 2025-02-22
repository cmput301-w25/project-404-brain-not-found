package com.example.cmput301_team_project;

import com.google.firebase.firestore.Exclude;

public class MoodConfusion extends Mood {
    public MoodConfusion(MoodSocialSituationEnum socialSituation, String trigger) {
        super(socialSituation, trigger);
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
