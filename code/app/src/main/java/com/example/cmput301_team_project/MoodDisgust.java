package com.example.cmput301_team_project;

public class MoodDisgust extends Mood{
    public MoodDisgust(MoodSocialSituationEnum socialSituation, String trigger) {
        super(socialSituation, trigger);
    }

    @Override
    public MoodEmotionEnum getEmotion() {
        return MoodEmotionEnum.DISGUST;
    }

    @Override
    public int getColour() {
        return R.color.disgust;
    }

    @Override
    public int getEmoji() {
        return R.string.emoji_disgust;
    }
}
