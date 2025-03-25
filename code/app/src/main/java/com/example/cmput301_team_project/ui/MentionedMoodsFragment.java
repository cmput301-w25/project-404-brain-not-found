package com.example.cmput301_team_project.ui;

import android.widget.Toast;

import com.example.cmput301_team_project.db.FirebaseAuthenticationService;
import com.example.cmput301_team_project.db.MoodDatabaseService;
import com.example.cmput301_team_project.db.UserDatabaseService;

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
        moodDatabaseService.getMoodList(FirebaseAuthenticationService.getInstance().getCurrentUser())
                .addOnSuccessListener(moods -> {
                    // Clear existing list and add new data
                    moodListAdapter.clear();
                    moodListAdapter.addAll(moods);

                    // Notify adapter that data has changed
                    moodListAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle loading errors
                    Toast.makeText(getContext(), "Failed to load mood data: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected boolean isMoodOwned() {
        return false;
    }
}
