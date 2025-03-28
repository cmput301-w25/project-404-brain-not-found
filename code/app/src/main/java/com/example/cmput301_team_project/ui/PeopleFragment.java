package com.example.cmput301_team_project.ui;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.db.BatchLoader;
import com.example.cmput301_team_project.enums.UserButtonActionEnum;
import com.example.cmput301_team_project.model.PublicUser;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class PeopleFragment extends BaseUserListFragment {
    @Override
    protected int getUserButtonTextId() {
        return R.string.follow;
    }

    @Override
    protected UserButtonActionEnum getUserButtonAction() {
        return UserButtonActionEnum.FOLLOW;
    }

    @Override
    protected Task<List<PublicUser>> loadDefaultData(BatchLoader batchLoader) {
        return userDatabaseService.getMostFollowedUsers(authService.getCurrentUser(), batchLoader);
    }

    public PeopleFragment() {
        super();
    }

    public static PeopleFragment newInstance() {
        return new PeopleFragment();
    }
}
