package com.example.cmput301_team_project;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.Exclude;

import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Base class for mood event.
 * Use {@link Mood#createMood(MoodEmotionEnum, MoodSocialSituationEnum, String, Date)} factory method
 * to create the correct subclass instance of the base class
 */
public abstract class Mood {
    private MoodSocialSituationEnum socialSituation;
    private String trigger;
    private Date date;
    private String location;

    protected Mood(MoodSocialSituationEnum socialSituation, String trigger) {
        this(socialSituation, trigger, new Date(), null);
    }

    protected Mood(MoodSocialSituationEnum socialSituation, String trigger, String location) {
        this(socialSituation, trigger, new Date(), location);
    }

    protected Mood(MoodSocialSituationEnum socialSituation, String trigger, Date date, String location){
        this.socialSituation = socialSituation;
        this.trigger = trigger;
        this.date = date;
        this.location = location;
    }

    protected Mood(MoodSocialSituationEnum socialSituation, String trigger, Date date) {
        this.socialSituation = socialSituation;
        this.trigger = trigger;
        this.date = date;
        this.location = null;
    }

    /**
     *
     * @param emotion emotional state of mood event
     * @param socialSituation social situation of mood event
     * @param trigger trigger of mood event
     * @param date date of mood event. If null value is supplied, the current date and time are used
     * @return Mood class instance
     */
    public static Mood createMood(MoodEmotionEnum emotion, MoodSocialSituationEnum socialSituation, String trigger, @Nullable Date date, @Nullable String location) {
            switch(emotion) {
                case ANGER:
                    if (date == null && location == null) return new MoodAnger(socialSituation, trigger);
                    if (date == null) return new MoodAnger(socialSituation, trigger, location);
                    if (location == null) return new MoodAnger(socialSituation, trigger, date);
                    return new MoodAnger(socialSituation, trigger, date, location);
                case CONFUSION:
                    if (date == null && location == null) return new MoodConfusion(socialSituation, trigger);
                    if (date == null) return new MoodConfusion(socialSituation, trigger, location);
                    if (location == null) return new MoodConfusion(socialSituation, trigger, date);
                    return new MoodConfusion(socialSituation, trigger, date, location);
                case DISGUST:
                    if (date == null && location == null) return new MoodDisgust(socialSituation, trigger);
                    if (date == null) return new MoodDisgust(socialSituation, trigger, location);
                    if (location == null) return new MoodDisgust(socialSituation, trigger, date);
                    return new MoodDisgust(socialSituation, trigger, date, location);
                case FEAR:
                    if (date == null && location == null) return new MoodFear(socialSituation, trigger);
                    if (date == null) return new MoodFear(socialSituation, trigger, location);
                    if (location == null) return new MoodFear(socialSituation, trigger, date);
                    return new MoodFear(socialSituation, trigger, date, location);
                case HAPPINESS:
                    if (date == null && location == null) return new MoodHappiness(socialSituation, trigger);
                    if (date == null) return new MoodHappiness(socialSituation, trigger, location);
                    if (location == null) return new MoodHappiness(socialSituation, trigger, date);
                    return new MoodHappiness(socialSituation, trigger, date, location);
                case SADNESS:
                    if (date == null && location == null) return new MoodSadness(socialSituation, trigger);
                    if (date == null) return new MoodSadness(socialSituation, trigger, location);
                    if (location == null) return new MoodSadness(socialSituation, trigger, date);
                    return new MoodSadness(socialSituation, trigger, date, location);
                case SHAME:
                    if (date == null && location == null) return new MoodShame(socialSituation, trigger);
                    if (date == null) return new MoodShame(socialSituation, trigger, location);
                    if (location == null) return new MoodShame(socialSituation, trigger, date);
                    return new MoodShame(socialSituation, trigger, date, location);
                case SURPRISE:
                    if (date == null && location == null) return new MoodSurprise(socialSituation, trigger);
                    if (date == null) return new MoodSurprise(socialSituation, trigger, location);
                    if (location == null) return new MoodSurprise(socialSituation, trigger, date);
                    return new MoodSurprise(socialSituation, trigger, date, location);
            }
            throw new IllegalArgumentException();
        }


        public abstract MoodEmotionEnum getEmotion();
    public abstract int getColour();
    public abstract int getEmoji();

    @Nullable
    public String getLocation() {
        return location;
    }

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
