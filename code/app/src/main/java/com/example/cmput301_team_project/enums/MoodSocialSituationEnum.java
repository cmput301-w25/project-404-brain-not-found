package com.example.cmput301_team_project.enums;

import android.content.Context;

import com.example.cmput301_team_project.ui.HintDropdownEnumInterface;
import com.example.cmput301_team_project.R;

/**
 * Enum that represents the social situations a Mood post can have.
 *
 * It implements {@link HintDropdownEnumInterface} in order to be used in hint dropdowns on mood form.
 */
public enum MoodSocialSituationEnum implements HintDropdownEnumInterface {
    NONE(R.string.social_situation_dropdown_hint),
    ALONE(R.string.social_alone),
    PAIR(R.string.social_pair),
    SEVERAL(R.string.social_several),
    CROWD(R.string.social_crowd);

    private final int resId;
    MoodSocialSituationEnum(int resId) {
        this.resId = resId;
    }

    /**
     * Returns the display String for the Social Situation based on its resource ID.
     *
     * @param context The context used to access the Social Situation resources.
     * @return The display String of the selected Social Situation from the dropdown.
     */
    @Override
    public String getDropdownDisplayName(Context context) {
        return context.getString(resId);
    }
}
