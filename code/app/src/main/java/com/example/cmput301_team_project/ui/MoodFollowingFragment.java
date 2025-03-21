package com.example.cmput301_team_project.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cmput301_team_project.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MoodFollowingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoodFollowingFragment extends Fragment {

    public MoodFollowingFragment() {
        // Required empty public constructor
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mood_following, container, false);
    }
}