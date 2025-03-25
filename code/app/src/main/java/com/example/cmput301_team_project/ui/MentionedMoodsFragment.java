package com.example.cmput301_team_project.ui;



import android.util.Log;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.db.FirebaseAuthenticationService;
import com.example.cmput301_team_project.db.MoodDatabaseService;
import com.example.cmput301_team_project.db.UserDatabaseService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class MentionedMoodsFragment extends BaseMoodListFragment{
    private final MoodDatabaseService moodDatabaseService;
    private final UserDatabaseService userDatabaseService;

    public MentionedMoodsFragment() {
        moodDatabaseService = MoodDatabaseService.getInstance();
        userDatabaseService = UserDatabaseService.getInstance();
    }

    public static MentionedMoodsFragment newInstance() {
        return new MentionedMoodsFragment();
    }



    @Override
    protected void loadMoodData() {
        userDatabaseService.getMentions(FirebaseAuthenticationService.getInstance().getCurrentUser())
                .addOnSuccessListener(mentions -> moodDatabaseService.getMentionedMoods(mentions)
                        .addOnSuccessListener(moods -> {
                            moodListAdapter.clear();
                            moodListAdapter.addAll(moods);
                            moodListAdapter.notifyDataSetChanged();
                        }));

    }

    @Override
    protected boolean isMoodOwned() {
        return false;
    }
}
