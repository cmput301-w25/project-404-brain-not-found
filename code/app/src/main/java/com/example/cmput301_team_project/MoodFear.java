package com.example.cmput301_team_project;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

public class MoodFear extends Mood {
    public MoodFear(MoodSocialSituationEnum socialSituation, String trigger) {
        super(socialSituation, trigger);
    }

    public MoodFear(MoodSocialSituationEnum socialSituation, String trigger, Date date) {
        super(socialSituation, trigger, date);
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
