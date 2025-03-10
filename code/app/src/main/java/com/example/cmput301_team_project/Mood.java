package com.example.cmput301_team_project;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

/**
 * Base class for mood event.
 * Use {@link Mood#createMood(MoodEmotionEnum, MoodSocialSituationEnum, String, String, Date, String)} factory method
 * to create the correct subclass instance of the base class
 */
public abstract class Mood implements Serializable {
    private String id; // Firestore mood document ID
    private MoodSocialSituationEnum socialSituation;
    private String trigger;
    private String author;
    private Date date;
    private String imageBase64;

    protected Mood(MoodSocialSituationEnum socialSituation, String trigger, String author, String imageBase64) {
        this(socialSituation, trigger, author, new Date(), imageBase64);
    }

    protected Mood(MoodSocialSituationEnum socialSituation, String trigger, String author, Date date, String imageBase64) {
        this.socialSituation = socialSituation;
        this.trigger = trigger;
        this.date = date;
        this.imageBase64 = imageBase64;
        this.author = author;
    }

    /**
     *
     * @param emotion emotional state of mood event
     * @param socialSituation social situation of mood event
     * @param trigger trigger of mood event
     * @param date date of mood event. If null value is supplied, the current date and time are used
     * @return Mood class instance
     */
    public static Mood createMood(MoodEmotionEnum emotion, MoodSocialSituationEnum socialSituation, String trigger, String author, @Nullable Date date, @Nullable String imageBase64) {
        switch(emotion) {
            case ANGER:
                return date == null ? new MoodAnger(socialSituation, trigger, author, imageBase64) : new MoodAnger(socialSituation, trigger, author, date, imageBase64);
            case CONFUSION:
                return date == null ? new MoodConfusion(socialSituation, trigger, author, imageBase64) : new MoodConfusion(socialSituation, trigger, author, date, imageBase64);
            case DISGUST:
                return date == null ? new MoodDisgust(socialSituation, trigger, author, imageBase64) : new MoodDisgust(socialSituation, trigger, author, date, imageBase64);
            case FEAR:
                return date == null ? new MoodFear(socialSituation, trigger, author, imageBase64) : new MoodFear(socialSituation, trigger, author, date, imageBase64);
            case HAPPINESS:
                return date == null ? new MoodHappiness(socialSituation, trigger, author, imageBase64) : new MoodHappiness(socialSituation, trigger, author, date, imageBase64);
            case SADNESS:
                return date == null ? new MoodSadness(socialSituation, trigger, author, imageBase64) : new MoodSadness(socialSituation, trigger, author, date, imageBase64);
            case SHAME:
                return date == null ? new MoodShame(socialSituation, trigger, author, imageBase64) : new MoodShame(socialSituation, trigger, author, date, imageBase64);
            case SURPRISE:
                return date == null ? new MoodSurprise(socialSituation, trigger, author, imageBase64) : new MoodSurprise(socialSituation, trigger, author, date, imageBase64);
        }
        throw new IllegalArgumentException();
    }

    public abstract MoodEmotionEnum getEmotion();

    public abstract int getColour();

    public abstract int getEmoji();
    public abstract int getDisplayName();

    public MoodSocialSituationEnum getSocialSituation() {
        return socialSituation;
    }

    public String getTrigger() {
        return trigger;
    }

    public Date getDate() {
        return date;
    }

    public Boolean equals(Mood mood){
        if (Objects.equals(this.id, mood.id)){
            return true;
        }
        return false;
    }
    @Exclude
    public String getDateLocal() {
        DateFormat dfLocal = DateFormat.getDateTimeInstance();
        dfLocal.setTimeZone(TimeZone.getDefault());
        return dfLocal.format(date);
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public String getAuthor() {
        return author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSocialSituation(MoodSocialSituationEnum socialSituation) {
        this.socialSituation = socialSituation;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }


}
