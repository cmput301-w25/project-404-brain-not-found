package com.example.cmput301_team_project;

public class MoodFear extends Mood {
    public MoodFear(MoodSocialSituationEnum socialSituation, String trigger) {
        super(socialSituation, trigger);
    }

    @Override
    public MoodEmotionEnum getEmotion() {
        return MoodEmotionEnum.FEAR;
    }

    @Override
    public int getColour() {
        return R.color.fear;
    }

    @Override
    public int getEmoji() {
        return R.string.emoji_fear;
    }
}
