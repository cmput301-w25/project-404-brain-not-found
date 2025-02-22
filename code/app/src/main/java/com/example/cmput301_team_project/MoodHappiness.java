package com.example.cmput301_team_project;

import com.google.firebase.firestore.Exclude;

public class MoodHappiness extends Mood {
    public MoodHappiness(MoodSocialSituationEnum socialSituation, String trigger) {
        super(socialSituation, trigger);
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
