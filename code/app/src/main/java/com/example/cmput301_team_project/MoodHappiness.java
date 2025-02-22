package com.example.cmput301_team_project;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

public class MoodHappiness extends Mood {
    public MoodHappiness(MoodSocialSituationEnum socialSituation, String trigger) {
        super(socialSituation, trigger);
    }

    public MoodHappiness(MoodSocialSituationEnum socialSituation, String trigger, Date date) {
        super(socialSituation, trigger, date);
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
