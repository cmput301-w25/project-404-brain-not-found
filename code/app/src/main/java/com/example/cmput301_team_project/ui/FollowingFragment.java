package com.example.cmput301_team_project.ui;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.enums.UserButtonAction;

public class FollowingFragment extends BaseUserListFragment {
    @Override
    protected int getUserButtonTextId() {
        return R.string.unfollow;
    }

    @Override
    protected UserButtonAction getUserButtonAction() {
        return UserButtonAction.UNFOLLOW;
    }

    public FollowingFragment() {
        super();
    }

    public static FollowingFragment newInstance() {
        return new FollowingFragment();
    }
}
