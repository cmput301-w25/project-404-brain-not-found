package com.example.cmput301_team_project;

public class MoodSadness extends Mood {
    public MoodSadness(MoodSocialSituationEnum socialSituation, String trigger) {
        super(socialSituation, trigger);
    }

    @Override
    public MoodEmotionEnum getEmotion() {
        return MoodEmotionEnum.SADNESS;
    }

    @Override
    public int getColour() {
        return R.color.sadness;
    }

    @Override
    public int getEmoji() {
        return R.string.emoji_sadness;
    }
}
