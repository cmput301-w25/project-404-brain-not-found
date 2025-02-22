package com.example.cmput301_team_project;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

public class MoodConfusion extends Mood {
    public MoodConfusion(MoodSocialSituationEnum socialSituation, String trigger) {
        super(socialSituation, trigger);
    }

    public MoodConfusion(MoodSocialSituationEnum socialSituation, String trigger, Date date) {
        super(socialSituation, trigger, date);
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
