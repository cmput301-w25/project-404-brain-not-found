package com.example.cmput301_team_project.model;

import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.example.cmput301_team_project.R;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

/**
 * Subclass of {@link Mood} that represents emotional state of shame.
 * Defines some emotional-state-specific attributes, such as display colour and emoji
 */
public class MoodShame extends Mood {

    /**
     * Constructor of the MoodShame class Object, using the current date as the Mood's posted date.
     *
     * @param socialSituation a {@link MoodSocialSituationEnum} representing the users social situation.
     * @param trigger a String containing the user's reason why they are the Mood they're posting.
     * @param isPublic a boolean statement representing if the post is publicly available.
     * @param author a String of the user's username that is posting the Mood.
     * @param imageBase64 a base64-encoded String representing an image that the user can have with their Mood
     *                    post.
     * @param location a {@link GeoPoint} showing the user's location where they posted the Mood.
     */
    public MoodShame(MoodSocialSituationEnum socialSituation, String trigger, boolean isPublic, String author, String imageBase64, GeoPoint location) {
        super(socialSituation, trigger, isPublic, author, imageBase64, location);
    }

    /**
     * Constructor for the MoodShame class, with a specified date and time for the Mood.
     *
     * @param socialSituation a {@link MoodSocialSituationEnum} representing the users social situation.
     * @param trigger a String containing the user's reason why they are the Mood they're posting.
     * @param isPublic a boolean statement representing if the post is publicly available.
     * @param author a String of the user's username that is posting the Mood.
     * @param date The specified date and time of the Mood post.
     * @param imageBase64 a base64-encoded String representing an image that the user can have with their Mood
     *                    post.
     * @param location a {@link GeoPoint} showing the user's location where they posted the Mood.
     */
    public MoodShame(MoodSocialSituationEnum socialSituation, String trigger, boolean isPublic, String author, Date date, String imageBase64, GeoPoint location) {
        super(socialSituation, trigger, isPublic, author, date, imageBase64, location);
    }

    /**
     * Returns the Mood's emotion (shame), overrides the base Moods getEmotion() method.
     *
     * @return The {@link MoodEmotionEnum} representing shame.
     */
    @Override
    public MoodEmotionEnum getEmotion() {
        return MoodEmotionEnum.SHAME;
    }

    /**
     * Gets the Mood's colour (pink), overrides the base Moods getColour() method.
     *
     * @return The String id of the shame colour.
     */
    @Override
    @Exclude
    public int getColour() {
        return R.color.shame;
    }

    /**
     * Gets the Mood's emoji (shameful), overrides the base Moods getEmoji() method.
     *
     * @return the emoji id representing shame.
     */
    @Override
    @Exclude
    public int getEmoji() {
        return R.string.emoji_shame;
    }

    /**
     * Gets the display name of the shame emotion.
     *
     * @return the String id of the shame Mood's display name.
     */
    @Exclude
    public int getDisplayName(){
        return R.string.mood_shame;
    }
}
