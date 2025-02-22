package com.example.cmput301_team_project;

public class MoodHappiness extends Mood {
    public MoodHappiness(MoodSocialSituationEnum socialSituation, String trigger) {
        super(socialSituation, trigger);
    }

    @Override
    public MoodEmotionEnum getEmotion() {
        return MoodEmotionEnum.HAPPINESS;
    }

    @Override
    public int getColour() {
        return R.color.happiness;
    }

    @Override
    public int getEmoji() {
        return R.string.emoji_happiness;
    }
}
