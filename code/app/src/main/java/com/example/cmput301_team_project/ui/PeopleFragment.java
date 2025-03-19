package com.example.cmput301_team_project.ui;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.enums.UserButtonAction;

public class PeopleFragment extends BaseUserListFragment {
    @Override
    protected int getUserButtonTextId() {
        return R.string.follow;
    }

    @Override
    protected UserButtonAction getUserButtonAction() {
        return UserButtonAction.FOLLOW;
    }

    public PeopleFragment() {
        super();
    }

    public static PeopleFragment newInstance() {
        return new PeopleFragment();
    }
}
