package com.example.cmput301_team_project;

public class MoodAnger extends Mood {
    public MoodAnger(MoodSocialSituationEnum socialSituation, String trigger) {
        super(socialSituation, trigger);
    }

    @Override
    public MoodEmotionEnum getEmotion() {
        return MoodEmotionEnum.ANGER;
    }

    @Override
    public int getColour() {
        return R.color.anger;
    }

    @Override
    public int getEmoji() {
        return R.string.emoji_anger;
    }
}
