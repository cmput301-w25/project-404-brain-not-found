package com.example.cmput301_team_project;

import com.google.firebase.firestore.Exclude;

public class MoodAnger extends Mood {
    public MoodAnger(MoodSocialSituationEnum socialSituation, String trigger) {
        super(socialSituation, trigger);
    }

    @Override
    public MoodEmotionEnum getEmotion() {
        return MoodEmotionEnum.ANGER;
    }

    @Override
    @Exclude
    public int getColour() {
        return R.color.anger;
    }

    @Override
    @Exclude
    public int getEmoji() {
        return R.string.emoji_anger;
    }
}
