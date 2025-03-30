package com.example.cmput301_team_project.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.View;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.db.FirebaseAuthenticationService;
import com.example.cmput301_team_project.db.MoodDatabaseService;
import com.example.cmput301_team_project.model.Mood;

/**
 * A simple {@link Fragment} subclass for user mood history screen.
 * Responsible for user's mood history list.
 * Use the {@link MoodHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoodHistoryFragment extends BaseMoodListFragment implements MoodFormFragment.MoodFormDialogListener {
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
    protected void setupUI(View view) {
        ImageButton addMoodButton;
        ViewStub addMoodStub = view.findViewById(R.id.add_mood_stub);
        if(addMoodStub == null) {
            addMoodButton = view.findViewById(R.id.add_mood_button);
        }
        else {
            addMoodButton = (ImageButton) addMoodStub.inflate();
        }

        addMoodButton.setOnClickListener(v -> {
            MoodFormFragment.newInstance(null).show(getChildFragmentManager(), "Add Mood Event");
        });
    }

    @Override
    public void addMood(Mood mood) {
        moodDatabaseService.addMood(mood);
        loadMoodData();
    }

    @Override
    public void replaceMood(Mood newMood) {
        moodDatabaseService.updateMood(newMood);
        loadMoodData();
    }

    /**
     * Loads mood data from Firestore database
     */
    @Override
    protected void loadMoodData() {
        moodDatabaseService.getMoodList(FirebaseAuthenticationService.getInstance().getCurrentUser(), moodFilterState)
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
        return true;
    }

}