package com.example.cmput301_team_project;

import android.content.Context;

/**
 * Enum that represents mood emotional state.
 * It implements {@link HintDropdownEnumInterface} in order to be used in hint dropdowns on mood form.
 */
public enum MoodEmotionEnum implements HintDropdownEnumInterface {
    NONE(R.string.emotional_state_dropdown_hint),
    ANGER(R.string.anger),
    CONFUSION(R.string.confusion),
    DISGUST(R.string.disgust),
    FEAR(R.string.fear),
    HAPPINESS(R.string.happiness),
    SADNESS(R.string.sadness),
    SHAME(R.string.shame),
    SURPRISE(R.string.surprise);

    private final int resId;
    MoodEmotionEnum(int resId) {
        this.resId = resId;
    }

    @Override
    public String getDropdownDisplayName(Context context) {
        return context.getString(resId);
    }
}
