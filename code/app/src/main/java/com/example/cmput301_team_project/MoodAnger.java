package com.example.cmput301_team_project;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

public class MoodAnger extends Mood {
    public MoodAnger(MoodSocialSituationEnum socialSituation, String trigger) {
        super(socialSituation, trigger);
    }

    public MoodAnger(MoodSocialSituationEnum socialSituation, String trigger, Date date) {
        super(socialSituation, trigger, date);
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
