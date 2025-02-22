package com.example.cmput301_team_project;

import com.google.firebase.firestore.Exclude;

public class MoodFear extends Mood {
    public MoodFear(MoodSocialSituationEnum socialSituation, String trigger) {
        super(socialSituation, trigger);
    }

    @Override
    public MoodEmotionEnum getEmotion() {
        return MoodEmotionEnum.FEAR;
    }

    @Override
    @Exclude
    public int getColour() {
        return R.color.fear;
    }

    @Override
    @Exclude
    public int getEmoji() {
        return R.string.emoji_fear;
    }
}
