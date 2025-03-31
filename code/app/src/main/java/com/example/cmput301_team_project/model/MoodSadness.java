package com.example.cmput301_team_project.model;

import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.example.cmput301_team_project.R;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

/**
 * Subclass of {@link Mood} that represents emotional state of sadness.
 * Defines some emotional-state-specific attributes, such as display colour and emoji
 */
public class MoodSadness extends Mood {
    /**
     * Constructor of the MoodSadness class Object, using the current date as the Mood's posted date.
     *
     * @param socialSituation   a {@link MoodSocialSituationEnum} representing the users social situation.
     * @param trigger           a String containing the user's reason why they are the Mood they're posting.
     * @param isPublic          a boolean statement representing if the post is publicly available.
     * @param author            a String of the user's username that is posting the Mood.
     * @param imageBase64       a base64-encoded String representing an image that the user can have with their Mood
     *                          post.
     * @param location          a {@link GeoPoint} showing the user's location where they posted the Mood.
     */
    public MoodSadness(MoodSocialSituationEnum socialSituation, String trigger, boolean isPublic, String author, String imageBase64, GeoPoint location) {
        super(socialSituation, trigger, isPublic, author, imageBase64, location);
    }

    /**
     * Constructor for the MoodSadness class, with a specified date and time for the Mood.
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
    public MoodSadness(MoodSocialSituationEnum socialSituation, String trigger, boolean isPublic, String author, Date date, String imageBase64, GeoPoint location) {
        super(socialSituation, trigger, isPublic, author, date, imageBase64, location);
    }

    /**
     * Returns the Mood's emotion (sadness), overrides the base Moods getEmotion() method.
     *
     * @return The {@link MoodEmotionEnum} representing sadness.
     */
    @Override
    public MoodEmotionEnum getEmotion() {
        return MoodEmotionEnum.SADNESS;
    }

    /**
     * Gets the Mood's colour (blue), overrides the base Moods getColour() method.
     *
     * @return The String id of the sadness colour.
     */
    @Override
    @Exclude
    public int getColour() {
        return R.color.sadness;
    }

    /**
     * Gets the Mood's emoji (sad), overrides the base Moods getEmoji() method.
     *
     * @return the emoji id representing sadness.
     */
    @Override
    @Exclude
    public int getEmoji() {
        return R.string.emoji_sadness;
    }

    /**
     * Gets the display name of the sadness emotion.
     *
     * @return the String id of the sad Mood's display name.
     */
    @Exclude
    public int getDisplayName(){
        return R.string.mood_sadness;
    }
}
