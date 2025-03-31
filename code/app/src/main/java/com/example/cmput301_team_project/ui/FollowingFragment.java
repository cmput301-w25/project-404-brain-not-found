package com.example.cmput301_team_project.ui;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.db.BatchLoader;
import com.example.cmput301_team_project.enums.UserButtonActionEnum;
import com.example.cmput301_team_project.model.PublicUser;
import com.google.android.gms.tasks.Task;

import java.util.List;

/**
 * A fragment that displays a list of users the current user is following,
 * with functionality to unfollow them.
 *
 * This fragment extends {@link BaseUserListFragment} to provide specific
 * behavior for managing followed users. Each user in the list is displayed
 * with an "Unfollow" button that allows the current user to stop following them.
 */
public class FollowingFragment extends BaseUserListFragment {
    /**
     * Provides the string resource ID for the action button text.
     *
     * @return The resource ID for the "Unfollow" button text (R.string.unfollow)
     */
    @Override
    protected int getUserButtonTextId() {
        return R.string.unfollow;
    }

    /**
     * Specifies the action type that determines search behavior and button functionality.
     * @return The {@link UserButtonActionEnum#UNFOLLOW} action type
     */
    @Override
    protected UserButtonActionEnum getUserButtonAction() {
        return UserButtonActionEnum.UNFOLLOW;
    }

    /**
     * Loads the default list of the accounts the user is following.
     *
     * @param batchLoader The {@link BatchLoader} holding the documents
     * @return A Task holding a List of {@link PublicUser}s that follow the user.
     */
    @Override
    protected Task<List<PublicUser>> loadDefaultData(BatchLoader batchLoader) {
        return userDatabaseService.getFollowing(authService.getCurrentUser(), batchLoader);
    }

    public FollowingFragment() {
        super();
    }

    /**
     * To create a new instance of this fragment.
     *
     * @return A new instance of FollowingFragment
     */
    public static FollowingFragment newInstance() {
        return new FollowingFragment();
    }
}
