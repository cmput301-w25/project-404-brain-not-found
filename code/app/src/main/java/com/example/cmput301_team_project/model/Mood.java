package com.example.cmput301_team_project.model;

import androidx.annotation.Nullable;

import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

/**
 * Base class for mood event.
 * Use {@link Mood#createMood(MoodEmotionEnum, MoodSocialSituationEnum, String, boolean, String, Date, String, GeoPoint)} factory method
 * to create the correct subclass instance of the base class
 */
public abstract class Mood implements Serializable {
    private String id; // Firestore mood document ID
    private MoodSocialSituationEnum socialSituation;
    private String trigger;
    private boolean isPublic;
    private String author;
    private Date date;
    private String imageBase64;
    private GeoPoint location;

    public Mood() {
        // Required no-arg constructor for Firestore (queries)
    }

    protected Mood(MoodSocialSituationEnum socialSituation, String trigger, boolean isPublic, String author, String imageBase64, GeoPoint location) {
        this(socialSituation, trigger, isPublic, author, new Date(), imageBase64, location);
    }

    protected Mood(MoodSocialSituationEnum socialSituation, String trigger, boolean isPublic, String author, Date date, String imageBase64, GeoPoint location) {
        this.socialSituation = socialSituation;
        this.trigger = trigger;
        this.isPublic = isPublic;
        this.date = date;
        this.imageBase64 = imageBase64;
        this.author = author;
        this.location = location;
    }

    /**
     *
     * @param emotion emotional state of mood event
     * @param socialSituation social situation of mood event
     * @param trigger trigger of mood event
     * @param date date of mood event. If null value is supplied, the current date and time are used
     * @return Mood class instance
     */
    public static Mood createMood(MoodEmotionEnum emotion, MoodSocialSituationEnum socialSituation, String trigger, boolean isPublic, String author, @Nullable Date date, @Nullable String imageBase64, @Nullable GeoPoint location) {
        return switch (emotion) {
            case ANGER ->
                    date == null ? new MoodAnger(socialSituation, trigger, isPublic, author, imageBase64, location) : new MoodAnger(socialSituation, trigger, isPublic, author, date, imageBase64, location);
            case CONFUSION ->
                    date == null ? new MoodConfusion(socialSituation, trigger, isPublic, author, imageBase64, location) : new MoodConfusion(socialSituation, trigger, isPublic, author, date, imageBase64, location);
            case DISGUST ->
                    date == null ? new MoodDisgust(socialSituation, trigger, isPublic, author, imageBase64, location) : new MoodDisgust(socialSituation, trigger, isPublic, author, date, imageBase64, location);
            case FEAR ->
                    date == null ? new MoodFear(socialSituation, trigger, isPublic, author, imageBase64, location) : new MoodFear(socialSituation, trigger, isPublic, author, date, imageBase64, location);
            case HAPPINESS ->
                    date == null ? new MoodHappiness(socialSituation, trigger, isPublic, author, imageBase64, location) : new MoodHappiness(socialSituation, trigger, isPublic, author, date, imageBase64, location);
            case SADNESS ->
                    date == null ? new MoodSadness(socialSituation, trigger, isPublic, author, imageBase64, location) : new MoodSadness(socialSituation, trigger, isPublic, author, date, imageBase64, location);
            case SHAME ->
                    date == null ? new MoodShame(socialSituation, trigger, isPublic, author, imageBase64, location) : new MoodShame(socialSituation, trigger, isPublic, author, date, imageBase64, location);
            case SURPRISE ->
                    date == null ? new MoodSurprise(socialSituation, trigger, isPublic, author, imageBase64, location) : new MoodSurprise(socialSituation, trigger, isPublic, author, date, imageBase64, location);
            default -> throw new IllegalArgumentException();
        };
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

    @Exclude
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


    public boolean isPublic() {
        return isPublic;
    }

    public GeoPoint getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object obj){
        Mood mood = (Mood) obj;
        if (Objects.equals(this.getId(), mood.getId())){
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
