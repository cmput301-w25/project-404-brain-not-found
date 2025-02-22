package com.example.cmput301_team_project;

import android.content.Context;

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
    public String getDisplayName(Context context) {
        return context.getString(resId);
    }
}
