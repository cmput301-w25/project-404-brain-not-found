package com.example.cmput301_team_project;

public class MoodSurprise extends Mood {

    public MoodSurprise(MoodSocialSituationEnum socialSituation, String trigger) {
        super(socialSituation, trigger);
    }

    @Override
    public MoodEmotionEnum getEmotion() {
        return MoodEmotionEnum.SURPRISE;
    }

    @Override
    public int getColour() {
        return R.color.surprise;
    }

    @Override
    public int getEmoji() {
        return R.string.emoji_surprise;
    }
}
