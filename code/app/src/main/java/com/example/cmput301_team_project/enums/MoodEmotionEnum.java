package com.example.cmput301_team_project.enums;

import android.content.Context;

import com.example.cmput301_team_project.ui.HintDropdownEnumInterface;
import com.example.cmput301_team_project.R;

/**
 * Enumeration that represents possible emotions a Mood post can have.
 *
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

    /**
     * Returns the display String for the emotion based on its resource ID.
     *
     * @param context The context used to access the emotion resources.
     * @return The display String of the selected emotion from the dropdown.
     */
    @Override
    public String getDropdownDisplayName(Context context) {
        return context.getString(resId);
    }
}
