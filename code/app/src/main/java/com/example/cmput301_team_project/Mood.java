package com.example.cmput301_team_project;

import com.google.firebase.firestore.Exclude;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

public abstract class Mood {
    private MoodSocialSituationEnum socialSituation;
    private String trigger;
    private String date;
    private final DateFormat dfUTC;
    private final DateFormat dfLocal;

    protected Mood(MoodSocialSituationEnum socialSituation, String trigger) {
        this.socialSituation = socialSituation;
        this.trigger = trigger;

        dfUTC = DateFormat.getTimeInstance();
        dfUTC.setTimeZone(TimeZone.getTimeZone("utc"));

        dfLocal = DateFormat.getTimeInstance();
        dfLocal.setTimeZone(TimeZone.getDefault());

        date = dfUTC.format(new Date());
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

    public MoodSocialSituationEnum getSocialSituation() {
        return socialSituation;
    }

    public String getTrigger() {
        return trigger;
    }

    public String getDate() {
        return date;
    }

    @Exclude
    public String getDateLocal() {
        try {
            return dfLocal.format(dfUTC.parse(date));
        } catch (ParseException e) {
            return "";
        }
    }

    @Exclude
    public abstract int getColour();
    @Exclude
    public abstract int getEmoji();
}
