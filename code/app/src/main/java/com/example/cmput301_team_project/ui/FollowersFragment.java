package com.example.cmput301_team_project.ui;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.enums.UserButtonAction;

public class FollowersFragment extends BaseUserListFragment {
    @Override
    protected int getUserButtonTextId() {
        return R.string.remove;
    }

    @Override
    protected UserButtonAction getUserButtonAction() {
        return UserButtonAction.REMOVE;
    }

    public FollowersFragment() {
        super();
    }

    public static FollowersFragment newInstance() {
        return new FollowersFragment();
    }
}
