package com.example.cmput301_team_project;

public class MoodConfusion extends Mood {
    public MoodConfusion(MoodSocialSituationEnum socialSituation, String trigger) {
        super(socialSituation, trigger);
    }

    @Override
    public MoodEmotionEnum getEmotion() {
        return MoodEmotionEnum.CONFUSION;
    }

    @Override
    public int getColour() {
        return R.color.confusion;
    }

    @Override
    public int getEmoji() {
        return R.string.emoji_confusion;
    }
}
