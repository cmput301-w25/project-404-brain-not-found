package com.example.cmput301_team_project;

import com.google.firebase.firestore.Exclude;

public class MoodSurprise extends Mood {

    public MoodSurprise(MoodSocialSituationEnum socialSituation, String trigger) {
        super(socialSituation, trigger);
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
