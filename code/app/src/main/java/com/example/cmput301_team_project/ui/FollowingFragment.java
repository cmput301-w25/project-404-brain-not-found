package com.example.cmput301_team_project.ui;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.db.FirebaseAuthenticationService;
import com.example.cmput301_team_project.enums.UserButtonActionEnum;
import com.example.cmput301_team_project.model.PublicUser;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class FollowingFragment extends BaseUserListFragment {
    @Override
    protected int getUserButtonTextId() {
        return R.string.unfollow;
    }

    @Override
    protected UserButtonActionEnum getUserButtonAction() {
        return UserButtonActionEnum.UNFOLLOW;
    }

    @Override
    protected Task<List<PublicUser>> loadDefaultData(int low, int high) {
        return userDatabaseService.getFollowing(FirebaseAuthenticationService.getInstance().getCurrentUser(), low, high);
    }

    public FollowingFragment() {
        super();
    }

    public static FollowingFragment newInstance() {
        return new FollowingFragment();
    }
}
