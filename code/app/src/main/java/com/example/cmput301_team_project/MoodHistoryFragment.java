package com.example.cmput301_team_project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MoodHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoodHistoryFragment extends Fragment implements MoodFormFragment.MoodFormDialogListener {
    private final MoodDatabaseService moodDatabaseService;

    public MoodHistoryFragment() {
        moodDatabaseService = MoodDatabaseService.getInstance();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MoodHistoryFragment.
     */
    public static MoodHistoryFragment newInstance() {
        return new MoodHistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mood_history, container, false);

        ImageButton addMoodButton = view.findViewById(R.id.add_mood_button);
        addMoodButton.setOnClickListener(v -> {
            MoodFormFragment.newInstance().show(getChildFragmentManager(), "Add Mood Event");
        });

        return view;
    }

    @Override
    public void addMood(Mood mood) {
        moodDatabaseService.addMood(mood);
    }
}