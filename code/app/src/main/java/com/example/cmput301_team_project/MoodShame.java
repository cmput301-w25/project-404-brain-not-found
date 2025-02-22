package com.example.cmput301_team_project;

public class MoodShame extends Mood {

    public MoodShame(MoodSocialSituationEnum socialSituation, String trigger) {
        super(socialSituation, trigger);
    }

    @Override
    public MoodEmotionEnum getEmotion() {
        return MoodEmotionEnum.SHAME;
    }

    @Override
    public int getColour() {
        return R.color.shame;
    }

    @Override
    public int getEmoji() {
        return R.string.emoji_shame;
    }
}
