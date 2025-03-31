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
    /**
     * gets the text of the button next to the user's list of followers
     *
     * @return the String id of the User button text.
     */
    @Override
    protected int getUserButtonTextId() {
        return R.string.remove;
    }

    /**
     * Gets the User button action enumeration for the user list button.
     *
     * @return The {@link UserButtonActionEnum} that corresponds to the list of followers.
     */
    @Override
    protected UserButtonActionEnum getUserButtonAction() {
        return UserButtonActionEnum.REMOVE;
    }

    /**
     * Loads the default list of the user's followers.
     *
     * @param batchLoader The {@link BatchLoader} holding the documents
     * @return A Task holding a List of {@link PublicUser}s that follow the user.
     */
    @Override
    protected Task<List<PublicUser>> loadDefaultData(BatchLoader batchLoader) {
        return userDatabaseService.getFollowers(authService.getCurrentUser(), batchLoader);
    }

    /**
     * Constructor for the follower fragment, calls the {@link BaseUserListFragment}'s constructor.
     */
    public FollowersFragment() {
        super();
    }

    /**
     * Creates a new instance of the followers fragment.
     *
     * @return the newly created followers fragment.
     */
    public static FollowersFragment newInstance() {
        return new FollowersFragment();
    }
}
