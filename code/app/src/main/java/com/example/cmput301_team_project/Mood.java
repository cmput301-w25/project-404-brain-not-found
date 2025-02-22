package com.example.cmput301_team_project;

import android.content.Context;

import com.google.type.DateTime;

public abstract class Mood {
    private MoodSocialSituationEnum socialSituation;
    private String trigger;
    private DateTime dateTime;

    protected Mood(MoodSocialSituationEnum socialSituation, String trigger) {
        this.socialSituation = socialSituation;
        this.trigger = trigger;
    }

    public static Mood createMood(MoodEmotionEnum emotion, MoodSocialSituationEnum socialSituation, String trigger) {
        switch(emotion) {
            case ANGER:
                return new MoodAnger(socialSituation, trigger);
            case CONFUSION:
                return new MoodConfusion(socialSituation, trigger);
            case DISGUST:
                return new MoodDisgust(socialSituation, trigger);
            case FEAR:
                return new MoodFear(socialSituation, trigger);
            case HAPPINESS:
                return new MoodHappiness(socialSituation, trigger);
            case SADNESS:
                return new MoodSadness(socialSituation, trigger);
            case SHAME:
                return new MoodShame(socialSituation, trigger);
            case SURPRISE:
                return new MoodSurprise(socialSituation, trigger);
        }
        throw new IllegalArgumentException();
    }

    public abstract MoodEmotionEnum getEmotion();
    public abstract int getColour();
    public abstract int getEmoji();
}
