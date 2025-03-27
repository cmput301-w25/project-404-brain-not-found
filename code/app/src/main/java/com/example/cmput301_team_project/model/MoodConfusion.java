package com.example.cmput301_team_project.model;

import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.example.cmput301_team_project.R;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

/**
 * Subclass of {@link Mood} that represents emotional state of confusion.
 * Defines some emotional-state-specific attributes, such as display colour and emoji
 */
public class MoodConfusion extends Mood {
    /**
     * Constructor of the MoodConfusion class Object, using the current date as the Mood's posted date.
     *
     * @param socialSituation   a {@link MoodSocialSituationEnum} representing the users social situation.
     * @param trigger           a String containing the user's reason why they are the Mood they're posting.
     * @param isPublic          a boolean statement representing if the post is publicly available.
     * @param author            a String of the user's username that is posting the Mood.
     * @param imageBase64       a base64-encoded String representing an image that the user can have with their Mood
     *                          post.
     * @param location          a {@link GeoPoint} showing the user's location where they posted the Mood.
     */
    public MoodConfusion(MoodSocialSituationEnum socialSituation, String trigger, boolean isPublic, String author, String imageBase64, GeoPoint location) {
        super(socialSituation, trigger, isPublic, author, imageBase64, location);
    }

    /**
     * Constructor for the MoodConfusion class, with a specified date and time for the Mood.
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
    public MoodConfusion(MoodSocialSituationEnum socialSituation, String trigger, boolean isPublic, String author, Date date, String imageBase64, GeoPoint location) {
        super(socialSituation, trigger, isPublic, author, date, imageBase64, location);
    }

    /**
     * Returns the Mood's emotion (confused), overrides the base Moods getEmotion() method.
     *
     * @return The {@link MoodEmotionEnum} representing anger.
     */
    @Override
    public MoodEmotionEnum getEmotion() {
        return MoodEmotionEnum.CONFUSION;
    }

    /**
     * Gets the Mood's colour (orange), overrides the base Moods getColour() method.
     *
     * @return The String id of the confusion colour.
     */
    @Override
    @Exclude
    public int getColour() {
        return R.color.confusion;
    }

    /**
     * Gets the Mood's emoji (confused), overrides the base Moods getEmoji() method.
     *
     * @return the emoji id representing confusion.
     */
    @Override
    @Exclude
    public int getEmoji() {
        return R.string.emoji_confusion;
    }

    /**
     * Gets the display name of the confused emotion.
     *
     * @return the String id of the confusion Mood's display name.
     */
    @Exclude
    public int getDisplayName(){
        return R.string.mood_confusion;
    }
}
