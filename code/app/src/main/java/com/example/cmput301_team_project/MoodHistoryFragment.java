package com.example.cmput301_team_project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass for user mood history screen.
 * Responsible for user's mood history list.
 * Use the {@link MoodHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoodHistoryFragment extends Fragment implements MoodFormFragment.MoodFormDialogListener,
                                                             MoodFilterFragment.MoodFilterDialogListener {
    private final MoodDatabaseService moodDatabaseService;
    private ListView moodListView;
    private MoodListAdapter moodListAdapter;
    private ArrayList<Mood> moodList;


    public MoodHistoryFragment() {
        moodDatabaseService = MoodDatabaseService.getInstance();
        moodList = new ArrayList<>();

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mood_history, container, false);
        moodListView = view.findViewById(R.id.Mood_List);
        moodListAdapter = new MoodListAdapter(getContext(), moodList, this);
        moodListView.setAdapter(moodListAdapter);

        ImageButton addMoodButton = view.findViewById(R.id.add_mood_button);
        addMoodButton.setOnClickListener(v -> {
            MoodFormFragment.newInstance(null).show(getChildFragmentManager(), "Add Mood Event");
        });

        ImageButton filterMoodButton = view.findViewById(R.id.filter_button);
        filterMoodButton.setOnClickListener( v -> {
            MoodFilterFragment.newInstance().show(getChildFragmentManager(), "Filter Moods");
        });
        loadMoodData();
        return view;
    }

    @Override
    public void addMood(Mood mood) {
        moodDatabaseService.addMood(mood);
        loadMoodData();
    }
    @Override
    public void onResume() {
        super.onResume();
        // Refresh mood data when returning to the fragment
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
    private void loadMoodData() {
        moodDatabaseService.getMoodList()
                .addOnSuccessListener(moods -> {
                    // Clear existing list and add new data
                    moodList.clear();
                    moodList.addAll(moods);

                    // Notify adapter that data has changed
                    moodListAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle loading errors
                    Toast.makeText(getContext(), "Failed to load mood data: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    public void filterByEmotion(String emotion) {
        moodDatabaseService.filterByEmotion(emotion, this::updateFilters);
    }

    public void resetFilters() {
        moodListAdapter.clear();
        loadMoodData();
    }

    public void updateFilters(ArrayList<Mood> filteredMoods) {
        moodListAdapter.clear();
        moodListAdapter.addAll(filteredMoods);
        moodListAdapter.notifyDataSetChanged();
    }
}