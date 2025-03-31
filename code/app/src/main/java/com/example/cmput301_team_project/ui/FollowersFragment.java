package com.example.cmput301_team_project.ui;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.db.BatchLoader;
import com.example.cmput301_team_project.enums.UserButtonActionEnum;
import com.example.cmput301_team_project.model.PublicUser;
import com.google.android.gms.tasks.Task;

import java.util.List;

/**
 * It displays a list of followers and provides functionality to remove them.
 * This fragment extends {@link BaseUserListFragment} to inherit common user list functionality
 *
 * The fragment displays each follower with a "Remove" button that allows the current user
 * to remove the follower.
 */
public class FollowersFragment extends BaseUserListFragment {
    @Override
    protected int getUserButtonTextId() {
        return R.string.remove;
    }

    @Override
    protected UserButtonActionEnum getUserButtonAction() {
        return UserButtonActionEnum.REMOVE;
    }

    @Override
    protected Task<List<PublicUser>> loadDefaultData(BatchLoader batchLoader) {
        return userDatabaseService.getFollowers(authService.getCurrentUser(), batchLoader);
    }

    public FollowersFragment() {
        super();
    }

    public static FollowersFragment newInstance() {
        return new FollowersFragment();
    }
}
