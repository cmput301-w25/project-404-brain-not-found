package com.example.cmput301_team_project;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.Exclude;

import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

public abstract class Mood {
    private MoodSocialSituationEnum socialSituation;
    private String trigger;
    private Date date;

    protected Mood(MoodSocialSituationEnum socialSituation, String trigger) {
        this(socialSituation, trigger, new Date());
    }

    protected Mood(MoodSocialSituationEnum socialSituation, String trigger, Date date) {
        this.socialSituation = socialSituation;
        this.trigger = trigger;
        this.date = date;
    }

    public static Mood createMood(MoodEmotionEnum emotion, MoodSocialSituationEnum socialSituation, String trigger, @Nullable Date date) {
        switch(emotion) {
            case ANGER:
                return date == null ? new MoodAnger(socialSituation, trigger) : new MoodAnger(socialSituation, trigger, date);
            case CONFUSION:
                return date == null ? new MoodConfusion(socialSituation, trigger) : new MoodConfusion(socialSituation, trigger, date);
            case DISGUST:
                return date == null ? new MoodDisgust(socialSituation, trigger) : new MoodDisgust(socialSituation, trigger, date);
            case FEAR:
                return date == null ? new MoodFear(socialSituation, trigger) : new MoodFear(socialSituation, trigger, date);
            case HAPPINESS:
                return date == null ? new MoodHappiness(socialSituation, trigger) : new MoodHappiness(socialSituation, trigger, date);
            case SADNESS:
                return date == null ? new MoodSadness(socialSituation, trigger) : new MoodSadness(socialSituation, trigger, date);
            case SHAME:
                return date == null ? new MoodShame(socialSituation, trigger) : new MoodShame(socialSituation, trigger, date);
            case SURPRISE:
                return date == null ? new MoodSurprise(socialSituation, trigger) : new MoodSurprise(socialSituation, trigger, date);
        }
        throw new IllegalArgumentException();
    }

    public abstract MoodEmotionEnum getEmotion();
    public abstract int getColour();
    public abstract int getEmoji();

    public MoodSocialSituationEnum getSocialSituation() {
        return socialSituation;
    }

    public String getTrigger() {
        return trigger;
    }

    public Date getDate() {
        return date;
    }

    @Exclude
    public String getDateLocal() {
        DateFormat dfLocal = DateFormat.getDateTimeInstance();
        dfLocal.setTimeZone(TimeZone.getDefault());
        return dfLocal.format(date);
    }
}
