package com.example.cmput301_team_project.ui;

import androidx.fragment.app.Fragment;

public abstract class BaseMoodListFragment extends Fragment {
    protected abstract void loadMoodData();

    @Override
    public void onResume() {
        super.onResume();
        // Refresh mood data when returning to the fragment
        loadMoodData();
    }
}
