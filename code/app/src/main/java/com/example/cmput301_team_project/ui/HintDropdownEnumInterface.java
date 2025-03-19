package com.example.cmput301_team_project.ui;

import android.content.Context;

/**
 * Interface for enumerations that provide a display name for dropdown menus.
 * This interface is intended to be implemented by enums that need to display
 * user-friendly names in dropdown menus.
 */
public interface HintDropdownEnumInterface {
    String getDropdownDisplayName(Context context);
}
