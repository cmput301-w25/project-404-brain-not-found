package com.example.cmput301_team_project.ui;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.enums.UserButtonActionEnum;

/**
 * A fragment that displays a list of people in the application.
 * Extends {@link BaseUserListFragment} to manage user interactions.
 */
public class PeopleFragment extends BaseUserListFragment {

    /**
     * Returns the resource ID for the user button text.
     *
     * @return The resource ID for the "Follow" button text.
     */
    @Override
    protected int getUserButtonTextId() {
        return R.string.follow;
    }

    /**
     * Returns the action associated with the user button.
     *
     * @return The {@link UserButtonActionEnum} action for "Follow".
     */
    @Override
    protected UserButtonActionEnum getUserButtonAction() {
        return UserButtonActionEnum.FOLLOW;
    }

    /**
     * Default constructor for PeopleFragment.
     */
    public PeopleFragment() {
        super();
    }

    /**
     * Factory method to create a new instance of PeopleFragment.
     *
     * @return A new instance of {@link PeopleFragment}.
     */
    public static PeopleFragment newInstance() {
        return new PeopleFragment();
    }
}
