package com.example.cmput301_team_project.ui;

import androidx.fragment.app.Fragment;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.SessionManager;
import com.example.cmput301_team_project.db.MoodDatabaseService;
import com.example.cmput301_team_project.db.UserDatabaseService;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MoodFollowingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoodFollowingFragment extends BaseMoodListFragment {
    private final MoodDatabaseService moodDatabaseService;
    private final UserDatabaseService userDatabaseService;

    public MoodFollowingFragment() {
        moodDatabaseService = MoodDatabaseService.getInstance();
        userDatabaseService = UserDatabaseService.getInstance();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MoodFollowingFragment.
     */
    public static MoodFollowingFragment newInstance() {
        return new MoodFollowingFragment();
    }

    @Override
    protected void loadMoodData() {
        userDatabaseService.getFollowing(SessionManager.getInstance().getCurrentUser())
                        .addOnSuccessListener(following -> moodDatabaseService.getFollowingMoods(following)
                                .addOnSuccessListener(moods -> {
                                    moodListAdapter.clear();
                                    moodListAdapter.addAll(moods);
                                    moodListAdapter.notifyDataSetChanged();
                                }));
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_mood_following;
    }

    @Override
    protected boolean isMoodOwned() {
        return false;
    }
}