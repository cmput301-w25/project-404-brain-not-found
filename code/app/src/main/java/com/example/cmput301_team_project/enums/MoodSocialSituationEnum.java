package com.example.cmput301_team_project.enums;

import android.content.Context;

import com.example.cmput301_team_project.ui.HintDropdownEnumInterface;
import com.example.cmput301_team_project.R;

/**
 * Enum that represents mood social situation.
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

    @Override
    public String getDropdownDisplayName(Context context) {
        return context.getString(resId);
    }
}
