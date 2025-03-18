package com.example.cmput301_team_project.model;

import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.example.cmput301_team_project.R;
import com.google.firebase.firestore.Exclude;

import java.util.Date;

/**
 * Subclass of {@link Mood} that represents emotional state of fear.
 * Defines some emotional-state-specific attributes, such as display colour and emoji
 */
public class MoodFear extends Mood {
    public MoodFear(MoodSocialSituationEnum socialSituation, String trigger, String author, String imageBase64) {
        super(socialSituation, trigger, author, imageBase64);
    }

    public MoodFear(MoodSocialSituationEnum socialSituation, String trigger, String author, Date date, String imageBase64) {
        super(socialSituation, trigger, author, date, imageBase64);
    }

    @Override
    public MoodEmotionEnum getEmotion() {
        return MoodEmotionEnum.FEAR;
    }

    @Override
    @Exclude
    public int getColour() {
        return R.color.fear;
    }

    @Override
    @Exclude
    public int getEmoji() {
        return R.string.emoji_fear;
    }

    public int getDisplayName(){
        return R.string.mood_fear;
    }
}
