package com.example.cmput301_team_project.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
    private MoodListAdapter moodListAdapter;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mood_following, container, false);
        ListView moodListView = view.findViewById(R.id.mood_List);
        moodListAdapter = new MoodListAdapter(getContext(), new ArrayList<>(), this, false);
        moodListView.setAdapter(moodListAdapter);
        // Inflate the layout for this fragment
        return view;
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
}