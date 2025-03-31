package com.example.cmput301_team_project.model;

import androidx.annotation.Nullable;

import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

/**
 * The Mood class is the base class for all Mood posts the user can make.
 *
 * Methods involve creating, setting, and getting contents of the Mood post.
 * NOTE: Use the {@link Mood#createMood(MoodEmotionEnum, MoodSocialSituationEnum, String, boolean, String, Date, String, GeoPoint)} factory method
 * to create the correct subclass instance of the base class.
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

    /**
     * Required no-arg constructor for Firestore (queries)
     */
    public Mood() {
        // Required no-arg constructor for Firestore (queries)
    }

    protected Mood(MoodSocialSituationEnum socialSituation, String trigger, boolean isPublic, String author, String imageBase64, GeoPoint location) {
        this(socialSituation, trigger, isPublic, author, new Date(), imageBase64, location);
    }

    /**
     * Constructor for the base Mood class, with a specified date and time for the Mood.
     *
     * @param socialSituation   a {@link MoodSocialSituationEnum} representing the users social situation.
     * @param trigger           a String containing the user's reason why they are the Mood they're posting.
     * @param isPublic          a boolean statement representing if the post is publicly available.
     * @param author            a String of the user's username that is posting the Mood.
     * @param date              The specified date and time of the Mood post.
     * @param imageBase64       a base64-encoded String representing an image that the user can have with their Mood
     *                          post.
     * @param location          a {@link GeoPoint} showing the user's location where they posted the Mood.
     */
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
     * Method to create specific Mood posts based on the user's specified emotion.
     * @param emotion           emotional state of mood event
     * @param socialSituation   a {@link MoodSocialSituationEnum} representing the users social situation.
     * @param trigger           a String containing the user's reason why they are the Mood they're posting.
     * @param isPublic          a boolean statement representing if the post is publicly available.
     * @param author            a String of the user's username that is posting the Mood.
     * @param date              (Optional) The specified date and time of the Mood post, if not included, then the
     *                          current date and time will be used.
     * @param imageBase64       (Optional) a base64-encoded String representing an image that the user can
     *                          have with their Mood post. if null, then no image is provided.
     * @param location          (Optional) a {@link GeoPoint} showing the user's location where they posted
     *                          the Mood. If null then no location is provided.
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
        DateFormat dfLocal = new SimpleDateFormat("MMM d, yyyy", Locale.CANADA);
        dfLocal.setTimeZone(TimeZone.getDefault());
        return dfLocal.format(date);
    }

    @Exclude
    public String getTimeLocal() {
        DateFormat dfLocal = new SimpleDateFormat("hh:mm a", Locale.CANADA);
        dfLocal.setTimeZone(TimeZone.getDefault());
        return dfLocal.format(date);
    }

    /**
     * gets the Mood's base64-encoded image if one is provided.
     *
     * @return a base64-encoded String representing the Mood's posted image, if no image was
     * provided, then a null String is returned.
     */
    public String getImageBase64() {
        return imageBase64;
    }

    /**
     * Gets the Mood's author that posted the mood.
     *
     * @return a String representing the username that posted the Mood.
     */
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
