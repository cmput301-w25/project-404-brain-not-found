package com.example.cmput301_team_project.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.cmput301_team_project.R;

import androidx.fragment.app.Fragment;

import com.example.cmput301_team_project.db.FirebaseAuthenticationService;
import com.example.cmput301_team_project.db.MoodDatabaseService;
import com.example.cmput301_team_project.db.UserDatabaseService;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MoodFollowingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoodFollowingFragment extends BaseMoodListFragment {
    private final MoodDatabaseService moodDatabaseService;
    private final UserDatabaseService userDatabaseService;
    private TextView mood_list_title;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // Initialize the title TextView
        mood_list_title = view.findViewById(R.id.mood_list_title);

        // Set the title to indicate it's for the user's followers
        if (mood_list_title != null) {
            mood_list_title.setText(R.string.followers_mood);
        }

        return view;
    }
    @Override
    protected void loadMoodData() {
        userDatabaseService.getFollowing(FirebaseAuthenticationService.getInstance().getCurrentUser())
                        .addOnSuccessListener(following -> moodDatabaseService.getFollowingMoods(following)
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