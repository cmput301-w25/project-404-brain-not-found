package com.example.cmput301_team_project.model;

import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.example.cmput301_team_project.R;
import com.google.firebase.firestore.Exclude;

import java.util.Date;

/**
 * Subclass of {@link Mood} that represents emotional state of anger.
 * Defines some emotional-state-specific attributes, such as display colour and emoji
 */
public class MoodAnger extends Mood {
    public MoodAnger(MoodSocialSituationEnum socialSituation, String trigger, String author, String imageBase64) {
        super(socialSituation, trigger, author, imageBase64);
    }

    public MoodAnger(MoodSocialSituationEnum socialSituation, String trigger, String author, Date date, String imageBase64) {
        super(socialSituation, trigger, author, date, imageBase64);
    }

    @Override
    public MoodEmotionEnum getEmotion() {
        return MoodEmotionEnum.ANGER;
    }

    @Exclude
    @Override
    public int getColour() {
        return R.color.anger;
    }

    @Override
    @Exclude
    public int getEmoji() {
        return R.string.emoji_anger;
    }

    public int getDisplayName(){
        return R.string.mood_anger;
    }
}
